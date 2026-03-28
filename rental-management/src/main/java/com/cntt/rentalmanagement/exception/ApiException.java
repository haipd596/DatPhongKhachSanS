package com.cntt.rentalmanagement.exception;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}
