package com.example.TalentAI.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CvAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Lob
    private String analysisText;

    private LocalDateTime analyzedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }
    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
}
