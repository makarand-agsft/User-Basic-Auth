package com.user.auth.dto.response;

import com.user.auth.dto.request.AddressDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class UserDto {
    private MultipartFile multipartFile;

    private Long userId;

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    private UserProfileDto userProfile;
    private String email;
    private List<AddressDto> addresses;
    private String token;
    private List<String> roles = new ArrayList<>();

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public UserProfileDto getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfileDto userProfile) {
        this.userProfile = userProfile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
