package com.user.auth.controller;

import com.user.auth.dto.ForgotPasswordDto;
import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.service.UserService;
import com.user.auth.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/user/register")
    public ResponseEntity registerNewUser(@RequestBody UserRegisterReqDto dto){
        ResponseMessage responseMessage;
        if(userService.registerNewUser(dto))
            responseMessage = new ResponseMessage(200, "User added successfully. Please check email for account activation",null);
        else
            responseMessage = new ResponseMessage(400,"User already exists in system",null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    @PostMapping(path = "user/forgotpassword")
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordDto forgotDto) throws Exception {
        ResponseMessage responseMessage = null;
        int message=userService.forgotPassword(forgotDto);
        if(message==200){
            responseMessage= new ResponseMessage(200,"Your password is sent to your registered email id.",null);
        }else if(message==400){
            responseMessage= new ResponseMessage(400,"Oops..! Something went wrong, email not sent.",null);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }
}
