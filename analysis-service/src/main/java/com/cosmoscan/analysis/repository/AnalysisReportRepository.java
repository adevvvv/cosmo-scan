package com.cosmoscan.analysis.repository;

import com.cosmoscan.analysis.entity.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, String> {
    List<AnalysisReport> findByFileId(String fileId);
    List<AnalysisReport> findByStudentName(String studentName);
}