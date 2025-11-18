package com.insurance.health.dto;

import lombok.Data;

@Data
public class UpdateClaimRequest {
    private String policyId;
    private String claimId;
    private String status;
    private Double approvedAmount;
}
