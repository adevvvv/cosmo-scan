package com.cosmoscan.analysis.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class AnalysisConfigTest {

    @Test
    void shouldCreateWebClientWithDefaultUrl() {
        // given
        AnalysisConfig config = new AnalysisConfig();

        // Используем ReflectionTestUtils для установки значения
        org.springframework.test.util.ReflectionTestUtils.setField(
                config, "fileServiceUrl", "http://localhost:8081");

        // when
        WebClient webClient = config.fileServiceWebClient();

        // then
        assertThat(webClient).isNotNull();
    }

    @Test
    void shouldCreateWebClientWithCustomUrl() {
        // given
        AnalysisConfig config = new AnalysisConfig();
        org.springframework.test.util.ReflectionTestUtils.setField(
                config, "fileServiceUrl", "http://custom-service:9090");

        // when
        WebClient webClient = config.fileServiceWebClient();

        // then
        assertThat(webClient).isNotNull();
    }
}