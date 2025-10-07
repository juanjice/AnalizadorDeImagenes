package com.example.image_analyzer.kafka;

import com.example.image_analyzer.dto.TagDto;
import com.example.image_analyzer.entity.ImageRecord;
import com.example.image_analyzer.entity.ImageStatus;
import com.example.image_analyzer.entity.ImageTag;
import com.example.image_analyzer.repository.ImageRecordRepository;
import com.example.image_analyzer.repository.ImageTagRepository;
import com.example.image_analyzer.service.SpringAiService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.util.List;
import java.util.Optional;

@Component
public class ImageAnalysisConsumer {
    private static final Logger log = LoggerFactory.getLogger(ImageAnalysisConsumer.class);

    @Autowired
    private ImageRecordRepository imageRecordRepository;
    @Autowired
    private ImageTagRepository imageTagRepository;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private SpringAiService imageAnalyzer;

    @Value("${s3.bucket}")
    private String bucket;

    @KafkaListener(topics = "${app.kafka.topic.analyze}", groupId = "image-analyzer")
    public void onAnalyzeEvent(AnalyzeImageEvent event) {
        log.info("[Consumer] Recibido evento id={} key={} size={} ct={}",
                event.getId(), event.getImageKey(), event.getSizeBytes(), event.getContentType());
        Optional<ImageRecord> opt = imageRecordRepository.findById(event.getId());
        if (opt.isEmpty()) return;
        ImageRecord record = opt.get();
        try {
            record.setStatus(ImageStatus.PROCESSING);
            imageRecordRepository.save(record);

            // Download image from S3
            byte[] bytes;
            try (ResponseInputStream<?> stream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(record.getImageKey())
                    .build())) {
                bytes = IOUtils.toByteArray(stream);
            }
            log.info("[Consumer] Descargada imagen: {} bytes", bytes.length);

            List<TagDto> tags = imageAnalyzer.analyze(bytes, record.getContentType());
            log.info("[Consumer] Análisis generado {} tags", tags.size());
            for (TagDto t : tags) {
                ImageTag tag = new ImageTag();
                tag.setImage(record);
                tag.setLabel(t.getLabel());
                tag.setConfidence(t.getConfidence());
                imageTagRepository.save(tag);
            }

            record.setStatus(ImageStatus.COMPLETED);
            imageRecordRepository.save(record);
            log.info("[Consumer] Marcado COMPLETED id={}", record.getId());
        } catch (Exception e) {
            log.error("[Consumer] Error procesando id={}: {}", record.getId(), e.getMessage(), e);
            record.setStatus(ImageStatus.FAILED);
            imageRecordRepository.save(record);
        }
    }
}
