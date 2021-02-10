package com.user.auth.service.impl;

import com.user.auth.dto.UserListResponseDto;
import com.user.auth.dto.UserLoginReqDto;
import com.user.auth.dto.UserLoginResDto;
import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.enums.TokenType;
import com.user.auth.model.Role;
import com.user.auth.model.Token;
import com.user.auth.model.User;
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

import java.util.*;

/**
 * This class is responsible for handling user authentication
 */
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

    /**
     * This method registers new user
     * @param dto
     * @throws Exception
     */
    @Override
    public void registerNewUser(UserRegisterReqDto dto) throws Exception {
        Optional<User> dupUser =  userRepository.findByEmail(dto.getEmail());
        if(!dupUser.isPresent()){
            User user = modelMapper.map(dto, User.class);
            user.getUserProfile().setActive(Boolean.FALSE);
            user.setCreatedBy("");
            List<Role> roles = new ArrayList<>();
            for(String r : dto.getRole()){
                Optional<Role> role = roleRepository.findByRole(r);
                if(role.isPresent())
                    roles.add(role.get());
            }
            user.setRoles(roles);
            Token token = new Token();
            token.setToken(userAuthUtils.generateKey(10));
            token.setTokenType(TokenType.RESET_PASSWORD_TOKEN);
            token.setUsers(user);
            token.setExpiryDate(new Date(System.currentTimeMillis()+jwTokenExpiry*1000));
            user.setTokens(Collections.singletonList(token));
            userRepository.save(user);
            tokenRepository.save(token);
            //send email
            String message ="Hello "+user.getUserProfile().getFirstName() +"This is your temporary password ,use this to change your password :"+token.getToken();
            emailUtils.sendInvitationEmail(user.getEmail(),"Invitation",message,fromEmail);
        }else{
            throw new Exception("User already exists");
        }
    }

    /**
     * This method verifies the user credentials and logs in and sends jwt token in response
     * @param dto
     * @return user information with jwt token
     */
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

    /**
     * Fetches list of user
     * @return This method returns list of all users with role admin
     */
    @Override public UserListResponseDto getAllAdminUsers() {
        Optional<Role> role = roleRepository.findByRole("ADMIN");
        UserListResponseDto userListResponseDto = new UserListResponseDto();
        List<UserRegisterReqDto> userResponse = new ArrayList<>();
        if (role.isPresent()) {
            List<User> users = userRepository.findByRoles(role.get());
            if (users != null) {

                for (User user : users) {
                    List<String> userRoles = new ArrayList<>();
                    for (Role userRole : user.getRoles()) {
                        userRoles.add(userRole.getRole());
                    }
                    UserRegisterReqDto userRegisterReqDto=modelMapper.map(user, UserRegisterReqDto.class);
                    userRegisterReqDto.setRole(userRoles);
                    userResponse.add(userRegisterReqDto);
                }
                userListResponseDto.setUserList(userResponse);
            }
        }
        return userListResponseDto;
    }

    /**
     * This method resets the one time password of user sent on email
     * @param dto containing user one time password and email address
     * @return Success message of user activation
     */
    @Override
    public UserRegisterReqDto resetPassword(ResetPasswordReqDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);
        if (null == user) {
            throw new RuntimeException("User Not found");
        }

        Token token = tokenRepository.findByTokenAndTokenTypeAndUsersUserId(dto.getToken(), TokenType.RESET_PASSWORD_TOKEN, user.getUserId());
        if (null == token) {
            throw new RuntimeException("Authentication Failed");
        }
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
//        user.getUserProfile().setActive(true);
         userRepository.save(user);
        UserRegisterReqDto userDto = modelMapper.map(user,UserRegisterReqDto.class);
        return userDto;

    }

    /**
     * This method soft deletes user from system ( only admin can delete other users)
     * @param userId
     * @throws Exception
     */
    @Override public void deleteUserById(Long userId) throws Exception {

        User loggedInUser = userAuthUtils.getLoggedInUser();
        if (loggedInUser.getRoles().stream().noneMatch(x -> x.getRole().equalsIgnoreCase(com.user.auth.enums.Role.ADMIN.name()))) {
            throw new Exception("Unauthorised");
        }
        if (userId == null) {
            throw new Exception("Error");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            user.get().setDeleted(true);
            userRepository.save(user.get());
        }
    }

}
