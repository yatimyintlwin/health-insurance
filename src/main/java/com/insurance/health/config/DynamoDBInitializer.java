package com.insurance.health.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Component
public class DynamoDBInitializer {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "HealthInsurance";

    public DynamoDBInitializer(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @PostConstruct
    public void initialize() {
        ListTablesResponse tablesResponse = dynamoDbClient.listTables();

        if (!tablesResponse.tableNames().contains(tableName)) {
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("pk")
                                    .keyType(KeyType.HASH)
                                    .build(),
                            KeySchemaElement.builder()
                                    .attributeName("sk")
                                    .keyType(KeyType.RANGE)
                                    .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("pk")
                                    .attributeType(ScalarAttributeType.S)
                                    .build(),
                            AttributeDefinition.builder()
                                    .attributeName("sk")
                                    .attributeType(ScalarAttributeType.S)
                                    .build(),
                            AttributeDefinition.builder()
                                    .attributeName("email")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()
                    )
                    .provisionedThroughput(
                            ProvisionedThroughput.builder()
                                    .readCapacityUnits(5L)
                                    .writeCapacityUnits(5L)
                                    .build()
                    )
                    .build();

            dynamoDbClient.createTable(request);
        }
    }
}
