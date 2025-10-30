package com.insurance.health.dto;

import lombok.Data;

@Data
public class PolicyListByCustomerResponse {
    private String userId;
    private String policyId;
    private String policyDescription;
    private String policyType;
    private String status;
}
