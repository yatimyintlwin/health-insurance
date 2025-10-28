package com.insurance.health.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AppUser {
    private String id;

    @NotBlank(message = "Name cannot be blank")
    @Schema(description = "Username", example = "alex")
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Schema(description = "User password", example = "password123")
    private String password;

    @NotBlank(message = "Gender cannot be blank")
    @Schema(description = "User gender", example = "Male or Female")
    private String gender;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Schema(description = "User email", example = "alex@example.com")
    private String email;

    @NotBlank(message = "Role cannot be blank")
    @Schema(description = "User role", example = "Admin")
    private String role;
}
