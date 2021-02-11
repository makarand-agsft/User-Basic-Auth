package com.user.auth.controller;

import com.user.auth.dto.request.*;
import com.user.auth.dto.response.*;
import com.user.auth.service.UserService;
import com.user.auth.utils.UserAuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * This class represents an endpoint of user authentication services
 */
@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
   private UserAuthUtils userAuthUtils;



    @PostMapping(path = "user/forgotPassword")
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordDto forgotDto) throws Exception {
        userService.forgotPassword(forgotDto);
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Your password token is sent to your registered email id.", null),
                        HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(path = "/user/changePassword")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest request) {
        userService.changePassword(changePasswordDto);
        ResponseDto responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Password changed successfully..!", null), HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is an endpoint for logging in the user
     * @param dto
     * @return user information including jwt token
     * @author aakash
     * @date 09/02/2021
     */
    @PostMapping(path = "/user/login")
    public ResponseEntity loginUser(@RequestBody UserLoginReqDto dto) {
        UserDto response = userService.loginUser(dto);
        ResponseDto responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Logged in successfully", response), HttpStatus.OK);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is an endpoint to reset user's password
     *
     * @param dto containing user one time password and email address
     * @return Success message of user activation
     */
    @PostMapping(path = "/user/resetPassword")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordReqDto dto) {
        UserDto response = userService.resetPassword(dto);
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User is Activated and changed password successfully", response),
                        HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    @PostMapping(path = "/user/logout")
    public ResponseEntity logout(HttpServletRequest httpServletRequest){
        ResponseDto responseDto;
        userService.logout(httpServletRequest);
            responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(),"Logged out successfully",null),HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

}
