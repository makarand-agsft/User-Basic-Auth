package com.user.auth.service;

import com.user.auth.dto.request.*;
import com.user.auth.dto.response.UserListResponseDto;
import com.user.auth.dto.response.UserDto;

import com.user.auth.dto.response.UserUpdateRoleRes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface UserService {

    UserListResponseDto getAllAdminUsers();

    UserDto loginUser(UserLoginReqDto dto);

    UserDto getUserProfile();

    void addUser(String userReqDto, MultipartFile file);

    byte[] getUserProfileImage() throws IOException;

    void addProfileImage(MultipartFile file);

    void forgotPassword(ForgotPasswordDto forgotDto) throws Exception;

    void changePassword(ChangePasswordDto changePasswordDto);

    UserUpdateRoleRes updateRole(UserUpdateRoleReqDto dto);

    UserDto resetPassword(ResetPasswordReqDto dto);

    void deleteUserById(Long userId) throws Exception;

    void deleteProfileImage();

    void logout(HttpServletRequest request);
}
