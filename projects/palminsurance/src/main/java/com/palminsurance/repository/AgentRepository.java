package com.palminsurance.repository;

import com.palminsurance.model.Agent;
import com.palminsurance.model.Policy;
import com.palminsurance.model.Suspense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByPolicy(Policy policy);
}
