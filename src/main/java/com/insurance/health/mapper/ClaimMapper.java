package com.insurance.health.mapper;

import com.insurance.health.model.Claim;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.util.Map;

@Component
public class ClaimMapper {

    public Claim mapToClaim(Map<String, AttributeValue> item) {
        if (item == null || item.isEmpty()) return null;

        Claim claim = new Claim();
        claim.setClaimId(item.get("claimId") != null ? item.get("claimId").s() : null);
        claim.setPolicyId(item.get("policyId") != null ? item.get("policyId").s() : null);
        claim.setUserId(item.get("userId") != null ? item.get("userId").s() : null);
        claim.setUserName(item.get("userName") != null ? item.get("userName").s() : null);
        claim.setClaimType(item.get("claimType") != null ? item.get("claimType").s() : null);
        claim.setClaimAmount(item.get("claimAmount") != null ? Double.parseDouble(item.get("claimAmount").n()) : 0.0);

        try {
            claim.setClaimDate(item.get("claimDate") != null ? LocalDate.parse(item.get("claimDate").s()) : null);
        } catch (Exception e) {
            claim.setClaimDate(null);
        }

        claim.setStatus(item.get("status") != null ? item.get("status").s() : null);
        
        try {
            claim.setApprovedDate(item.get("approvedDate") != null ? LocalDate.parse(item.get("approvedDate").s()) : null);
        } catch (Exception e) {
            claim.setApprovedDate(null);
        }
        
        try {
            claim.setRejectedDate(item.get("rejectedDate") != null ? LocalDate.parse(item.get("rejectedDate").s()) : null);
        } catch (Exception e) {
            claim.setRejectedDate(null);
        }
        
        claim.setApprovedAmount(item.get("approvedAmount") != null ? Double.parseDouble(item.get("approvedAmount").n()) : null);

        return claim;
    }
}
