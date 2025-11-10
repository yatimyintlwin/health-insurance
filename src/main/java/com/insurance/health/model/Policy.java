package com.insurance.health.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Policy {

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Policy ID cannot be blank")
    private String policyId;

    @NotBlank(message = "Policy description cannot be blank")
    private String policyDescription;

    @NotBlank(message = "Policy type cannot be blank")
    private String policyType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Premium amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Premium amount must be greater than zero")
    private Double premiumAmount;

    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Coverage amount must be greater than zero")
    private Double coverageAmount;

    @NotBlank(message = "Status cannot be blank")
    private String status;
}
