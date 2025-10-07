package com.example.image_analyzer.dto;

import com.example.image_analyzer.entity.ImageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private UUID id;
    private String imageKey;
    private String contentType;
    private long sizeBytes;
    private ImageStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private List<TagDto> tags;
    private String presignedUrl;
}
