package com.cosmoscan.gateway.controller;

import com.cosmoscan.common.dto.AnalysisResponse;
import com.cosmoscan.common.dto.WorkSubmissionResponse;
import com.cosmoscan.gateway.WorkOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
@Slf4j
public class WorkController {

    private final WorkOrchestrationService orchestrationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<WorkSubmissionResponse>> submitWork(
            @RequestPart("file") FilePart filePart,
            @RequestPart("studentName") String studentName) {

        log.info("Received: {} from {}", filePart.filename(), studentName);

        return filePart.content()
                .collectList()
                .flatMap(buffers -> {
                    int size = buffers.stream()
                            .mapToInt(b -> b.readableByteCount())
                            .sum();
                    byte[] bytes = new byte[size];
                    int pos = 0;
                    for (var buf : buffers) {
                        int len = buf.readableByteCount();
                        buf.read(bytes, pos, len);
                        pos += len;
                    }
                    return orchestrationService.submitWork(
                            bytes, filePart.filename(), studentName);
                })
                .map(resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp));
    }

    @GetMapping("/{workId}/report")
    public Mono<ResponseEntity<AnalysisResponse>> getWorkReport(@PathVariable String workId) {
        return orchestrationService.getWorkReport(workId)
                .map(ResponseEntity::ok);
    }
}