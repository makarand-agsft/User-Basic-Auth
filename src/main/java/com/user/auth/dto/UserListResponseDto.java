package com.user.auth.dto;

import java.util.List;

public class UserListResponseDto {

	private  List<UserRegisterReqDto> userList;

	public List<UserRegisterReqDto> getUserList() {
		return userList;
	}

	public void setUserList(List<UserRegisterReqDto> userList) {
		this.userList = userList;
	}
}
