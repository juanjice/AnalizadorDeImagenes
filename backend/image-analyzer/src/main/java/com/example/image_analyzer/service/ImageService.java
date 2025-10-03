package com.example.image_analyzer.service;

import com.example.image_analyzer.entity.ImageRecord;
import com.example.image_analyzer.exceptions.BusinessRuleException;
import com.example.image_analyzer.repository.ImageRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ImageService {

    @Autowired
    private ImageRecordRepository imageRecordRepository;
    @Autowired
    private S3Client s3Client;
    @Value("${s3.bucket}")
    private String bucket;

    public ImageRecord submitForAnalysis(MultipartFile file){

        try{
            UUID id = UUID.randomUUID();
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String key = "uploads/" + id + (ext != null && !ext.isBlank() ? ("." + ext) : "");

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


        return null;
    }

}
