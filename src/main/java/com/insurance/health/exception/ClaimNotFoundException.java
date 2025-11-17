package com.insurance.health.exception;

public class ClaimNotFoundException extends ResourceNotFoundException {
    public ClaimNotFoundException(String message) {
        super(message);
    }
}
