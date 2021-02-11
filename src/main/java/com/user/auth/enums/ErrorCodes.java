package com.user.auth.enums;

public enum ErrorCodes {

    USER_NOT_FOUND(404,"User not found"),
    BAD_REQUEST(400,"Request invalid"),
    UNAUTHORIZED(401,"You are not authorise to access this resource"),
    INVALID_CREDENTIALS(400,"Invalid credentials provided"),
    INVALID_TOKEN(400,"Please provide valid token");

    private  String value;

    private  Integer code;
    ErrorCodes(Integer code,String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
