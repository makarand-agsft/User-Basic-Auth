package com.user.auth.controller;

import com.user.auth.dto.*;

import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This class represents an endpoint of user authentication services
 */
@RestController public class UserController {
    @Autowired private UserService userService;

    /**
     * This method registers new user in system
     *
     * @param dto
     * @return Success message is user adds successfully
     * @throws Exception
     */
    @PostMapping(path = "/user/register") public ResponseEntity registerNewUser(@RequestBody UserRegisterReqDto dto) throws Exception {
        userService.registerNewUser(dto);
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User added successfully. Please check email for account activation", null),
                        HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is an endpoint for logging in the user
     *
     * @param dto
     * @return user information including jwt token
     */
    @PostMapping(path = "/user/login") public ResponseEntity loginUser(@RequestBody UserLoginReqDto dto) {
        ResponseObject responseMessage;
        UserLoginResDto response = userService.loginUser(dto);
        ResponseDto responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Logged in successfully", response), HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is an endpoint to reset user's password
     *
     * @param dto containing user one time password and email address
     * @return Success message of user activation
     */
    @PostMapping(path = "/user/resetpassword") public ResponseEntity registerNewUser(@RequestBody ResetPasswordReqDto dto) {
        UserRegisterReqDto response = userService.resetPassword(dto);
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User is Activated and changed password successfully", response),
                        HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method fetches all list of 'ADMIN' users
     *
     * @return
     */
    @GetMapping(value = "/user/getAllAdminUsers") @ResponseBody public ResponseEntity getAllAdminUsers() {
        UserListResponseDto userListResponseDto = userService.getAllAdminUsers();
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User is Activated and changed password successfully", userListResponseDto),
                        HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is end point for service to delete user from system
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping(value = "user/deleteByEmail") public ResponseEntity deleteUserById(@RequestParam(value = "userId", required = true) Long userId)
            throws Exception {
        userService.deleteUserById(userId);
        ResponseDto responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User deleted successfully", null), HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

}
