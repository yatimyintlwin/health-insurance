package com.insurance.health.repository;

import com.insurance.health.dto.UpdateClaimRequest;
import com.insurance.health.model.Claim;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClaimRepository {
    Claim save(Claim claim);

    List<Map<String, AttributeValue>> findAllByPolicyId(String policyId);

    Optional<Map<String, AttributeValue>> findById(String claimId);

    Map<String, AttributeValue> updateClaim(UpdateClaimRequest request);
}
