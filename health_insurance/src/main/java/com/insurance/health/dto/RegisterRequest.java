package com.insurance.health.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String id;
    private String name;
    private String email;
    private String password;
    private String gender;
    private String role;
}