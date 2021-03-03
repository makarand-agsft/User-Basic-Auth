package com.formz.controller;

import com.formz.constants.ApiStatus;
import com.formz.dto.FormDataListDTO;
import com.formz.dto.RequestStatusDTO;
import com.formz.dto.ResponseDto;
import com.formz.dto.ResponseObject;
import com.formz.exception.BadRequestException;
import com.formz.service.FormDataService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(value = "/form-data")
public class FormDataController {

    @Autowired
    private FormDataService formDataService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping(value = "/add")
    public ResponseEntity addFormData(@RequestBody List<FormDataListDTO> formDataListDTO) throws IOException, DocumentException {
    String requestId = formDataService.addForms(formDataListDTO);
    return ResponseEntity.ok(requestId);
    }

    @PostMapping(value = "/check-request-status")
    public ResponseEntity checkRequestStatusById(@RequestParam(value = "requestId") String requestId) throws IOException, DocumentException {
        ResponseDto responseDto = null;

        try {
            RequestStatusDTO requestStatusDTO =formDataService.checkRequestStatus(requestId);
            responseDto = new ResponseDto(new ResponseObject(200, messageSource.getMessage("request.status.fetched.successfully", null, Locale.ENGLISH), requestStatusDTO), ApiStatus.SUCCESS);
        } catch (BadRequestException badRequestException) {
            responseDto = new ResponseDto(new ResponseObject(400, badRequestException.getMessage(), null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping(value = "/download-pdf")
    public ResponseEntity downloadPdfByRequestId(@RequestParam(value = "requestId") String requestId, HttpServletResponse response) throws IOException, DocumentException {
        ResponseDto responseDto = null;
        byte[] fileData=null;
        try {
             fileData=formDataService.downloadPDF(requestId);
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment; filename="+ "abc.pdf");
            responseDto = new ResponseDto(new ResponseObject(200, messageSource.getMessage("request.status.fetched.successfully", null, Locale.ENGLISH), fileData), ApiStatus.SUCCESS);
        } catch (BadRequestException badRequestException) {
            responseDto = new ResponseDto(new ResponseObject(400, badRequestException.getMessage(), null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().contentLength(fileData.length).contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileData);
    }


}
