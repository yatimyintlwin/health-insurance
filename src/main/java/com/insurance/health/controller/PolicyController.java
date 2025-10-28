package com.insurance.health.controller;

import com.insurance.health.dto.PolicyDTO;
import com.insurance.health.model.Policy;
import com.insurance.health.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Policy> getPolicyDetail(@RequestParam String customerId,
                                                  @RequestParam String policyId) {
        Policy policy = policyService.getPolicyForCustomer(customerId, policyId);
        return ResponseEntity.ok(policy);
    }
}
