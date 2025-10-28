package com.insurance.health.mapper;

import com.insurance.health.model.Policy;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.util.Map;

public class PolicyMapper {

    private PolicyMapper() {
    }

    public static Policy fromItem(String policyId, Map<String, AttributeValue> item) {
        Policy policy = new Policy();
        policy.setPolicyId(policyId);
        if (item.containsKey("userId")) {
            policy.setUserId(item.get("userId").s());
        }
        policy.setPolicyDescription(item.get("policyDescription").s());
        policy.setPolicyType(item.get("policyType").s());
        policy.setStartDate(LocalDate.parse(item.get("startDate").s()));
        policy.setEndDate(LocalDate.parse(item.get("endDate").s()));
        policy.setPremiumAmount(Double.parseDouble(item.get("premiumAmount").n()));
        policy.setCoverageAmount(Double.parseDouble(item.get("coverageAmount").n()));
        policy.setStatus(item.get("status").s());
        return policy;
    }
}
