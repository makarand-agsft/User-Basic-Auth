package com.user.auth.exception;

public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException() {
        super("Invalid Email Id, Please provide valid email id...!");
    }
}
