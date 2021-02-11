package com.user.auth.repository;

import com.user.auth.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.auth.model.Token;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByTokenAndUsersUserId(String token,Long userId);

    Optional<Token> findByToken(String token);

    @Query("from Token where expiryDate >= :date")
    List<Token> findAllExpired(@Param("date") Date date);
}
