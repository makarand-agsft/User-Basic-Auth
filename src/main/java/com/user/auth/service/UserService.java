package com.user.auth.service;

import com.user.auth.dto.UserLoginReqDto;
import com.user.auth.dto.UserLoginResDto;
import com.user.auth.dto.UserListResponseDto;
import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.dto.request.ResetPasswordReqDto;

public interface UserService {

    void registerNewUser(UserRegisterReqDto dto) throws Exception;

    UserListResponseDto getAllAdminUsers();

    UserLoginResDto loginUser(UserLoginReqDto dto);

    UserRegisterReqDto resetPassword(ResetPasswordReqDto dto);

    void deleteUserById(Long userId) throws Exception;
}
