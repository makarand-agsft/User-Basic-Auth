package com.formz.controller;

import com.formz.dto.TenantDto;
import com.formz.service.impl.TenantService;
import com.formz.service.impl.TenantServiceImpl;
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
        tenantService.addTenant(tenantDto);

        return ResponseEntity.ok("Added");
    }

}
