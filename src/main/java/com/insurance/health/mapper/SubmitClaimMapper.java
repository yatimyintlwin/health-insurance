package com.insurance.health.mapper;

import com.insurance.health.dto.SubmitClaimRequest;
import com.insurance.health.dto.SubmitClaimResponse;
import com.insurance.health.model.Claim;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class SubmitClaimMapper {

    public Claim toModel(SubmitClaimRequest dto) {
        Claim claim = new Claim();
        claim.setClaimId("CL" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
        claim.setPolicyId(dto.getPolicyId());
        claim.setUserId(dto.getUserId());
        claim.setClaimType(dto.getClaimType());
        claim.setUserName(dto.getUserName());
        claim.setClaimAmount(dto.getClaimAmount());
        claim.setClaimDate(LocalDate.now());
        claim.setStatus("Pending");
        claim.setApprovedAmount(0.0);
        return claim;
    }

    public SubmitClaimResponse toResponse(Claim claim) {
        return SubmitClaimResponse.builder()
                .claimId(claim.getClaimId())
                .policyId(claim.getPolicyId())
                .userId(claim.getUserId())
                .claimType(claim.getClaimType())
                .userName(claim.getUserName())
                .claimAmount(claim.getClaimAmount())
                .claimDate(claim.getClaimDate())
                .status(claim.getStatus())
                .build();
    }
}
