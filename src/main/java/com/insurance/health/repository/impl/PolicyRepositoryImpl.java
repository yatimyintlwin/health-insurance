package com.insurance.health.repository.impl;

import com.insurance.health.mapper.PolicyMapper;
import com.insurance.health.model.Policy;
import com.insurance.health.repository.PolicyRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PolicyRepositoryImpl implements PolicyRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "HealthInsurance";

    public PolicyRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public Policy save(Policy policy) {

        Map<String, AttributeValue> policyItem = Map.ofEntries(
                Map.entry("pk", AttributeValue.fromS("CUSTOMER#" + policy.getUserId())),
                Map.entry("sk", AttributeValue.fromS("POLICY#" + policy.getPolicyId())),
                Map.entry("policyId", AttributeValue.fromS(policy.getPolicyId())),
                Map.entry("policyDescription", AttributeValue.fromS(policy.getPolicyDescription())),
                Map.entry("policyType", AttributeValue.fromS(policy.getPolicyType())),
                Map.entry("status", AttributeValue.fromS(policy.getStatus()))
        );

        Map<String, AttributeValue> detailsItem = Map.ofEntries(
                Map.entry("pk", AttributeValue.fromS("POLICY#" + policy.getPolicyId())),
                Map.entry("sk", AttributeValue.fromS("DETAILS")),
                Map.entry("userId", AttributeValue.fromS(policy.getUserId())),
                Map.entry("policyId", AttributeValue.fromS(policy.getPolicyId())),
                Map.entry("policyDescription", AttributeValue.fromS(policy.getPolicyDescription())),
                Map.entry("policyType", AttributeValue.fromS(policy.getPolicyType())),
                Map.entry("startDate", AttributeValue.fromS(policy.getStartDate().toString())),
                Map.entry("endDate", AttributeValue.fromS(policy.getEndDate().toString())),
                Map.entry("premiumAmount", AttributeValue.fromN(String.valueOf(policy.getPremiumAmount()))),
                Map.entry("coverageAmount", AttributeValue.fromN(String.valueOf(policy.getCoverageAmount()))),
                Map.entry("status", AttributeValue.fromS(policy.getStatus()))
        );

        TransactWriteItemsRequest transactionRequest = TransactWriteItemsRequest.builder()
                .transactItems(List.of(
                        TransactWriteItem.builder()
                                .put(Put.builder()
                                        .tableName(tableName)
                                        .item(policyItem)
                                        .conditionExpression("attribute_not_exists(pk) AND attribute_not_exists(sk)")
                                        .build())
                                .build(),
                        TransactWriteItem.builder()
                                .put(Put.builder()
                                        .tableName(tableName)
                                        .item(detailsItem)
                                        .conditionExpression("attribute_not_exists(pk) AND attribute_not_exists(sk)")
                                        .build())
                                .build()
                ))
                .build();

        dynamoDbClient.transactWriteItems(transactionRequest);

        return policy;
    }

    @Override
    public boolean isExist(String pkValue, String skValue) {
        try {
            Map<String, AttributeValue> key = Map.of(
                    "pk", AttributeValue.builder().s(pkValue).build(),
                    "sk", AttributeValue.builder().s(skValue).build()
            );

            GetItemRequest request = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build();

            GetItemResponse response = dynamoDbClient.getItem(request);

            return response.hasItem();

        } catch (DynamoDbException e) {
            return false;
        }
    }

    @Override
    public Optional<Policy> findById(String policyId) {
        try {
            Map<String, AttributeValue> key = Map.of(
                    "pk", AttributeValue.fromS("POLICY#" + policyId),
                    "sk", AttributeValue.fromS("DETAILS")
            );

            GetItemRequest request = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build();

            GetItemResponse response = dynamoDbClient.getItem(request);

            if (!response.hasItem()) {
                return Optional.empty();
            }

            Policy policy = PolicyMapper.fromItem(policyId, response.item());
            return Optional.of(policy);

        } catch (DynamoDbException ex) {
            throw new RuntimeException("Failed to fetch policy: " + policyId, ex);
        }
    }
}
