package com.formz.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formz.constants.RequestStatus;
import com.formz.dto.*;
import com.formz.exception.BadRequestException;
import com.formz.model.Field;
import com.formz.model.Form;
import com.formz.model.FormPage;
import com.formz.model.RequestHistory;
import com.formz.multitenancy.TenantContext;
import com.formz.repo.*;
import com.formz.service.FormDataService;
import com.formz.utils.FormzUtils;
import com.itextpdf.html2pdf.HtmlConverter;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is responsible for providing form data operations
 */
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


    /**
     * This method accepts form request data and generates its pdf via velocity template
     *
     * @param formDataListDTO
     * @param requestId
     * @return
     * @throws IOException
     */
    @Async
    @Override
    public String addForms(List<FormDataListDTO> formDataListDTO, String requestId) throws IOException {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        if (formDataListDTO == null) {
            throw new BadRequestException("Invalid request");
        }
        RequestHistory requestHistory = new RequestHistory();

        File jsonFile = saveJsonFile(formDataListDTO, requestId);
        requestHistory.setRequestId(requestId);
        requestHistory.setRequestStatus(RequestStatus.PROCESSING);
        requestHistory.setRequestJson(jsonFile.getAbsolutePath());
        requestHistory = requestHistoryRepository.save(requestHistory);

        for (FormDataListDTO formDataList : formDataListDTO) {
            Optional<Form> form = formRepository.findByName(formDataList.getFormName());
            if (!form.isPresent()) {
                requestHistory.setRequestStatus(RequestStatus.FAILED);
                String failedCause = String.format("Form [%s] not found: [Request Id: ]", formDataList.getFormName(), requestId);
                requestHistory.setResult(failedCause);
                requestHistoryRepository.save(requestHistory);
                throw new BadRequestException(failedCause);
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
                        String failedCause = String.format
                                ("Field name[%s] is not valid: [Request Id: %s]", userFieldData.getFieldName(), requestId);
                        requestHistory.setResult(failedCause);
                        requestHistoryRepository.save(requestHistory);
                        throw new BadRequestException(failedCause);
                    }
                    if (userFieldData.getFieldValue() == null)
                        userFieldData.setFieldValue("NONE");
                    userDataMap.put(field.get().getFieldName(), userFieldData.getFieldValue());

                }

                userDataMap.put("currentDate", getFormatedDate());
                //retrieve form pages and template location for each page
                List<FormPage> formPages = formPageRepository.findByForm(form.get());
                for (FormPage formPage : formPages) {
                    String location = formPage.getTemplateLocation();
                    InputStream is = FormDataServiceImpl.class.getClassLoader().getResourceAsStream(location);
                    if (is == null || is.available() == 0) {
                        requestHistory.setRequestStatus(RequestStatus.FAILED);
                        String failedCause = "Template " + location.substring(location.lastIndexOf('/') + 1) + " not found";
                        requestHistory.setResult(failedCause);
                        requestHistoryRepository.save(requestHistory);
                        throw new BadRequestException(failedCause);
                    }
                    Resource resource = new ClassPathResource("images/sample.png");
                    byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
                    userDataMap.put("logoimage", "data:image/png;charset=utf-8;base64," + Base64.getEncoder().encodeToString(bytes));
                    String html = formzUtils.getTemplatetoText(location, userDataMap);
                    convertHtmlToPdf(formId, form.get().getName(), pdfMergerUtility, html);
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
        requestHistory.setRequestStatus(RequestStatus.GENERATED);
        requestHistory.setResult(mainPDF.getAbsolutePath());
        requestHistoryRepository.save(requestHistory);

        return requestId;
    }

    /**
     * This method returns current date with specified format
     *
     * @return formatted date
     */
    private String getFormatedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd , yyyy ");
        String date = dateFormat.format(new Date());
        return date;
    }

    /**
     * This method is responsible for converting html content to pdf with image
     *
     * @param formId
     * @param name
     * @param pdfMergerUtility
     * @param html
     * @throws IOException
     */
    private void convertHtmlToPdf(int formId, String name, PDFMergerUtility pdfMergerUtility, String html) throws IOException {
        File file = new File(name + formId + ".pdf");
        if (!file.exists())
            HtmlConverter.convertToPdf(html, new FileOutputStream(file));
        pdfMergerUtility.addSource(file);
        file.delete();
    }

    /**
     * This method accepts form data and request id, saves the request json in specified directory
     *
     * @param formDataListDTO
     * @param requestId
     * @return json file
     * @throws IOException
     */
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

    /**
     * This method is responsible for checking request status by given request id
     *
     * @param requestId
     * @return
     */
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
        requestStatusDTO.setOutput(requestHistory.getResult());
        return requestStatusDTO;
    }

    /**
     * This method returns pdf file for given request id
     *
     * @param requestId
     * @return
     * @throws IOException
     */
    @Override
    public FileDTO downloadPDF(String requestId) throws IOException {
        if (requestId == null || requestId.isEmpty()) {
            throw new BadRequestException(messageSource.getMessage("invalid.request", null, Locale.ENGLISH));
        }

        RequestHistory requestHistory = requestHistoryRepository.
                findByRequestId(requestId).orElseThrow(() -> new BadRequestException("invalid request id"));

        String pdfDownloadPath = requestHistory.getResult();

        if (pdfDownloadPath == null || !new File(pdfDownloadPath).exists()) {
            throw new BadRequestException(messageSource.getMessage("pdf.file.not.found", null, Locale.ENGLISH));
        }
        FileDTO fileDTO = null;
        byte[] pdfFileData = Files.readAllBytes(Paths.get(pdfDownloadPath));
        File file = new File(pdfDownloadPath);
        if (pdfFileData != null) {
            requestHistory.setRequestStatus(RequestStatus.COMPLETED);
            requestHistory.setResult(null);
            requestHistory.setRequestJson(null);
            requestHistoryRepository.save(requestHistory);
            fileDTO = new FileDTO();
            fileDTO.setFileData(pdfFileData);
            fileDTO.setFileName(file.getName());
        }
        file.delete();
        return fileDTO;
    }

}
