package com.formz.controller;

import com.formz.constants.ApiStatus;
import com.formz.dto.*;
import com.formz.exception.BadRequestException;
import com.formz.service.FormDataService;
import com.itextpdf.text.DocumentException;
import com.sun.mail.iap.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping(value = "/add")
    public ResponseEntity addFormData(@RequestBody List<FormDataListDTO> formDataListDTO) throws IOException, DocumentException {
        ResponseDto responseDto = null;
        try {
            String requestId = RandomStringUtils.randomAlphanumeric(12);
            formDataService.addForms(formDataListDTO, requestId);
            responseDto = new ResponseDto(new ResponseObject(200, messageSource.getMessage("form.request.accepted.successfully", null, Locale.ENGLISH), "Your request id is : "+requestId), ApiStatus.SUCCESS);

        } catch (BadRequestException exception) {
            responseDto = new ResponseDto(new ResponseObject(200, exception.getMessage(), null), ApiStatus.FAILURE);
        }
        return ResponseEntity.ok().body(responseDto);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN')")
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

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping(value = "/download-pdf")
    public ResponseEntity downloadPdfByRequestId(@RequestParam(value = "requestId") String requestId, HttpServletResponse response) throws IOException, DocumentException {
        FileDTO fileDTO = null;
        ResponseDto responseDto = null;
        try {
            fileDTO = formDataService.downloadPDF(requestId);
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment; filename=" + fileDTO.getFileName());

        } catch (BadRequestException badRequestException) {
            responseDto = new ResponseDto(new ResponseObject(400, badRequestException.getMessage(), null), ApiStatus.FAILURE);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDto);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileDTO.getFileData());
    }


}
