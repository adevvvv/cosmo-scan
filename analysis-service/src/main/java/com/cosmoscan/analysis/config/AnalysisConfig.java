package com.cosmoscan.analysis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class AnalysisConfig {

    @Value("${app.file-service.url:http://localhost:8081}")
    private String fileServiceUrl;

    @Bean
    public WebClient fileServiceWebClient() {
        return WebClient.builder()
                .baseUrl(fileServiceUrl)
                .build();
    }
}