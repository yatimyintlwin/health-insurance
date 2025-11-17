package com.insurance.health.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Policy {
    private String userId;
    private String policyId;
    private String policyDescription;
    private String policyType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double premiumAmount;
    private Double coverageAmount;
    private String status;
}
