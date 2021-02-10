package com.user.auth.controller;

import com.user.auth.dto.*;

import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    @PostMapping(path = "user/forgotpassword")
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordDto forgotDto) throws Exception {
        ResponseDto responseMessage = null;
        int message=userService.forgotPassword(forgotDto);
        if(message==200){
            responseMessage= new ResponseDto(200,"Your password token is sent to your registered email id.",null);
        }else if(message==400){
            responseMessage= new ResponseDto(400,"Oops..! Something went wrong, email not sent.",null);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    @PostMapping(path = "/user/changepassword")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest request){
       ResponseDto responseDto;
        if(userService.changePassword(changePasswordDto,request)){
            responseDto= new ResponseDto(200,"Password changed successfully..!",null);
        }else{
            responseDto= new ResponseDto(400,"Oops..! Failed to changed the password.",null);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    @PostMapping(path = "/user/resetpassword")
    public ResponseEntity registerNewUser(@RequestBody ResetPasswordReqDto dto){
        ResponseDto responseMessage;
        if(null!=userService.resetPassword(dto))
            responseMessage = new ResponseDto(200, "User is Activated and changed password successfully",null);
        else
            responseMessage = new ResponseDto(400,"Failed to changed the password",null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }


    @GetMapping(value = "/user/getAllAdminUsers")
    @ResponseBody
    public UserListResponseDto getAllAdminUsers(){
        return userService.getAllAdminUsers();
    }
}
