package com.palminsurance.dto.pdfdata;

import java.util.List;

public class FormDataListDTO {

    private String formName;

    private List<UserDataRequestDTO> userDataList;

    public List<UserDataRequestDTO> getUserDataList() {
        return userDataList;
    }

    public void setUserDataList(List<UserDataRequestDTO> userDataList) {
        this.userDataList = userDataList;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }
}
