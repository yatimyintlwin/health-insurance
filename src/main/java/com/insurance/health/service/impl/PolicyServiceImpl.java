package com.insurance.health.service.impl;

import com.insurance.health.dto.PolicyDTO;
import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.exception.DatabaseOperationException;
import com.insurance.health.exception.PolicyNotFoundException;
import com.insurance.health.exception.ResourceAlreadyExistException;
import com.insurance.health.model.Policy;
import com.insurance.health.repository.PolicyRepository;
import com.insurance.health.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyServiceImpl(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public Policy createPolicy(PolicyDTO request) {
        try {
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

        } catch (ConditionalCheckFailedException ex) {
            throw new ResourceAlreadyExistException("Policy already exists for this customer");
        } catch (DynamoDbException ex) {
            log.error("Database error while saving policy: {}", ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to save policy due to database error", ex);
        }
    }

    @Override
    public Policy getPolicyForCustomer(String customerId, String policyId) {
        try {
            String pkValue = "CUSTOMER#" + customerId;
            String skValue = "POLICY#" + policyId;

            if (!policyRepository.isExist(pkValue, skValue)) {
                throw new PolicyNotFoundException("Policy not found or not owned by customer: " + policyId);
            }

            return policyRepository.findById(policyId)
                    .orElseThrow(() -> new PolicyNotFoundException(
                            "Policy details not found for policy: " + policyId));

        } catch (DynamoDbException ex) {
            log.error("Database error while fetching policy: {}", ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to fetch policy due to database error", ex);
        }
    }

    @Override
    public List<PolicyListByCustomerResponse> listPoliciesByCustomer(String customerId) {
        try {
            return policyRepository.findPoliciesByCustomer(customerId);
        } catch (DynamoDbException ex) {
            log.error("Database error while listing policies: {}", ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to list policies due to database error", ex);
        }
    }

    @Override
    public Policy updatePolicy(Policy policy) {
        try {
            String pkValue = "CUSTOMER#" + policy.getUserId();
            String skValue = "POLICY#" + policy.getPolicyId();

            if (!policyRepository.isExist(pkValue, skValue)) {
                throw new PolicyNotFoundException("Policy not found for user: " + policy.getUserId());
            }

            policyRepository.update(policy);
            return policy;

        } catch (ConditionalCheckFailedException ex) {
            throw new PolicyNotFoundException("Failed to update: Policy not found or already removed");
        } catch (DynamoDbException ex) {
            log.error("Database error while updating policy: {}", ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to update policy due to database error", ex);
        }
    }

    @Override
    public String deletePolicy(String customerId, String policyId) {
        try {
            String pkValue = "CUSTOMER#" + customerId;
            String skValue = "POLICY#" + policyId;

            if (!policyRepository.isExist(pkValue, skValue)) {
                throw new PolicyNotFoundException("Policy not found for user: " + customerId);
            }

            policyRepository.delete(customerId, policyId);
            return "Policy deleted successfully!";

        } catch (ConditionalCheckFailedException ex) {
            throw new PolicyNotFoundException("Failed to delete: Policy not found or already deleted");
        } catch (DynamoDbException ex) {
            log.error("Database error while deleting policy: {}", ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to delete policy due to database error", ex);
        }
    }
}
