package com.formz.model;


import com.formz.constants.TokenType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "token")
public class Token extends AuditingEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long tokenId;

	@Column(name = "token", columnDefinition = "text")
	private String token;

	@Column(name = "expiry_date")
	private Date expiryDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "token_type")
	private TokenType tokenType;

	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User users;

    @Column(name = "is_expired")
	private Boolean isExpired;

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

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public User getUsers() {
		return users;
	}

	public void setUsers(User users) {
		this.users = users;
	}

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }
	@Override
    public String toString() {
		return "Token{" + "tokenId=" + tokenId + ", token='" + token + '\'' + ", expiryDate=" + expiryDate + ", tokenType='" + tokenType + '\'' + ", users=" + users + '}';
	}
}
