package com.insurance.health.service.impl;

import com.insurance.health.dto.PolicyDTO;
import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.exception.PolicyNotFoundException;
import com.insurance.health.model.Policy;
import com.insurance.health.repository.PolicyRepository;
import com.insurance.health.service.EmailService;
import com.insurance.health.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    public Policy getPolicyDetailByUser(String policyId, String userId) {

        Optional<Policy> policy = policyRepository.findById(policyId);
        if (policy.isEmpty()) {
            throw new PolicyNotFoundException("Policy not found for user: " + userId);
        }
        if (!policy.get().getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to view this claim");
        }

        return policy.get();
    }

    @Override
    public List<PolicyListByCustomerResponse> listPoliciesByUser(String userId) {
        return policyRepository.findPoliciesByCustomer(userId);
    }

    @Override
    public Policy updatePolicy(String policyId, Policy policy) {
        policyRepository.update(policy);

        String subject = "Policy Updated Successfully";
        String message = String.format(
                "Dear %s,\nYour policy (%s) has been updated.\nNew status: %s",
                policy.getUserId(), policy.getPolicyId(), policy.getStatus());
        emailService.sendPolicyNotification(policy.getUserId(), subject, message);

        return policy;
    }

    @Override
    public String deletePolicy(String policyId, String userId) {
        policyRepository.delete(policyId, userId);
        return "Policy deleted successfully!";
    }
}
