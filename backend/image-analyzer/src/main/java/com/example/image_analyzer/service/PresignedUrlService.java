package com.example.image_analyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class PresignedUrlService {
    private final S3Presigner presigner;
    private final String bucket;

    public PresignedUrlService(S3Presigner presigner, @Value("${s3.bucket}") String bucket) {
        this.presigner = presigner;
        this.bucket = bucket;
    }

    public String presign(String key, Duration ttl) {
        var req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        var presignReq = GetObjectPresignRequest.builder()
                .getObjectRequest(req)
                .signatureDuration(ttl)
                .build();
        return presigner.presignGetObject(presignReq).url().toString();
    }
}

