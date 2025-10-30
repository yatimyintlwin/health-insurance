package com.insurance.health.service.impl;

import com.insurance.health.dto.PolicyDTO;
import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.exception.PolicyNotFoundException;
import com.insurance.health.model.Policy;
import com.insurance.health.repository.PolicyRepository;
import com.insurance.health.service.PolicyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyServiceImpl(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public Policy createPolicy(PolicyDTO request) {
        Policy policy = new Policy();
        String policyId = "P" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        policy.setUserId(request.getUserId());
        policy.setPolicyId(policyId);
        policy.setPolicyDescription(request.getPolicyDescription());
        policy.setPolicyType(request.getPolicyType());
        policy.setCoverageAmount(request.getCoverageAmount());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setStartDate(startDate);
        policy.setEndDate(endDate);
        policy.setStatus("Pending");

        return policyRepository.save(policy);
    }

    @Override
    public Policy getPolicyForCustomer(String customerId, String policyId) {
        String pkValue = "CUSTOMER#" + customerId;
        String skValue = "POLICY#" + policyId;

        if (!policyRepository.isExist(pkValue, skValue)) {
            throw new PolicyNotFoundException("Policy not found or not owned by customer: " + policyId);
        }

        return policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found: " + policyId));
    }

    @Override
    public List<PolicyListByCustomerResponse> listPoliciesByCustomer(String customerId) {
        return policyRepository.findPoliciesByCustomer(customerId);
    }

    @Override
    public boolean updatePolicy(Policy policy) {
        String pkValue = "CUSTOMER#" + policy.getUserId();
        String skValue = "POLICY#" + policy.getPolicyId();

        if (!policyRepository.isExist(pkValue, skValue)) {
            return false;
        }

        policyRepository.update(policy);
        return true;
    }
}
