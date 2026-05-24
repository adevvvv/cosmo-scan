package com.cosmoscan.analysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisReport {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "file_id", nullable = false)
    private String fileId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "issues", length = 2000)
    private String issues;

    @Column(name = "word_cloud_path")
    private String wordCloudPath;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    @PrePersist
    protected void onCreate() {
        analyzedAt = LocalDateTime.now();
    }
}