package com.user.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfile extends JpaRepository<UserProfile,Long>{
}
