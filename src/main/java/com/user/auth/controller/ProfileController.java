package com.user.auth.controller;

import com.user.auth.constants.ApiStatus;
import com.user.auth.dto.request.UserUpdateRoleReqDto;
import com.user.auth.dto.response.*;
import com.user.auth.exception.InvalidEmailException;
import com.user.auth.exception.InvalidRequestException;
import com.user.auth.exception.UserNotFoundException;
import com.user.auth.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

/**
 * This api represents an endpoint of user profile update features
 */
@RestController
@RequestMapping(value = "/user/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;


    @Autowired
    private MessageSource messageSource;

    /**
     * This method registers new user in system
     *
     * @param
     * @return Success message is user adds successfully
     * @throws Exception
     * @author aakash
     * @date 09/02/2021
     */
    @PostMapping(path = "/add")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity addUser(
            @RequestParam(name = "file", required = false) MultipartFile file, @RequestParam("userReqDto") String userReqDto,
            HttpServletRequest request) {
        ResponseDto responseDto = null;
        try {
            profileService.addUser(userReqDto,file);
            responseDto =
                    new ResponseDto(new ResponseObject(200, messageSource.getMessage("add.user.successful", null, Locale.ENGLISH), null), ApiStatus.SUCCESS);
        } catch (InvalidEmailException exception) {
            responseDto =
                    new ResponseDto(new ResponseObject(201, exception.getMessage(), null), ApiStatus.FAILURE);
        } catch (Exception exception) {
            responseDto =
                    new ResponseDto(new ResponseObject(201, exception.getMessage(), null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is used to add profile image for user/admin in the system
     *
     * @param request
     * @return profile photo added message
     * @throws Exception
     * @author aakash
     * @date 10/02/21
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping(path = "/add-profile-image")
    public ResponseEntity addProfileImage(
            @RequestParam(name = "file", required = false) MultipartFile file, HttpServletRequest request) {
        ResponseDto responseMessage;
        try {
            profileService.addProfileImage(file);
            responseMessage = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), messageSource.getMessage("profile.image.added.successfully", null, Locale.ENGLISH), null), ApiStatus.SUCCESS);
        } catch (UserNotFoundException exception) {
            responseMessage = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Profile image added successfully", null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    /**
     * This method fetches all list of 'ADMIN' users
     *
     * @return
     * @author makarand
     * @date 10/02/21
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(value = "/getAllUsers")
    @ResponseBody
    public ResponseEntity getAllAdminUsers() {
        ResponseDto responseDto = null;
        try {
            UserListResponseDto userListResponseDto = profileService.getAllUsers();
            responseDto =
                    new ResponseDto(new ResponseObject(HttpStatus.OK.value(), messageSource.getMessage("fetch.user.successfully", null, Locale.ENGLISH), userListResponseDto),
                            ApiStatus.SUCCESS);
        } catch (InvalidRequestException exception) {
            responseDto =
                    new ResponseDto(new ResponseObject(201, exception.getMessage(), null),
                            ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    /**
     * This method is used to fetch details for user/admin in the system
     *
     * @param request
     * @return user's profile details
     * @throws Exception
     * @author aakash
     * @date 10/02/21
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping(path = "/getProfile")
    public ResponseEntity getUserProfile(
            HttpServletRequest request) {
        ResponseDto responseDto = null;
        try {
            UserDto resDto = profileService.getUserProfile();
             responseDto =
                    new ResponseDto(new ResponseObject(200, messageSource.getMessage("user.profile.fetched",null,Locale.ENGLISH), resDto), ApiStatus.SUCCESS);
        }catch (UserNotFoundException exception){
             responseDto =
                    new ResponseDto(new ResponseObject(201, exception.getMessage(), null), ApiStatus.FAILURE);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);

    }

    /**
     * This method is used get profile picture of user.
     *
     * @param request
     * @return profile photo
     * @throws Exception
     * @author aakash
     * @date 10/02/21
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
     *
     * @param userId
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping(value = "/deleteUserById")
    public ResponseEntity deleteUserById(@RequestParam(value = "userId", required = true) Long userId)
            throws Exception {
        ResponseDto responseDto = null;
        try {
             responseDto = new ResponseDto(new ResponseObject(200, messageSource.getMessage("user.deleted.successfully",null,Locale.ENGLISH), null), ApiStatus.SUCCESS);
            profileService.deleteUserById(userId);
        }catch (InvalidRequestException exception){
             responseDto = new ResponseDto(new ResponseObject(201, exception.getMessage(), null), ApiStatus.FAILURE);
        }
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
     *
     * @param file, request
     * @return User updated message
     * @throws Exception
     * @author aakash
     * @date 10/02/21
     */
    @PostMapping(path = "/update")
    public ResponseEntity editUser(@RequestParam(name = "file", required = false) MultipartFile file, @RequestParam("userReqDto") String userReqDto) {
        ResponseDto responseMessage;
        profileService.updateUser(userReqDto, file);
        responseMessage = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "User updated successfully.", null), ApiStatus.SUCCESS);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMessage);
    }

    /**
     * This method is used to delete profile image of user/admin
     *
     * @param
     * @return Photo deleted message
     * @throws Exception
     * @author aakash
     * @date 10/02/21
     */
    @DeleteMapping(path = "/delete-profile-image")
    public ResponseEntity deleteProfileImage(HttpServletRequest request) {
        ResponseDto responseDto;
        profileService.deleteProfileImage();
        responseDto = new ResponseDto(new ResponseObject(HttpStatus.OK.value(), "Profile picture deleted", null), ApiStatus.SUCCESS);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

}
