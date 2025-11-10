package com.insurance.health.repository.impl;

import com.insurance.health.dto.PolicyListByCustomerResponse;
import com.insurance.health.exception.DatabaseOperationException;
import com.insurance.health.mapper.PolicyListMapper;
import com.insurance.health.mapper.PolicyMapper;
import com.insurance.health.mapper.PolicyUpdateMapper;
import com.insurance.health.model.Policy;
import com.insurance.health.repository.PolicyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class PolicyRepositoryImpl implements PolicyRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "HealthInsurance";

    public PolicyRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public Policy save(Policy policy) {
        try {
            log.info("Saving new policy for user: {}", policy.getUserId());
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
            log.info("Policy saved successfully: {}", policy.getPolicyId());
            return policy;

        } catch (DynamoDbException ex) {
            log.error("Failed to save policy {}: {}", policy.getPolicyId(), ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to save policy to database", ex);
        }
    }

    @Override
    public boolean isExist(String pkValue, String skValue) {
        try {
            log.debug("Checking existence for item pk={}, sk={}", pkValue, skValue);
            Map<String, AttributeValue> key = Map.of(
                    "pk", AttributeValue.builder().s(pkValue).build(),
                    "sk", AttributeValue.builder().s(skValue).build()
            );

            GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build());

            boolean exists = response.hasItem();
            log.debug("Item existence result: {} (pk={}, sk={})", exists, pkValue, skValue);
            return exists;

        } catch (DynamoDbException ex) {
            log.error("Database error while checking existence (pk={}, sk={}): {}", pkValue, skValue, ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to check item existence", ex);
        }
    }

    @Override
    public Optional<Policy> findById(String policyId) {
        try {
            log.info("Fetching policy by ID: {}", policyId);
            Map<String, AttributeValue> key = Map.of(
                    "pk", AttributeValue.fromS("POLICY#" + policyId),
                    "sk", AttributeValue.fromS("POLICY_DETAILS")
            );

            GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build());

            if (!response.hasItem()) {
                log.warn("Policy not found in DB for ID: {}", policyId);
                return Optional.empty();
            }

            Policy policy = PolicyMapper.fromItem(policyId, response.item());
            log.debug("Policy found: {}", policy);
            return Optional.of(policy);

        } catch (DynamoDbException ex) {
            log.error("Database error while fetching policy {}: {}", policyId, ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to fetch policy by ID", ex);
        }
    }

    @Override
    public List<PolicyListByCustomerResponse> findPoliciesByCustomer(String customerId) {
        try {
            log.info("Querying policies for customer: {}", customerId);
            String pkValue = "CUSTOMER#" + customerId;

            QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                    .tableName(tableName)
                    .keyConditionExpression("pk = :pk and begins_with(sk, :skPrefix)")
                    .expressionAttributeValues(Map.of(
                            ":pk", AttributeValue.fromS(pkValue),
                            ":skPrefix", AttributeValue.fromS("POLICY#")
                    ))
                    .build());

            if (response.items().isEmpty()) {
                log.info("No policies found for customer: {}", customerId);
            } else {
                log.info("Found {} policies for customer: {}", response.count(), customerId);
            }

            return PolicyListMapper.fromQueryItems(customerId, response.items());

        } catch (DynamoDbException ex) {
            log.error("Database error while querying policies for customer {}: {}", customerId, ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to query policies for customer", ex);
        }
    }

    @Override
    public void update(Policy policy) {
        try {
            log.info("Updating policy: {}", policy.getPolicyId());
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
            log.info("Policy updated successfully: {}", policy.getPolicyId());

        } catch (ConditionalCheckFailedException ex) {
            log.warn("Attempted to update non-existent policy: {}", policy.getPolicyId());
            throw new DatabaseOperationException("Policy not found when updating", ex);
        } catch (DynamoDbException ex) {
            log.error("Database error while updating policy {}: {}", policy.getPolicyId(), ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to update policy", ex);
        }
    }

    @Override
    public void delete(String customerId, String policyId) {
        try {
            log.warn("Deleting policy: {} for customer: {}", policyId, customerId);
            Map<String, AttributeValue> customerKey = Map.of(
                    "pk", AttributeValue.fromS("CUSTOMER#" + customerId),
                    "sk", AttributeValue.fromS("POLICY#" + policyId)
            );

            Map<String, AttributeValue> detailsKey = Map.of(
                    "pk", AttributeValue.fromS("POLICY#" + policyId),
                    "sk", AttributeValue.fromS("POLICY_DETAILS")
            );

            dynamoDbClient.transactWriteItems(TransactWriteItemsRequest.builder()
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
                    .build());

            log.info("Policy deleted successfully: {}", policyId);

        } catch (DynamoDbException ex) {
            log.error("Database error while deleting policy {}: {}", policyId, ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to delete policy", ex);
        }
    }

    @Override
    public List<Map<String, AttributeValue>> findPoliciesToExpire(LocalDate today) {
        try {
            log.info("Finding policies expiring on or before {}", today);

            QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                    .tableName(tableName)
                    .indexName("EndDateIndex")
                    .keyConditionExpression("sk = :skVal AND endDate <= :today")
                    .expressionAttributeValues(Map.of(
                            ":skVal", AttributeValue.fromS("POLICY_DETAILS"),
                            ":today", AttributeValue.fromS(today.toString())
                    ))
                    .build());

            log.info("Found {} policies to expire", response.count());
            return response.items();

        } catch (DynamoDbException ex) {
            log.error("Database error while finding policies to expire: {}", ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to find expiring policies", ex);
        }
    }

    @Override
    public void updatePolicyStatusTransaction(String policyId, String newStatus) {
        try {
            log.info("Updating status for policy {} → {}", policyId, newStatus);
            String pkPolicy = "POLICY#" + policyId;
            String skPolicy = "POLICY_DETAILS";

            Update update = Update.builder()
                    .tableName(tableName)
                    .key(Map.of(
                            "pk", AttributeValue.fromS(pkPolicy),
                            "sk", AttributeValue.fromS(skPolicy)
                    ))
                    .updateExpression("SET #status = :newStatus")
                    .expressionAttributeNames(Map.of("#status", "status"))
                    .expressionAttributeValues(Map.of(":newStatus", AttributeValue.fromS(newStatus)))
                    .conditionExpression("attribute_exists(pk) AND attribute_exists(sk)")
                    .build();

            dynamoDbClient.transactWriteItems(TransactWriteItemsRequest.builder()
                    .transactItems(List.of(TransactWriteItem.builder().update(update).build()))
                    .build());

            log.info("Policy {} status updated successfully", policyId);

        } catch (ConditionalCheckFailedException ex) {
            log.warn("Attempted to update status for non-existent policy: {}", policyId);
            throw new DatabaseOperationException("Policy not found when updating status", ex);
        } catch (DynamoDbException ex) {
            log.error("Database error while updating policy status: {}", ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to update policy status", ex);
        }
    }

    @Override
    public List<Map<String, AttributeValue>> findExpiredPoliciesToDelete(LocalDate today) {
        try {
            LocalDate thresholdDate = today.minusDays(3);
            log.info("Finding expired policies to delete before {}", thresholdDate);

            QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                    .tableName(tableName)
                    .indexName("EndDateIndex")
                    .keyConditionExpression("sk = :detailsKey AND endDate <= :thresholdDate")
                    .filterExpression("#status = :expiredStatus")
                    .expressionAttributeNames(Map.of("#status", "status"))
                    .expressionAttributeValues(Map.of(
                            ":detailsKey", AttributeValue.fromS("POLICY_DETAILS"),
                            ":thresholdDate", AttributeValue.fromS(thresholdDate.toString()),
                            ":expiredStatus", AttributeValue.fromS("EXPIRED")
                    ))
                    .build());

            log.info("Found {} expired policies to delete", response.count());
            return response.items();

        } catch (DynamoDbException ex) {
            log.error("Database error while finding expired policies to delete: {}", ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to find expired policies to delete", ex);
        }
    }
}
