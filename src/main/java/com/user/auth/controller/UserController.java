package com.user.auth.controller;

import com.user.auth.dto.UserLoginReqDto;
import com.user.auth.dto.UserLoginResDto;
import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.service.UserService;
import com.user.auth.dto.ResponseDto;
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
        ResponseDto responseMessage;
        if(userService.registerNewUser(dto))
            responseMessage = new ResponseDto(200, "User added successfully. Please check email for account activation",null);
        else
            responseMessage = new ResponseDto(400,"User already exists in system",null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }
    @PostMapping(path = "/user/login")
    public ResponseEntity loginUser(@RequestBody UserLoginReqDto dto){
        ResponseDto responseMessage;
        UserLoginResDto response = userService.loginUser(dto);
        if(response!=null)
            responseMessage = new ResponseDto(200,"Logged in successfully",response);
        else
            responseMessage = new ResponseDto(401,"Bad credentials", null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }
}
