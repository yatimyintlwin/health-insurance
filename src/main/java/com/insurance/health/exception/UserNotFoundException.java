package com.insurance.health.exception;

public class UserNotFoundException extends ResourceNotFoundException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
