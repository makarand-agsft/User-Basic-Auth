package com.user.auth.dto;

import com.user.auth.model.Address;
import com.user.auth.model.UserProfile;

import java.util.List;

public class UserLoginResDto {
    private Long userId;
    private UserProfileDto userProfile;
    private String email;
    private List<AddressDto> addresses;
    private String token;

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
