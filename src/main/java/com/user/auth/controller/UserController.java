package com.user.auth.controller;

import com.user.auth.dto.request.*;
import com.user.auth.dto.response.ResponseDto;
import com.user.auth.dto.response.ResponseObject;
import com.user.auth.dto.response.UserDto;
import com.user.auth.dto.response.UserListResponseDto;
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
     * @param
     * @return Success message is user adds successfully
     * @author aakash
     * @date 09/02/2021
     * @throws Exception
     */
    @PostMapping(path = "/user/add")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity addUser(@RequestParam(name = "file", required = false)MultipartFile file, @RequestParam("userReqDto")String userReqDto,HttpServletRequest request){
        ResponseDto responseMessage;
        if(userService.addUser(userReqDto,file,request))
            responseMessage = new ResponseDto(new ResponseObject(200, "User added successfully. Please check users email for account activation",null),HttpStatus.OK);
        else
            responseMessage = new ResponseDto(new ResponseObject(400,"User already exists in system",null),HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    /**
     *
     * @param forgotDto
     * @return
     * @throws Exception
     */
    @PostMapping(path = "user/forgotPassword")
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

    /**
     *
     * @param changePasswordDto
     * @param request
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(path = "/user/changePassword")
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
     * @param dto
     * @return user information including jwt token
     * @author aakash
     * @date 09/02/2021
     */
    @PostMapping(path = "/user/login") public ResponseEntity loginUser(@RequestBody UserLoginReqDto dto) {
        ResponseDto responseDto;
        UserDto response = userService.loginUser(dto);
        if(response != null)
            responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Logged in successfully", response), HttpStatus.OK);
        else
            responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Bad credentials", null),HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is an endpoint to reset user's password
     * @Author Akshay
     * @param dto containing user one time password and email address
     * @return Success message of user activation
     * @Date 09-02-2021
     */
    @PostMapping(path = "/user/resetPassword") public ResponseEntity registerNewUser(@RequestBody ResetPasswordReqDto dto) {
        UserDto response = userService.resetPassword(dto);
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User is Activated and changed password successfully", response),
                        HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }
    /**
     * This method is used to add profile image for user/admin in the system
     * @param request
     * @author aakash
     * @date 10/02/21
     * @return profile photo added message
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','User)")
    @PostMapping(path = "/user/add/profileImage")
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
     * @author makarand
     * @date 10/02/21
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(value = "/user/getAllAdminUsers") @ResponseBody public ResponseEntity getAllAdminUsers() {
        UserListResponseDto userListResponseDto = userService.getAllAdminUsers();
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "All users are fetched", userListResponseDto),
                        HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }
    /**
     * This method is used to fetch details for user/admin in the system
     * @param request
     * @author aakash
     * @date 10/02/21
     * @return user's profile details
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping(path = "/user/getProfile")
    public ResponseEntity getUserProfile(HttpServletRequest request){
        ResponseDto responseDto;
        UserDto resDto = userService.getUserProfile();
        if(resDto!=null)
            responseDto = new ResponseDto(new ResponseObject(200, "User profile fetched successfully",resDto),HttpStatus.OK);
        else
            responseDto = new ResponseDto(new ResponseObject(400,"Failed to fetch user profile",null),HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);

    }
    /**
     * This method is used get profile picture of user.
     * @param request
     * @author aakash
     * @date 10/02/21
     * @return profile photo
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(path = "/user/get/profileImage", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping(value = "user/deleteUserById") public ResponseEntity deleteUserById(@RequestParam(value = "userId", required = true) Long userId)
            throws Exception {
        userService.deleteUserById(userId);
        ResponseDto responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User deleted successfully", null), HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is used to change the roles of users
     * @author Akshay
     * @param httpServletRequest
     * @param dto
     * @return
     * Date :09-02-2021
     */
    @PostMapping(path = "/user/updateRole")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity updateUserRole(HttpServletRequest httpServletRequest, @RequestBody UserUpdateRoleReqDto dto){

        ResponseDto responseMessage;
        if (userAuthUtils.checkAccess(httpServletRequest)) {
            if (null != userService.updateRole(dto))
                responseMessage = new ResponseDto(new ResponseObject(200, "User Role changed successfully", null), HttpStatus.OK);
            else
                responseMessage = new ResponseDto(new ResponseObject(400, "Failed to changed the Roles", null), HttpStatus.NOT_FOUND);

        } else {
            responseMessage = new ResponseDto(new ResponseObject(401, "Access denied", null), HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);

    }
    /**
     * This method is used to update details for user/admin in the system
     * @param file, request
     * @author aakash
     * @date 10/02/21
     * @return User updated message
     * @throws Exception
     */
    @PostMapping(path = "/user/update")
    public ResponseEntity editUser(@RequestParam(name = "file", required = false)MultipartFile file, @RequestParam("userReqDto")String userReqDto,HttpServletRequest request){
        ResponseDto responseMessage;
        if(userService.addUser(userReqDto,file,request))
            responseMessage = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User updated successfully.",null),HttpStatus.OK);
        else
            responseMessage = new ResponseDto(new ResponseObject(400,"User not updated.",null),HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    /**
     * This method is used to delete profile image of user/admin
     * @param
     * @author aakash
     * @date 10/02/21
     * @return Photo deleted message
     * @throws Exception
     */
    @DeleteMapping(path = "/user/delete/profileImage")
    public ResponseEntity deleteProfileImage(HttpServletRequest request){
        ResponseDto responseDto;
        boolean deleted = userService.deleteProfileImage(request);
        if(deleted)
            responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(),"Profile picture deleted",null),HttpStatus.OK);
        else
            responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Profile picture not found",null),HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    @PostMapping(path = "/user/logout")
    public ResponseEntity logout(HttpServletRequest httpServletRequest){
        ResponseDto responseDto;
        userService.logout(httpServletRequest);
            responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(),"Logged out successfully",null),HttpStatus.OK);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

}
