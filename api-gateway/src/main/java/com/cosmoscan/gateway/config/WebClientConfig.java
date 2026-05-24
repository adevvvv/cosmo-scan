package com.cosmoscan.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${app.file-service.url:http://localhost:8081}")
    private String fileServiceUrl;

    @Value("${app.analysis-service.url:http://localhost:8082}")
    private String analysisServiceUrl;

    @Bean
    public WebClient fileServiceWebClient() {
        return WebClient.builder()
                .baseUrl(fileServiceUrl)
                .build();
    }

    @Bean
    public WebClient analysisServiceWebClient() {
        return WebClient.builder()
                .baseUrl(analysisServiceUrl)
                .build();
    }
}