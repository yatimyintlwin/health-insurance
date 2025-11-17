package com.insurance.health.repository;

import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.model.Policy;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PolicyRepository {
    Policy save(Policy policy);
//    boolean isExist(String pkValue, String skValue);
    Optional<Policy> findById(String policyId);
    List<PolicyListByCustomerResponse> findPoliciesByCustomer(String customerId);
    void update(Policy policy);
    void delete(String customerId, String policyId);

    List<Map<String, AttributeValue>> findPoliciesToExpire(LocalDate today);

    void updatePolicyStatusTransaction(String policyId, String newStatus);

    List<Map<String, AttributeValue>> findExpiredPoliciesToDelete(LocalDate today);
}
