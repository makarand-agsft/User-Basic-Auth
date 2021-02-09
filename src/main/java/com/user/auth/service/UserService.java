package com.user.auth.service;

import com.user.auth.dto.request.UserRegisterReqDto;
import com.user.auth.enums.TokenType;
import com.user.auth.model.Role;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.repository.RoleRepository;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private RoleRepository roleRepository;


    private Long tokenExpiry = 3000l;

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
            user.setCreatedBy("");
            List<Role> roles = new ArrayList<>();
            for(Role r : dto.getRoles()){
                Optional<Role> role = roleRepository.findByRole(r.getRole());
                if(role.isPresent())
                    roles.add(role.get());
            }
            user.setRoles(roles);
            Token token = new Token();
            token.setToken(generateKey(10));
            token.setTokenType(TokenType.RESET_PASSWORD_TOKEN);
            token.setUsers(user);
            token.setExpiryDate(new Date(System.currentTimeMillis()+tokenExpiry*1000));
            user.setTokens(Collections.singletonList(token));
            userRepository.save(user);
            tokenRepository.save(token);
            return true;
        }else
            return false;
    }
    private String generateKey(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
}
