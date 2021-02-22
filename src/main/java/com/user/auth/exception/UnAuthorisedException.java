package com.user.auth.exception;

public class UnAuthorisedException extends RuntimeException {


    public UnAuthorisedException(String message) {

        super(message);

    }

    public UnAuthorisedException() {
    }
}
