package com.insurance.health.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitClaimRequest {
    @NotBlank(message = "Policy ID is required")
    private String policyId;

    @NotBlank(message = "Claim type is required")
    private String claimType;

    @NotBlank(message = "User name is required")
    private String userName;

    @NotNull(message = "Claim amount is required")
    @Min(value = 1, message = "Claim amount must be greater than 0")
    private Double claimAmount;
}
