package com.formz.exception;

public class InvalidTenantException extends RuntimeException{


    public InvalidTenantException(String message) {
        super(message);
    }

    public InvalidTenantException() {
    }
}
