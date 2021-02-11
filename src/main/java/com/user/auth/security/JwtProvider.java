package com.user.auth.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.auth.model.Role;
import com.user.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class is responsible for jwt token creation/validation
 * @author makarand 
 */
@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	public String secretKey;
	@Value("${jwt.tokenValidity}")
	public Long jwtTokenValidity;
	@Autowired
	ObjectMapper objectMapper;

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(User user) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", user.getRoles());
		claims.put("userName", user.getEmail());
		claims.put("firstName", user.getUserProfile().getFirstName());
		claims.put("lastName", user.getUserProfile().getLastName());
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
