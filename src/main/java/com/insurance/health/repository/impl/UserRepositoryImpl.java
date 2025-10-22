package com.insurance.health.repository.impl;

import com.insurance.health.exception.UserAlreadyExistException;
import com.insurance.health.exception.UserNotFoundException;
import com.insurance.health.model.AppUser;
import com.insurance.health.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Slf4j
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
                Map.entry("pk", AttributeValue.fromS("USER")),
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
                Map.entry("role", AttributeValue.fromS(appUser.getRole().toUpperCase())),
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
            return appUser;
        } catch (ConditionalCheckFailedException e) {
            log.warn("Failed to save user '{}': user already exists", appUser.getName());
            throw new UserAlreadyExistException("User " + appUser.getName() + " already exists");
        }
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

        QueryResponse response = dynamoDbClient.query(request);

        if (response.hasItems() && !response.items().isEmpty()) {
            Map<String, AttributeValue> item = response.items().getFirst();

            AppUser user = new AppUser();
            user.setId(item.get("id").s());
            user.setEmail(email);
            user.setPassword(item.get("password").s());
            user.setRole(item.get("role").s());
            log.info("Found user by email: {}", email);
            return Optional.of(user);
        }
        log.warn("User not found with email: {}", email);
        return Optional.empty();
    }

    @Override
    public Optional<AppUser> findById(String id) {
        QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("pk = :pk")
                .expressionAttributeValues(Map.of(":pk", AttributeValue.fromS("USER")))
                .build();

        QueryResponse response = dynamoDbClient.query(request);
        for (Map<String, AttributeValue> item : response.items()) {
            if (item.get("id").s().equals(id)) {
                AppUser user = new AppUser();
                user.setId(id);
                user.setName(item.get("username").s());
                user.setEmail(item.get("email").s());
                user.setGender(item.get("gender").s());
                user.setRole(item.get("role").s());
                user.setPassword(item.get("password").s());
                log.info("Found user: ID={}, Username={}", id, user.getName());
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<AppUser> findAll() {
        QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("pk = :pk")
                .expressionAttributeValues(Map.of(":pk", AttributeValue.fromS("USER")))
                .build();

        QueryResponse response = dynamoDbClient.query(request);
        List<AppUser> users = new ArrayList<>();

        for (Map<String, AttributeValue> item : response.items()) {
            AppUser user = new AppUser();
            user.setId(item.get("id").s());
            user.setName(item.get("username").s());
            user.setEmail(item.get("email").s());
            user.setGender(item.get("gender").s());
            user.setRole(item.get("role").s());
            users.add(user);
        }
        log.info("Found {} users", users.size());
        return users;
    }

    @Override
    public AppUser update(AppUser user) {
        Optional<AppUser> existingUser = findById(user.getId());
        if (existingUser.isEmpty()) {
            log.warn("Failed to update user: User with ID '{}' not found", user.getId());
            throw new UserNotFoundException("User with id '" + user.getId() + "' not found");
        }

        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("username", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(user.getName()))
                .action(AttributeAction.PUT).build());
        updates.put("gender", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(user.getGender()))
                .action(AttributeAction.PUT).build());
        updates.put("password", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(user.getPassword()))
                .action(AttributeAction.PUT).build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                        "pk", AttributeValue.fromS("USER"),
                        "sk", AttributeValue.fromS("PROFILE#" + user.getId())
                ))
                .attributeUpdates(updates)
                .conditionExpression("attribute_exists(pk) AND attribute_exists(sk)")
                .returnValues(ReturnValue.ALL_NEW)
                .build();

        try {
            dynamoDbClient.updateItem(request);
            log.info("Successfully updated user: ID={}, Username={}", user.getId(), user.getName());
            return user;
        } catch (ConditionalCheckFailedException e) {
            log.warn("Failed to update user: User with ID '{}' not found", user.getId());
            throw new UserNotFoundException("User with id '" + user.getId() + "' not found");
        }
    }

    @Override
    public void deleteById(String id, String email) {
        TransactWriteItemsRequest transactionRequest = TransactWriteItemsRequest.builder()
                .transactItems(List.of(
                        TransactWriteItem.builder()
                                .delete(Delete.builder()
                                        .tableName(tableName)
                                        .key(Map.of(
                                                "pk", AttributeValue.fromS("USER"),
                                                "sk", AttributeValue.fromS("PROFILE#" + id)
                                        ))
                                        .conditionExpression("attribute_exists(pk) AND attribute_exists(sk)")
                                        .build())
                                .build(),
                        TransactWriteItem.builder()
                                .delete(Delete.builder()
                                        .tableName(tableName)
                                        .key(Map.of(
                                                "pk", AttributeValue.fromS("EMAIL#" + email),
                                                "sk", AttributeValue.fromS("LOGIN")
                                        ))
                                        .conditionExpression("attribute_exists(pk) AND attribute_exists(sk)")
                                        .build())
                                .build()
                ))
                .build();

        try {
            dynamoDbClient.transactWriteItems(transactionRequest);
            log.info("Deleted user successfully: ID={}, Email={}", id, email);
        } catch (TransactionCanceledException e) {
            log.warn("Failed to delete user: User with ID '{}' and Email '{}' not found", id, email);
            throw new UserNotFoundException("User with ID '" + id + "' not found or already deleted");
        }
    }
}
