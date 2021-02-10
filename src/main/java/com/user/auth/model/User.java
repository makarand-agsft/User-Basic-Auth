package com.user.auth.model;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User extends AuditingEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long userId;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@ManyToMany(cascade= CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinTable(name="user_roles",
			joinColumns = {@JoinColumn(name="user_id", referencedColumnName="id")},
			inverseJoinColumns = {@JoinColumn(name="role_id", referencedColumnName="id")}
	)
	private List<Role> roles;

	@OneToMany(mappedBy = "users")
	private List<Token> tokens;

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
	private List<Address> addresses;

	@OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
	private UserProfile userProfile;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	@Override
	public String toString() {
		return "User{" +
				"userId=" + userId +
				", password='" + password + '\'' +
				'}';
	}
}
