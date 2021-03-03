package com.formz.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formz.constants.RequestStatus;
import com.formz.dto.FieldDataDTO;
import com.formz.dto.FormDataListDTO;
import com.formz.dto.RequestStatusDTO;
import com.formz.dto.UserDataRequestDTO;
import com.formz.exception.BadRequestException;
import com.formz.model.Field;
import com.formz.model.Form;
import com.formz.model.FormPage;
import com.formz.model.RequestHistory;
import com.formz.multitenancy.TenantContext;
import com.formz.repo.*;
import com.formz.service.FormDataService;
import com.formz.utils.FormzUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    @Autowired
    private RequestHistoryRepository requestHistoryRepository;

    @Autowired
    private MessageSource messageSource;

    @Value("${application.directory.base.path}")
    private String applicationDirectoryBasePath;

    @Autowired
    private ModelMapper modelMapper;

    private PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();


    @Override
    public String addForms(List<FormDataListDTO> formDataListDTO) throws IOException, DocumentException {
        if (formDataListDTO == null) {
            throw new BadRequestException("Invalid request");
        }
        RequestHistory requestHistory = new RequestHistory();
        String requestId = RandomStringUtils.randomAlphanumeric(12);
        File jsonFile = saveJsonFile(formDataListDTO, requestId);
        requestHistory.setRequestId(requestId);
        requestHistory.setRequestStatus(RequestStatus.PROCESSING);
        requestHistory.setRequestJson(jsonFile.getAbsolutePath());
        requestHistory = requestHistoryRepository.save(requestHistory);

        for (FormDataListDTO formDataList : formDataListDTO) {
            Optional<Form> form = formRepository.findByName(formDataList.getFormName());
            if (!form.isPresent()) {
                requestHistory.setRequestStatus(RequestStatus.FAILED);
                requestHistoryRepository.save(requestHistory);
                throw new BadRequestException
                        (String.format("Form [%s] not found: [Request Id: ]", formDataList.getFormName(), requestId));
            }
            //user data list
            int formId = 1;
            for (UserDataRequestDTO userData : formDataList.getUserDataList()) {
                HashMap<String, Object> userDataMap = new HashMap<>();
                //field list
                for (FieldDataDTO userFieldData : userData.getFieldDataList()) {
                    //validate fields here
                    Optional<Field> field = fieldRepository.findByFieldName(userFieldData.getFieldName());
                    if (!field.isPresent()) {
                        requestHistory.setRequestStatus(RequestStatus.FAILED);
                        requestHistoryRepository.save(requestHistory);
                        throw new BadRequestException(String.format
                                ("Field name[%s] is not valid: [Request Id: %s]", userFieldData.getFieldName(), requestId));
                    }
                    userDataMap.put(field.get().getFieldName(), userFieldData.getFieldValue());

                }
                //retrieve form pages and template location for each page
                List<FormPage> formPages = formPageRepository.findByForm(form.get());
                for (FormPage formPage : formPages) {
                    String location = formPage.getTemplateLocation();
//                    File file = new File(location);
//                    if(!file.exists()){
//                        throw new BadRequestException("Template "+location.substring(location.lastIndexOf('/')+1)+" not found");
//                    }
                    String html = formzUtils.getTemplatetoText(location, userDataMap);
                    byte[] formPageBytes = new String(html.getBytes(), Charset.defaultCharset().name()).getBytes();
                    convertToPDF(formPageBytes, form.get().getName() + formPage.getPageNo(), formId);
                }
                formId++;
            }
        }
        File mainPDF = new File(applicationDirectoryBasePath + "/" + TenantContext.getCurrentTenant() + "/pdfs/" + requestId + ".pdf");
        if (!mainPDF.exists()) {
            mainPDF.createNewFile();
        }
        pdfMergerUtility.setDestinationFileName(mainPDF.getAbsolutePath());
        MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMainMemoryOnly();
        pdfMergerUtility.mergeDocuments(memoryUsageSetting);
        //set request status as generated
        requestHistory.setRequestStatus(RequestStatus.GENERATED);
        requestHistory.setPdfDownloadPath(mainPDF.getAbsolutePath());
        requestHistoryRepository.save(requestHistory);

        return requestId;
    }

    private File saveJsonFile(List<FormDataListDTO> formDataListDTO, String requestId) throws IOException {
        File jsonFile = new File(applicationDirectoryBasePath + "/" + TenantContext.getCurrentTenant() + "/request-json-strings/" + requestId + ".json");
        ObjectMapper mapper = new ObjectMapper();
        if (!jsonFile.exists())
            jsonFile.createNewFile();
        FileWriter writer = new FileWriter(jsonFile);
        writer.write(mapper.writeValueAsString(formDataListDTO));
        writer.close();
        return jsonFile;
    }

    private boolean convertToPDF(byte[] pdfData, String pageName, int formId) throws IOException, DocumentException {
        File file = new File("src/main/resources/" + pageName + formId + ".pdf");
        if (!file.exists())
            file.createNewFile();
        Document document = new Document(PageSize.A4);
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

    @Override
    public RequestStatusDTO checkRequestStatus(String requestId) {
        if (requestId == null || requestId.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("invalid.request", null, Locale.ENGLISH));
        }
        RequestHistory requestHistory = requestHistoryRepository.
                findByRequestId(requestId).orElseThrow(() -> new BadRequestException("invalid request id"));
        RequestStatusDTO requestStatusDTO = modelMapper.map(requestHistory, RequestStatusDTO.class);
        requestStatusDTO.setLastUpdatedAt(requestHistory.getLastModifiedDate());
        requestStatusDTO.setRequestStatus(requestHistory.getRequestStatus().getValue());
        return requestStatusDTO;
    }

    @Override
    public byte[] downloadPDF(String requestId) throws IOException {
        if (requestId == null || requestId.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("invalid.request", null, Locale.ENGLISH));
        }
        RequestHistory requestHistory = requestHistoryRepository.
                findByRequestId(requestId).orElseThrow(() -> new BadRequestException("invalid request id"));

        String pdfDownloadPath = requestHistory.getPdfDownloadPath();

        if (pdfDownloadPath == null && !new File(pdfDownloadPath).exists()) {
            throw new BadRequestException(messageSource.getMessage("pdf.file.not.found", null, Locale.ENGLISH));
        }
        byte[] pdfFileData = Files.readAllBytes(Paths.get(pdfDownloadPath));
        File file = new File(pdfDownloadPath);
        file.delete();
        return pdfFileData;
    }


}
