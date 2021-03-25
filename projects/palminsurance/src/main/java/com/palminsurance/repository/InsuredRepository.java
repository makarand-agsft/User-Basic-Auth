package com.palminsurance.repository;

import com.palminsurance.model.Agent;
import com.palminsurance.model.Insured;
import com.palminsurance.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsuredRepository extends JpaRepository<Insured, Long> {
    Optional<Insured> findByPolicy(Policy policy);
}
