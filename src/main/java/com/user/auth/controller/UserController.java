package com.user.auth.controller;

import com.user.auth.dto.*;

import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.dto.request.UserUpdateRoleReqDto;
import com.user.auth.service.UserService;
import com.user.auth.utils.UserAuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    UserAuthUtils userAuthUtils;

    @PostMapping(path = "/user/add")
    public ResponseEntity addUser(@RequestParam(name = "file", required = false)MultipartFile file, @RequestParam("userReqDto")String userReqDto,HttpServletRequest request){
        ResponseDto responseMessage;
        if(userService.addUser(userReqDto,file,request))
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

    @GetMapping(path = "/user/getprofile")
    public ResponseEntity getUserProfile(HttpServletRequest request){
        ResponseDto responseDto;
        UserProfileResDto resDto = userService.getUserProfile(request);
        if(resDto!=null)
            responseDto = new ResponseDto(200, "User profile fetched successfully",resDto);
        else
            responseDto = new ResponseDto(400,"Failed to fetch user profile",null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);

    }

    @PostMapping(path = "/user/get/profileimg")


    @GetMapping(value = "/user/getAllAdminUsers")
    @ResponseBody
    public UserListResponseDto getAllAdminUsers(){
        return userService.getAllAdminUsers();
    }

    @PostMapping(path = "/user/updaterole")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity updateUserRole(HttpServletRequest httpServletRequest, @RequestBody UserUpdateRoleReqDto dto){

        ResponseDto responseMessage;
        if(userAuthUtils.checkAccess(httpServletRequest)) {


            if (null != userService.updateRole(dto))
                responseMessage = new ResponseDto(200, "User Role changed successfully", null);
            else
                responseMessage = new ResponseDto(400, "Failed to changed the Roles", null);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
        }
        responseMessage = new ResponseDto(401, "Access denied", null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);

    }
}
