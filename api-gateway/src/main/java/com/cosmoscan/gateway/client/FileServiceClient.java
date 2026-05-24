package com.cosmoscan.gateway.client;

import com.cosmoscan.common.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileServiceClient {

    private final WebClient fileServiceWebClient;

    public Mono<FileUploadResponse> uploadFile(MultipartFile file) {
        log.info("Uploading file to file service: {}", file.getOriginalFilename());

        try {
            byte[] fileBytes = file.getBytes();
            String filename = file.getOriginalFilename();

            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            assert filename != null;
            bodyBuilder.part("file", fileBytes)
                    .filename(filename)
                    .contentType(MediaType.parseMediaType(
                            file.getContentType() != null ? file.getContentType() : "application/octet-stream"));

            return fileServiceWebClient.post()
                    .uri("/api/v1/files")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(FileUploadResponse.class)
                    .doOnError(e -> log.error("Failed to upload file: {}", e.getMessage()));

        } catch (Exception e) {
            log.error("Failed to read file bytes", e);
            return Mono.error(new RuntimeException("Failed to read file", e));
        }
    }
}