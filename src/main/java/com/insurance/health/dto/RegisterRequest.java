package com.insurance.health.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Schema(description = "Username", example = "alex")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "User email", example = "alex@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "User password", example = "password123")
    private String password;

    @NotBlank(message = "Gender is required")
    @Schema(description = "User gender", example = "Male or Female")
    private String gender;

    @NotBlank(message = "Role is required")
    @Schema(description = "User role", example = "Admin")
    private String role;
}
