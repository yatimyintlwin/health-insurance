package com.insurance.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HealthInsuranceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthInsuranceApplication.class, args);
    }

}
