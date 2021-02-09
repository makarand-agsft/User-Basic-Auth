package com.user.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.auth.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

}
