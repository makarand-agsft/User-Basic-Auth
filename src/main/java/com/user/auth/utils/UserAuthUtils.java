package com.user.auth.utils;

import com.user.auth.model.Role;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.repository.TokenRepository;
import com.user.auth.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserAuthUtils {

    @Value("${upload.directory}")
    private String UPLOAD_DIRECTORY ;
    @Autowired
    private TokenRepository tokenRepository;
    @Value("${jwt.header}")
    private String jwtHeader;
    @Autowired
    private JwtProvider jwtProvider;


    public String generateKey(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
    public Optional<User> getUserFromToken(String token){
        Optional<Token> optToken = tokenRepository.findByToken(token);
        if(optToken.isPresent() && !optToken.get().getExpiryDate().before(new Date()))
            return Optional.of(optToken.get().getUsers());
        return Optional.empty();
    }
    public String saveProfileImage(MultipartFile file, User user){
        String profile_path = null;
        if(!file.isEmpty()){
            try {
                String fileName = user.getEmail();
                byte[] bytes = file.getBytes();
                profile_path = UPLOAD_DIRECTORY + fileName;
                Path path = Paths.get(profile_path);
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return profile_path;
    }

    public boolean validateEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }
        return false;
    }


    public boolean checkAccess(HttpServletRequest httpServletRequest) {
        if (null == httpServletRequest) {
            throw new RuntimeException("Request is null");
        }
        String token = httpServletRequest.getHeader(jwtHeader);
        if (null == token && token.isEmpty()) {
            throw new RuntimeException("Authorization failed");
        }
        List<Role> roleList =jwtProvider.getRolesfromToken(token);

       for(Role role:roleList)
       {
           if(role.getRole()!=null && role.getRole().equals("ADMIN"))
           {
               return true;
           }
       }
        throw new RuntimeException("Access Denied");
    }
}
