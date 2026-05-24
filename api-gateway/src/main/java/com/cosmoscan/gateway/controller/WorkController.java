package com.cosmoscan.gateway.controller;

import com.cosmoscan.common.dto.AnalysisResponse;
import com.cosmoscan.common.dto.WorkSubmissionResponse;
import com.cosmoscan.gateway.service.WorkOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
@Slf4j
public class WorkController {

    private final WorkOrchestrationService orchestrationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<WorkSubmissionResponse>> submitWork(
            @RequestParam("file") MultipartFile file,
            @RequestParam("studentName") String studentName) {

        log.info("Received work submission: {} from student: {}",
                file.getOriginalFilename(), studentName);

        return orchestrationService.submitWork(file, studentName)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{workId}/report")
    public Mono<ResponseEntity<AnalysisResponse>> getWorkReport(@PathVariable String workId) {
        log.info("Fetching report for work: {}", workId);
        return orchestrationService.getWorkReport(workId)
                .map(ResponseEntity::ok);
    }
}