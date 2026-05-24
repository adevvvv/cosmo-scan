package com.cosmoscan.gateway.client;

import com.cosmoscan.common.dto.AnalysisRequest;
import com.cosmoscan.common.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalysisServiceClient {

    private final WebClient analysisServiceWebClient;

    public Mono<AnalysisResponse> analyzeFile(AnalysisRequest request) {
        log.info("Sending analysis request for file: {}", request.getFileId());

        return analysisServiceWebClient.post()
                .uri("/api/v1/analysis")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AnalysisResponse.class)
                .doOnError(e -> log.error("Failed to analyze file", e));
    }

    public Mono<AnalysisResponse> getReport(String reportId) {
        log.info("Fetching report: {}", reportId);

        return analysisServiceWebClient.get()
                .uri("/api/v1/analysis/{reportId}", reportId)
                .retrieve()
                .bodyToMono(AnalysisResponse.class)
                .doOnError(e -> log.error("Failed to fetch report", e));
    }
}