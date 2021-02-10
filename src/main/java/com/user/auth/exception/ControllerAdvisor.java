package com.user.auth.exception;

import com.user.auth.dto.ResponseDto;
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
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException userNotFoundException,WebRequest webRequest){
        Map<String,Object> body= new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message","User Not Found !");
        return new ResponseEntity<>(body,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Object> handleInvalidEmailException(InvalidEmailException invalidEmailException,WebRequest webRequest){
        Map<String,Object> body= new LinkedHashMap<>();
        body.put("timestamp",LocalDateTime.now());
        body.put("message","Invalid Email Id, Please provide valid email id...!");
        return  new ResponseEntity<>(body,HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidOldPasswordException(InvalidPasswordException invalidPasswordException){
        ResponseDto responseDto= new ResponseDto(invalidPasswordException.getCode(),invalidPasswordException.getMessage(),null);
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }
}
