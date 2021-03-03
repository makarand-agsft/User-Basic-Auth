package com.formz.repo;

import com.formz.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form,Long> {
    Optional<Form> findByName(String formName);
}
