package com.cosmoscan.gateway.service;

import com.cosmoscan.common.dto.AnalysisRequest;
import com.cosmoscan.common.dto.AnalysisResponse;
import com.cosmoscan.common.dto.WorkSubmissionResponse;
import com.cosmoscan.gateway.client.AnalysisServiceClient;
import com.cosmoscan.gateway.client.FileServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkOrchestrationService {

    private final FileServiceClient fileServiceClient;
    private final AnalysisServiceClient analysisServiceClient;

    public Mono<WorkSubmissionResponse> submitWork(MultipartFile file, String studentName) {
        String workId = UUID.randomUUID().toString();
        log.info("Processing work submission: {} for student: {}", workId, studentName);

        return fileServiceClient.uploadFile(file)
                .flatMap(fileResponse -> {
                    log.info("File saved with ID: {}", fileResponse.getFileId());

                    AnalysisRequest analysisRequest = AnalysisRequest.builder()
                            .fileId(fileResponse.getFileId())
                            .studentName(studentName)
                            .build();

                    return analysisServiceClient.analyzeFile(analysisRequest)
                            .map(analysisResponse -> {
                                log.info("Analysis completed with status: {}", analysisResponse.getStatus());

                                return WorkSubmissionResponse.builder()
                                        .workId(workId)
                                        .fileId(fileResponse.getFileId())
                                        .reportId(analysisResponse.getReportId())
                                        .status(analysisResponse.getStatus())
                                        .issues(analysisResponse.getIssues())
                                        .wordcloudUrl(analysisResponse.getWordcloudUrl())
                                        .build();
                            });
                });
    }

    public Mono<AnalysisResponse> getWorkReport(String reportId) {
        log.info("Fetching report for work: {}", reportId);
        return analysisServiceClient.getReport(reportId);
    }
}