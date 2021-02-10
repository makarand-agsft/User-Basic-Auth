package com.user.auth.controller;

import com.user.auth.dto.*;

import com.user.auth.dto.UserRegisterReqDto;
import com.user.auth.dto.request.ResetPasswordReqDto;
import com.user.auth.service.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.http.HttpResponse;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/user/add")
    public ResponseEntity addUser(@RequestParam(name = "file", required = false)MultipartFile file, @RequestParam("userReqDto")String userReqDto,HttpServletRequest request){
        ResponseDto responseMessage;
        if(userService.addUser(userReqDto,file,request))
            responseMessage = new ResponseDto(200, "User added successfully. Please check email for account activation",null);
        else
            responseMessage = new ResponseDto(400,"User already exists in system",null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }
    @PostMapping(path = "/user/login")
    public ResponseEntity loginUser(@RequestBody UserLoginReqDto dto){
        ResponseDto responseMessage;
        UserLoginResDto response = userService.loginUser(dto);
        if(response!=null)
            responseMessage = new ResponseDto(200,"Logged in successfully",response);
        else
            responseMessage = new ResponseDto(401,"Bad credentials", null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    @PostMapping(path = "/user/resetpassword")
    public ResponseEntity registerNewUser(@RequestBody ResetPasswordReqDto dto){
        ResponseDto responseMessage;
        if(null!=userService.resetPassword(dto))
            responseMessage = new ResponseDto(200, "User is Activated and changed password successfully",null);
        else
            responseMessage = new ResponseDto(400,"Failed to changed the password",null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    @PostMapping(path = "/user/add/profileimg")
    public ResponseEntity addProfileImage(@RequestParam(name = "file", required = false)MultipartFile file,HttpServletRequest request){
        ResponseDto responseMessage;
        if(userService.addProfileImage(file,request))
            responseMessage = new ResponseDto(200, "Profile image added successfully",null);
        else
            responseMessage = new ResponseDto(400,"Failed to add image",null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    @GetMapping(path = "/user/getprofile")
    public ResponseEntity getUserProfile(HttpServletRequest request){
        ResponseDto responseDto;
        UserProfileResDto resDto = userService.getUserProfile(request);
        if(resDto!=null)
            responseDto = new ResponseDto(200, "User profile fetched successfully",resDto);
        else
            responseDto = new ResponseDto(400,"Failed to fetch user profile",null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);

    }

    @PostMapping(path = "/user/profileimg", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity getUserProfileImage(HttpServletRequest request) throws IOException {
        byte[] image = userService.getUserProfileImage(request);
        if (image != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(image);
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("No image uploaded yet");
    }



    @GetMapping(value = "/user/getAllAdminUsers")
    public UserListResponseDto getAllAdminUsers(){
        return userService.getAllAdminUsers();
    }

    @PostMapping(path = "/user/edituser")
    public ResponseEntity editUser(@RequestParam(name = "file", required = false)MultipartFile file, @RequestParam("userReqDto")String userReqDto,HttpServletRequest request){
        ResponseDto responseMessage;
        if(userService.UpdateUser(userReqDto,file,request))
            responseMessage = new ResponseDto(200, "User updated successfully.",null);
        else
            responseMessage = new ResponseDto(400,"User not updated.",null);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

}
