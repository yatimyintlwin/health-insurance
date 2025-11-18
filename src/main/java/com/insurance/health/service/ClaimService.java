package com.insurance.health.service;

import com.insurance.health.dto.SubmitClaimRequest;
import com.insurance.health.dto.SubmitClaimResponse;
import com.insurance.health.dto.UpdateClaimRequest;
import com.insurance.health.model.Claim;

import java.util.List;

public interface ClaimService {
    SubmitClaimResponse submitClaim(SubmitClaimRequest request);


    Claim getClaimDetailByUser(String claimId);

    List<Claim> getAllClaimsByPolicy(String policyId, String userId);

    Claim updateClaim(UpdateClaimRequest request);
}
