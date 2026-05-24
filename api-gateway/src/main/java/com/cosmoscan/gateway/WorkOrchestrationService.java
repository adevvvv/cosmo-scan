package com.cosmoscan.gateway;

import com.cosmoscan.common.dto.AnalysisRequest;
import com.cosmoscan.common.dto.AnalysisResponse;
import com.cosmoscan.common.dto.WorkSubmissionResponse;
import com.cosmoscan.gateway.client.AnalysisServiceClient;
import com.cosmoscan.gateway.client.FileServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkOrchestrationService {

    private final FileServiceClient fileServiceClient;
    private final AnalysisServiceClient analysisServiceClient;

    public Mono<WorkSubmissionResponse> submitWork(byte[] fileBytes, String filename, String studentName) {
        String workId = UUID.randomUUID().toString();
        log.info("Processing: {} for {}", workId, studentName);

        return fileServiceClient.uploadFile(fileBytes, filename)
                .flatMap(fileResp -> {
                    AnalysisRequest req = AnalysisRequest.builder()
                            .fileId(fileResp.getFileId())
                            .studentName(studentName)
                            .build();
                    return analysisServiceClient.analyzeFile(req)
                            .map(analysisResp -> WorkSubmissionResponse.builder()
                                    .workId(workId)
                                    .fileId(fileResp.getFileId())
                                    .reportId(analysisResp.getReportId())
                                    .status(analysisResp.getStatus())
                                    .issues(analysisResp.getIssues())
                                    .wordcloudUrl(analysisResp.getWordcloudUrl())
                                    .build());
                });
    }

    public Mono<AnalysisResponse> getWorkReport(String reportId) {
        return analysisServiceClient.getReport(reportId);
    }
}