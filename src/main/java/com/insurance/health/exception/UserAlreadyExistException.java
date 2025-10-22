package com.insurance.health.exception;

public class UserAlreadyExistException extends ResourceAlreadyExistException{
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
