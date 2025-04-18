package com.example.TalentAI.controller;

import com.example.TalentAI.security.JwtService;
import com.example.TalentAI.dto.LoginRequest;
import com.example.TalentAI.model.Admin;
import com.example.TalentAI.model.Company;
import com.example.TalentAI.repository.AdminRepository;
import com.example.TalentAI.repository.CompanyRepository;
import com.example.TalentAI.service.AdminService;
import com.example.TalentAI.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final AdminService adminService;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final JwtService jwtService;

    // Admin Login
    @PostMapping("/login")
    public ResponseEntity<Object> loginAdmin(@RequestBody LoginRequest loginRequest) {
        Optional<Admin> adminOpt = adminService.loginAdmin(loginRequest);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            String token = jwtService.generateToken(admin.getEmail(), "ADMIN");

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", "ADMIN");
            response.put("name", admin.getNameAndSurname());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Giriş bilgileri hatalı");
        }
    }

    //Yeni admin ekle
    @PostMapping("addAdmin")
    public ResponseEntity<Admin> addAdmin(@RequestBody Admin admin) {
        Admin createdAdmin = adminService.addAdmin(admin);
        return ResponseEntity.ok(createdAdmin);
    }

    //Var olan admini güncelle
    @PostMapping("updateAdmin/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @RequestBody Admin updatedAdmin) {
        Admin updated = adminService.updateAdmin(id, updatedAdmin);
        return ResponseEntity.ok(updated);
    }

    //Admin sil
    @DeleteMapping("deleteAdmin/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Admin deleted!");
    }

    //Belirli bir admini getir
    @GetMapping("getAdminById/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id){
        Admin admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }

    //Tüm adminleri listele
    @GetMapping("getAdmins/{id}")
    public ResponseEntity<List<Admin>> getAllAdmins(){
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }
    
    // Tüm şirketleri listele
    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return ResponseEntity.ok(companies);
    }

    // Belirli bir şirketin detaylarını getir
    @GetMapping("/companies/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        return companyRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Şirket bulunamadı"));
    }

    // Şirket bilgilerini güncelle
    @PutMapping("/companies/update/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Long id, @RequestBody Company updatedCompany) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setCompanyName(updatedCompany.getCompanyName());
                    company.setEmail(updatedCompany.getEmail());
                    companyRepository.save(company);
                    return company;
                })
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Şirket bulunamadı"));
    }

    // Şirketi manuel olarak aktif et
    @PutMapping("/companies/{id}/activate")
    public ResponseEntity<String> activateCompany(@PathVariable Long id) {
        try {
            companyService.activateCompany(id);
            return ResponseEntity.ok("Şirket başarıyla aktif hale getirildi!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Şirketi pasif hale getir
    @PutMapping("/companies/{id}/deactivate")
    public ResponseEntity<String> deactivateCompany(@PathVariable Long id) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setActive(false);
                    companyRepository.save(company);
                    return ResponseEntity.ok("Şirket pasif hale getirildi.");
                })
                .orElse(ResponseEntity.status(404).body("Şirket bulunamadı."));
    }

    // Aktivasyon kodunu üret
    @PutMapping("/companies/{id}/generate-activation-code")
    public ResponseEntity<String> generateActivationCode(@PathVariable Long id, @RequestParam String subscriptionType) {
        try {
            int codeLength;
            if(subscriptionType.equalsIgnoreCase("monthly")){
                codeLength = 6;
            } else if(subscriptionType.equalsIgnoreCase("yearly")){
                codeLength = 9;
            } else {
                return ResponseEntity.badRequest().body("Geçersiz abonelik tipi. 'monthly' veya 'yearly' kullanınız.");
            }
            String code = companyService.generateAndSaveActivationCode(id, codeLength);
            return ResponseEntity.ok("Oluşturulan aktivasyon kodu: " + code);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // Aktivasyon koduyla şirketi aktif et
    @PutMapping("/companies/{id}/activate-with-code")
    public ResponseEntity<String> activateWithCode(
            @PathVariable Long id,
            @RequestParam String activationCode) {
        return companyRepository.findById(id)
                .map(company -> {
                    if (company.getActivationCode() != null && company.getActivationCode().equals(activationCode)) {
                        company.setActive(true);
                        company.setActivationCode(null); // Kod artık kullanılmaz
                        companyRepository.save(company);
                        return ResponseEntity.ok("Şirket başarıyla aktif edildi!");
                    } else {
                        return ResponseEntity.status(403).body("Aktivasyon kodu geçersiz.");
                    }
                })
                .orElse(ResponseEntity.status(404).body("Şirket bulunamadı."));
    }

    @PutMapping("/companies/{companyId}/assign-free-rights")
    public ResponseEntity<?> assignFreeUsageRights(
            @PathVariable Long companyId,
            @RequestParam int freeUsageCount) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            company.setFreeAnalysisRemaining(freeUsageCount);
            companyRepository.save(company);
            return ResponseEntity.ok("Şirketin ücretsiz analiz hakları " + freeUsageCount + " olarak güncellendi.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Şirket bulunamadı.");
        }
    }
}
