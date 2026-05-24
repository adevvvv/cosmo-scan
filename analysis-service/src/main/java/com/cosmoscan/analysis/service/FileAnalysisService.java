package com.cosmoscan.analysis.service;

import com.cosmoscan.analysis.client.FileServiceClient;
import com.cosmoscan.analysis.entity.AnalysisReport;
import com.cosmoscan.analysis.repository.AnalysisReportRepository;
import com.cosmoscan.common.dto.AnalysisRequest;
import com.cosmoscan.common.dto.AnalysisResponse;
import com.cosmoscan.common.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileAnalysisService {

    private final FileServiceClient fileServiceClient;
    private final AnalysisReportRepository reportRepository;
    private final WordCloudService wordCloudService;

    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1 MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".pdf", ".docx", ".txt");
    private static final List<String> FORBIDDEN_EXTENSIONS = List.of(".zip", ".rar", ".7z", ".tar", ".gz");

    @Transactional
    public AnalysisResponse analyzeFile(AnalysisRequest request) {
        log.info("Starting analysis for file: {} from student: {}",
                request.getFileId(), request.getStudentName());

        String reportId = UUID.randomUUID().toString();
        List<String> issues = new ArrayList<>();
        String textContent = "";
        String wordCloudPath = null;

        // 1. Get file metadata synchronously
        FileUploadResponse fileMetadata = fileServiceClient.getFileMetadata(request.getFileId());

        // 2. Validate file format
        String fileName = fileMetadata.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);

        boolean isForbidden = FORBIDDEN_EXTENSIONS.stream()
                .anyMatch(fileExtension::equalsIgnoreCase);
        if (isForbidden) {
            issues.add("Сжатые архивы не допускаются к проверке: " + fileExtension);
        }

        boolean isAllowed = ALLOWED_EXTENSIONS.stream()
                .anyMatch(fileExtension::equalsIgnoreCase);
        if (!isAllowed && !isForbidden) {
            issues.add("Недопустимый формат файла. Разрешены: PDF, DOCX, TXT. Получен: " + fileExtension);
        }

        // 3. Validate file size
        if (fileMetadata.getSizeBytes() > MAX_FILE_SIZE) {
            double sizeMB = fileMetadata.getSizeBytes() / (1024.0 * 1024.0);
            issues.add(String.format(
                    "Размер файла (%.2f МБ) превышает допустимый лимит (1 МБ)", sizeMB
            ));
        }

        // 4. Extract text and generate word cloud for valid files
        if (issues.isEmpty()) {
            try {
                byte[] fileContent = fileServiceClient.downloadFile(request.getFileId());
                textContent = extractText(fileContent, fileExtension);

                if (textContent != null && !textContent.isBlank()) {
                    try {
                        wordCloudPath = wordCloudService.generateWordCloud(reportId, textContent);
                        if (wordCloudPath != null) {
                            log.info("Word cloud generated for report: {}", reportId);
                        } else {
                            log.warn("Word cloud was not generated for report: {}", reportId);
                        }
                    } catch (Exception e) {
                        log.error("Failed to generate word cloud for report: {}", reportId, e);
                        // Облако слов не влияет на статус проверки
                    }
                }
            } catch (Exception e) {
                log.error("Failed to extract text for word cloud", e);
                // Ошибка извлечения текста не влияет на статус проверки
            }
        }

        // 5. Determine status
        String status = issues.isEmpty() ? "ACCEPTED" : "NEEDS_REVISION";

        // 6. Save report
        AnalysisReport report = AnalysisReport.builder()
                .id(reportId)
                .fileId(request.getFileId())
                .studentName(request.getStudentName())
                .status(status)
                .issues(String.join("; ", issues))
                .wordCloudPath(wordCloudPath)
                .analyzedAt(LocalDateTime.now())
                .build();

        reportRepository.save(report);

        log.info("Analysis completed. Report ID: {}, Status: {}", reportId, status);

        return AnalysisResponse.builder()
                .reportId(reportId)
                .fileId(request.getFileId())
                .studentName(request.getStudentName())
                .status(status)
                .issues(issues.isEmpty() ? List.of() : issues)
                .wordcloudUrl(wordCloudPath != null ?
                        "/api/v1/analysis/" + reportId + "/wordcloud" : null)
                .analyzedAt(report.getAnalyzedAt())
                .build();
    }

    public AnalysisResponse getReport(String reportId) {
        AnalysisReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));

        return AnalysisResponse.builder()
                .reportId(report.getId())
                .fileId(report.getFileId())
                .studentName(report.getStudentName())
                .status(report.getStatus())
                .issues(report.getIssues() != null && !report.getIssues().isEmpty() ?
                        Arrays.asList(report.getIssues().split("; ")) : List.of())
                .wordcloudUrl(report.getWordCloudPath() != null ?
                        "/api/v1/analysis/" + reportId + "/wordcloud" : null)
                .analyzedAt(report.getAnalyzedAt())
                .build();
    }

    public byte[] getWordCloud(String reportId) {
        return wordCloudService.getWordCloudImage(reportId);
    }

    private String extractText(byte[] content, String extension) throws Exception {
        return switch (extension.toLowerCase()) {
            case ".txt" -> new String(content);
            case ".pdf" -> extractTextFromPdf(content);
            case ".docx" -> extractTextFromDocx(content);
            default -> "";
        };
    }

    private String extractTextFromPdf(byte[] content) throws Exception {
        try (PDDocument document = Loader.loadPDF(content)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractTextFromDocx(byte[] content) throws Exception {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(content))) {
            StringBuilder text = new StringBuilder();
            document.getParagraphs().forEach(p -> text.append(p.getText()).append("\n"));
            return text.toString();
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot).toLowerCase() : "";
    }
}