package com.formz.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.formz.model.Role;
import com.formz.model.Token;
import com.formz.model.User;
import com.formz.repo.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	public String secretKey;
	@Value("${jwt.tokenValidity}")
	public Long jwtTokenValidity;
	@Autowired
	ObjectMapper objectMapper;

	Logger log = LoggerFactory.getLogger(JwtProvider.class);

	@Autowired
	private TokenRepository tokenRepository;

	private Boolean isTokenExpired(String token) {

			Claims claims = getClaimFromToken(token);
			Optional<Token> authToken = tokenRepository.findByToken(token);
			if (authToken.isPresent()) {
				if (authToken.get().getExpired() || authToken.get().getExpiryDate().getTime() < new Date().getTime()) {
					throw new ExpiredJwtException(null, claims, "Session Expired,Please login again");
				}
			}
			return false;


	}

	public String generateToken(User user) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", user.getRoles());
		claims.put("userName", user.getEmail());
		claims.put("name", user.getName());
		return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1000))
				.signWith(SignatureAlgorithm.HS256, secretKey).compact();
	}

	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = getClaimFromToken(token);
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}

	public Claims getClaimFromToken(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public String getUsernameFromToken(String token) {

		String username;
		try {
			final Claims claims = getClaimFromToken(token);
			username = (String) claims.get("userName");
		} catch (Exception e) {
			username = null;
		}
		return username;

	}

	public List<Role> getRolesfromToken(String token) {
		try {
			final Claims claims = getClaimFromToken(token);
			return  objectMapper.convertValue(claims.get("roles"), new TypeReference<ArrayList<Role>>() {});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
