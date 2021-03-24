package com.user.auth.enums;

public enum ErrorCodes {

    USER_NOT_FOUND("User not found"),
    BAD_REQUEST("Request invalid"),
    UNAUTHORIZED("You are not authorise to access this resource"),
    INVALID_CREDENTIALS("Invalid credentials provided"),
    INVALID_TOKEN("Please provide valid token");

    private final String value;

    ErrorCodes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
