package com.cosmoscan.analysis.controller;

import com.cosmoscan.analysis.service.FileAnalysisService;
import com.cosmoscan.common.dto.AnalysisRequest;
import com.cosmoscan.common.dto.AnalysisResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FileAnalysisService analysisService;

    @Test
    void shouldAnalyzeFile() throws Exception {
        // given
        AnalysisRequest request = AnalysisRequest.builder()
                .fileId("file-123")
                .studentName("Иванов И.И.")
                .build();

        AnalysisResponse response = AnalysisResponse.builder()
                .reportId("report-456")
                .fileId("file-123")
                .studentName("Иванов И.И.")
                .status("ACCEPTED")
                .issues(List.of())
                .wordcloudUrl("/api/v1/analysis/report-456/wordcloud")
                .analyzedAt(LocalDateTime.now())
                .build();

        when(analysisService.analyzeFile(any(AnalysisRequest.class))).thenReturn(response);

        // when/then
        mockMvc.perform(post("/api/v1/analysis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.report_id").value("report-456"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void shouldGetReport() throws Exception {
        // given
        String reportId = "report-456";
        AnalysisResponse response = AnalysisResponse.builder()
                .reportId(reportId)
                .fileId("file-123")
                .studentName("Иванов И.И.")
                .status("ACCEPTED")
                .issues(List.of())
                .analyzedAt(LocalDateTime.now())
                .build();

        when(analysisService.getReport(reportId)).thenReturn(response);

        // when/then
        mockMvc.perform(get("/api/v1/analysis/{reportId}", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.report_id").value(reportId))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void shouldGetWordCloud() throws Exception {
        // given
        String reportId = "report-456";
        byte[] imageBytes = "fake-image-data".getBytes();

        when(analysisService.getWordCloud(reportId)).thenReturn(imageBytes);

        // when/then
        mockMvc.perform(get("/api/v1/analysis/{reportId}/wordcloud", reportId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void shouldReturnNoContentWhenWordCloudIsEmpty() throws Exception {
        // given
        String reportId = "report-456";
        when(analysisService.getWordCloud(reportId)).thenReturn(new byte[0]);

        // when/then
        mockMvc.perform(get("/api/v1/analysis/{reportId}/wordcloud", reportId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContentWhenWordCloudIsNull() throws Exception {
        // given
        String reportId = "report-456";
        when(analysisService.getWordCloud(reportId)).thenReturn(null);

        // when/then
        mockMvc.perform(get("/api/v1/analysis/{reportId}/wordcloud", reportId))
                .andExpect(status().isNoContent());
    }
}