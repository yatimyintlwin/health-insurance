package com.insurance.health.controller;

import com.insurance.health.dto.SubmitClaimRequest;
import com.insurance.health.dto.SubmitClaimResponse;
import com.insurance.health.dto.UpdateClaimRequest;
import com.insurance.health.model.Claim;
import com.insurance.health.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @PreAuthorize("#request.userId == authentication.name")
    public ResponseEntity<SubmitClaimResponse> submitClaim(@Valid @RequestBody SubmitClaimRequest request) {
        SubmitClaimResponse response = claimService.submitClaim(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{claimId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.name)")
    public ResponseEntity<Claim> getClaimDetail(@PathVariable String claimId,
                                                @PathVariable String userId) {
        Claim claim = claimService.getClaimDetailByUser(claimId);
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/policies/{policyId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.name)")
    public ResponseEntity<List<Claim>> getAllClaimsByPolicy(@PathVariable String policyId,
                                                            @PathVariable String userId) {
        List<Claim> claims = claimService.getAllClaimsByPolicy(policyId, userId);
        return ResponseEntity.ok(claims);
    }

    @PutMapping
    public ResponseEntity<Claim> updateClaim(@Valid @RequestBody UpdateClaimRequest request) {
        Claim updatedClaim = claimService.updateClaim(request);
        return ResponseEntity.ok(updatedClaim);
    }
}
