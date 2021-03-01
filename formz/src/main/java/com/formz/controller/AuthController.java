package com.formz.controller;

import com.formz.constants.ApiStatus;
import com.formz.dto.*;
import com.formz.exception.BadRequestException;
import com.formz.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public UserDto login(@RequestBody UserLoginRequestDTO userLoginRequestDTO){
        return authService.loginUser(userLoginRequestDTO);
    }

    @PostMapping(path = "/activate-user")
    public ResponseEntity activateUser(@RequestParam("token") String token, @RequestBody ActivateUserDto dto, HttpServletRequest httpServletRequest) {
        ResponseDto responseDto = null;
        try {
            UserDto response = authService.activateUser(dto, token, httpServletRequest);
            responseDto =
                    new ResponseDto(new ResponseObject(200, "User is Activated and changed password successfully", response),
                            ApiStatus.SUCCESS);
        } catch (BadRequestException exception) {
            responseDto =
                    new ResponseDto(new ResponseObject(201, exception.getMessage(), null),
                            ApiStatus.SUCCESS);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }
}
