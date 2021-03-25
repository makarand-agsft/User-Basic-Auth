package com.palminsurance.dto;

import com.palminsurance.constants.FormNames;

import java.util.List;

public class FormPolicyDTO {

    public FormNames formName;

    public List<Long> policyIds;

    public FormNames getFormName() {
        return formName;
    }

    public void setFormName(FormNames formName) {
        this.formName = formName;
    }

    public List<Long> getPolicyIds() {
        return policyIds;
    }

    public void setPolicyIds(List<Long> policyIds) {
        this.policyIds = policyIds;
    }


}
