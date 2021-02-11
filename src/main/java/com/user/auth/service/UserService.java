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

    boolean addUser(String userReqDto, MultipartFile file, HttpServletRequest request);

    byte[] getUserProfileImage(HttpServletRequest request) throws IOException;

    Boolean UpdateUser(String userReqDto, MultipartFile file, HttpServletRequest request);

    boolean addProfileImage(MultipartFile file, HttpServletRequest request);

    int forgotPassword(ForgotPasswordDto forgotDto) throws Exception;

    boolean changePassword(ChangePasswordDto changePasswordDto, HttpServletRequest request);

    UserUpdateRoleRes updateRole(UserUpdateRoleReqDto dto);

    UserDto resetPassword(ResetPasswordReqDto dto);

    void deleteUserById(Long userId) throws Exception;

    boolean deleteProfileImage(HttpServletRequest request);

    void logout(HttpServletRequest request);
}
