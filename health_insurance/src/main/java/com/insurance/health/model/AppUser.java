package com.insurance.health.model;

import lombok.Data;

@Data
public class AppUser {
    private String id;
    private String name;
    private String password;
    private String gender;
    private String email;
    private String role;
}
