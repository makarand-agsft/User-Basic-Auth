package com.formz.model;

import com.formz.constants.RequestStatus;

import javax.persistence.*;

@Entity
@Table(name = "request_history")
public class RequestHistory extends AuditingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_id")
    private String requestId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private RequestStatus requestStatus;

    @Column(name = "request_json")
    private String requestJson;

    @Column(name = "pdf_download_path")
    private String pdfDownloadPath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public String getPdfDownloadPath() {
        return pdfDownloadPath;
    }

    public void setPdfDownloadPath(String pdfDownloadPath) {
        this.pdfDownloadPath = pdfDownloadPath;
    }
}
