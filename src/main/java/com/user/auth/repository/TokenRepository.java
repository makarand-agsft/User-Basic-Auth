package com.user.auth.repository;

import com.user.auth.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.auth.model.Token;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByTokenAndTokenTypeOrTokenTypeAndUsersUserId(String token, TokenType resetToken,TokenType forgetToken, Long userId);

    Optional<Token> findByToken(String token);
}
