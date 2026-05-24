package com.cosmoscan.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSubmissionResponse {

    @JsonProperty("work_id")
    private String workId;

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("report_id")
    private String reportId;

    private String status;

    private List<String> issues;

    @JsonProperty("wordcloud_url")
    private String wordcloudUrl;
}