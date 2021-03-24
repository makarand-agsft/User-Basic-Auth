package com.formz.service;

import com.formz.dto.TenantDto;

import java.io.IOException;
import java.sql.SQLException;

public interface TenantService {
    public void addTenant(TenantDto tenantDto) throws SQLException, IOException;
}
