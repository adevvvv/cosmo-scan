package com.cosmoscan.analysis.client;

import com.cosmoscan.analysis.exception.ServiceUnavailableException;
import com.cosmoscan.common.dto.FileUploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceClientTest {

    @Mock
    private WebClient fileServiceWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private FileServiceClient fileServiceClient;

    @BeforeEach
    void setUp() {
        fileServiceClient = new FileServiceClient(fileServiceWebClient);
    }

    @Test
    void shouldGetFileMetadataSuccessfully() {
        // given
        String fileId = "test-123";
        FileUploadResponse expectedResponse = FileUploadResponse.builder()
                .fileId(fileId)
                .originalFilename("test.pdf")
                .sizeBytes(1024L)
                .contentType("application/pdf")
                .uploadedAt(LocalDateTime.now())
                .build();

        when(fileServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/files/{fileId}/metadata", fileId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FileUploadResponse.class))
                .thenReturn(Mono.just(expectedResponse));

        // when
        FileUploadResponse response = fileServiceClient.getFileMetadata(fileId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getFileId()).isEqualTo(fileId);
        assertThat(response.getOriginalFilename()).isEqualTo("test.pdf");
    }

    @Test
    void shouldHandleWebClientExceptionInGetFileMetadata() {
        // given
        String fileId = "test-123";

        when(fileServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/files/{fileId}/metadata", fileId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(FileUploadResponse.class))
                .thenReturn(Mono.error(
                        new WebClientResponseException(500, "Server Error", null, null, null)
                ));

        // when/then
        assertThatThrownBy(() -> fileServiceClient.getFileMetadata(fileId))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessageContaining("File service error");
    }

    @Test
    void shouldDownloadFileSuccessfully() {
        // given
        String fileId = "test-123";
        byte[] expectedContent = "test content".getBytes();

        when(fileServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/files/{fileId}", fileId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(byte[].class))
                .thenReturn(Mono.just(expectedContent));

        // when
        byte[] content = fileServiceClient.downloadFile(fileId);

        // then
        assertThat(content).isNotNull();
        assertThat(content).isEqualTo(expectedContent);
    }

    @Test
    void shouldHandleWebClientExceptionInDownloadFile() {
        // given
        String fileId = "test-123";

        when(fileServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/files/{fileId}", fileId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(byte[].class))
                .thenReturn(Mono.error(
                        new WebClientResponseException(404, "Not Found", null, null, null)
                ));

        // when/then
        assertThatThrownBy(() -> fileServiceClient.downloadFile(fileId))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessageContaining("File download error");
    }

    @Test
    void shouldUseFallbackForGetFileMetadata() {
        // given
        String fileId = "test-123";
        Exception exception = new RuntimeException("Service unavailable");

        // when/then
        assertThatThrownBy(() -> fileServiceClient.getFileMetadataFallback(fileId, exception))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessageContaining("File service is currently unavailable");
    }

    @Test
    void shouldUseFallbackForDownloadFile() {
        // given
        String fileId = "test-123";
        Exception exception = new RuntimeException("Service unavailable");

        // when/then
        assertThatThrownBy(() -> fileServiceClient.downloadFileFallback(fileId, exception))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessageContaining("File service is currently unavailable");
    }
}