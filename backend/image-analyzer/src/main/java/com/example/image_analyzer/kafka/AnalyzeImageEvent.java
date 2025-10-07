package com.example.image_analyzer.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeImageEvent {
    private UUID id;
    private String imageKey;
    private String contentType;
    private long sizeBytes;
    private Instant createdAt;
}
