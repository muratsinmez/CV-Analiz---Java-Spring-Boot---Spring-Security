package com.example.TalentAI.repository;

import com.example.TalentAI.model.CvAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CvAnalysisRepository extends JpaRepository<CvAnalysis, Long> {
    List<CvAnalysis> findByCompanyId(Long companyId);

}
