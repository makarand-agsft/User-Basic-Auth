package com.user.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.auth.dto.*;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.enums.TokenType;
import com.user.auth.model.*;
import com.user.auth.repository.RoleRepository;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import com.user.auth.security.JwtProvider;
import com.user.auth.service.UserService;
import com.user.auth.utils.EmailUtils;
import com.user.auth.utils.UserAuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Value("${reset.token.validity}")
    private Long resetTokenExpiry;

    @Value("${jwt.tokenValidity}")
    private Long jwTokenExpiry;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${jwt.header}")
    private String jwtHeader;

    @Autowired
    private UserAuthUtils authUtils;

    @Value("${upload.directory}")
    private String UPLOAD_DIRECTORY;

    @Override
    public boolean addUser(String jsonString, MultipartFile file, HttpServletRequest request) {
        User admin = authUtils.getUserFromToken(request.getHeader(jwtHeader)).orElseThrow(
                ()-> new RuntimeException("Unauthorized"));
        ObjectMapper objectMapper = new ObjectMapper();
        UserRegisterReqDto dto = null;
        try {
           dto = objectMapper.readValue(jsonString, UserRegisterReqDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Optional<User> dupUser =  userRepository.findByEmail(dto.getEmail());
        String profile_path = null;
        if(!dupUser.isPresent()){
            User user = modelMapper.map(dto, User.class);
            UserProfile userProfile = modelMapper.map(dto, UserProfile.class);
            Address address = modelMapper.map(dto, Address.class);
            address.setUser(user);
            profile_path = userAuthUtils.saveProfileImage(file, user);
            userProfile.setActive(Boolean.FALSE);
            userProfile.setProfilePicture(profile_path);
            userProfile.setUser(user);
           String uname = admin.getUserProfile().getFirstName()+"."+admin.getUserProfile().getLastName();
           user.setCreatedBy(uname);
           userProfile.setCreatedBy(uname);
            List<Role> roles = new ArrayList<>();
            for(String r : dto.getRoles()){
                Optional<Role> role = roleRepository.findByRole(r);
                if(role.isPresent())
                    roles.add(role.get());
            }
            Token token = new Token();
            token.setToken(userAuthUtils.generateKey(10));
            token.setTokenType(TokenType.RESET_PASSWORD_TOKEN);
            token.setUsers(user);
            token.setExpiryDate(new Date(System.currentTimeMillis()+jwTokenExpiry*1000));
            user.setTokens(Collections.singletonList(token));
            user.setRoles(roles);
            user.setAddresses(Collections.singletonList(address));
            user.setUserProfile(userProfile);
            userRepository.save(user);
            tokenRepository.save(token);
            //send email
            String message ="Hello "+user.getUserProfile().getFirstName() +"This is your temporary password ,use this to change your password :"+token.getToken();
            emailUtils.sendInvitationEmail(user.getEmail(),"Invitation",message,fromEmail);
            return true;
        }else
            return false;
    }

    @Override
    public byte[] getUserProfileImage(HttpServletRequest request) throws IOException {
        User user = authUtils.getUserFromToken(request.getHeader(jwtHeader)).orElseThrow(
                ()-> new RuntimeException("Unauthorized"));
        return Files.readAllBytes(Paths.get(user.getUserProfile().getProfilePicture()));
    }

    @Override
    public Boolean UpdateUser(String userReqDto, MultipartFile file, HttpServletRequest request) {
        User userAuth = authUtils.getUserFromToken(request.getHeader(jwtHeader)).orElseThrow(
                ()-> new RuntimeException("Unauthorized"));
        ObjectMapper objectMapper = new ObjectMapper();
        UserRegisterReqDto dto = null;
        try {
            dto = objectMapper.readValue(userReqDto, UserRegisterReqDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if(!userAuth.getEmail().equals(dto.getEmail()))
            return false;
        String profile_path = null;
        User user = modelMapper.map(dto, User.class);
        user.setUserId(userAuth.getUserId());
        UserProfile userProfile = modelMapper.map(dto, UserProfile.class);
        userProfile.setId(userAuth.getUserProfile().getId());
        for(Address a : user.getAddresses())
            a.setUser(user);
        profile_path = userAuthUtils.saveProfileImage(file, user);
        userProfile.setProfilePicture(profile_path);
        userProfile.setUser(user);
        user.setUserProfile(userProfile);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean addProfileImage(MultipartFile file, HttpServletRequest request) {
        User user = authUtils.getUserFromToken(request.getHeader(jwtHeader)).orElseThrow(
                ()-> new RuntimeException("Unauthorized"));
        String name = userAuthUtils.saveProfileImage(file, user);
        if(name!=null){
            user.getUserProfile().setProfilePicture(name);
            userRepository.save(user);
        return true;}
        else
            return false;
    }

    @Override
    public UserLoginResDto loginUser(UserLoginReqDto dto) {
        Optional<User> optUser = userRepository.findByEmail(dto.getEmail());
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (passwordEncoder.matches(dto.getPassword(), user.getPassword()) && user.getUserProfile().getActive().equals(Boolean.TRUE)) {
                Token token = new Token();
                token.setToken(jwtProvider.generateToken(user));
                token.setTokenType(TokenType.LOGIN_TOKEN);
                token.setCreatedBy(user.getUserProfile().getFirstName() + "." + user.getUserProfile().getLastName());
                token.setUsers(user);
                token.setCreatedDate(new Date());
                token.setExpiryDate(new Date(System.currentTimeMillis() + jwTokenExpiry * 1000));
                tokenRepository.save(token);
                UserLoginResDto resDto = modelMapper.map(user, UserLoginResDto.class);
                resDto.setToken(token.getToken());
                return resDto;
            }
        }
        return null;
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

    @Override
    public User resetPassword(ResetPasswordReqDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);
        if (null == user) {
            throw new RuntimeException("User Not found");
        }

        Token token = tokenRepository.findByTokenAndTokenTypeAndUsersUserId(dto.getToken(), TokenType.RESET_PASSWORD_TOKEN, user.getUserId());
        if (null == token) {
            throw new RuntimeException("Authentication Failed");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.getUserProfile().setActive(Boolean.TRUE);
        return userRepository.save(user);

    }

    @Override
    public UserProfileResDto getUserProfile(HttpServletRequest request) {
        String token = request.getHeader(jwtHeader);
        User user = authUtils.getUserFromToken(token).orElseThrow(()-> new RuntimeException("Unauthorized"));
        UserProfile userProfile = user.getUserProfile();
        UserProfileResDto resDto = modelMapper.map(userProfile, UserProfileResDto.class);
        resDto.setAddresses(user.getAddresses());
        resDto.setEmail(user.getEmail());
        return resDto;
    }

}
