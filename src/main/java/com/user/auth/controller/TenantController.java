package com.user.auth.controller;

import com.user.auth.constants.ApiStatus;
import com.user.auth.dto.request.TenantDto;
import com.user.auth.dto.response.ResponseDto;
import com.user.auth.dto.response.ResponseObject;
import com.user.auth.exception.InvalidRequestException;
import com.user.auth.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/add")
    public ResponseEntity addTenant(@RequestBody TenantDto tenantDto) throws IOException, SQLException {
        ResponseDto responseDto = null;
        try {
            tenantService.addTenant(tenantDto);
            responseDto = new ResponseDto(new ResponseObject(200, "Tenant Addedd successfully", null), ApiStatus.SUCCESS);
        } catch (InvalidRequestException invalidRequestException) {
            responseDto = new ResponseDto(new ResponseObject(200, invalidRequestException.getMessage(), null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok(responseDto);
    }
}
