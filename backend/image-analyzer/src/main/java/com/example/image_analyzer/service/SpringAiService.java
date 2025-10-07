package com.example.image_analyzer.service;

import com.example.image_analyzer.dto.TagDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.util.*;


@Service
public class SpringAiService {


    private final ChatClient chatClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public SpringAiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    public List<TagDto> analyze(byte[] imageBytes, String mimeType) {
        try {
            var resp = chatClient
                    .prompt()
                    .user(u -> u
                            .text("Analiza esta imagen y devuelve solo JSON con el formato: {\\\"tags\\\":[{\\\"label\\\":string, \\\"confidence\\\": number}]} sin texto adicional.")
                            .media(MimeType.valueOf(mimeType == null ? "image/jpeg" : mimeType), new ByteArrayResource(imageBytes))
                    )
                    .call();


            String content = Optional.of(resp)
                    .map(ChatClient.CallResponseSpec::content)
                    .orElse("");

            if (content.isBlank()) return List.of();

            String cleaned = content.strip();
            if (cleaned.startsWith("```") && cleaned.endsWith("```")) {
                int firstNl = cleaned.indexOf('\n');
                cleaned = cleaned.substring(firstNl + 1, cleaned.length() - 3).trim();
            }

            Map<String, Object> json = mapper.readValue(cleaned, new TypeReference<Map<String, Object>>(){});
            Object tagsObj = json.get("tags");
            if (!(tagsObj instanceof List<?> list)) return List.of();
            List<TagDto> tags = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    String label = Objects.toString(m.get("label"), null);
                    double conf = 0.0;
                    Object c = m.get("confidence");
                    if (c instanceof Number n) conf = n.doubleValue();
                    if (label != null) tags.add(new TagDto(label, conf));
                }
            }
            return tags;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
