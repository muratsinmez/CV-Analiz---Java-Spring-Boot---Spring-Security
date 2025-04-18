package com.example.TalentAI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean active = false;

    @Column(name = "activation_code")
    @JsonIgnore
    private String activationCode;

    @Column(nullable = false)
    private LocalDateTime activationDate;

    @Column(nullable = false)
    private String businessType;

    @Column
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private int freeAnalysisRemaining = 3;

    @Column
    private String subscriptionType; // Aylık yıllık
    @Column
    private LocalDateTime subscriptionExpiry;

    public void setActivationDate(LocalDateTime activationDate) {
        this.activationDate = activationDate;
    }

    public LocalDateTime getSubscriptionExpiry() {
        return subscriptionExpiry;
    }

    public void setSubscriptionExpiry(LocalDateTime subscriptionExpiry) {
        this.subscriptionExpiry = subscriptionExpiry;
    }

    public int getFreeAnalysisRemaining() {
        return freeAnalysisRemaining;
    }
    public void setFreeAnalysisRemaining(int freeAnalysisRemaining) {
        this.freeAnalysisRemaining = freeAnalysisRemaining;
    }
    public String getCity(){return city;}
    public void setCity(String city){this.city = city;}
    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public String getBusinessType() {return businessType;}
    public void setBusinessType(String businessType) {this.businessType = businessType;}
    public String getActivationDate() {
        return activationDate != null ? activationDate.toString() : null;
    }
    public void setActivationDate(String activationDate){this.activationDate = LocalDateTime.parse(activationDate);}
    public String getActivationCode() {
        return activationCode;
    }
    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
