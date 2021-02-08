package com.user.auth.service;

import com.user.auth.dto.request.UserRegisterReqDto;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static com.user.auth.TokenType.RESET_PASSWORD_TOKEN;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;


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
            user.setRoles(dto.getRoles());
            Token token = new Token();
            token.setToken(generateKey(10));
            token.setTokenType(RESET_PASSWORD_TOKEN);
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
