package com.insurance.health.repository.impl;


import com.insurance.health.model.AppUser;
import com.insurance.health.repository.CustomerRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "HealthInsurance";

    public CustomerRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public AppUser save(AppUser appUser) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("pk", AttributeValue.fromS("CUSTOMER"));
        item.put("sk", AttributeValue.fromS("PROFILE#" + appUser.getId()));
        item.put("id", AttributeValue.fromS(appUser.getId()));
        item.put("username", AttributeValue.fromS(appUser.getName()));
        item.put("password", AttributeValue.fromS(appUser.getPassword()));
        item.put("email", AttributeValue.fromS(appUser.getEmail()));
        item.put("role", AttributeValue.fromS(appUser.getRole().toUpperCase()));

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .conditionExpression("attribute_not_exists(username)")
                .returnValues(ReturnValue.ALL_OLD)
                .build();
        
        try {
            dynamoDbClient.putItem(request);
        } catch (Exception e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
        
        return appUser;
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {

            return null;
    }
}
