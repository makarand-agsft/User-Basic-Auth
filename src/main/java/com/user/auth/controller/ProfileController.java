package com.user.auth.controller;

import com.user.auth.constants.ApiStatus;
import com.user.auth.dto.request.UserUpdateRoleReqDto;
import com.user.auth.dto.response.*;
import com.user.auth.service.ProfileService;
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
 * This api represents an endpoint of user profile update features
 */
@RestController
@RequestMapping(value = "/user/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;
   
    /**
     * This method registers new user in system
     * @param
     * @return Success message is user adds successfully
     * @author aakash
     * @date 09/02/2021
     * @throws Exception
     */
    @PostMapping(path = "/add")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity addUser(
             @RequestBody UserDto userReqDto,
            HttpServletRequest request) {
        ResponseDto responseDto;
        profileService.addUser(userReqDto);
        responseDto =
                new ResponseDto(new ResponseObject(200, "User added successfully. Please check email for account activation", null), ApiStatus.SUCCESS);
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
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(path = "/add-profile-image") public ResponseEntity addProfileImage(
            @RequestParam(name = "file", required = false) MultipartFile file, HttpServletRequest request) {
        ResponseDto responseMessage;
        profileService.addProfileImage(file);
        responseMessage = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Profile image added successfully", null), ApiStatus.SUCCESS);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    /**
     * This method fetches all list of 'ADMIN' users
     * @author makarand
     * @date 10/02/21
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(value = "/getAllUsers") @ResponseBody public ResponseEntity getAllAdminUsers() {
        UserListResponseDto userListResponseDto = profileService.getAllUsers();
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Users fetched successfully", userListResponseDto),
                        ApiStatus.SUCCESS);
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
    @GetMapping(path = "/getProfile") public ResponseEntity getUserProfile(
            HttpServletRequest request) {
        UserDto resDto = profileService.getUserProfile();
        ResponseDto responseDto =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User profile fetched successfully", resDto), ApiStatus.SUCCESS);
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
    @PostMapping(path = "/get-profile-image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity getUserProfileImage(HttpServletRequest request) throws IOException {
        byte[] image = profileService.getUserProfileImage();
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
    @DeleteMapping(value = "/deleteUserById") public ResponseEntity deleteUserById(@RequestParam(value = "userId", required = true) Long userId)
            throws Exception {
        profileService.deleteUserById(userId);
        ResponseDto responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User deleted successfully", null), ApiStatus.SUCCESS);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    @PostMapping(path = "/updateRole")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity updateUserRole(HttpServletRequest httpServletRequest, @RequestBody UserUpdateRoleReqDto dto) {
        ResponseDto responseMessage;
        UserUpdateRoleRes userUpdateRoleRes = profileService.updateRole(dto);
        responseMessage =
                new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User Role updated successfully", userUpdateRoleRes), ApiStatus.SUCCESS);
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
    @PostMapping(path = "/update")
    public ResponseEntity editUser(@RequestParam(name = "file", required = false) MultipartFile file, @RequestParam("userReqDto") String userReqDto){
        ResponseDto responseMessage;
        profileService.updateUser(userReqDto,file);
        responseMessage = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User updated successfully.",null),ApiStatus.SUCCESS);
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
    @DeleteMapping(path = "/delete-profile-image")
    public ResponseEntity deleteProfileImage(HttpServletRequest request) {
        ResponseDto responseDto;
        profileService.deleteProfileImage();
        responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Profile picture deleted", null), ApiStatus.SUCCESS);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

}
