package com.insurance.health.repository.impl;

import com.insurance.health.exception.DatabaseOperationException;
import com.insurance.health.mapper.ClaimMapper;
import com.insurance.health.model.Claim;
import com.insurance.health.repository.ClaimRepository;
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
public class ClaimRepositoryImpl implements ClaimRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "HealthInsurance";

    public ClaimRepositoryImpl(DynamoDbClient dynamoDbClient, ClaimMapper claimMapper) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public Claim save(Claim claim) {
        try {
            Map<String, AttributeValue> claimDetailsItem = Map.ofEntries(
                    Map.entry("pk", AttributeValue.fromS("CLAIM#" + claim.getClaimId())),
                    Map.entry("sk", AttributeValue.fromS("DETAILS")),
                    Map.entry("claimId", AttributeValue.fromS(claim.getClaimId())),
                    Map.entry("policyId", AttributeValue.fromS(claim.getPolicyId())),
                    Map.entry("userId", AttributeValue.fromS(claim.getUserId())),
                    Map.entry("claimType", AttributeValue.fromS(claim.getClaimType())),
                    Map.entry("userName", AttributeValue.fromS(claim.getUserName())),
                    Map.entry("claimAmount", AttributeValue.fromN(String.valueOf(claim.getClaimAmount()))),
                    Map.entry("claimDate", AttributeValue.fromS(LocalDate.parse(claim.getClaimDate().toString()).toString())),
                    Map.entry("status", AttributeValue.fromS(claim.getStatus()))
            );

            Map<String, AttributeValue> policyClaimItem = Map.ofEntries(
                    Map.entry("pk", AttributeValue.fromS("POLICY#" + claim.getPolicyId())),
                    Map.entry("sk", AttributeValue.fromS("CLAIM#" + claim.getClaimId())),
                    Map.entry("claimId", AttributeValue.fromS(claim.getClaimId())),
                    Map.entry("policyId", AttributeValue.fromS(claim.getPolicyId())),
                    Map.entry("userId", AttributeValue.fromS(claim.getUserId())),
                    Map.entry("claimType", AttributeValue.fromS(claim.getClaimType())),
                    Map.entry("claimDate", AttributeValue.fromS(LocalDate.parse(claim.getClaimDate().toString()).toString())),
                    Map.entry("status", AttributeValue.fromS(claim.getStatus()))
            );

            TransactWriteItemsRequest transactionRequest = TransactWriteItemsRequest.builder()
                    .transactItems(List.of(
                            TransactWriteItem.builder()
                                    .put(Put.builder()
                                            .tableName(tableName)
                                            .item(policyClaimItem)
                                            .conditionExpression("attribute_not_exists(pk) AND attribute_not_exists(sk)")
                                            .build())
                                    .build(),
                            TransactWriteItem.builder()
                                    .put(Put.builder()
                                            .tableName(tableName)
                                            .item(claimDetailsItem)
                                            .conditionExpression("attribute_not_exists(pk) AND attribute_not_exists(sk)")
                                            .build())
                                    .build()
                    ))
                    .build();

            dynamoDbClient.transactWriteItems(transactionRequest);
            log.info("Claim submitted successfully: {}", claim.getClaimId());

            return claim;

        } catch (DynamoDbException ex) {
            log.error("Failed to submit claim {}: {}", claim.getClaimId(), ex.getMessage(), ex);
            throw new DatabaseOperationException("Failed to submit claim to database", ex);
        }
    }

    @Override
    public Optional<Map<String, AttributeValue>> findById(String claimId) {
        Map<String, AttributeValue> key = Map.of(
                "pk", AttributeValue.fromS("CLAIM#" + claimId),
                "sk", AttributeValue.fromS("DETAILS")
        );

        GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build());

        if (response.hasItem() && !response.item().isEmpty()) {
            return Optional.of(response.item());
        }
        return Optional.empty();
    }

    @Override
    public List<Map<String, AttributeValue>> findAllByPolicyId(String policyId) {
            QueryRequest queryRequest = QueryRequest.builder()
                    .tableName(tableName)
                    .keyConditionExpression("pk = :pk and begins_with(sk, :skPrefix)")
//                    .keyConditionExpression("pk = :pk")
                    .expressionAttributeValues(Map.of(
                            ":pk", AttributeValue.fromS("POLICY#" + policyId),
                            ":skPrefix", AttributeValue.fromS("CLAIM#") //No need prefix
                    ))
                    .build();

            QueryResponse response = dynamoDbClient.query(queryRequest);
            return response.items();
    }

    @Override
    public Map<String, AttributeValue> updateClaimStatus(String claimId, Map<String, AttributeValue> updates) {
        return null;
    }
}
