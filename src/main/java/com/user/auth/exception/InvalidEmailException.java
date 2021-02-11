package com.user.auth.exception;

public class InvalidEmailException extends RuntimeException{

    int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public InvalidEmailException(int code,String message) {

        super(message);
        this.code=code;
    }
}
