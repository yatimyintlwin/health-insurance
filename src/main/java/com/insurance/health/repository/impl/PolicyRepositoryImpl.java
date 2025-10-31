package com.insurance.health.repository.impl;

import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.mapper.PolicyListMapper;
import com.insurance.health.mapper.PolicyMapper;
import com.insurance.health.mapper.PolicyUpdateMapper;
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
        Map<String, AttributeValue> policyItem = PolicyUpdateMapper.toCustomerItem(policy);
        Map<String, AttributeValue> detailsItem = PolicyUpdateMapper.toDetailsItem(policy);

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
    }

    @Override
    public Optional<Policy> findById(String policyId) {
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
    }

    @Override
    public List<PolicyListByCustomerResponse> findPoliciesByCustomer(String customerId) {
        String pkValue = "CUSTOMER#" + customerId;

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("pk = :pk and begins_with(sk, :skPrefix)")
                .expressionAttributeValues(Map.of(
                        ":pk", AttributeValue.fromS(pkValue),
                        ":skPrefix", AttributeValue.fromS("POLICY#")
                ))
                .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);
        return PolicyListMapper.fromQueryItems(customerId, response.items());
    }

    @Override
    public void update(Policy policy) {
        Map<String, AttributeValue> policyItem = PolicyUpdateMapper.toCustomerItem(policy);
        Map<String, AttributeValue> detailsItem = PolicyUpdateMapper.toDetailsItem(policy);

        TransactWriteItemsRequest transactionRequest = TransactWriteItemsRequest.builder()
                .transactItems(List.of(
                        TransactWriteItem.builder()
                                .put(Put.builder()
                                        .tableName(tableName)
                                        .item(policyItem)
                                        .conditionExpression("attribute_exists(pk) AND attribute_exists(sk)")
                                        .build())
                                .build(),
                        TransactWriteItem.builder()
                                .put(Put.builder()
                                        .tableName(tableName)
                                        .item(detailsItem)
                                        .conditionExpression("attribute_exists(pk) AND attribute_exists(sk)")
                                        .build())
                                .build()
                ))
                .build();

        dynamoDbClient.transactWriteItems(transactionRequest);
    }

    @Override
    public void delete(String customerId, String policyId) {
        Map<String, AttributeValue> customerKey = Map.of(
                "pk", AttributeValue.fromS("CUSTOMER#" + customerId),
                "sk", AttributeValue.fromS("POLICY#" + policyId)
        );

        Map<String, AttributeValue> detailsKey = Map.of(
                "pk", AttributeValue.fromS("POLICY#" + policyId),
                "sk", AttributeValue.fromS("DETAILS")
        );

        TransactWriteItemsRequest deleteTransaction = TransactWriteItemsRequest.builder()
                .transactItems(List.of(
                        TransactWriteItem.builder()
                                .delete(Delete.builder()
                                        .tableName(tableName)
                                        .key(customerKey)
                                        .conditionExpression("attribute_exists(pk) AND attribute_exists(sk)")
                                        .build())
                                .build(),
                        TransactWriteItem.builder()
                                .delete(Delete.builder()
                                        .tableName(tableName)
                                        .key(detailsKey)
                                        .conditionExpression("attribute_exists(pk) AND attribute_exists(sk)")
                                        .build())
                                .build()
                ))
                .build();

        dynamoDbClient.transactWriteItems(deleteTransaction);
    }
}
