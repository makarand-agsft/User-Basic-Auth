package com.user.auth.controller;

import com.user.auth.constants.ApiStatus;
import com.user.auth.dto.request.*;
import com.user.auth.dto.response.*;
import com.user.auth.exception.InvalidEmailException;
import com.user.auth.exception.InvalidRequestException;
import com.user.auth.exception.UserNotFoundException;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

/**
 * This class represents an endpoint of user authentication services
 */
@RestController
@RequestMapping(value = "/user/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
   private UserAuthUtils userAuthUtils;

    @Autowired
    private MessageSource messageSource;

    Logger logger= LoggerFactory.getLogger(AuthController.class);
    /**
     * This method is used for reset forgotten password.
     * @param forgotDto
     * @return send password token to registered user
     * @author dipak
     * @date 09/02/2021
     */
    @PostMapping(path = "user/forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordDto forgotDto, HttpServletRequest request) throws Exception {
        ResponseDto responseDto = null;
        try {
            String userAgent = request.getHeader("User-Agent");
            logger.info("User agent info is {}" + userAgent);
            authService.forgotPassword(forgotDto);
        } catch (UserNotFoundException | InvalidRequestException | InvalidEmailException exception) {
            responseDto = new ResponseDto(new ResponseObject(201, exception.getMessage(), null),
                    ApiStatus.FAILURE);
        }
        catch (Exception exception){
            responseDto = new ResponseDto(new ResponseObject(500, exception.getMessage(), null),
                    ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is used for changing password.
     * @param changePasswordDto
     * @return success message of changed password
     * @author dipak
     * @date 09/02/2021
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(path = "/user/changePassword")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        authService.changePassword(changePasswordDto);
        ResponseDto responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Password changed successfully..!", null), ApiStatus.SUCCESS);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is an endpoint for logging in the user
     * @param dto
     * @return user information including jwt token
     * @author aakash
     * @date 09/02/2021
     */
    @PostMapping(path = "/user/login")
    public ResponseEntity loginUser(@RequestBody UserLoginReqDto dto,HttpServletRequest servletRequest) {
        UserDto response = authService.loginUser(dto,servletRequest);
        ResponseDto responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Logged in successfully", response), ApiStatus.SUCCESS);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is an endpoint to reset user's password
     *
     * @param dto containing user one time password and email address
     * @return Success message of user activation
     */
    @PostMapping(path = "/user/resetPassword")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordReqDto dto) {
        UserDto response = authService.resetPassword(dto);
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User is Activated and changed password successfully", response),
                        ApiStatus.SUCCESS);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    @PostMapping(path = "/user/logout")
    public ResponseEntity logout(HttpServletRequest httpServletRequest){
        ResponseDto responseDto;
        authService.logout(httpServletRequest);
            responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(),"Logged out successfully",null),ApiStatus.SUCCESS);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);

    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @PostMapping(path = "/user/add/tenant")
    public ResponseEntity addTenant(@RequestBody TenantDto userDto) throws SQLException, IOException {
        ResponseDto responseDto;
        authService.addTenant(userDto);

        responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(),"Tenant added successfully",null),ApiStatus.SUCCESS);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

}
