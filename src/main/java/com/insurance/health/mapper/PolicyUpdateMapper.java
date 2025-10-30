package com.insurance.health.mapper;

import com.insurance.health.model.Policy;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class PolicyUpdateMapper {

    private PolicyUpdateMapper() {
    }

    public static Map<String, AttributeValue> toCustomerItem(Policy policy) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("pk", AttributeValue.fromS("CUSTOMER#" + policy.getUserId()));
        item.put("sk", AttributeValue.fromS("POLICY#" + policy.getPolicyId()));
        putCommonFields(item, policy);
        return item;
    }

    public static Map<String, AttributeValue> toDetailsItem(Policy policy) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("pk", AttributeValue.fromS("POLICY#" + policy.getPolicyId()));
        item.put("sk", AttributeValue.fromS("DETAILS"));
        putCommonFields(item, policy);
        item.put("startDate", AttributeValue.fromS(policy.getStartDate().toString()));
        item.put("endDate", AttributeValue.fromS(policy.getEndDate().toString()));
        item.put("premiumAmount", AttributeValue.fromN(String.valueOf(policy.getPremiumAmount())));
        item.put("coverageAmount", AttributeValue.fromN(String.valueOf(policy.getCoverageAmount())));
        return item;
    }

    private static void putCommonFields(Map<String, AttributeValue> item, Policy policy) {
        item.put("userId", AttributeValue.fromS(policy.getUserId()));
        item.put("policyId", AttributeValue.fromS(policy.getPolicyId()));
        item.put("policyDescription", AttributeValue.fromS(policy.getPolicyDescription()));
        item.put("policyType", AttributeValue.fromS(policy.getPolicyType()));
        item.put("status", AttributeValue.fromS(policy.getStatus()));
    }
}
