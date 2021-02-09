package com.user.auth.service;

import com.user.auth.dto.request.UserRegisterReqDto;

public interface UserService {

    boolean registerNewUser(UserRegisterReqDto dto);
}
