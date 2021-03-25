package com.palminsurance.dto.pdfdata;

import java.util.List;

public class UserDataRequestDTO {

    private List<FieldDataDTO> fieldDataList;

    public List<FieldDataDTO> getFieldDataList() {
        return fieldDataList;
    }

    public void setFieldDataList(List<FieldDataDTO> fieldDataList) {
        this.fieldDataList = fieldDataList;
    }
}
