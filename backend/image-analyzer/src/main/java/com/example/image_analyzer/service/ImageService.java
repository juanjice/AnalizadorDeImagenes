package com.example.image_analyzer.service;

import com.example.image_analyzer.dto.ImageDetailsDto;
import com.example.image_analyzer.dto.TagDto;
import com.example.image_analyzer.kafka.AnalyzeImageEvent;
import com.example.image_analyzer.entity.ImageRecord;
import com.example.image_analyzer.entity.ImageStatus;
import com.example.image_analyzer.exceptions.BusinessRuleException;
import com.example.image_analyzer.kafka.ImageAnalysisProducer;
import com.example.image_analyzer.repository.ImageRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.apache.commons.io.FilenameUtils;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageService {

    @Autowired
    private ImageRecordRepository imageRecordRepository;
    @Autowired
    private S3Client s3Client;
    @Value("${s3.bucket}")
    private String bucket;
    @Autowired
    private ImageAnalysisProducer imageAnalysisProducer;
    @Autowired
    private PresignedUrlService presignedUrlService;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");

    public ImageDetailsDto getById(UUID id){
        if(!imageRecordRepository.existsById(id)){
            throw new BusinessRuleException("La imagen no existe");
        }
        ImageRecord imageRecord = imageRecordRepository.findById(id).get();
        return toDto(imageRecord);
    }

    public ImageRecord submitForAnalysis(MultipartFile file){

        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("El archivo está vacío o no fue enviado.");
        }
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        UUID id = UUID.randomUUID();
        String key = "uploads/" + id + (ext != null && !ext.isBlank() ? ("." + ext) : "");

        if (ext == null || !ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new BusinessRuleException("Tipo de archivo no permitido. Solo se admiten imágenes: " + ALLOWED_EXTENSIONS);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessRuleException("Content-Type inválido. Solo se permiten imágenes.");
        }
        try{
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        ImageRecord imageRecord = new ImageRecord();
        imageRecord.setId(id);
        imageRecord.setImageKey(key);
        imageRecord.setSizeBytes(file.getSize());
        imageRecord.setStatus(ImageStatus.PENDING);
        imageRecord.setContentType(file.getContentType());
        imageRecord=imageRecordRepository.save(imageRecord);

        AnalyzeImageEvent event = new AnalyzeImageEvent(
                imageRecord.getId(),
                imageRecord.getImageKey(),
                imageRecord.getContentType(),
                imageRecord.getSizeBytes(),
                imageRecord.getCreatedAt()
        );
        imageAnalysisProducer.publish(event);

        return imageRecord;
    }

    private ImageDetailsDto toDto(ImageRecord r) {
        var tags = r.getTags() == null ? List.<TagDto>of() : r.getTags().stream()
                .map(t -> new TagDto(t.getLabel(), t.getConfidence()))
                .collect(Collectors.toList());

        String url = presignedUrlService.presign(r.getImageKey(), Duration.ofMinutes(10));
        return new ImageDetailsDto(r.getId(), r.getImageKey(), r.getContentType(), r.getSizeBytes(),
                r.getStatus(), r.getCreatedAt(), r.getUpdatedAt(), tags, url);
    }



}
