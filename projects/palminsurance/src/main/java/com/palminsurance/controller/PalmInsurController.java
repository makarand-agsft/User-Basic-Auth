package com.palminsurance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palminsurance.dto.FormDTO;
import com.palminsurance.service.PalmInsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/palm-insurance")
public class PalmInsurController {

    @Autowired
    private PalmInsuranceService palmInsuranceService;

    @PostMapping(value = "/generatePDF")
    public void generatePDF(@RequestBody FormDTO formDto) throws JsonProcessingException {

        palmInsuranceService.generatePDF(formDto);


    }
}
