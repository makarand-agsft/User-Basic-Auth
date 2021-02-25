package com.user.auth.service;

import com.user.auth.dto.request.*;
import com.user.auth.dto.response.UserDto;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    UserDto loginUser(UserLoginReqDto dto,HttpServletRequest httpServletRequest) ;

    void forgotPassword(ForgotPasswordDto forgotDto) throws Exception;

    void changePassword(ChangePasswordDto changePasswordDto);

    UserDto activateUser(ActivateUserDto dto, String userToken, HttpServletRequest httpServletRequest);

    void logout(HttpServletRequest request);

}
