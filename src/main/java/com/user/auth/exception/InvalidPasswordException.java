package com.user.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends RuntimeException{
    int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public InvalidPasswordException(int code, String message) {
        super(message);
        this.code=code;
    }
}
