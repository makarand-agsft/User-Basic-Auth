package com.user.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.auth.dto.*;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.enums.ErrorCodes;
import com.user.auth.dto.request.UserUpdateRoleReqDto;
import com.user.auth.enums.TokenType;
import com.user.auth.exception.*;
import com.user.auth.model.*;
import com.user.auth.model.Role;
import com.user.auth.model.Token;
import com.user.auth.model.User;
import com.user.auth.model.UserProfile;
import com.user.auth.exception.InvalidEmailException;
import com.user.auth.exception.InvalidPasswordException;
import com.user.auth.exception.UserNotFoundException;
import com.user.auth.model.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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
        boolean isUser = admin.getEmail().equals(dto.getEmail());
        String profile_path;
        if(!dupUser.isPresent() || isUser){
            User user = modelMapper.map(dto, User.class);
            UserProfile userProfile = modelMapper.map(dto, UserProfile.class);
            if(isUser){
                user.setUserId(admin.getUserId());
                userProfile.setId(admin.getUserProfile().getId());
            }
            for(Address a : user.getAddresses())
                a.setUser(user);
            userProfile.setUser(user);
            user.setUserProfile(userProfile);
            profile_path = userAuthUtils.saveProfileImage(file, (isUser)?admin:user);
            userProfile.setProfilePicture(profile_path);
            if(!isUser){
                userProfile.setActive(Boolean.FALSE);
                String uname = admin.getUserProfile().getFirstName()+"."+admin.getUserProfile().getLastName();
                user.setCreatedBy(uname);
                userProfile.setCreatedBy(uname);
                List<Role> roles = new ArrayList<>();
                for(String r : dto.getRoles()){
                    Optional<Role> role = roleRepository.findByRole(r);
                    role.ifPresent(roles::add);
                }
                Token token = new Token();
                token.setToken(userAuthUtils.generateKey(10));
                token.setTokenType(TokenType.RESET_PASSWORD_TOKEN);
                token.setUsers(user);
                token.setExpiryDate(new Date(System.currentTimeMillis()+jwTokenExpiry*1000));
                user.setTokens(Collections.singletonList(token));
                user.setRoles(roles);
                userRepository.save(user);
                tokenRepository.save(token);
                //send email
                String message ="Hello "+user.getUserProfile().getFirstName() +"This is your temporary password ,use this to change your password :"+token.getToken();
                emailUtils.sendInvitationEmail(user.getEmail(),"Invitation",message,fromEmail);
            }else
                userRepository.save(user);
            return true;
        }else
            return false;
    }

    @Override
    public byte[] getUserProfileImage(HttpServletRequest request) throws IOException {
        User user = authUtils.getUserFromToken(request.getHeader(jwtHeader)).orElseThrow(
                ()-> new RuntimeException("Unauthorized"));
        if(user.getUserProfile().getProfilePicture()==null)
            return null;
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
        String profile_path;
        User user = modelMapper.map(dto, User.class);
        user.setUserId(userAuth.getUserId());
        UserProfile userProfile = modelMapper.map(dto, UserProfile.class);
        userProfile.setId(userAuth.getUserProfile().getId());
        for(Address a : user.getAddresses())
            a.setUser(user);
        userProfile.setUser(user);
        user.setUserProfile(userProfile);
        profile_path = userAuthUtils.saveProfileImage(file, userAuth);
        userProfile.setProfilePicture(profile_path);
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

    @Override public int forgotPassword(ForgotPasswordDto forgotDto) throws Exception {
        int responseErrorCode = 0;
        if (forgotDto != null && forgotDto.getEmail() != null) {
            if (userAuthUtils.validateEmail(forgotDto.getEmail())) {
                Optional<User> userFromDb = userRepository.findByEmail(forgotDto.getEmail());
                if (userFromDb.isEmpty()) {
                    throw new UserNotFoundException(ErrorCodes.USER_NOT_FOUND.getCode(), ErrorCodes.USER_NOT_FOUND.getValue());
                }
                if (sendTokenMailToUser(userFromDb.get(), TokenType.FORGOT_PASSWORD_TOKEN)) {
                    responseErrorCode = 200;
                } else {
                    responseErrorCode = 400;
                }
                return responseErrorCode;
            } else {
                throw new InvalidEmailException(ErrorCodes.BAD_REQUEST.getCode(), ErrorCodes.BAD_REQUEST.getValue());
            }
        } else {
            throw new InvalidRequestException(ErrorCodes.BAD_REQUEST.getCode(), ErrorCodes.BAD_REQUEST.getValue());
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

    private boolean sendTokenMailToUser(User user, TokenType tokenType) {
        if(user.getEmail()!=null ){
            String token=userAuthUtils.generateKey(10);
            String subject="Forgot password auto generated mail.";
            String text=" Hello "+user.getUserProfile().getFirstName()+" , \n your requested token is "+token +" \n Use this token to change or reset your password.";

            Token tokenToBeSave= new Token();
            tokenToBeSave.setToken(token);
            tokenToBeSave.setTokenType(tokenType);
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
        } else {
            throw new UserNotFoundException(ErrorCodes.USER_NOT_FOUND.getCode(), ErrorCodes.USER_NOT_FOUND.getValue());
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
            throw new UserNotFoundException(ErrorCodes.USER_NOT_FOUND.getCode(),ErrorCodes.USER_NOT_FOUND.getValue());
        }

        Token token = tokenRepository.findByTokenAndTokenTypeOrTokenTypeAndUsersUserId(dto.getToken(), TokenType.RESET_PASSWORD_TOKEN,TokenType.FORGOT_PASSWORD_TOKEN, user.getUserId());
        if (null == token) {
            throw new UnAuthorisedException(ErrorCodes.UNAUTHORIZED.getCode(),ErrorCodes.UNAUTHORIZED.getValue());
        }
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.getUserProfile().setActive(true);
         userRepository.save(user);
        return modelMapper.map(user,UserRegisterReqDto.class);

    }


    /**
     * This method soft deletes user from system ( only admin can delete other users)
     * @param userId
     * @throws Exception
     */
    @Override public void deleteUserById(Long userId) throws Exception {

        User loggedInUser = userAuthUtils.getLoggedInUser();
        if (loggedInUser.getRoles().stream().noneMatch(x -> x.getRole().equalsIgnoreCase(com.user.auth.enums.Role.ADMIN.name()))) {
            throw new UnAuthorisedException(ErrorCodes.UNAUTHORIZED.getCode(),ErrorCodes.UNAUTHORIZED.getValue());
        }
        if (userId == null) {
            throw new InvalidRequestException(ErrorCodes.BAD_REQUEST.getCode(),ErrorCodes.BAD_REQUEST.getValue());
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            user.get().setDeleted(true);
            userRepository.save(user.get());
        }else{
            throw new InvalidRequestException(ErrorCodes.USER_NOT_FOUND.getCode(),ErrorCodes.USER_NOT_FOUND.getValue());
        }
    }

    @Override
    public boolean deleteProfileImage(HttpServletRequest request) {
        User user = authUtils.getUserFromToken(request.getHeader(jwtHeader)).orElseThrow(
                ()-> new RuntimeException("Unauthorized"));
        if (user != null) {
            String location = user.getUserProfile().getProfilePicture();
            if (location != null) {
                File file = new File(location);
                file.delete();
                user.getUserProfile().setProfilePicture(null);
                userRepository.save(user);
                return true;
            }
       }
        return false;
    }


    @Override
    public UserProfileResDto getUserProfile() {
        User user = userAuthUtils.getLoggedInUser();
        UserProfile userProfile = user.getUserProfile();
        UserProfileResDto resDto = modelMapper.map(userProfile, UserProfileResDto.class);
        resDto.setAddresses(user.getAddresses());
        resDto.setEmail(user.getEmail());
        return resDto;
    }

    public UserUpdateRoleRes updateRole(UserUpdateRoleReqDto dto) {
        if (null == dto.getUserId() || dto.getRoleList().isEmpty()) {
            throw new RuntimeException("Invalid Request");
        }
        User user = userRepository.findById(dto.getUserId()).orElse(null);
        if (null == user) {
            throw new RuntimeException("User Not Found");
        }
        List<Role> roleList = new ArrayList<>();
        for (Role role : dto.getRoleList()) {
            role = roleRepository.findById(role.getRoleId()).orElse(null);
            if (null != role) {
                roleList.add(role);
            }
        }
        user.setRoles(roleList);
        userRepository.save(user);


        List<String> roles =new ArrayList<>();
       for(Role role:user.getRoles())
       {
           roles.add(role.getRole());
       }
        String message = "Hello " + user.getUserProfile().getFirstName() + "Your Roles are changed "+roles.toString();

        emailUtils.sendInvitationEmail(user.getEmail(), "Invitation", message, fromEmail);
        return new UserUpdateRoleRes(user.getEmail(), roles);

    }

}
