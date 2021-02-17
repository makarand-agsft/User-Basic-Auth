package com.user.auth.repository;

import com.user.auth.model.MasterUser;
import com.user.auth.model.Role;
import com.user.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterUserRepository extends JpaRepository<MasterUser, Long> {
}
