package com.user.auth.service;

import com.user.auth.dto.request.*;
import com.user.auth.dto.response.UserDto;
import com.user.auth.dto.response.UserListResponseDto;
import com.user.auth.dto.response.UserUpdateRoleRes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {

    void addUser(String userReqDto, MultipartFile file);

    Boolean updateUser(String userReqDto, MultipartFile file);

    byte[] getUserProfileImage() throws IOException;

    void addProfileImage(MultipartFile file);

    UserUpdateRoleRes updateRole(UserUpdateRoleReqDto dto);

    void deleteUserById(Long userId) throws Exception;

    void deleteProfileImage();

    UserDto getUserProfile();

    UserListResponseDto getAllUsers();

}
