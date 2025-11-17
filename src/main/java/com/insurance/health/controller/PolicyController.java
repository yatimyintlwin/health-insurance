package com.insurance.health.controller;

import com.insurance.health.dto.PolicyDTO;
import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.model.Policy;
import com.insurance.health.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PolicyController {
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping("/policies")
    @PreAuthorize("#policyDTO.userId == authentication.name")
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody PolicyDTO policyDTO) {
        Policy created = policyService.createPolicy(policyDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/policies/{policyId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.name)")
    public ResponseEntity<Policy> getPolicyDetail(@PathVariable String policyId,
                                                  @PathVariable String userId) {
        Policy policy = policyService.getPolicyDetailByUser(policyId, userId);
        return ResponseEntity.ok(policy);
    }

    @GetMapping("/users/{userId}/policies")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.name)")
    public ResponseEntity<List<PolicyListByCustomerResponse>> listPolicies(@PathVariable String userId) {
        List<PolicyListByCustomerResponse> policies = policyService.listPoliciesByUser(userId);
        return ResponseEntity.ok(policies);
    }

    @PutMapping("/policies/{policyId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #policy.userId == authentication.name)")
    public ResponseEntity<Policy> updatePolicy(@PathVariable String policyId,
                                               @Valid @RequestBody Policy policy) {
        Policy updatedPolicy = policyService.updatePolicy(policyId, policy);
        return ResponseEntity.ok(updatedPolicy);
    }

    @DeleteMapping("/policies/{policyId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.name)")
    public ResponseEntity<String> deletePolicy(@PathVariable String policyId,
                                               @PathVariable String userId) {
        String result = policyService.deletePolicy(policyId, userId);
        return ResponseEntity.ok(result);
    }
}
