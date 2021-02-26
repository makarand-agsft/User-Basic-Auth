package com.user.auth.controller;

import com.itextpdf.text.DocumentException;
import com.user.auth.constants.ApiStatus;
import com.user.auth.dto.request.TenantDto;
import com.user.auth.dto.response.ResponseDto;
import com.user.auth.dto.response.ResponseObject;
import com.user.auth.exception.InvalidRequestException;
import com.user.auth.multitenancy.MultiTenantDataSourceConfig;
import com.user.auth.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@RestController
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @PostMapping(path = "/add/tenant")
    public ResponseEntity addTenant(@RequestBody TenantDto userDto) throws IOException, SQLException {

        ResponseDto responseDto;

        try {
            tenantService.addTenant(userDto);
            responseDto = new ResponseDto(new ResponseObject(200, "Tenant added successfully", null), ApiStatus.SUCCESS);
        } catch (InvalidRequestException exception) {
            multiTenantDataSourceConfig.getAnyConnection().createStatement().execute("DROP DATABASE "+userDto.getTenantName());
            responseDto = new ResponseDto(new ResponseObject(201, exception.getMessage(), null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }

    @PostMapping(path = "/convertToPDF")
    public String convertToPDF() throws DocumentException, com.lowagie.text.DocumentException, IOException {
        return tenantService.convert();
    }

}
