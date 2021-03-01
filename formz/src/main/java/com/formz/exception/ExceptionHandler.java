package com.formz.exception;

import com.formz.constants.ApiStatus;
import com.formz.dto.ResponseDto;
import com.formz.dto.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionHandler  extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleUserNotFoundException(BadRequestException badRequestException) {
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(400, badRequestException.getMessage(), null), ApiStatus.FAILURE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
