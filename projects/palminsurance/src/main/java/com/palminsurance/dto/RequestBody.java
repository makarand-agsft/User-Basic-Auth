package com.palminsurance.dto;

import java.util.List;

public class RequestBody {

    List<FormDTO> formList;

    public List<FormDTO> getFormList() {
        return formList;
    }

    public void setFormList(List<FormDTO> formList) {
        this.formList = formList;
    }
}
