package com.formz.repo;

import com.formz.model.Role;
import com.formz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRoles(Role role);

    User findByEmailAndPassword(String userName, String password);
}
