package com.alargo.account_movements_microservice.exception;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
