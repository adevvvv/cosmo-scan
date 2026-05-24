package com.cosmoscan.analysis.controller;

import com.cosmoscan.analysis.service.FileAnalysisService;
import com.cosmoscan.common.dto.AnalysisRequest;
import com.cosmoscan.common.dto.AnalysisResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {

    private final FileAnalysisService analysisService;

    @PostMapping
    public ResponseEntity<AnalysisResponse> analyzeFile(
            @Valid @RequestBody AnalysisRequest request) {

        log.info("Received analysis request for file: {}", request.getFileId());
        AnalysisResponse response = analysisService.analyzeFile(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<AnalysisResponse> getReport(@PathVariable String reportId) {
        log.info("Received report request: {}", reportId);
        return ResponseEntity.ok(analysisService.getReport(reportId));
    }

    @GetMapping(value = "/{reportId}/wordcloud", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getWordCloud(@PathVariable String reportId) {
        log.info("Received word cloud request for report: {}", reportId);
        byte[] image = analysisService.getWordCloud(reportId);

        if (image == null || image.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }
}