package com.insurance.health.service.impl;

import com.insurance.health.dto.PolicyDTO;
import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.exception.PolicyNotFoundException;
import com.insurance.health.model.Policy;
import com.insurance.health.repository.PolicyRepository;
import com.insurance.health.service.EmailService;
import com.insurance.health.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final EmailService emailService;

    public PolicyServiceImpl(PolicyRepository policyRepository, EmailService emailService) {
        this.policyRepository = policyRepository;
        this.emailService = emailService;
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

        Policy saved = policyRepository.save(policy);

        String subject = "Policy Created Successfully";
        String message = String.format(
                "Dear %s,\nYour policy (%s) has been created successfully.\nStatus: %s",
                policy.getUserId(), policy.getPolicyId(), policy.getStatus());
        emailService.sendPolicyNotification(request.getUserId(), subject, message);

        return saved;
    }

    @Override
    public Policy getPolicyForCustomer(String customerId, String policyId) {
        String pkValue = "CUSTOMER#" + customerId;
        String skValue = "POLICY#" + policyId;

        if (!policyRepository.isExist(pkValue, skValue)) {
            throw new PolicyNotFoundException("Policy not found or not owned by customer: " + policyId);
        }

        return policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException(
                        "Policy details not found for policy: " + policyId));
    }

    @Override
    public List<PolicyListByCustomerResponse> listPoliciesByCustomer(String customerId) {
        return policyRepository.findPoliciesByCustomer(customerId);
    }

    @Override
    public Policy updatePolicy(Policy policy) {
        String pkValue = "CUSTOMER#" + policy.getUserId();
        String skValue = "POLICY#" + policy.getPolicyId();

        if (!policyRepository.isExist(pkValue, skValue)) {
            throw new PolicyNotFoundException("Policy not found for user: " + policy.getUserId());
        }

        policyRepository.update(policy);

        String subject = "Policy Updated Successfully";
        String message = String.format(
                "Dear %s,\nYour policy (%s) has been updated.\nNew status: %s",
                policy.getUserId(), policy.getPolicyId(), policy.getStatus());
        emailService.sendPolicyNotification(policy.getUserId(), subject, message);

        return policy;
    }

    @Override
    public String deletePolicy(String customerId, String policyId) {
        String pkValue = "CUSTOMER#" + customerId;
        String skValue = "POLICY#" + policyId;

        if (!policyRepository.isExist(pkValue, skValue)) {
            throw new PolicyNotFoundException("Policy not found for user: " + customerId);
        }

        policyRepository.delete(customerId, policyId);
        return "Policy deleted successfully!";
    }
}
