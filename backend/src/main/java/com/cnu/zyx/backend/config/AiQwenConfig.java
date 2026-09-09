package com.cnu.zyx.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.qwen")
public class AiQwenConfig {
    private String apiKey;
    private String baseUrl;
    private String model;
}