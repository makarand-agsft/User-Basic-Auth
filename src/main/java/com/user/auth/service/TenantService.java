package com.user.auth.service;

import com.user.auth.dto.request.TenantDto;

import java.io.IOException;
import java.sql.SQLException;

public interface TenantService {

    void addTenant(TenantDto tenantDto) throws IOException, SQLException;
}
