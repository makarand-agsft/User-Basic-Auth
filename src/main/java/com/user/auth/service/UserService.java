package com.user.auth.service;

import com.user.auth.dto.UserListResponseDto;
import com.user.auth.dto.UserRegisterReqDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    boolean registerNewUser(UserRegisterReqDto dto);

    UserListResponseDto getAllAdminUsers();
}
