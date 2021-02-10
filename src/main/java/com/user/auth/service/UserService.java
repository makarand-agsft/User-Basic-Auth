package com.user.auth.service;


import com.user.auth.dto.*;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    UserListResponseDto getAllAdminUsers();

    UserLoginResDto loginUser(UserLoginReqDto dto);

    public User resetPassword(ResetPasswordReqDto dto);

    UserProfileResDto getUserProfile(HttpServletRequest request);

    boolean addUser(String userReqDto, MultipartFile file, HttpServletRequest request);

    int forgotPassword(ForgotPasswordDto forgotDto) throws Exception;

    boolean changePassword(ChangePasswordDto changePasswordDto, HttpServletRequest request);
}
