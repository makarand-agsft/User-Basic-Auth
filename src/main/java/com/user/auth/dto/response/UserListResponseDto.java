package com.user.auth.dto.response;

import java.util.List;

public class UserListResponseDto {

	private  List<UserDto> userList;

	public List<UserDto> getUserList() {
		return userList;
	}

	public void setUserList(List<UserDto> userList) {
		this.userList = userList;
	}
}
