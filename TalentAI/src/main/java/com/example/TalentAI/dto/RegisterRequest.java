package com.example.TalentAI.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nameAndSurname;
    private String companyName;
    private String email;
    private String password;
    private String businessType;
    private String address;
    private String city;
    private String phone;
}
