package com.formz.controller;

import com.formz.dto.FormDataListDTO;
import com.formz.service.FormDataService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/form-data")
public class FormDataController {

    @Autowired
    private FormDataService formDataService;

    @PostMapping(value = "/add")
    public ResponseEntity addFormData(@RequestBody List<FormDataListDTO> formDataListDTO) throws IOException, DocumentException {
    String requestId = formDataService.addForms(formDataListDTO);
    return ResponseEntity.ok(requestId);
    }
}
