package com.formz.service.impl;

import com.formz.dto.FieldDataDTO;
import com.formz.dto.FormDataListDTO;
import com.formz.dto.UserDataRequestDTO;
import com.formz.exception.BadRequestException;
import com.formz.model.Field;
import com.formz.model.Form;
import com.formz.model.FormPage;
import com.formz.repo.FieldRepository;
import com.formz.repo.FormFieldRepository;
import com.formz.repo.FormPageRepository;
import com.formz.repo.FormRepository;
import com.formz.service.FormDataService;
import com.formz.utils.FormzUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

@Service
public class FormDataServiceImpl implements FormDataService {

    HashMap<String, Object> dataMap = new HashMap<>();

    @Autowired
    private FormzUtils formzUtils;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private FormFieldRepository formFieldRepository;

    @Autowired
    private FormPageRepository formPageRepository;

    PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();


    @Override
    public String addForms(List<FormDataListDTO> formDataListDTO) throws IOException, DocumentException {
        if (formDataListDTO == null) {
            throw new BadRequestException("Invalid request");

        }
        for (FormDataListDTO formDataList : formDataListDTO) {
            Form form = formRepository.findByName(formDataList.getFormName()).orElseThrow(() -> new BadRequestException("Form not found"));
            //user data list
            int formId = 1;
            for (UserDataRequestDTO userData : formDataList.getUserDataList()) {
                HashMap<String, Object> userDataMap = new HashMap<>();
                //field list
                for (FieldDataDTO userFieldData : userData.getFieldDataList()) {
                    //validate fields here
                    Field field = fieldRepository.findByFieldName(userFieldData.getFieldName()).orElseThrow(() ->
                            new BadRequestException("Field not found"));
                    userDataMap.put(field.getFieldName(), userFieldData.getFieldValue());
                }
                //retrieve form pages and template location for each page
                List<FormPage> formPages = formPageRepository.findByForm(form);
                for (FormPage formPage : formPages) {
                    String location = formPage.getTemplateLocation();
                    String html = formzUtils.getTemplatetoText(location, userDataMap);
                    byte[] formPageBytes = new String(html.getBytes(), Charset.defaultCharset().name()).getBytes();
                    convertToPDF(formPageBytes, form.getName() + formPage.getPageNo(), formId);
                }
                formId++;
            }
        }
        File mainPDF = new File("src/main/resources/main.pdf");
        if (!mainPDF.exists()) {
            mainPDF.createNewFile();
        }
        pdfMergerUtility.setDestinationFileName(mainPDF.getAbsolutePath());
        MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMainMemoryOnly();
        pdfMergerUtility.mergeDocuments(memoryUsageSetting);
        //set request status as
        return null;
    }

    private boolean convertToPDF(byte[] pdfData, String pageName, int formId) throws IOException, DocumentException {
        File file = new File("src/main/resources/" + pageName + formId + ".pdf");
        if (!file.exists())
            file.createNewFile();
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayInputStream templateInputStream = new ByteArrayInputStream(pdfData);
        FileOutputStream fos = new FileOutputStream(file);
        PdfWriter writer = PdfWriter.getInstance(document, fos);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, templateInputStream, XMLWorkerHelper.class.getResourceAsStream("/default.css"));
        document.close();
        pdfMergerUtility.addSource(file);
        file.delete();
        return false;
    }
}
