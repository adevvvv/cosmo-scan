package com.cosmoscan.analysis.service;

import com.cosmoscan.analysis.client.FileServiceClient;
import com.cosmoscan.analysis.entity.AnalysisReport;
import com.cosmoscan.analysis.repository.AnalysisReportRepository;
import com.cosmoscan.common.dto.AnalysisRequest;
import com.cosmoscan.common.dto.AnalysisResponse;
import com.cosmoscan.common.dto.FileUploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileAnalysisServiceTest {

    @Mock
    private FileServiceClient fileServiceClient;

    @Mock
    private AnalysisReportRepository reportRepository;

    @Mock
    private WordCloudService wordCloudService;

    @InjectMocks
    private FileAnalysisService analysisService;

    private AnalysisRequest request;
    private FileUploadResponse fileMetadata;

    @BeforeEach
    void setUp() {
        request = AnalysisRequest.builder()
                .fileId("file-123")
                .studentName("Иванов И.И.")
                .build();

        fileMetadata = FileUploadResponse.builder()
                .fileId("file-123")
                .originalFilename("test.txt")
                .sizeBytes(100L)
                .contentType("text/plain")
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldAcceptValidTxtFile() {
        when(fileServiceClient.getFileMetadata("file-123")).thenReturn(fileMetadata);
        when(fileServiceClient.downloadFile("file-123")).thenReturn("Sample text".getBytes());
        when(wordCloudService.generateWordCloud(anyString(), anyString()))
                .thenReturn("/path/to/wordcloud.png");
        when(reportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AnalysisResponse response = analysisService.analyzeFile(request);

        assertThat(response.getStatus()).isEqualTo("ACCEPTED");
        assertThat(response.getIssues()).isEmpty();
        assertThat(response.getWordcloudUrl()).isNotNull();
    }

    @Test
    void shouldRejectFileExceedingSizeLimit() {
        fileMetadata.setSizeBytes(2_000_000L);
        when(fileServiceClient.getFileMetadata("file-123")).thenReturn(fileMetadata);
        when(reportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AnalysisResponse response = analysisService.analyzeFile(request);

        assertThat(response.getStatus()).isEqualTo("NEEDS_REVISION");
        assertThat(response.getIssues()).anyMatch(i -> i.contains("превышает"));
    }

    @Test
    void shouldRejectZipArchive() {
        fileMetadata.setOriginalFilename("archive.zip");
        when(fileServiceClient.getFileMetadata("file-123")).thenReturn(fileMetadata);
        when(reportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AnalysisResponse response = analysisService.analyzeFile(request);

        assertThat(response.getStatus()).isEqualTo("NEEDS_REVISION");
        assertThat(response.getIssues()).anyMatch(i -> i.contains("не допускаются"));
    }

    @Test
    void shouldRejectInvalidFormat() {
        fileMetadata.setOriginalFilename("photo.jpg");
        when(fileServiceClient.getFileMetadata("file-123")).thenReturn(fileMetadata);
        when(reportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AnalysisResponse response = analysisService.analyzeFile(request);

        assertThat(response.getStatus()).isEqualTo("NEEDS_REVISION");
    }

    @Test
    void shouldHandleFileWithoutExtension() {
        fileMetadata.setOriginalFilename("noext");
        when(fileServiceClient.getFileMetadata("file-123")).thenReturn(fileMetadata);
        when(reportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AnalysisResponse response = analysisService.analyzeFile(request);

        assertThat(response.getStatus()).isEqualTo("NEEDS_REVISION");
    }

    @Test
    void shouldReturnReportById() {
        String reportId = "report-123";
        AnalysisReport report = AnalysisReport.builder()
                .id(reportId)
                .fileId("file-123")
                .studentName("Петров П.П.")
                .status("ACCEPTED")
                .issues("")
                .wordCloudPath("/path/to/cloud.png")
                .analyzedAt(LocalDateTime.now())
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        AnalysisResponse response = analysisService.getReport(reportId);

        assertThat(response.getReportId()).isEqualTo(reportId);
        assertThat(response.getStatus()).isEqualTo("ACCEPTED");
    }

    @Test
    void shouldThrowWhenReportNotFound() {
        when(reportRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> analysisService.getReport("bad-id"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldHandleMultipleIssues() {
        fileMetadata.setOriginalFilename("archive.rar");
        fileMetadata.setSizeBytes(3_000_000L);
        when(fileServiceClient.getFileMetadata("file-123")).thenReturn(fileMetadata);
        when(reportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AnalysisResponse response = analysisService.analyzeFile(request);

        assertThat(response.getStatus()).isEqualTo("NEEDS_REVISION");
        assertThat(response.getIssues().size()).isGreaterThanOrEqualTo(2);
    }
}