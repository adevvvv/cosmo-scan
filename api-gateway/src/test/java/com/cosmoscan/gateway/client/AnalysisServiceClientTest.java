package com.cosmoscan.gateway.client;

import com.cosmoscan.common.dto.AnalysisRequest;
import com.cosmoscan.common.dto.AnalysisResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceClientTest {

    @Mock
    private WebClient analysisServiceWebClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @InjectMocks
    private AnalysisServiceClient analysisServiceClient;

    @Test
    void shouldAnalyzeFileSuccessfully() {
        // given
        AnalysisRequest request = AnalysisRequest.builder()
                .fileId("file-123")
                .studentName("Иванов И.И.")
                .build();

        AnalysisResponse expectedResponse = AnalysisResponse.builder()
                .reportId("report-456")
                .fileId("file-123")
                .studentName("Иванов И.И.")
                .status("ACCEPTED")
                .issues(List.of())
                .analyzedAt(LocalDateTime.now())
                .build();

        when(analysisServiceWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AnalysisResponse.class)).thenReturn(Mono.just(expectedResponse));

        // when
        Mono<AnalysisResponse> result = analysisServiceClient.analyzeFile(request);

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getReportId()).isEqualTo("report-456");
                    assertThat(response.getStatus()).isEqualTo("ACCEPTED");
                })
                .verifyComplete();
    }

    @Test
    void shouldGetReportSuccessfully() {
        // given
        String reportId = "report-456";
        AnalysisResponse expectedResponse = AnalysisResponse.builder()
                .reportId(reportId)
                .fileId("file-123")
                .studentName("Иванов И.И.")
                .status("ACCEPTED")
                .issues(List.of())
                .analyzedAt(LocalDateTime.now())
                .build();

        when(analysisServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AnalysisResponse.class)).thenReturn(Mono.just(expectedResponse));

        // when
        Mono<AnalysisResponse> result = analysisServiceClient.getReport(reportId);

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getReportId()).isEqualTo(reportId);
                    assertThat(response.getStatus()).isEqualTo("ACCEPTED");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleErrorInAnalyzeFile() {
        // given
        AnalysisRequest request = AnalysisRequest.builder()
                .fileId("file-123")
                .studentName("Иванов И.И.")
                .build();

        when(analysisServiceWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AnalysisResponse.class))
                .thenReturn(Mono.error(new RuntimeException("Analysis failed")));

        // when
        Mono<AnalysisResponse> result = analysisServiceClient.analyzeFile(request);

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}