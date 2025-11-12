package com.insurance.health.service.impl;

import com.insurance.health.dto.SubmitClaimRequest;
import com.insurance.health.dto.SubmitClaimResponse;
import com.insurance.health.exception.InvalidOperationException;
import com.insurance.health.exception.PolicyNotFoundException;
import com.insurance.health.exception.UnauthorizedException;
import com.insurance.health.mapper.ClaimMapper;
import com.insurance.health.model.Claim;
import com.insurance.health.model.Policy;
import com.insurance.health.repository.ClaimRepository;
import com.insurance.health.service.ClaimService;
import com.insurance.health.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final PolicyService policyService;

    public ClaimServiceImpl(ClaimRepository claimRepository, ClaimMapper claimMapper, PolicyService policyService) {
        this.claimRepository = claimRepository;
        this.claimMapper = claimMapper;
        this.policyService = policyService;
    }

    @Override
    public SubmitClaimResponse submitClaim(SubmitClaimRequest request, String userId) {
        validatePolicyOwnership(request.getPolicyId(), userId);

        Claim claim = claimMapper.toModel(request);
        claimRepository.save(claim);

        return claimMapper.toResponse(claim);
    }

    private void validatePolicyOwnership(String policyId, String userId) {
        Policy policy = policyService.getPolicyForCustomer(userId, policyId);

        if (policy == null) {
            throw new PolicyNotFoundException("Policy not found for this user.");
        }

        if (!userId.equals(policy.getUserId())) {
            throw new UnauthorizedException("You are not authorized to claim this policy.");
        }

        if ("EXPIRED".equalsIgnoreCase(policy.getStatus())) {
            throw new InvalidOperationException("Cannot submit claim for expired policy.");
        }
    }
}
