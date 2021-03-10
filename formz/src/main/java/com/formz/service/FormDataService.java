package com.formz.service;

import com.formz.dto.FileDTO;
import com.formz.dto.FormDataListDTO;
import com.formz.dto.RequestStatusDTO;

import java.io.IOException;
import java.util.List;

public interface FormDataService {

    String addForms(List<FormDataListDTO> formDataListDTO, String requestId) throws IOException;

    RequestStatusDTO checkRequestStatus(String requestId);

    FileDTO downloadPDF(String requestId) throws IOException;
}
