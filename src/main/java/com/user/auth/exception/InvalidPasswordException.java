package com.user.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException( String message) {
        super(message);
    }

    public InvalidPasswordException() {
    }
}
