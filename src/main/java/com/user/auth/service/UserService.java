package com.user.auth.service;


import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.dto.request.ResetPasswordReqDto;

import com.user.auth.enums.TokenType;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;


public interface UserService {


    boolean registerNewUser(UserRegisterReqDto dto);
    public User resetPassword(ResetPasswordReqDto dto);
}


