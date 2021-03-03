package com.formz.service;

import com.formz.dto.FormDataListDTO;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface FormDataService {

    public String addForms(List<FormDataListDTO> formDataListDTO) throws IOException, DocumentException;
}
