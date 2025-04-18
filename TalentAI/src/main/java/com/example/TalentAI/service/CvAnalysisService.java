package com.example.TalentAI.service;

import com.example.TalentAI.model.Company;
import com.example.TalentAI.model.CvAnalysis;
import com.example.TalentAI.repository.CompanyRepository;
import com.example.TalentAI.repository.CvAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CvAnalysisService {

    @Autowired
    private CvAnalysisRepository repository;

    @Autowired
    private OpenAiService openAiService;
    private final CompanyRepository companyRepository;

    public CvAnalysisService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public String performAnalysis(Long companyId, String cvContent) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Şirket bulunamadı"));

        if (!company.isActive()) {
            if (company.getFreeAnalysisRemaining() > 0) {

                company.setFreeAnalysisRemaining(company.getFreeAnalysisRemaining() - 1);
                companyRepository.save(company);

                return "Analiz tamamlandı. Kalan ücretsiz hak: " + company.getFreeAnalysisRemaining();
            } else {
                throw new RuntimeException("Ücretsiz analiz haklarınız kalmadı. Devam edebilmek için ödeme yapmalısınız.");
            }
        } else {
            return "Analiz gerçekleştirildi (aktif şirket)";
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(is);
            org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        }
    }

    public String analyzeAndSaveCV(MultipartFile file, Company company) throws IOException {
        String extractedText = extractTextFromPdf(file);
        String aiResult = openAiService.analyzeCvText(extractedText);

        String[] lines = extractedText.split("\\r?\\n");
        String possibleNameLine = lines[0].trim();
        String[] nameParts = possibleNameLine.split(" ");
        String firstName = nameParts.length > 0 ? nameParts[0] : "Bilinmiyor";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        CvAnalysis analysis = new CvAnalysis();
        analysis.setFirstName(firstName);
        analysis.setLastName(lastName);
        analysis.setAnalysisText(aiResult);
        analysis.setCompany(company);
        analysis.setAnalyzedAt(LocalDateTime.now());

        repository.save(analysis);

        return aiResult;
    }

    public List<CvAnalysis> getAnalysisHistory(Long companyId) {
        return repository.findByCompanyId(companyId);
    }

    public List<CvAnalysis> getAnalysesByCompany(Long companyId) {
        return repository.findByCompanyId(companyId);
    }
}
