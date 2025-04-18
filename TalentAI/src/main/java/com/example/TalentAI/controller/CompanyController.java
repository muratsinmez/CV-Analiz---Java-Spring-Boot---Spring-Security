package com.example.TalentAI.controller;

import com.example.TalentAI.dto.CvAnalysisDTO;
import com.example.TalentAI.model.Company;
import com.example.TalentAI.model.CvAnalysis;
import com.example.TalentAI.repository.CompanyRepository;
import com.example.TalentAI.security.JwtService;
import com.example.TalentAI.service.CompanyService;
import com.example.TalentAI.dto.LoginRequest;
import com.example.TalentAI.dto.RegisterRequest;
import com.example.TalentAI.service.CvAnalysisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final CvAnalysisService cvAnalysisService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // ✅ Kayıt
    @PostMapping("/register")
    public ResponseEntity<Company> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(companyService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Company company = companyRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (company == null || !passwordEncoder.matches(request.getPassword(), company.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Giriş bilgileri hatalı.");
        }

        if (!company.isActive() && company.getFreeAnalysisRemaining() == 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Şirket hesabınız aktif değil ve ücretsiz analiz haklarınız tükenmiştir.");
        }

        String token = jwtService.generateToken(company.getEmail(), "COMPANY");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", "COMPANY");
        response.put("name", company.getCompanyName());

        if (!company.isActive()) {
            response.put("status", "inactive - free rights available");
            response.put("freeAnalysisRemaining", company.getFreeAnalysisRemaining());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<?> uploadCv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("companyId") Long companyId
    ) {
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Firma bulunamadı"));

            if (!company.isActive()) {
                if (company.getFreeAnalysisRemaining() > 0) {
                    int currentRights = company.getFreeAnalysisRemaining();
                    company.setFreeAnalysisRemaining(currentRights - 1);
                    companyRepository.save(company);
                    System.out.println("Ücretsiz hak kullanıldı. Kalan hak: " + company.getFreeAnalysisRemaining());
                } else {
                    return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                            .body("Ücretsiz analiz haklarınız tükendi, lütfen ödeme yapınız.");
                }
            }

            String result = cvAnalysisService.analyzeAndSaveCV(file, company);
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("CV analizi sırasında hata oluştu: " + e.getMessage());
        }
    }

    // CV Analiz Geçmişi
    @GetMapping("/cv/history")
    public ResponseEntity<List<CvAnalysisDTO>> getCvHistory(@RequestParam Long companyId) {
        return companyRepository.findById(companyId)
                .map(company -> {
                    List<CvAnalysis> history = cvAnalysisService.getAnalysesByCompany(companyId);
                    List<CvAnalysisDTO> dtoList = history.stream()
                            .map(CvAnalysisDTO::new)
                            .toList();
                    return ResponseEntity.ok(dtoList);
                })
                .orElse(ResponseEntity.status(404).build());
    }

    // Aktivasyon Kodu Oluşturma - ödeme sonrası kullan
    @PutMapping("/generate-activation-code/{id}")
    public ResponseEntity<String> generateActivationCode(
            @PathVariable Long id,
            @RequestParam String subscriptionType) {
        try {
            int codeLength;
            if (subscriptionType.equalsIgnoreCase("monthly")) {
                codeLength = 6;
            } else if (subscriptionType.equalsIgnoreCase("yearly")) {
                codeLength = 9;
            } else {
                return ResponseEntity.badRequest()
                        .body("Geçersiz abonelik tipi. Lütfen 'monthly' veya 'yearly' giriniz.");
            }
            String code = companyService.generateAndSaveActivationCode(id, codeLength);
            return ResponseEntity.ok("🧾 Aktivasyon kodu: " + code);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Aktivasyon Kodu ile Aktifleştirme
    @PutMapping("/activate")
    public ResponseEntity<String> activateCompanyWithCode(
            @RequestParam Long companyId,
            @RequestParam String activationCode
    ) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);

        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            if (company.getActivationCode() != null && company.getActivationCode().equals(activationCode)) {
                company.setActive(true);
                company.setActivationCode(null); // Kod artık gerekli değil
                companyRepository.save(company);
                return ResponseEntity.ok("🎉 Şirket başarıyla aktif hale getirildi!");
            } else {
                return ResponseEntity.status(403).body("❌ Aktivasyon kodu geçersiz.");
            }
        } else {
            return ResponseEntity.status(404).body("🧐 Şirket bulunamadı.");
        }
    }

    // Şirketi Manuel Pasif Hale Getirme
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivate(@PathVariable Long id) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setActive(false);
                    companyRepository.save(company);
                    return ResponseEntity.ok("⛔ Şirket pasif hale getirildi.");
                })
                .orElse(ResponseEntity.status(404).body("Şirket bulunamadı."));
    }

    @RestController
    @RequestMapping("/api/company")
    @RequiredArgsConstructor
    @CrossOrigin(origins = "http://localhost:3000")
    public class ProfileController {

        private final CompanyRepository companyRepository;
        private final JwtService jwtService;

        @GetMapping("/profile")
        public ResponseEntity<?> getProfile(HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Yetkisiz erişim: Token mevcut değil veya geçersiz.");
            }

            String token = authHeader.substring(7);
            String email;
            try {
                email = jwtService.extractUsername(token);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Token doğrulanamadı: " + e.getMessage());
            }

            Company company = companyRepository.findByEmail(email).orElse(null);
            if (company == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Şirket bulunamadı.");
            }

            return ResponseEntity.ok(company);
        }
    }
}
