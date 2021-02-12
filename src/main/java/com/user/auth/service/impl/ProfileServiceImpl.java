package com.user.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.auth.dto.request.*;
import com.user.auth.dto.response.UserDto;
import com.user.auth.dto.response.UserListResponseDto;
import com.user.auth.dto.response.UserUpdateRoleRes;
import com.user.auth.enums.ErrorCodes;
import com.user.auth.enums.TokenType;
import com.user.auth.exception.*;
import com.user.auth.model.*;
import com.user.auth.repository.RoleRepository;
import com.user.auth.repository.TokenRepository;
import com.user.auth.repository.UserRepository;
import com.user.auth.security.JwtProvider;
import com.user.auth.service.ProfileService;
import com.user.auth.utils.EmailUtils;
import com.user.auth.utils.UserAuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for handling user authentication
 */
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

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

    @Value("${forgot.token.validity}")
    private Long forgotTokenValidity;

    @Value("${reset.otp.size}")
    private int otpSize;

    Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);
    /**
     * This method registers new user
     * @param
     * @throws Exception
     */
    @Override
    public void addUser(String jsonString, MultipartFile file) {
       // User loggedInUser = authUtils.getLoggedInUser();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto dto = null;
        try {
           dto = objectMapper.readValue(jsonString, UserDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error in mapping object");
            e.printStackTrace();
        }
        log.info("Saving user :"+dto.getEmail());
        Optional<User> existingUser =  userRepository.findByEmail(dto.getEmail());
        boolean isDeletedUser = existingUser.isPresent() && existingUser.get().getDeleted();
        String profile_path;
        if(!existingUser.isPresent() || isDeletedUser) {
            User mappedUser = modelMapper.map(dto, User.class);
            mappedUser.setDeleted(Boolean.FALSE);
            User user = isDeletedUser ? existingUser.get() : mappedUser;
            profile_path = userAuthUtils.saveProfileImage(file, user);
            user.getUserProfile().setProfilePicture(profile_path);
            user.getUserProfile().setActive(Boolean.FALSE);
            user.getUserProfile().setUser(user);
            user.getAddresses().forEach(x->{
                x.setUser(user);
            });
            if(!isDeletedUser){
                List<Role> roles = new ArrayList<>();
                for (String r : dto.getRoles()) {
                    Optional<Role> role = roleRepository.findByRole(r);
                    role.ifPresent(roles::add);
                }
                Token token = new Token();
                token.setToken(userAuthUtils.generateKey(otpSize));
                token.setTokenType(TokenType.RESET_PASSWORD_TOKEN);
                token.setUsers(mappedUser);
                token.setExpiryDate(new Date(System.currentTimeMillis() + resetTokenExpiry * 1000));
                mappedUser.setTokens(Collections.singletonList(token));
                mappedUser.setRoles(roles);
                userRepository.save(mappedUser);
                tokenRepository.save(token);
                String message = "Hello " + mappedUser.getUserProfile().getFirstName() + "This is your temporary password ,use this to change your password :" + token.getToken();
                emailUtils.sendInvitationEmail(mappedUser.getEmail(), "User-Auth Invitation", message, fromEmail);
            }else
                throw new InvalidEmailException(ErrorCodes.EMAIL_ALREADY_EXISTS.getCode(), ErrorCodes.EMAIL_ALREADY_EXISTS.getValue());
            log.info("User saved successfully : " + dto.getEmail());
        }
    }
    @Override
    public Boolean updateUser(String jsonString, MultipartFile file) {
        User loggedUser = userAuthUtils.getLoggedInUser();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto userDto = null;
        try {
            userDto = objectMapper.readValue(jsonString, UserDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error in mapping object");
            e.printStackTrace();
        }
        if(!loggedUser.getEmail().equals(userDto.getEmail()) || !loggedUser.getUserId().equals(userDto.getUserId()) ||
        !loggedUser.getUserProfile().getId().equals(userDto.getUserProfile().getId()))
            throw new  InvalidRequestException(ErrorCodes.UNAUTHORIZED.getCode(),ErrorCodes.UNAUTHORIZED.getValue());
        String profile_path;
        User mappedUser = convertUserDtoToUser(userDto, loggedUser);
        profile_path = userAuthUtils.saveProfileImage(file, loggedUser);
        mappedUser.getUserProfile().setProfilePicture(profile_path);
        userRepository.save(mappedUser);
        return true;
    }

    private User convertUserDtoToUser(UserDto userDto, User loggedUser) {
        User user = modelMapper.map(userDto, User.class);
        user.setDeleted(Boolean.FALSE);
        user.setRoles(loggedUser.getRoles());
        user.setPassword(loggedUser.getPassword());
        user.getUserProfile().setActive(Boolean.TRUE);
        return user;
    }

    @Override
    public byte[] getUserProfileImage() throws IOException {

        User user =userAuthUtils.getLoggedInUser();
        log.info("Fetching profile image for user :"+user.getEmail());
        String fileName = user.getUserProfile().getProfilePicture();
        if (fileName != null && new File(fileName).exists()) {
            log.info("Fetched profile image for user :" + user.getEmail());
            return Files.readAllBytes(Paths.get(user.getUserProfile().getProfilePicture()));
        }
        else
            return null;
    }


    @Override public void addProfileImage(MultipartFile file) {
        User user = authUtils.getLoggedInUser();
        log.info("Adding profile image for user : "+user.getEmail());
        String name = userAuthUtils.saveProfileImage(file, user);
        user.getUserProfile().setProfilePicture(name);
        userRepository.save(user);
        log.info("Profile image saved for user :"+user.getEmail());
    }

    /**
     * Fetches list of user
     * @return This method returns list of all users with role admin
     */
    @Override public UserListResponseDto getAllUsers() {
        log.info("Fetching all admin users");
        Optional<Role> role = roleRepository.findByRole(com.user.auth.enums.Role.USER.name());
        UserListResponseDto userListResponseDto = new UserListResponseDto();
        List<UserDto> userResponse = new ArrayList<>();
        if (role.isPresent()) {
            List<User> users = userRepository.findByRoles(role.get());
            if (users != null) {

                for (User user : users) {
                    List<String> userRoles = new ArrayList<>();
                    for (Role userRole : user.getRoles()) {
                        userRoles.add(userRole.getRole());
                    }
                    UserDto userRegisterReqDto=modelMapper.map(user, UserDto.class);
                    userRegisterReqDto.setRoles(userRoles);
                    userResponse.add(userRegisterReqDto);
                }
                userListResponseDto.setUserList(userResponse);
            }
        }
        log.info("Fetched all admin users");
        return userListResponseDto;
    }


    /**
     * This method soft deletes user from system ( only admin can delete other users)
     * @author makarand
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
            log.info("Deleting user :"+user.get().getEmail());
            user.get().setDeleted(true);
            user.get().getUserProfile().setActive(Boolean.FALSE);
            userRepository.save(user.get());
        }else{
            throw new InvalidRequestException(ErrorCodes.USER_NOT_FOUND.getCode(),ErrorCodes.USER_NOT_FOUND.getValue());
        }
    }

    /**
     * This method deletes user profile image
     * @author aakash rajput
     */
    @Override
    public void deleteProfileImage() {
        User user = authUtils.getLoggedInUser();
        log.info("Deleting user profile image :"+user.getEmail());
        String fileLocation = user.getUserProfile().getProfilePicture();
        if (fileLocation != null) {
            File file = new File(fileLocation);
            file.delete();
            user.getUserProfile().setProfilePicture(null);
            userRepository.save(user);
        }else
            throw new InvalidRequestException(ErrorCodes.FILE_NOT_FOUND.getCode(),ErrorCodes.FILE_NOT_FOUND.getValue());
        log.info("User profile image deleted successfully");
    }

    /**
     * This method returns user profile details of logged in user
     * @author aakash rajput
     * @return
     */
    @Override public UserDto getUserProfile() {

        User user = userAuthUtils.getLoggedInUser();
        log.info("Fetching user profile of user :"+user.getEmail());
        UserProfile userProfile = user.getUserProfile();
        UserDto resDto = modelMapper.map(userProfile, UserDto.class);
        if (user.getAddresses() != null && !user.getAddresses().isEmpty())
            resDto.setAddresses(user.getAddresses().stream().map(x -> modelMapper.map(x, AddressDto.class)).collect(Collectors.toList()));
        resDto.setRoles(user.getRoles().stream().map(x -> x.getRole()).collect(Collectors.toList()));
        resDto.setEmail(user.getEmail());
        log.info("Fetched user profile :"+user.getEmail());
        return resDto;
    }

    /**
     * This method updates the role of user
     * @author akshay kamble
     * @param updateRoleReqDto
     * @return
     */
    @Override
    public UserUpdateRoleRes updateRole(UserUpdateRoleReqDto updateRoleReqDto) {

        if (null == updateRoleReqDto.getUserId() || updateRoleReqDto.getRoleList().isEmpty()) {
            throw new RuntimeException("Invalid Request");
        }
        User user = userRepository.findById(updateRoleReqDto.getUserId()).orElse(null);
        if (null == user) {
            throw new RuntimeException("User Not Found");
        }
        log.info("Updating user role for user :"+updateRoleReqDto.getUserId());
        List<Role> roleList = new ArrayList<>();
        for (Role role : updateRoleReqDto.getRoleList()) {
            role = roleRepository.findById(role.getRoleId()).orElse(null);
            if (null != role) {
                roleList.add(role);
            }
        }
        user.setRoles(roleList);
        userRepository.save(user);
        log.info("User role updated successfully :"+user.getEmail());

        List<String> roles =new ArrayList<>();
       for(Role role:user.getRoles())
       {
           roles.add(role.getRole());
       }
        String message = "Hello " + user.getUserProfile().getFirstName() + "Your role is changed to : "+roles.toString();

        emailUtils.sendInvitationEmail(user.getEmail(), "Role Updation", message, fromEmail);
        return new UserUpdateRoleRes(user.getEmail(), roles);

    }
}
