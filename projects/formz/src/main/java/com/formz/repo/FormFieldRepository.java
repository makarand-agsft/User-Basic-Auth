package com.formz.repo;

import com.formz.model.Form;
import com.formz.model.FormField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormFieldRepository extends JpaRepository<FormField,Long> {
    List<FormField> findByForm(Form form);
}
