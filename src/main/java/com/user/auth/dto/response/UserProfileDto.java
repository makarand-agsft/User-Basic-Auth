package com.user.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.user.auth.model.AuditingEntity;
import com.user.auth.model.User;

import javax.persistence.*;

public class UserProfileDto extends AuditingEntity {
    private Long id;

    private String firstName;

    private String lastName;

    private Long mobileNumber;

    private Boolean isActive;

    private String profilePicture;


    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }


}
