package com.cosmoscan.analysis.repository;

import com.cosmoscan.analysis.entity.AnalysisReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AnalysisReportRepositoryTest {

    @Autowired
    private AnalysisReportRepository repository;

    @Test
    void shouldSaveAndFindReport() {
        // given
        AnalysisReport report = AnalysisReport.builder()
                .id("report-1")
                .fileId("file-1")
                .studentName("Тестов С.Т.")
                .status("ACCEPTED")
                .issues("")
                .wordCloudPath("/data/clouds/report-1.png")
                .analyzedAt(LocalDateTime.now())
                .build();

        // when
        repository.save(report);
        Optional<AnalysisReport> found = repository.findById("report-1");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo("ACCEPTED");
        assertThat(found.get().getStudentName()).isEqualTo("Тестов С.Т.");
    }

    @Test
    void shouldSaveReportWithIssues() {
        // given
        AnalysisReport report = AnalysisReport.builder()
                .id("report-2")
                .fileId("file-2")
                .studentName("Ошибкин О.О.")
                .status("NEEDS_REVISION")
                .issues("Файл слишком большой; Недопустимый формат")
                .analyzedAt(LocalDateTime.now())
                .build();

        // when
        repository.save(report);
        Optional<AnalysisReport> found = repository.findById("report-2");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getIssues()).contains("Недопустимый формат");
    }
}