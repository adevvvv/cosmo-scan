package com.cosmoscan.gateway.client;

import com.cosmoscan.common.dto.FileUploadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceClientTest {

    @Mock
    private WebClient fileServiceWebClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private FileServiceClient fileServiceClient;

    @Test
    void shouldUploadFileSuccessfully() {
        // given
        byte[] fileBytes = "test content".getBytes();
        String filename = "test.txt";

        FileUploadResponse expectedResponse = FileUploadResponse.builder()
                .fileId("file-123")
                .originalFilename(filename)
                .sizeBytes(12L)
                .contentType("text/plain")
                .uploadedAt(LocalDateTime.now())
                .build();

        when(fileServiceWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FileUploadResponse.class)).thenReturn(Mono.just(expectedResponse));

        // when
        Mono<FileUploadResponse> result = fileServiceClient.uploadFile(fileBytes, filename);

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getFileId()).isEqualTo("file-123");
                    assertThat(response.getOriginalFilename()).isEqualTo(filename);
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleUploadError() {
        // given
        byte[] fileBytes = "test".getBytes();
        String filename = "test.txt";

        when(fileServiceWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FileUploadResponse.class))
                .thenReturn(Mono.error(new RuntimeException("Upload failed")));

        // when
        Mono<FileUploadResponse> result = fileServiceClient.uploadFile(fileBytes, filename);

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}