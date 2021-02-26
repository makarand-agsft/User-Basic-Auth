package com.user.auth.service;

import com.itextpdf.text.DocumentException;
import com.user.auth.dto.request.TenantDto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

public interface TenantService {

    void addTenant(TenantDto tenantDto) throws IOException, SQLException;

    String convert() throws IOException, DocumentException, com.lowagie.text.DocumentException;
}
