package com.user.auth.service;

import com.user.auth.dto.*;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.model.User;

import org.springframework.http.ResponseEntity;

public interface UserService {

    boolean registerNewUser(UserRegisterReqDto dto);

    UserListResponseDto getAllAdminUsers();

    UserLoginResDto loginUser(UserLoginReqDto dto);

    public User resetPassword(ResetPasswordReqDto dto);

    int forgotPassword(ForgotPasswordDto forgotDto) throws Exception;

    boolean changePassword(ChangePasswordDto changePasswordDto);
}
