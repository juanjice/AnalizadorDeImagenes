package com.example.image_analyzer.dto;

import java.util.UUID;

public record AnalyzeRequest(
        UUID jobId,
        String imageKey,
        String contentType
) {
}
