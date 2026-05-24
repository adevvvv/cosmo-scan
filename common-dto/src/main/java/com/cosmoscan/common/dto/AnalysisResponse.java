package com.cosmoscan.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {

    @JsonProperty("report_id")
    private String reportId;

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("student_name")
    private String studentName;

    private String status;

    private List<String> issues;

    @JsonProperty("wordcloud_url")
    private String wordcloudUrl;

    @JsonProperty("analyzed_at")
    private LocalDateTime analyzedAt;
}