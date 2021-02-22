package com.user.auth.exception;

public class InvalidEmailException extends RuntimeException{


    public InvalidEmailException(String message) {
        super(message);
    }

    public InvalidEmailException() {
    }
}
