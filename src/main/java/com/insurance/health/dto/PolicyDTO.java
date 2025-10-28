package com.insurance.health.dto;

import lombok.Data;

@Data
public class PolicyDTO {
    private String userId;
    private String policyDescription;
    private String policyType;
    private Double premiumAmount;
    private Double coverageAmount;
}
