package com.user.auth.exception;

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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(userNotFoundException.getCode(), userNotFoundException.getMessage(), null), HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Object> handleInvalidEmailException(InvalidEmailException invalidEmailException, WebRequest webRequest) {
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(invalidEmailException.getCode(), invalidEmailException.getMessage(), null), HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidOldPasswordException(InvalidPasswordException invalidPasswordException){
        ResponseDto responseDto= new ResponseDto(new ResponseObject(invalidPasswordException.getCode(),invalidPasswordException.getMessage(),null),
                HttpStatus.OK);
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> invalidRequest(InvalidRequestException invalidRequest){
        ResponseDto responseDto= new ResponseDto(new ResponseObject(invalidRequest.getCode(),invalidRequest.getMessage(),null),
                HttpStatus.OK);
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }

    @ExceptionHandler(UnAuthorisedException.class)
    public ResponseEntity<Object> invalidRequest(UnAuthorisedException unAuthorisedException){
        ResponseDto responseDto= new ResponseDto(new ResponseObject(unAuthorisedException.getCode(),unAuthorisedException.getMessage(),null),
                HttpStatus.OK);
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }

    @ExceptionHandler(InvalidTenantException.class)
    public ResponseEntity<Object> invalidTenant(InvalidTenantException invalidTenantException) {
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(invalidTenantException.getCode(), invalidTenantException.getMessage(), null), HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
