package com.example.image_analyzer.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ImageAnalysisProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String analyzeTopic;

    public ImageAnalysisProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topic.analyze}") String analyzeTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.analyzeTopic = analyzeTopic;
    }

    public void publish(AnalyzeImageEvent event) {
        kafkaTemplate.send(analyzeTopic, event.getId().toString(), event);
    }
}
