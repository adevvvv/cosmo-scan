package com.cosmoscan.analysis.client;

import com.cosmoscan.common.dto.FileUploadResponse;
import com.cosmoscan.analysis.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileServiceClient {

    private final WebClient fileServiceWebClient;

    @CircuitBreaker(name = "fileService", fallbackMethod = "getFileMetadataFallback")
    public FileUploadResponse getFileMetadata(String fileId) {
        log.debug("Requesting metadata for file: {}", fileId);

        try {
            return fileServiceWebClient.get()
                    .uri("/api/v1/files/{fileId}/metadata", fileId)
                    .retrieve()
                    .bodyToMono(FileUploadResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Failed to get file metadata: {}", e.getMessage());
            throw new ServiceUnavailableException("File service error: " + e.getMessage(), e);
        }
    }

    public FileUploadResponse getFileMetadataFallback(String fileId, Exception e) {
        log.error("File service unavailable, using fallback", e);
        throw new ServiceUnavailableException("File service is currently unavailable");
    }

    @CircuitBreaker(name = "fileService", fallbackMethod = "downloadFileFallback")
    public byte[] downloadFile(String fileId) {
        log.debug("Downloading file: {}", fileId);

        try {
            return fileServiceWebClient.get()
                    .uri("/api/v1/files/{fileId}", fileId)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Failed to download file: {}", e.getMessage());
            throw new ServiceUnavailableException("File download error: " + e.getMessage(), e);
        }
    }

    public byte[] downloadFileFallback(String fileId, Exception e) {
        log.error("File download unavailable, using fallback", e);
        throw new ServiceUnavailableException("File service is currently unavailable");
    }
}