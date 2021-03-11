package com.formz.service.impl;

import com.formz.constants.TokenType;
import com.formz.dto.ActivateUserDto;
import com.formz.dto.UserDto;
import com.formz.dto.UserLoginRequestDTO;
import com.formz.exception.BadRequestException;
import com.formz.model.Token;
import com.formz.model.User;
import com.formz.repo.TokenRepository;
import com.formz.repo.UserRepository;
import com.formz.security.JwtProvider;
import com.formz.service.AuthService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
/**
 * This class is responsible for providing user auth features
 */
@Service
public class AuthServiceImpl implements AuthService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${jwt.tokenValidity}")
    private Long jwTokenExpiry;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ModelMapper modelMapper;

    Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    /**
     * This method logs the user in with valid credentials
     *
     * @param userLoginRequestDTO
     * @return user details along with login token
     */
    @Override
    public UserDto loginUser(UserLoginRequestDTO userLoginRequestDTO) {
        UserDto resDto = null;
        if (userLoginRequestDTO == null || userLoginRequestDTO.getUserName() == null || userLoginRequestDTO.getPassword() == null) {
            throw new BadRequestException("Invalid Request");
        }
        Optional<User> user = userRepository.findByEmail(userLoginRequestDTO.getUserName());
        if (!user.isPresent()) {
            throw new BadRequestException("Invalid credentials");
        }
        log.info("user logging in :"+user.get().getEmail());
        if (passwordEncoder.matches(userLoginRequestDTO.getPassword(), user.get().getPassword()) && user.get().getActive().equals(Boolean.TRUE)) {
            Token token = new Token();
            token.setToken(jwtProvider.generateToken(user.get()));
            token.setTokenType(TokenType.LOGIN_TOKEN);
            token.setCreatedBy(user.get().getName());
            token.setUsers(user.get());
            token.setCreatedDate(new Date());
            token.setExpired(false);
            token.setExpiryDate(new Date(System.currentTimeMillis() + jwTokenExpiry * 1000));
            tokenRepository.save(token);

            resDto = modelMapper.map(user.get(), UserDto.class);
            resDto.setRoles(user.get().getRoles().stream().map(x -> x.getRole()).collect(Collectors.toList()));
            resDto.setToken(token.getToken());

        } else {
            log.info("Invalid credentials provided by user:"+user.get().getEmail());
            throw new BadRequestException("Invalid Credentials");
        }
        return resDto;
    }

    /**
     * This method is responsible for activating user profile with permanent password
     *
     * @param activateUserDto
     * @param userToken
     * @param httpServletRequest
     * @return user details
     */
    @Override
    public UserDto activateUser(ActivateUserDto activateUserDto, String userToken, HttpServletRequest httpServletRequest) {
        String resetToken = httpServletRequest.getParameter("token");
        if (resetToken == null) {
            throw new BadRequestException("Invalid Request");
        }
        String userEmail = jwtProvider.getUsernameFromToken(resetToken);

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (null == user) {
            log.info("User not found:"+userEmail);
            throw new BadRequestException("User not found");
        }
        if (!activateUserDto.getPassword().equalsIgnoreCase(activateUserDto.getConfirmPassword())) {
            log.info("Password doesn't match for user :"+userEmail);
            throw new BadRequestException("Password doesn't match");
        }
        log.info("Resetting password for user : " + userEmail);
        Token token = tokenRepository.findByTokenAndUsersId(userToken, user.getId());
        if (null == token) {
            throw new BadRequestException("Password already reset");
        }
        if (token.getExpiryDate().getTime() < new Date().getTime()) {
            throw new BadRequestException("Expired token");
        }
        user.setPassword(passwordEncoder.encode(activateUserDto.getPassword()));
        user.setActive(true);
        userRepository.save(user);
        tokenRepository.delete(token);
        log.info("Reset Token deleted for user :" + userEmail);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setRoles(user.getRoles().stream().map(x -> x.getRole()).collect(Collectors.toList()));
        log.info("Password reset successful for user :" + userEmail);
        return userDto;

    }

}
