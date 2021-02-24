package com.user.auth.controller;

import com.user.auth.constants.ApiStatus;
import com.user.auth.dto.request.*;
import com.user.auth.dto.response.*;
import com.user.auth.exception.*;
import com.user.auth.service.AuthService;
import com.user.auth.utils.UserAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * This class represents an endpoint of user authentication services
 */
@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserAuthUtils userAuthUtils;

    @Autowired
    private MessageSource messageSource;

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * This method is used for reset forgotten password.
     *
     * @param forgotDto
     * @return send password token to registered user
     * @author dipak
     * @date 09/02/2021
     */
    @PostMapping(path = "/forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordDto forgotDto, HttpServletRequest request) throws Exception {
        ResponseDto responseDto = null;
        try {
            String userAgent = request.getHeader("User-Agent");
            logger.info("User agent info is {}" + userAgent);
            authService.forgotPassword(forgotDto);
            responseDto = new ResponseDto(new ResponseObject(200, messageSource.getMessage("forgot.password.successful", null, Locale.ENGLISH), null), ApiStatus.SUCCESS);
        } catch (UserNotFoundException | InvalidRequestException | InvalidEmailException exception) {
            responseDto = new ResponseDto(new ResponseObject(201, exception.getMessage(), null),
                    ApiStatus.FAILURE);
        } catch (Exception exception) {
            responseDto = new ResponseDto(new ResponseObject(500, exception.getMessage(), null),
                    ApiStatus.FAILURE);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is used for changing password.
     *
     * @param changePasswordDto
     * @return success message of changed password
     * @author dipak
     * @date 09/02/2021
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(path = "/change-password")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        ResponseDto responseDto = null;
        try {
            authService.changePassword(changePasswordDto);
            responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), messageSource.getMessage("change.password.successful", null, Locale.ENGLISH), null), ApiStatus.SUCCESS);
        } catch (InvalidPasswordException | UserNotFoundException | UnAuthorisedException exception) {
            responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), exception.getMessage(), null), ApiStatus.FAILURE);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is an endpoint for logging in the user
     *
     * @param dto
     * @return user information including jwt token
     * @author aakash
     * @date 09/02/2021
     */
    @PostMapping(path = "/login")
    public ResponseEntity loginUser(@RequestBody UserLoginReqDto dto, HttpServletRequest servletRequest) {
        ResponseDto responseDto = null;
        try {
            UserDto response = authService.loginUser(dto, servletRequest);
            responseDto = new ResponseDto(new ResponseObject(200, messageSource.getMessage("login.successful", null, Locale.ENGLISH), response), ApiStatus.SUCCESS);
        } catch (UserNotFoundException exception) {
            responseDto = new ResponseDto(new ResponseObject(201, exception.getMessage(), null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is an endpoint to reset user's password
     *
     * @param dto containing user one time password and email address
     * @return Success message of user activation
     */
    @PostMapping(path = "/activate-user")
    public ResponseEntity activateUser(@RequestParam("email") String email,@RequestParam("token") String token,@RequestBody ActivateUserDto dto) {
        ResponseDto responseDto = null;
        try {
            UserDto response = authService.activateUser(dto,email,token );
            responseDto =
                    new ResponseDto(new ResponseObject(200, "User is Activated and changed password successfully", response),
                            ApiStatus.SUCCESS);
        } catch (UserNotFoundException | UnAuthorisedException | InvalidRequestException exception) {
            responseDto =
                    new ResponseDto(new ResponseObject(201, exception.getMessage(), null),
                            ApiStatus.SUCCESS);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity logout(HttpServletRequest httpServletRequest) {
        ResponseDto responseDto = null;
        try {
            authService.logout(httpServletRequest);
            responseDto = new ResponseDto(new ResponseObject(200, messageSource.getMessage("logout.successful", null, Locale.ENGLISH), null), ApiStatus.SUCCESS);
        } catch (UserNotFoundException exception) {
            responseDto = new ResponseDto(new ResponseObject(201, exception.getMessage(), null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);

    }


}
