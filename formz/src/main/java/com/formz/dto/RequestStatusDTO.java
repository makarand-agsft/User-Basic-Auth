package com.formz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class RequestStatusDTO {

    private String requestId;

    private String requestStatus;

    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date lastUpdatedAt;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
