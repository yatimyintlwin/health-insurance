package com.insurance.health.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Claim {
    private String claimId;
    private String policyId;
    private String claimType;
    private String userName;
    private Double claimAmount;
    private LocalDate claimDate;
    private String status;
    private LocalDate approvedDate;
    private LocalDate rejectedDate;
    private Double approvedAmount;
}
