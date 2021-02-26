package com.user.auth.exception;

import com.user.auth.constants.ApiStatus;
import com.user.auth.constants.ErrorCodes;
import com.user.auth.dto.response.ResponseDto;
import com.user.auth.dto.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> genericException(Exception exception) {
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(ErrorCodes.BAD_REQUEST.ordinal(), exception.getMessage(), null), ApiStatus.FAILURE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}
