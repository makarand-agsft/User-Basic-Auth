package com.user.auth.exception;

import com.user.auth.dto.ResponseDto;
import com.user.auth.dto.ResponseObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
}
