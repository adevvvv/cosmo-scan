package com.cosmoscan.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class WordCloudServiceTest {

    private WordCloudService wordCloudService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        wordCloudService = new WordCloudService();
        ReflectionTestUtils.setField(wordCloudService, "outputDir", tempDir.toString());
    }

    @Test
    void shouldGenerateWordCloudImage() {
        String reportId = "test-123";
        String text = "Java Spring Boot microservices Docker Kubernetes cloud native";

        String imagePath = wordCloudService.generateWordCloud(reportId, text);

        assertThat(imagePath).isNotNull();
        assertThat(Files.exists(Path.of(imagePath))).isTrue();
    }

    @Test
    void shouldCreateImageForEmptyText() {
        // Пустая строка - split("\\W+") вернёт [""] без ошибки
        String reportId = "empty-test";
        String text = "";

        String result = wordCloudService.generateWordCloud(reportId, text);

        // Изображение создаётся, путь не null
        assertThat(result).isNotNull();
    }

    @Test
    void shouldReturnNullForNullText() {
        // null вызовет NPE -> catch -> return null
        String result = wordCloudService.generateWordCloud("null-test", null);

        assertThat(result).isNull();
    }

    @Test
    void shouldGetImageBytes() {
        String reportId = "bytes-test";
        wordCloudService.generateWordCloud(reportId, "test content");

        byte[] bytes = wordCloudService.getWordCloudImage(reportId);

        assertThat(bytes).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyArrayForMissingImage() {
        byte[] result = wordCloudService.getWordCloudImage("non-existent");

        assertThat(result).isEmpty();
    }
}