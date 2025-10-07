package com.example.image_analyzer.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    @ConditionalOnBean(ChatModel.class)
    public ChatClient chatClient(ChatModel model) {
        return ChatClient.create(model);
    }
}
