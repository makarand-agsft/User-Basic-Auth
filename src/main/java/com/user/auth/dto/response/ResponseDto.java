package com.user.auth.dto.response;

import com.user.auth.constants.ApiStatus;
import org.springframework.http.HttpStatus;

/**
 * This class is responsible for sending custom response
 * Http status will always be 200 if api hits successfully
 */
public class ResponseDto {

    private ResponseObject responseObject;
    private ApiStatus apiStatus;

    public ResponseDto(ResponseObject responseObject, ApiStatus apiStatus) {
        this.responseObject = responseObject;
        this.apiStatus = apiStatus;
    }

    public ApiStatus getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(ApiStatus apiStatus) {
        this.apiStatus = apiStatus;
    }

    public ResponseObject getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(ResponseObject responseObject) {
        this.responseObject = responseObject;
    }
}
