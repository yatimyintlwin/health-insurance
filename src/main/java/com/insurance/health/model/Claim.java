package com.insurance.health.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Claim {
    private String claimId;
    private String policyId;
    private String userId;
    private String claimType;
    private String userName;
    private Double claimAmount;
    private LocalDate claimDate;
    private String status;
    private LocalDate approvedDate;
    private LocalDate rejectedDate;
    private Double approvedAmount;
}
