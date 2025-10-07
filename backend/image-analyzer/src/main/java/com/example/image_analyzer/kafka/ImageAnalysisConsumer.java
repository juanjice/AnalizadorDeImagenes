package com.example.image_analyzer.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ImageAnalysisConsumer {
    @KafkaListener(topics = "${app.kafka.topic.analyze}", groupId = "image-analyzer")
    public void onAnalyzeEvent(AnalyzeImageEvent event) {
        try {
            Thread.sleep(5_000); // simulo un evento que demora 5 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
