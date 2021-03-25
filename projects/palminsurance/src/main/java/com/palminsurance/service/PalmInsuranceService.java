package com.palminsurance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palminsurance.config.GeneralConfig;
import com.palminsurance.constants.FormNames;
import com.palminsurance.dto.FormDTO;
import com.palminsurance.dto.FormPolicyDTO;
import com.palminsurance.dto.pdfdata.FieldDataDTO;
import com.palminsurance.dto.pdfdata.FormDataListDTO;
import com.palminsurance.dto.pdfdata.UserDataRequestDTO;
import com.palminsurance.exception.BadRequestException;
import com.palminsurance.model.Policy;
import com.palminsurance.model.Suspense;
import com.palminsurance.repository.AgentRepository;
import com.palminsurance.repository.InsuredRepository;
import com.palminsurance.repository.PolicyRepository;
import com.palminsurance.repository.SuspenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

@Service
public class PalmInsuranceService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private SuspenseRepository suspenseRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private InsuredRepository insuredRepository;


    @Autowired
    private GeneralConfig generalConfig;

    @Autowired
    private ObjectMapper mapper;

    public void generatePDF(FormDTO formDto) throws JsonProcessingException {
        if (formDto == null) {
            throw new BadRequestException("Request invalid");
        }
        List<FormDataListDTO> inputJsonList = new ArrayList<>();
        for (FormPolicyDTO form : formDto.getFormPolicyDTO()) {
            if (form.policyIds != null && !form.policyIds.isEmpty()) {
                List<Policy> policies = policyRepository.findByIdIn(form.policyIds);
                if (policies.isEmpty()) {
                    throw new BadRequestException("Policy id's invalid");
                }
                if (form.getFormName().equals(FormNames.SUSPENSE)) {
                    FormDataListDTO inputJson = getJsonData(policies, FormNames.SUSPENSE);
                    inputJsonList.add(inputJson);
                } else if (form.getFormName().equals(FormNames.AGENT)) {
                    FormDataListDTO inputJson = getJsonData(policies, FormNames.AGENT);
                    inputJsonList.add(inputJson);
                } else if (form.getFormName().equals(FormNames.INSURED)) {
                    FormDataListDTO inputJson = getJsonData(policies, FormNames.INSURED);
                    inputJsonList.add(inputJson);
                }

            }
            String s = mapper.writeValueAsString(inputJsonList);
        }
    }

    private FormDataListDTO getJsonData(List<Policy> policies, FormNames formNames) throws JsonProcessingException {
        FormDataListDTO formDataList = new FormDataListDTO();

        List<UserDataRequestDTO> userDataList = new ArrayList<>();
        for (Policy policy : policies) {
            List<FieldDataDTO> userFieldList = new ArrayList<>();
            Optional<?> policyFormData = null;
            if (formNames.equals(FormNames.SUSPENSE)) {
                policyFormData = suspenseRepository.findByPolicy(policy);
            } else if (formNames.equals(FormNames.AGENT)) {
                policyFormData = agentRepository.findByPolicy(policy);
            } else if (formNames.equals(FormNames.INSURED)) {
                policyFormData = insuredRepository.findByPolicy(policy);
            }
            if (policyFormData.isPresent()) {
                UserDataRequestDTO userData = new UserDataRequestDTO();

                Map<String, Object> userDataMap = mapper.readValue(mapper.writeValueAsString(policyFormData),
                        new TypeReference<Map<String, Object>>() {
                        });
                if (formNames.equals(FormNames.SUSPENSE)) {
                    userDataMap.put("suspenseitems", userDataMap.get("suspenseitems").toString().split("sep "));
                }
                for (Map.Entry<String, Object> entry : userDataMap.entrySet()) {
                    FieldDataDTO formFields = new FieldDataDTO();
                    formFields.setFieldName(entry.getKey().toUpperCase());
                    formFields.setFieldValue(entry.getValue());
                    userFieldList.add(formFields);
                }
                userDataList.add(userData);
                userData.setFieldDataList(userFieldList);
            }
        }
        formDataList.setFormName(formNames.formName);
        formDataList.setUserDataList(userDataList);

        return formDataList;
    }

}
