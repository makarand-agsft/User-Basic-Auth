package com.user.auth.dto.response;

import org.springframework.http.HttpStatus;

/**
 * This class is responsible for sending custom response
 * Http status will always be 200 if api hits successfully
 */
public class ResponseDto {

    private ResponseObject responseObject;
    private HttpStatus httpStatus;

    public ResponseDto(ResponseObject responseObject, HttpStatus httpStatus) {
        this.responseObject = responseObject;
        this.httpStatus = httpStatus;
    }

    public ResponseObject getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(ResponseObject responseObject) {
        this.responseObject = responseObject;
    }
}
