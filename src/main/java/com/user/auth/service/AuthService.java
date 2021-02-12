package com.user.auth.service;

import com.user.auth.dto.request.ChangePasswordDto;
import com.user.auth.dto.request.ForgotPasswordDto;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.dto.request.UserLoginReqDto;
import com.user.auth.dto.response.UserDto;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    UserDto loginUser(UserLoginReqDto dto);

    void forgotPassword(ForgotPasswordDto forgotDto) throws Exception;

    void changePassword(ChangePasswordDto changePasswordDto);

    UserDto resetPassword(ResetPasswordReqDto dto);

    void logout(HttpServletRequest request);
}
