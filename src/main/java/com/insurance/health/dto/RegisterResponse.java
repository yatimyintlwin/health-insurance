package com.insurance.health.dto;

import lombok.Data;

@Data
public class RegisterResponse {
    private String id;
    private String name;
    private String email;
    private String gender;
}
