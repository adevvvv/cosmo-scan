package com.cosmoscan.fileservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StorageConfigTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateStorageDirectory() {
        // given
        StorageConfig config = new StorageConfig();
        Path newDir = tempDir.resolve("new-storage");
        org.springframework.test.util.ReflectionTestUtils.setField(config, "storageLocation", newDir.toString());

        // when
        config.init();

        // then
        assertThat(Files.exists(newDir)).isTrue();
    }

    @Test
    void shouldNotFailWhenDirectoryExists() {
        // given
        StorageConfig config = new StorageConfig();
        org.springframework.test.util.ReflectionTestUtils.setField(config, "storageLocation", tempDir.toString());

        // when
        config.init();

        // then
        assertThat(Files.exists(tempDir)).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCannotCreateDirectory() {
        // given
        StorageConfig config = new StorageConfig();

        // Создаем путь, который включает существующий файл как часть пути
        Path existingFile = tempDir.resolve("existing.txt");
        try {
            Files.write(existingFile, "test".getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed", e);
        }

        // Пытаемся создать поддиректорию внутри файла - это вызовет ошибку
        Path invalidPath = existingFile.resolve("subdir");
        org.springframework.test.util.ReflectionTestUtils.setField(
                config, "storageLocation", invalidPath.toString());

        // when/then
        assertThatThrownBy(() -> config.init())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not create storage directory");
    }
}