package com.cosmoscan.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("original_filename")
    private String originalFilename;

    @JsonProperty("size_bytes")
    private Long sizeBytes;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("uploaded_at")
    private LocalDateTime uploadedAt;
}