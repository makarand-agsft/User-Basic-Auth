package com.formz.exception;

import com.formz.constants.ApiStatus;
import com.formz.dto.ResponseDto;
import com.formz.dto.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This class is responsible for catching the custom exceptions thrown in the system
 */
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * This method throws bad request exception
     *
     * @param badRequestException
     * @return response entity with status OK and custom message
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleUserNotFoundException(BadRequestException badRequestException) {
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(400, badRequestException.getMessage(), null), ApiStatus.FAILURE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * This method throws the generic exception
     *
     * @param ex
     * @return response entity with status OK and exception message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> genericException(Exception ex) {
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(400, ex.getMessage(), null), ApiStatus.FAILURE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * This method throws invalid tenant exception if tenant is not valid
     *
     * @param ex
     * @return response entity with status OK  custom exception message
     */
    @ExceptionHandler(InvalidTenantException.class)
    public ResponseEntity<Object> invalidTenant(InvalidTenantException ex) {
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(400, ex.getMessage(), null), ApiStatus.FAILURE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}