package com.insurance.health.exception;

public class PolicyNotFoundException extends ResourceNotFoundException{
    public PolicyNotFoundException(String message) {
        super(message);
    }
}
