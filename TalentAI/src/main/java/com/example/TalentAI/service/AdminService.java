package com.example.TalentAI.service;

import com.example.TalentAI.dto.LoginRequest;
import com.example.TalentAI.model.Admin;
import com.example.TalentAI.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public Admin addAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public Admin updateAdmin(Long id, Admin updatedAdmin) {
        return adminRepository.findById(id)
                .map(admin -> {
                    admin.setNameAndSurname(updatedAdmin.getNameAndSurname());
                    admin.setEmail(updatedAdmin.getEmail());
                    admin.setPassword(updatedAdmin.getPassword());
                    return adminRepository.save(admin);
                })
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> loginAdmin(LoginRequest loginRequest) {
        return adminRepository.findByEmailAndPassword(
                loginRequest.getEmail(),
                loginRequest.getPassword());

    }
}
