package com.insurance.health.service;

import com.insurance.health.dto.PolicyDTO;
import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.model.Policy;

import java.util.List;

public interface PolicyService {
    Policy createPolicy(PolicyDTO request);
    Policy getPolicyForCustomer(String customerId, String policyId);
    List<PolicyListByCustomerResponse> listPoliciesByCustomer(String customerId);
    boolean updatePolicy(Policy policy);
}
