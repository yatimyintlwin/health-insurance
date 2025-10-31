package com.insurance.health.controller;

import com.insurance.health.dto.PolicyDTO;
import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.model.Policy;
import com.insurance.health.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    @PreAuthorize("#dto.userId == principal.username and hasRole('USER')")
    public ResponseEntity<Policy> createPolicy(@RequestBody PolicyDTO dto) {
        Policy created = policyService.createPolicy(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/details")
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.name")
    public ResponseEntity<Policy> getPolicyDetail(@RequestParam String customerId,
                                                  @RequestParam String policyId) {
        Policy policy = policyService.getPolicyForCustomer(customerId, policyId);
        return ResponseEntity.ok(policy);
    }

    @GetMapping("/lists")
    @PreAuthorize("#customerId == principal.username or hasRole('ADMIN')")
    public ResponseEntity<List<PolicyListByCustomerResponse>> listPolicies(@RequestParam String customerId) {
        List<PolicyListByCustomerResponse> policies = policyService.listPoliciesByCustomer(customerId);
        return ResponseEntity.ok(policies);
    }

    @PutMapping("/updates")
    @PreAuthorize("#policy.userId == principal.username or hasRole('ADMIN')")
    public ResponseEntity<Policy> updatePolicy(@RequestBody Policy policy) {
        Policy updatedPolicy = policyService.updatePolicy(policy);
        return ResponseEntity.ok(updatedPolicy);
    }

    @DeleteMapping("/cancels")
    @PreAuthorize("hasRole('ADMIN') or #customerId == principal.username")
    public ResponseEntity<String> cancelPolicy(@RequestParam String customerId,
                                               @RequestParam String policyId) {
        String result = policyService.deletePolicy(customerId, policyId);
        return ResponseEntity.ok(result);
    }
}
