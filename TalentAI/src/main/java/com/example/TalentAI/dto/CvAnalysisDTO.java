package com.example.TalentAI.dto;

import com.example.TalentAI.model.CvAnalysis;

import java.time.format.DateTimeFormatter;

public class CvAnalysisDTO {
    private String firstName;
    private String lastName;
    private String analysisText;
    private String analyzedDate;

    public CvAnalysisDTO(CvAnalysis analysis) {
        this.firstName = analysis.getFirstName();
        this.lastName = analysis.getLastName();
        this.analysisText = analysis.getAnalysisText();
        this.analyzedDate = analysis.getAnalyzedAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAnalysisText() {
        return analysisText;
    }

    public void setAnalysisText(String analysisText) {
        this.analysisText = analysisText;
    }

    public String getAnalyzedDate() {
        return analyzedDate;
    }

    public void setAnalyzedDate(String analyzedDate) {
        this.analyzedDate = analyzedDate;
    }
}
