package com.user.auth.controller;

import com.user.auth.dto.*;

import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.dto.request.UserUpdateRoleReqDto;
import com.user.auth.service.UserService;
import com.user.auth.utils.UserAuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This class represents an endpoint of user authentication services
 */
@RestController public class UserController {
    @Autowired private UserService userService;

    @Autowired
   private UserAuthUtils userAuthUtils;

    /**
     * This method registers new user in system
     *
     * @param
     * @return Success message is user adds successfully
     * @throws Exception
     */
    @PostMapping(path = "/user/add")
    public ResponseEntity addUser(@RequestParam(name = "file", required = false)MultipartFile file, @RequestParam("userReqDto")String userReqDto,HttpServletRequest request){
        ResponseDto responseMessage;
        if(userService.addUser(userReqDto,file,request))
            responseMessage = new ResponseDto(new ResponseObject(200, "User added successfully. Please check email for account activation",null),HttpStatus.OK);
        else
            responseMessage = new ResponseDto(new ResponseObject(400,"User already exists in system",null),HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }
    @PostMapping(path = "user/forgotpassword")
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordDto forgotDto) throws Exception {
        ResponseDto responseDto = null;
        int message=userService.forgotPassword(forgotDto);
        if(message==200){
            responseDto= new ResponseDto(new ResponseObject(200,"Your password token is sent to your registered email id.",null),HttpStatus.OK);
        }else if(message==400){
            responseDto= new ResponseDto(new ResponseObject(400,"Oops..! Something went wrong, email not sent.",null),HttpStatus.OK);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    @PostMapping(path = "/user/changepassword")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest request){
        ResponseDto responseDto;
        if(userService.changePassword(changePasswordDto,request)){
            responseDto= new ResponseDto(new ResponseObject(200,"Password changed successfully..!",null),HttpStatus.OK);
        }else{
            responseDto= new ResponseDto(new ResponseObject(400,"Oops..! Failed to changed the password.",null),HttpStatus.OK);
        }
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

    @PostMapping(path = "/user/add/profileimg")
    public ResponseEntity addProfileImage(@RequestParam(name = "file", required = false)MultipartFile file,HttpServletRequest request){
        ResponseDto responseMessage;
        if(userService.addProfileImage(file,request))
            responseMessage = new ResponseDto(new ResponseObject(200, "Profile image added successfully",null),HttpStatus.OK);
        else
            responseMessage = new ResponseDto(new ResponseObject(400,"Failed to add image",null),HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
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
    @GetMapping(path = "/user/getprofile")
    public ResponseEntity getUserProfile(HttpServletRequest request){
        ResponseDto responseDto;
        UserProfileResDto resDto = userService.getUserProfile(request);
        if(resDto!=null)
            responseDto = new ResponseDto(new ResponseObject(200, "User profile fetched successfully",resDto),HttpStatus.OK);
        else
            responseDto = new ResponseDto(new ResponseObject(400,"Failed to fetch user profile",null),HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);

    }

    @PostMapping(path = "/user/get/profileimg", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity getUserProfileImage(HttpServletRequest request) throws IOException {
        byte[] image = userService.getUserProfileImage(request);
        if (image != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(image);
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("No image uploaded yet");
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

    @PostMapping(path = "/user/updaterole")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity updateUserRole(HttpServletRequest httpServletRequest, @RequestBody UserUpdateRoleReqDto dto){

        ResponseDto responseMessage;
        if(userAuthUtils.checkAccess(httpServletRequest)) {

            UserUpdateRoleRes userUpdateRoleRes=userService.updateRole(dto);

            if (null !=userUpdateRoleRes )
                responseMessage = new ResponseDto(new ResponseObject(200, "User Role changed successfully", userUpdateRoleRes),HttpStatus.OK);
            else
                responseMessage = new ResponseDto(new ResponseObject(400, "Failed to changed the Roles", null),HttpStatus.NOT_FOUND);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
        }
        responseMessage = new ResponseDto(new ResponseObject(401, "Access denied", null),HttpStatus.FORBIDDEN);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);

    }

    @PostMapping(path = "/user/update")
    public ResponseEntity editUser(@RequestParam(name = "file", required = false)MultipartFile file, @RequestParam("userReqDto")String userReqDto,HttpServletRequest request){
        ResponseDto responseMessage;
        if(userService.addUser(userReqDto,file,request))
            responseMessage = new ResponseDto(new ResponseObject(200, "User updated successfully.",null),HttpStatus.OK);
        else
            responseMessage = new ResponseDto(new ResponseObject(400,"User not updated.",null),HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

}
