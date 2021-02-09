package com.user.auth.service;


import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.model.User;


public interface UserService {


    boolean registerNewUser(UserRegisterReqDto dto);
    public User resetPassword(ResetPasswordReqDto dto);
}


