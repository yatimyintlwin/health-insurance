package com.insurance.health.repository;

import com.insurance.health.model.Claim;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClaimRepository {
    Claim save(Claim claim);

    List<Map<String, AttributeValue>> findAllByPolicyId(String policyId);

    Map<String, AttributeValue> updateClaimStatus(String claimId, Map<String, AttributeValue> updates);

    Optional<Map<String, AttributeValue>> findById(String claimId);
}
