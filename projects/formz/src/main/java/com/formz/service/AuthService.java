package com.formz.service;

import com.formz.dto.ActivateUserDto;
import com.formz.dto.UserDto;
import com.formz.dto.UserLoginRequestDTO;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    public UserDto loginUser(UserLoginRequestDTO userLoginRequestDTO);

    UserDto activateUser(ActivateUserDto dto, String userToken, HttpServletRequest httpServletRequest);
}
