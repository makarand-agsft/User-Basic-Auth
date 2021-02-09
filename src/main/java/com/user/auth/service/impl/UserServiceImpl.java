package com.user.auth.service.impl;

import com.user.auth.dto.ResponseDto;
import com.user.auth.dto.UserListResponseDto;
import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.enums.TokenType;
import com.user.auth.model.Role;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.repository.RoleRepository;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import com.user.auth.service.UserService;
import com.user.auth.utils.EmailUtils;
import com.user.auth.utils.UserAuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserAuthUtils userAuthUtils;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private ModelMapper modelMapper;
    @Value("${reset.token.validity}")
    private Long tokenExpiry = 3000l;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public boolean registerNewUser(UserRegisterReqDto dto) {
        Optional<User> dupUser =  userRepository.findByEmail(dto.getEmail());
        if(!dupUser.isPresent()){
            User user = new User();
            user.setEmail(dto.getEmail());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setActive(Boolean.FALSE);
            user.setAddress(dto.getAddress());
            user.setMobileNumber(dto.getMobile());
            List<Role> roles = new ArrayList<>();
            for(Role r : dto.getRoles()){
                Optional<Role> role = roleRepository.findByRole(r.getRole());
                if(role.isPresent())
                    roles.add(role.get());
            }
            user.setRoles(roles);
            Token token = new Token();
            token.setToken(userAuthUtils.generateKey(10));
            token.setTokenType(TokenType.RESET_PASSWORD_TOKEN);
            token.setUsers(user);
            token.setExpiryDate(new Date(System.currentTimeMillis()+tokenExpiry*1000));
            user.setTokens(Collections.singletonList(token));
            userRepository.save(user);
            tokenRepository.save(token);
            //send email
            String message ="Hello "+user.getFirstName() +"This is your temporary password ,use this to change your password :"+token.getToken();
            emailUtils.sendInvitationEmail(user.getEmail(),"Invitation",message,fromEmail);
            return true;
        }else
            return false;
    }

    @Override public UserListResponseDto getAllAdminUsers() {
        Optional<Role> role = roleRepository.findByRole("ADMIN");
        UserListResponseDto userListResponseDto = new UserListResponseDto();
        if (role.isPresent()){
            List<User> users=userRepository.findByRoles(role.get());
            if(users!=null)
            userListResponseDto.setUserList(users.stream().map(x->modelMapper.map(x,UserRegisterReqDto.class)).collect(Collectors.toList()));
        }
      return  userListResponseDto;
    }

}
