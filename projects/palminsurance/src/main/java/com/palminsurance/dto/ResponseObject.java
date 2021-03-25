package com.palminsurance.dto;

/**
 * This class is responsible for sending custom object
 */
public class ResponseObject {
    private int code;
    private String message;
    private Object object;

    public ResponseObject(){

    }
    public ResponseObject(int code, String message, Object object) {
        this.code = code;
        this.message = message;
        this.object = object;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
