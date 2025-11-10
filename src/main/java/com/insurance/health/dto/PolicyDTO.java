package com.insurance.health.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PolicyDTO {

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Policy description cannot be blank")
    @Size(max = 255, message = "Policy description must be less than 255 characters")
    private String policyDescription;

    @NotBlank(message = "Policy type cannot be blank")
    private String policyType;

    @NotNull(message = "Premium amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Premium amount must be greater than zero")
    private Double premiumAmount;

    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Coverage amount must be greater than zero")
    private Double coverageAmount;
}
