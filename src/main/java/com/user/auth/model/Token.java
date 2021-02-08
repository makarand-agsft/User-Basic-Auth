package com.user.auth.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "token")
public class Token extends AuditingEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long tokenId;

	@Column(name = "token")
	private String token;
	
	@Column(name = "expiry_date")
	private Date expiryDate;
	
	@Column(name = "token_type")
	private String tokenType;

	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User users;

	public Long getTokenId() {
		return tokenId;
	}

	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public User getUsers() {
		return users;
	}

	public void setUsers(User users) {
		this.users = users;
	}

	@Override public String toString() {
		return "Token{" + "tokenId=" + tokenId + ", token='" + token + '\'' + ", expiryDate=" + expiryDate + ", tokenType='" + tokenType + '\'' + ", users=" + users + '}';
	}
}
