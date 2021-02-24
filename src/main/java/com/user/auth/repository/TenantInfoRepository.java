package com.user.auth.repository;

import com.user.auth.model.TenantInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantInfoRepository extends JpaRepository<TenantInfo,Long> {

}
