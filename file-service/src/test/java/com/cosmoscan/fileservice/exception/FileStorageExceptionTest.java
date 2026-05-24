package com.cosmoscan.fileservice.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileStorageExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // when
        FileStorageException exception = new FileStorageException("Storage error");

        // then
        assertThat(exception.getMessage()).isEqualTo("Storage error");
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // given
        Throwable cause = new RuntimeException("Root cause");

        // when
        FileStorageException exception = new FileStorageException("Storage error", cause);

        // then
        assertThat(exception.getMessage()).isEqualTo("Storage error");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}