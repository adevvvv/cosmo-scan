package com.cosmoscan.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataResponse {

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("original_filename")
    private String originalFilename;

    @JsonProperty("size_bytes")
    private Long sizeBytes;
}