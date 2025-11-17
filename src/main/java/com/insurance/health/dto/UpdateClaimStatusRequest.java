package com.insurance.health.dto;

import lombok.Data;

@Data
public class UpdateClaimStatusRequest {
    private String status;
    private Double approvedAmount;
}
