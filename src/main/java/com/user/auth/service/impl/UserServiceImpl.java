package com.user.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.auth.dto.*;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.enums.TokenType;
import com.user.auth.model.*;
import com.user.auth.exception.InvalidEmailException;
import com.user.auth.exception.InvalidPasswordException;
import com.user.auth.exception.UserNotFoundException;
import com.user.auth.model.Role;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.model.UserProfile;
import com.user.auth.repository.RoleRepository;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import com.user.auth.security.JwtProvider;
import com.user.auth.service.UserService;
import com.user.auth.utils.EmailUtils;
import com.user.auth.utils.UserAuthUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class is responsible for handling user authentication
 */
@Service
@Transactional
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

    /**
     * This method registers new user
     * @param
     * @throws Exception
     */
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
    public int forgotPassword(ForgotPasswordDto forgotDto) throws Exception {
        if(forgotDto!=null && forgotDto.getEmail()!=null){
            int responseErrorCode;
            if(userAuthUtils.validateEmail(forgotDto.getEmail())){
                Optional<User> userFromDb=userRepository.findByEmail(forgotDto.getEmail());
                if(userFromDb.isEmpty()) {
                    throw new UserNotFoundException();
                }
                if(sendTokenMailToUser(userFromDb.get())){
                    responseErrorCode=200;
                }else{
                    responseErrorCode=400;
                }
                return responseErrorCode;
            }else{
                throw new InvalidEmailException();
            }
        }else{
            throw new InvalidEmailException();
        }
    }

    @Override
    public boolean changePassword(ChangePasswordDto changePasswordDto , HttpServletRequest request) {
        if (changePasswordDto != null && changePasswordDto.getEmail() != null && changePasswordDto.getOldPassword() != null
                && changePasswordDto.getNewPassword() != null) {
            String header = request.getHeader("Authorization");
            String email = null;
            if (header != null) {
                try {
                    email=jwtProvider.getUsernameFromToken(header);
                } catch (IllegalArgumentException e) {
                   // logger.error("an error occured during getting username from token", e);
                } catch (ExpiredJwtException e) {
                   // logger.warn("the token is expired and not valid anymore", e);
                } catch(SignatureException e){
                   // logger.error("Authentication Failed. Username or Password not valid.");
                }

                if(email.equalsIgnoreCase(changePasswordDto.getEmail())) {

                    Optional<User> userFromDb = userRepository.findByEmail(email);
                    if (userFromDb.isPresent()) {
                        // userFromDb.get().getPassword().equalsIgnoreCase(passwordEncoder.encode(changePasswordDto.getOldPassword()))
                        if (passwordEncoder.matches(changePasswordDto.getOldPassword(),userFromDb.get().getPassword())) {
                            userFromDb.get().setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
                            userRepository.save(userFromDb.get());
                            return true;
                        } else {
                            throw new InvalidPasswordException(101,"Your old password is incorrect...!");
                        }

                    } else {
                        // user not present
                        return false;
                    }
                }else{
                    // provided email id not matched with token mail id
                    return false;
                }
            }else{
                throw new RuntimeException("No token found");
            }
        }
        return false;
    }

    private boolean sendTokenMailToUser(User user) {
        if(user.getEmail()!=null ){
            String token=userAuthUtils.generateKey(10);
            String subject="Forgot password auto generated mail.";
            String text=" Hello "+user.getUserProfile().getFirstName()+" , \n your requested token is "+token +" \n Use this token to change or reset your password.";

            Token tokenToBeSave= new Token();
            tokenToBeSave.setToken(token);
            tokenToBeSave.setTokenType(TokenType.FORGOT_PASSWORD_TOKEN);
            tokenToBeSave.setUsers(user);
            tokenToBeSave.setCreatedBy(user.getUserProfile().getFirstName()+"."+user.getUserProfile().getLastName());
            tokenToBeSave.setCreatedDate(new Date());
            tokenToBeSave.setExpiryDate(new Date(System.currentTimeMillis() + jwTokenExpiry * 1000));
            tokenRepository.save(tokenToBeSave);
            emailUtils.sendInvitationEmail(user.getEmail(),subject,text,fromEmail);
            return true;
        }
        return false;
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
                    userRegisterReqDto.setRoles(userRoles);
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
        user.getUserProfile().setActive(true);
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
