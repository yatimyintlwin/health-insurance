package com.insurance.health.controller;

import com.insurance.health.dto.SubmitClaimRequest;
import com.insurance.health.dto.SubmitClaimResponse;
import com.insurance.health.model.Claim;
import com.insurance.health.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/submits")
    public ResponseEntity<SubmitClaimResponse> submitClaim(@Valid @RequestBody SubmitClaimRequest request,
                                                           Authentication authentication) {
        String userId = authentication.getName();
        SubmitClaimResponse response = claimService.submitClaim(request, userId);
        return ResponseEntity.ok(response);
    }
}
