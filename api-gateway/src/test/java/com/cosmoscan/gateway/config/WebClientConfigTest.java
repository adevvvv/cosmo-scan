package com.cosmoscan.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientConfigTest {

    @Test
    void shouldCreateFileServiceWebClient() {
        // given
        WebClientConfig config = new WebClientConfig();
        ReflectionTestUtils.setField(config, "fileServiceUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(config, "analysisServiceUrl", "http://localhost:8082");

        // when
        WebClient webClient = config.fileServiceWebClient();

        // then
        assertThat(webClient).isNotNull();
    }

    @Test
    void shouldCreateAnalysisServiceWebClient() {
        // given
        WebClientConfig config = new WebClientConfig();
        ReflectionTestUtils.setField(config, "fileServiceUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(config, "analysisServiceUrl", "http://localhost:8082");

        // when
        WebClient webClient = config.analysisServiceWebClient();

        // then
        assertThat(webClient).isNotNull();
    }
}