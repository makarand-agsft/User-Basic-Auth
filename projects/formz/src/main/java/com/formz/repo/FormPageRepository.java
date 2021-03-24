package com.formz.repo;

import com.formz.model.Form;
import com.formz.model.FormPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormPageRepository extends JpaRepository<FormPage, Long> {
    List<FormPage> findByForm(Form form);
}
