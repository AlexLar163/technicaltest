package com.alargo.client_person_microservice.exception;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
