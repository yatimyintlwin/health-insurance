package com.insurance.health.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SubmitClaimResponse {
    private String claimId;
    private String policyId;
    private String claimType;
    private String userName;
    private Double claimAmount;
    private LocalDate claimDate;
    private String status;
}
