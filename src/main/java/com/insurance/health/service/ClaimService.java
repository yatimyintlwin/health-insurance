package com.insurance.health.service;


import com.insurance.health.dto.SubmitClaimRequest;
import com.insurance.health.dto.SubmitClaimResponse;

public interface ClaimService {

    SubmitClaimResponse submitClaim(SubmitClaimRequest request, String userId);
}
