package com.insurance.health.repository;

import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.model.Policy;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository {
    Policy save(Policy policy);
    boolean isExist(String pkValue, String skValue);
    Optional<Policy> findById(String policyId);
    List<PolicyListByCustomerResponse> findPoliciesByCustomer(String customerId);
    void update(Policy policy);
}
