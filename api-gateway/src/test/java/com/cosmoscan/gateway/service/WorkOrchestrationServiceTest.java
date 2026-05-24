package com.cosmoscan.gateway.service;

import com.cosmoscan.common.dto.AnalysisRequest;
import com.cosmoscan.common.dto.AnalysisResponse;
import com.cosmoscan.common.dto.FileUploadResponse;
import com.cosmoscan.common.dto.WorkSubmissionResponse;
import com.cosmoscan.gateway.WorkOrchestrationService;
import com.cosmoscan.gateway.client.AnalysisServiceClient;
import com.cosmoscan.gateway.client.FileServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrchestrationServiceTest {

    @Mock
    private FileServiceClient fileServiceClient;

    @Mock
    private AnalysisServiceClient analysisServiceClient;

    @InjectMocks
    private WorkOrchestrationService orchestrationService;

    @Test
    void shouldOrchestrateSuccessfulSubmission() {
        byte[] fileBytes = "test content".getBytes();
        String filename = "test.txt";
        String studentName = "Иванов И.И.";

        FileUploadResponse fileResponse = FileUploadResponse.builder()
                .fileId("file-123")
                .originalFilename(filename)
                .sizeBytes(12L)
                .contentType("text/plain")
                .uploadedAt(LocalDateTime.now())
                .build();

        AnalysisResponse analysisResponse = AnalysisResponse.builder()
                .reportId("report-456")
                .fileId("file-123")
                .studentName(studentName)
                .status("ACCEPTED")
                .issues(List.of())
                .wordcloudUrl("/api/v1/analysis/report-456/wordcloud")
                .analyzedAt(LocalDateTime.now())
                .build();

        when(fileServiceClient.uploadFile(fileBytes, filename))
                .thenReturn(Mono.just(fileResponse));
        when(analysisServiceClient.analyzeFile(any(AnalysisRequest.class)))
                .thenReturn(Mono.just(analysisResponse));

        Mono<WorkSubmissionResponse> result = orchestrationService.submitWork(
                fileBytes, filename, studentName);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getWorkId()).isNotNull();
                    assertThat(response.getFileId()).isEqualTo("file-123");
                    assertThat(response.getReportId()).isEqualTo("report-456");
                    assertThat(response.getStatus()).isEqualTo("ACCEPTED");
                    assertThat(response.getIssues()).isEmpty();
                })
                .verifyComplete();

        ArgumentCaptor<AnalysisRequest> captor = ArgumentCaptor.forClass(AnalysisRequest.class);
        verify(analysisServiceClient).analyzeFile(captor.capture());
        assertThat(captor.getValue().getFileId()).isEqualTo("file-123");
        assertThat(captor.getValue().getStudentName()).isEqualTo(studentName);
    }

    @Test
    void shouldHandleFileServiceError() {
        byte[] fileBytes = "test".getBytes();
        String filename = "test.txt";

        when(fileServiceClient.uploadFile(fileBytes, filename))
                .thenReturn(Mono.error(new RuntimeException("File service unavailable")));

        Mono<WorkSubmissionResponse> result = orchestrationService.submitWork(
                fileBytes, filename, "Test");

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldGetWorkReport() {
        String reportId = "report-123";
        AnalysisResponse expectedResponse = AnalysisResponse.builder()
                .reportId(reportId)
                .fileId("file-123")
                .studentName("Сидоров С.С.")
                .status("ACCEPTED")
                .issues(List.of())
                .analyzedAt(LocalDateTime.now())
                .build();

        when(analysisServiceClient.getReport(reportId))
                .thenReturn(Mono.just(expectedResponse));

        Mono<AnalysisResponse> result = orchestrationService.getWorkReport(reportId);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getReportId()).isEqualTo(reportId);
                    assertThat(response.getStatus()).isEqualTo("ACCEPTED");
                })
                .verifyComplete();
    }
}