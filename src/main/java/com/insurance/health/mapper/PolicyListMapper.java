package com.insurance.health.mapper;

import com.insurance.health.dto.PolicyListByCustomerResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PolicyListMapper {
    private PolicyListMapper() {
    }

    public static List<PolicyListByCustomerResponse> fromQueryItems(String customerId, List<Map<String, AttributeValue>> items) {
        List<PolicyListByCustomerResponse> responses = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            PolicyListByCustomerResponse dto = new PolicyListByCustomerResponse();
            dto.setUserId(customerId);
            dto.setPolicyId(item.get("policyId").s());
            dto.setPolicyDescription(item.get("policyDescription").s());
            dto.setPolicyType(item.get("policyType").s());
            dto.setStatus(item.get("status").s());
            responses.add(dto);
        }

        return responses;
    }
}
