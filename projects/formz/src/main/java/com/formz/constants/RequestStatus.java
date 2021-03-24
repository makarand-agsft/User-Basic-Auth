package com.formz.constants;

public enum RequestStatus {

    PROCESSING(1,"PROCESSING"),
    GENERATED(2,"GENERATED"),
    COMPLETED(3,"COMPLETED"),
    FAILED(4,"FAILED");
    Integer id;
    String value;

    RequestStatus(Integer id, String value) {
        this.id=id;
        this.value=value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
