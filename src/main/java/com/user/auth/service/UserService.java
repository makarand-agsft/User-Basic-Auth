package com.user.auth.service;

import com.user.auth.dto.ForgotPasswordDto;
import com.user.auth.dto.UserRegisterReqDto;

public interface UserService {

    boolean registerNewUser(UserRegisterReqDto dto);

    int forgotPassword(ForgotPasswordDto forgotDto) throws Exception;
}
