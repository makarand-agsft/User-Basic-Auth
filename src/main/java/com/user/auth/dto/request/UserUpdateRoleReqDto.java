package com.user.auth.dto.request;

import com.user.auth.model.Role;

import java.util.List;

public class UserUpdateRoleReqDto {

    Long userId;
    List<Role> roleList;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
