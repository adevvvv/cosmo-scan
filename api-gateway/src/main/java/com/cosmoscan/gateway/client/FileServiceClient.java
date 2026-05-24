package com.cosmoscan.gateway.client;

import com.cosmoscan.common.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileServiceClient {

    private final WebClient fileServiceWebClient;

    public Mono<FileUploadResponse> uploadFile(byte[] fileBytes, String filename) {
        log.info("Uploading: {} ({} bytes)", filename, fileBytes.length);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(fileBytes))
                .filename(filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM);

        return fileServiceWebClient.post()
                .uri("/api/v1/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(FileUploadResponse.class);
    }
}