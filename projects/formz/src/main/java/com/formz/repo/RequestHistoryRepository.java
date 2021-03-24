package com.formz.repo;

import com.formz.model.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory,Long> {
    Optional<RequestHistory> findByRequestId(String requestId);
}
