package com.user.auth.dto.response;

import com.user.auth.model.Role;

import java.util.List;

public class UserUpdateRoleRes {

    private String email;

    private List<String> roleList;

    public UserUpdateRoleRes(String email, List<String> roleList) {
        this.email = email;
        this.roleList = roleList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }
}
