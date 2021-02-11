package com.user.auth.dto;

import com.user.auth.enums.AddressType;
import com.user.auth.model.Address;

import java.util.ArrayList;
import java.util.List;

public class UserRegisterReqDto {

	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private Long mobileNumber;
	
	private List<AddressDto> addresses;

	private List<String> roles = new ArrayList<>();

	public List<AddressDto> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressDto> addresses) {
		this.addresses = addresses;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(Long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "UserRegisterReqDto{" +
				"firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", email='" + email + '\'' +
				", mobileNumber=" + mobileNumber +
				", roles=" + roles +
				'}';
	}
}
