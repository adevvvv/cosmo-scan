package com.cosmoscan.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequest {

    @NotBlank(message = "File ID is required")
    @JsonProperty("file_id")
    private String fileId;

    @NotBlank(message = "Student name is required")
    @JsonProperty("student_name")
    private String studentName;
}