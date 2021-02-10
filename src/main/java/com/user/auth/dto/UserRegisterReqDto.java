package com.user.auth.dto;

import com.user.auth.enums.AddressType;

import java.util.ArrayList;
import java.util.List;

public class UserRegisterReqDto {

	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private Long mobileNumber;
	
	private String city;

	private String state;

	private String addressString;

	private String country;

	private Long pincode;

	private AddressType addressType;

	private List<String> roles = new ArrayList<>();

	public AddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAddressString() {
		return addressString;
	}

	public void setAddressString(String addressString) {
		this.addressString = addressString;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
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
				", city='" + city + '\'' +
				", state='" + state + '\'' +
				", addressString='" + addressString + '\'' +
				", country='" + country + '\'' +
				", pincode=" + pincode +
				", roles=" + roles +
				'}';
	}
}
