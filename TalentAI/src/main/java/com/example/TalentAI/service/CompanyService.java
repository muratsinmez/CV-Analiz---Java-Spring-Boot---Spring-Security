package com.example.TalentAI.service;


import com.example.TalentAI.dto.LoginRequest;
import com.example.TalentAI.dto.RegisterRequest;
import com.example.TalentAI.model.Company;
import com.example.TalentAI.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public Company register(RegisterRequest request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .email(request.getEmail())
                .password(hashedPassword)
                .businessType(request.getBusinessType())
                .address(request.getAddress())
                .city(request.getCity())
                .phone(request.getPhone())
                .active(false)
                .freeAnalysisRemaining(3)
                .build();

        return companyRepository.save(company);
    }

    public Optional<Company> login(LoginRequest request) {
        return companyRepository.findByEmail(request.getEmail())
                .filter(company -> passwordEncoder.matches(request.getPassword(), company.getPassword()));
    }

    public void activateCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Firma bulunamadı"));

        company.setActive(true);
        companyRepository.save(company);
    }

    private String generateActivationCode(int length) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid.substring(0, length);
    }

    public String generateAndSaveActivationCode(Long companyId, int codeLength) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Şirket bulunamadı"));

        String code = generateActivationCode(codeLength); // Parametreye göre kod uzunluğu belirlenir

        company.setActivationCode(code);
        companyRepository.save(company);
        emailService.sendActivationEmail(company.getEmail(), code);
        return code;
    }

    public boolean activateWithCode(Long companyId, String code) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Şirket bulunamadı"));

        if (code.equals(company.getActivationCode())) {
            company.setActive(true);
            if (code.length() == 6) {
                company.setSubscriptionType("MONTHLY");
                company.setSubscriptionExpiry(LocalDateTime.now().plusMonths(1));
            } else if (code.length() == 9) {
                company.setSubscriptionType("YEARLY");
                company.setSubscriptionExpiry(LocalDateTime.now().plusYears(1));
            } else {
                throw new RuntimeException("Aktivasyon kodu uzunluğu abonelik türü için desteklenmiyor");
            }
            company.setActivationCode(null);
            companyRepository.save(company);
            return true;
        }
        return false;
    }

}
