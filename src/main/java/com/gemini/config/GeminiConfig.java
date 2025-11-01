package com.gemini.config;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api-key}")
    private String geminiApiKey;
    @Value("${gemini.base-url}")
    private String baseUrl;
    @Value("${gemini.completion-path}")
    private String completionsPath;

    @Bean
    public OpenAiApi geminiApi() {
        return OpenAiApi.builder()
                .apiKey(geminiApiKey)
                .baseUrl(baseUrl)
                .completionsPath(completionsPath)
                .build();
    }
}
