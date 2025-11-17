package com.insurance.health.service.impl;

import com.insurance.health.dto.SubmitClaimRequest;
import com.insurance.health.dto.SubmitClaimResponse;
import com.insurance.health.dto.UpdateClaimStatusRequest;
import com.insurance.health.exception.ClaimNotFoundException;
import com.insurance.health.exception.InvalidOperationException;
import com.insurance.health.exception.ResourceNotFoundException;
import com.insurance.health.mapper.ClaimMapper;
import com.insurance.health.mapper.SubmitClaimMapper;
import com.insurance.health.model.Claim;
import com.insurance.health.model.Policy;
import com.insurance.health.repository.ClaimRepository;
import com.insurance.health.repository.impl.PolicyRepositoryImpl;
import com.insurance.health.service.ClaimService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final SubmitClaimMapper submitClaimMapper;
    private final PolicyRepositoryImpl policyRepository ;

    private final ClaimMapper claimMapper;

    public ClaimServiceImpl(ClaimRepository claimRepository, SubmitClaimMapper submitClaimMapper, PolicyRepositoryImpl policyRepository, ClaimMapper claimMapper) {
        this.claimRepository = claimRepository;
        this.submitClaimMapper = submitClaimMapper;
        this.policyRepository = policyRepository;
        this.claimMapper = claimMapper;
    }

    @Override
    public SubmitClaimResponse submitClaim(SubmitClaimRequest request) {
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!policy.getUserId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("You cannot submit a claim for someone else's policy");
        }

        if ("EXPIRED".equalsIgnoreCase(policy.getStatus())) {
            throw new InvalidOperationException("Cannot submit a claim for an expired policy");
        }

        Claim claim = submitClaimMapper.toModel(request);
        claimRepository.save(claim);

        return submitClaimMapper.toResponse(claim);
    }

    @Override
    public Claim getClaimDetailByUser(String claimId) {
        return claimRepository.findById(claimId)
                .map(claimMapper::mapToClaim)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with id: " + claimId));
    }

    @Override
    public List<Claim> getAllClaimsByPolicy(String policyId, String userId) {
        List<Map<String, AttributeValue>> items = claimRepository.findAllByPolicyId(policyId);

        if (items.isEmpty()) {
            throw new ClaimNotFoundException("No claims found for policy ID: " + policyId);
        }

        return items.stream()
                .map(claimMapper::mapToClaim)
                .toList();
    }

    @Override
    public Claim updateClaimStatus(String claimId, UpdateClaimStatusRequest request) {

        return null;
    }
}
