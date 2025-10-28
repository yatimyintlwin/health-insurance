package com.insurance.health.service;

import com.insurance.health.dto.PolicyDTO;
import com.insurance.health.model.Policy;

public interface PolicyService {
    Policy createPolicy(PolicyDTO dto);

    Policy getPolicyForCustomer(String customerId, String policyId);
}
