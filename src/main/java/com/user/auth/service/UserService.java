package com.user.auth.service;

import com.user.auth.dto.UserLoginReqDto;
import com.user.auth.dto.UserLoginResDto;
import com.user.auth.dto.UserListResponseDto;
import com.user.auth.dto.UserRegisterReqDto;

import com.user.auth.dto.*;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    UserListResponseDto getAllAdminUsers();

    UserLoginResDto loginUser(UserLoginReqDto dto);

    UserProfileResDto getUserProfile(HttpServletRequest request);

    boolean addUser(String userReqDto, MultipartFile file, HttpServletRequest request);

    int forgotPassword(ForgotPasswordDto forgotDto) throws Exception;

    boolean changePassword(ChangePasswordDto changePasswordDto, HttpServletRequest request);

    UserRegisterReqDto resetPassword(ResetPasswordReqDto dto);

    void deleteUserById(Long userId) throws Exception;
}
