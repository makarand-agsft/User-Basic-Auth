package com.palminsurance.constants;

public enum FormNames {

    SUSPENSE("Suspense letter"),
    INSURED("Dishonoured Deposit Insured"),
    AGENT("Dishonoured Deposit Agent");

    public String formName;

    FormNames(String s) {
        this.formName = s;
    }
}
