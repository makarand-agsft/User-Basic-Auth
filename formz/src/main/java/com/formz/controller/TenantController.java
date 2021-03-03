package com.formz.controller;

import com.formz.constants.ApiStatus;
import com.formz.dto.ResponseDto;
import com.formz.dto.ResponseObject;
import com.formz.dto.TenantDto;
import com.formz.exception.BadRequestException;
import com.formz.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;


@RestController
@RequestMapping(value = "/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN')")
    @PostMapping(value = "/add")
    public ResponseEntity addTenant(@RequestBody TenantDto tenantDto) throws IOException, SQLException {
        ResponseDto responseDto = null;
        try {
            tenantService.addTenant(tenantDto);
            responseDto = new ResponseDto(new ResponseObject(201, "Tenant Added", null), ApiStatus.SUCCESS);
            return ResponseEntity.ok("Added");
        } catch (BadRequestException exception) {
            responseDto = new ResponseDto(new ResponseObject(200, exception.getMessage(), null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
    }


}
