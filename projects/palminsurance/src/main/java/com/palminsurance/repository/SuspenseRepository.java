package com.palminsurance.repository;

import com.palminsurance.model.Policy;
import com.palminsurance.model.Suspense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuspenseRepository extends JpaRepository<Suspense, Long> {
    Optional<Suspense> findByPolicy(Policy policy);
}
