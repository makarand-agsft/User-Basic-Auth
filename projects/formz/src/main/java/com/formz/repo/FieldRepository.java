package com.formz.repo;

import com.formz.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldRepository extends JpaRepository<Field,Long> {
    Optional<Field> findByFieldName(String fieldName);
}
