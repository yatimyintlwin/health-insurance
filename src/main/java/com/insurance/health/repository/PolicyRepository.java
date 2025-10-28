package com.insurance.health.repository;

import com.insurance.health.model.Policy;

import java.util.Optional;

public interface PolicyRepository {
    Policy save(Policy policy);

    boolean isExist(String pkValue, String skValue);

    Optional<Policy> findById(String policyId);
}
