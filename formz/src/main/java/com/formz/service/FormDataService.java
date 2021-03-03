package com.formz.service;

import com.formz.dto.FormDataListDTO;
import com.formz.dto.RequestStatusDTO;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;

public interface FormDataService {

    String addForms(List<FormDataListDTO> formDataListDTO) throws IOException, DocumentException;

    RequestStatusDTO checkRequestStatus(String requestId);

    byte[] downloadPDF(String requestId) throws IOException;
}
