package com.user.auth.service;

import com.user.auth.dto.UserLoginReqDto;
import com.user.auth.dto.UserLoginResDto;
import com.user.auth.dto.UserRegisterReqDto;

public interface UserService {

    boolean registerNewUser(UserRegisterReqDto dto);

    UserLoginResDto loginUser(UserLoginReqDto dto);
}
