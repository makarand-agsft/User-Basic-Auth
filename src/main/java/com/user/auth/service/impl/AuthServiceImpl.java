package com.user.auth.service.impl;

import com.user.auth.constants.TokenType;
import com.user.auth.dto.request.*;
import com.user.auth.dto.response.UserDto;
import com.user.auth.exception.*;
import com.user.auth.model.*;
import com.user.auth.multitenancy.MultiTenantDataSourceConfig;
import com.user.auth.repository.*;
import com.user.auth.security.JwtProvider;
import com.user.auth.service.AuthService;
import com.user.auth.utils.EmailUtils;
import com.user.auth.utils.UserAuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for handling user authentication
 */
@Service
public class AuthServiceImpl implements AuthService {

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

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    MultiTenantDataSourceConfig multiTenantDataSourceConfig;

    @Autowired
    MessageSource messageSource;

    @Autowired
    TenantInfoRepository tenantInfoRepository;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${reset.otp.size}")
    private int otpSize;

    Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    /**
     * This method helps to reset password of the user
     *
     * @param forgotDto
     * @author Dipak Desai
     */
    @Override
    public void forgotPassword(ForgotPasswordDto forgotDto) throws Exception {

        if (forgotDto != null && forgotDto.getEmail() != null) {
            if (userAuthUtils.validateEmail(forgotDto.getEmail())) {
                log.info("Forgot password request received for user :" + forgotDto.getEmail());
                Optional<User> userFromDb = userRepository.findByEmail(forgotDto.getEmail());
                if (userFromDb.isEmpty()) {
                    throw new UserNotFoundException(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
                }
                if (!sendTokenMailToUser(userFromDb.get(), TokenType.FORGOT_PASSWORD_TOKEN)) {
                    throw new InvalidRequestException(messageSource.getMessage("error.sending.email", null,
                            Locale.ENGLISH));
                }
                userRepository.save(userFromDb.get());
                log.info("Forgot password email token sent to user :" + forgotDto.getEmail());
            } else {
                log.info("Invalid email for forgot password request");
                throw new InvalidEmailException(messageSource.getMessage("invalid.email", null, Locale.ENGLISH));
            }
        } else {
            throw new InvalidRequestException(messageSource.getMessage("invalid.request", null, Locale.ENGLISH));
        }
    }

    /**
     * This method changes the password of logged in user
     *
     * @param changePasswordDto
     * @author Dipak Desai
     */
    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {
        if (changePasswordDto != null && changePasswordDto.getEmail() != null && changePasswordDto.getOldPassword() != null && changePasswordDto
                .getNewPassword() != null) {
            User loggedInUser = userAuthUtils.getLoggedInUser();
            if (loggedInUser.getEmail().equalsIgnoreCase(changePasswordDto.getEmail())) {
                log.info("Change password request received for user :" + changePasswordDto.getEmail());
                Optional<User> userFromDb = userRepository.findByEmail(loggedInUser.getEmail());
                if (userFromDb.isPresent()) {
                    if (passwordEncoder.matches(changePasswordDto.getOldPassword(), userFromDb.get().getPassword())) {
                        userFromDb.get().setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
                        userRepository.save(userFromDb.get());
                    } else {
                        log.info("Invalid credentials provided for change password :" + changePasswordDto.getEmail());
                        throw new InvalidPasswordException(messageSource.getMessage("invalid.password", null, Locale.ENGLISH));
                    }
                } else {
                    log.info("User not found :" + changePasswordDto.getEmail());
                    throw new UserNotFoundException(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
                }
            } else {
                log.info("Invalid email provided for change password request : " + changePasswordDto.getEmail());
                throw new InvalidPasswordException(messageSource.getMessage("invalid.password", null, Locale.ENGLISH));
            }
        } else {
            throw new UnAuthorisedException(messageSource.getMessage("unauthorized.access", null, Locale.ENGLISH));
        }
    }

    /**
     * This method send change_password token to registered user via mail
     *
     * @param user & tokenType
     * @author Dipak Desai
     */
    private boolean sendTokenMailToUser(User user, TokenType tokenType) {
        if (user.getEmail() != null) {
            String token = userAuthUtils.generateKey(10);
            String subject = "Forgot password auto generated mail.";
            String text = " Hello " + user.getUserProfile().getFirstName() + " , \n your requested token is " + token + " \n Use this token to reset your password.";

            Token tokenToBeSave = new Token();
            tokenToBeSave.setToken(token);
            tokenToBeSave.setTokenType(tokenType);
            tokenToBeSave.setUsers(user);
            tokenToBeSave.setCreatedBy(user.getUserProfile().getFirstName() + "." + user.getUserProfile().getLastName());
            tokenToBeSave.setCreatedDate(new Date());
            if (tokenType.equals(TokenType.FORGOT_PASSWORD_TOKEN)) {
                tokenToBeSave.setExpiryDate(new Date(System.currentTimeMillis() + forgotTokenValidity * 1000));
            } else if (tokenType.equals(TokenType.RESET_PASSWORD_TOKEN)) {
                tokenToBeSave.setExpiryDate(new Date(System.currentTimeMillis() + resetTokenExpiry * 1000));
            }
            tokenRepository.save(tokenToBeSave);
            emailUtils.sendInvitationEmail(user.getEmail(), subject, text, fromEmail,"");
            log.info("Email sent successfully with " + tokenType + "to :" + user.getEmail());
            return true;
        }
        return false;
    }

    /**
     * This method verifies the user credentials and logs in and sends jwt token in response
     *
     * @param loginDto
     * @return user information with jwt token
     */
    @Override
    public UserDto loginUser(UserLoginReqDto loginDto, HttpServletRequest httpServletRequest) {
        Optional<User> optUser = userRepository.findByEmail(loginDto.getEmail());

        if (optUser.isPresent()) {
            User user = optUser.get();
            if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword()) && user.getUserProfile().getActive().equals(Boolean.TRUE)) {
                Token token = new Token();
                token.setToken(jwtProvider.generateToken(user, httpServletRequest));
                token.setTokenType(TokenType.LOGIN_TOKEN);
                token.setCreatedBy(user.getUserProfile().getFirstName() + "." + user.getUserProfile().getLastName());
                token.setUsers(user);
                token.setCreatedDate(new Date());
                token.setExpired(false);
                token.setExpiryDate(new Date(System.currentTimeMillis() + jwTokenExpiry * 1000));
                tokenRepository.save(token);
                UserDto resDto = modelMapper.map(user, UserDto.class);
                List<String> userRoles = new ArrayList<>();
                for (Role userRole : user.getRoles()) {
                    userRoles.add(userRole.getRole());
                }
                resDto.setRoles(userRoles);
                resDto.setToken(token.getToken());
                log.info("Login successfully :" + loginDto.getEmail());
                return resDto;
            }
        }
        throw new UserNotFoundException(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
    }


    /**
     * This method resets the one time password of user sent on email
     *
     * @param activateUserDto containing user one time password and email address
     * @param email
     * @param userToken
     * @return Success message of user activation
     * @author akshay kamble
     */
    @Override
    public UserDto activateUser(ActivateUserDto activateUserDto, String email, String userToken) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (null == user) {
            throw new UserNotFoundException(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
        }
        log.info("Resetting password for user : " + email);
        Token token = tokenRepository.findByTokenAndUsersUserId(userToken, user.getUserId());
        if (null == token) {
            throw new UnAuthorisedException(messageSource.getMessage("already.reset.password", null, Locale.ENGLISH));
        }
        if (token.getExpiryDate().getTime() < new Date().getTime()) {
            throw new InvalidRequestException(messageSource.getMessage("token.expired", null, Locale.ENGLISH));
        }
        user.setPassword(passwordEncoder.encode(activateUserDto.getPassword()));
        user.getUserProfile().setActive(true);
        userRepository.save(user);
        tokenRepository.delete(token);
        log.info("Reset Token deleted for user :" + email);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setRoles(user.getRoles().stream().map(x -> x.getRole()).collect(Collectors.toList()));
        log.info("Password reset successful for user :" + email);
        return userDto;

    }

    /**
     * This method logs out the user
     *
     * @param request
     * @author makarand
     */
    @Override
    public void logout(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        User loggedInUser = userAuthUtils.getLoggedInUser();
        if (loggedInUser != null) {
            log.info("Logging out user :" + loggedInUser.getEmail());
            Token token = tokenRepository.findByTokenAndUsersUserId(authToken, loggedInUser.getUserId());
            token.setExpiryDate(null);
            token.setExpired(true);
            tokenRepository.save(token);
        } else {
            throw new UserNotFoundException(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
        }
        log.info("Logged out user :" + loggedInUser.getEmail());
    }

}
