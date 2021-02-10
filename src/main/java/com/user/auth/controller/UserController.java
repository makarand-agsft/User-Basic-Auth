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

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    UserAuthUtils userAuthUtils;

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
