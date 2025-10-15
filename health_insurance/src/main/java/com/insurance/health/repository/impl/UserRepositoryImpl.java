package com.insurance.health.repository.impl;

import com.insurance.health.model.AppUser;
import com.insurance.health.repository.UserRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "HealthInsurance";

    public UserRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public AppUser save(AppUser appUser) {
        Map<String, AttributeValue> profileItem = Map.ofEntries(
                Map.entry("pk", AttributeValue.fromS("CUSTOMER")),
                Map.entry("sk", AttributeValue.fromS("PROFILE#" + appUser.getId())),
                Map.entry("id", AttributeValue.fromS(appUser.getId())),
                Map.entry("username", AttributeValue.fromS(appUser.getName())),
                Map.entry("email", AttributeValue.fromS(appUser.getEmail())),
                Map.entry("password", AttributeValue.fromS(appUser.getPassword())),
                Map.entry("gender", AttributeValue.fromS(appUser.getGender())),
                Map.entry("role", AttributeValue.fromS(appUser.getRole().toUpperCase()))
        );

        Map<String, AttributeValue> loginItem = Map.ofEntries(
                Map.entry("pk", AttributeValue.fromS("EMAIL#" + appUser.getEmail())),
                Map.entry("sk", AttributeValue.fromS("LOGIN")),
                Map.entry("id", AttributeValue.fromS(appUser.getId())),
                Map.entry("password", AttributeValue.fromS(appUser.getPassword()))
        );

        TransactWriteItemsRequest transactionRequest = TransactWriteItemsRequest.builder()
                .transactItems(List.of(
                        TransactWriteItem.builder()
                                .put(Put.builder()
                                        .tableName(tableName)
                                        .item(profileItem)
                                        .conditionExpression("attribute_not_exists(pk) AND attribute_not_exists(sk)")
                                        .build())
                                .build(),
                        TransactWriteItem.builder()
                                .put(Put.builder()
                                        .tableName(tableName)
                                        .item(loginItem)
                                        .conditionExpression("attribute_not_exists(pk) AND attribute_not_exists(sk)")
                                        .build())
                                .build()
                ))
                .build();

        try {
            dynamoDbClient.transactWriteItems(transactionRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error saving user transaction: " + e.getMessage(), e);
        }

        return appUser;
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("pk = :pk AND sk = :sk")
                .expressionAttributeValues(Map.of(
                        ":pk", AttributeValue.fromS("EMAIL#" + email),
                        ":sk", AttributeValue.fromS("LOGIN")
                ))
                .limit(1)
                .build();

        try {
            QueryResponse response = dynamoDbClient.query(request);

            if (response.hasItems() && !response.items().isEmpty()) {
                Map<String, AttributeValue> item = response.items().getFirst();

                AppUser user = new AppUser();
                user.setId(item.get("id").s());
                user.setEmail(email);
                user.setPassword(item.get("password").s());

                return Optional.of(user);
            }

            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Error finding user by email", e);
        }
    }

    @Override
    public Optional<AppUser> findById(String id) {
        ///
        return Optional.empty();
    }

    @Override
    public List<AppUser> findAll() {
        ///
        return List.of();
    }

    @Override
    public AppUser deleteById(String id) {
        ///
        return null;
    }
}
