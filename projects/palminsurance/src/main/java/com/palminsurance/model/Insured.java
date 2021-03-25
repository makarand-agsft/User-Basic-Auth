package com.palminsurance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "insured")
public class Insured {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String CONAME;
    private String COSTREET;
    private String COCITY;
    private String COST;
    private String COZIP;
    private String POLICYNO;
    private String POLEFTDATE;
    private String POLEXPDATE;
    private String POLMALDATE;
    private String INSUREDNAME;
    private String INSUREDSTREET;
    private String INSCITY;
    private String INSST;
    private String INSZIP;
    private String AGENTNAME;
    private String AGENTCITY;
    private String AGENTSTREET;
    private String AGENTST;
    private String AGENTCODE;
    private String AGENTZIP;
    private String POLCANDATE;
    private String COPHONE;
    private String AGENTEMAIL;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getCONAME() {
        return CONAME;
    }

    public void setCONAME(String CONAME) {
        this.CONAME = CONAME;
    }

    public String getCOSTREET() {
        return COSTREET;
    }

    public void setCOSTREET(String COSTREET) {
        this.COSTREET = COSTREET;
    }

    public String getCOCITY() {
        return COCITY;
    }

    public void setCOCITY(String COCITY) {
        this.COCITY = COCITY;
    }

    public String getCOST() {
        return COST;
    }

    public void setCOST(String COST) {
        this.COST = COST;
    }

    public String getCOZIP() {
        return COZIP;
    }

    public void setCOZIP(String COZIP) {
        this.COZIP = COZIP;
    }

    public String getPOLICYNO() {
        return POLICYNO;
    }

    public void setPOLICYNO(String POLICYNO) {
        this.POLICYNO = POLICYNO;
    }

    public String getPOLEFTDATE() {
        return POLEFTDATE;
    }

    public void setPOLEFTDATE(String POLEFTDATE) {
        this.POLEFTDATE = POLEFTDATE;
    }

    public String getPOLEXPDATE() {
        return POLEXPDATE;
    }

    public void setPOLEXPDATE(String POLEXPDATE) {
        this.POLEXPDATE = POLEXPDATE;
    }

    public String getPOLMALDATE() {
        return POLMALDATE;
    }

    public void setPOLMALDATE(String POLMALDATE) {
        this.POLMALDATE = POLMALDATE;
    }

    public String getINSUREDNAME() {
        return INSUREDNAME;
    }

    public void setINSUREDNAME(String INSUREDNAME) {
        this.INSUREDNAME = INSUREDNAME;
    }

    public String getINSUREDSTREET() {
        return INSUREDSTREET;
    }

    public void setINSUREDSTREET(String INSUREDSTREET) {
        this.INSUREDSTREET = INSUREDSTREET;
    }

    public String getINSCITY() {
        return INSCITY;
    }

    public void setINSCITY(String INSCITY) {
        this.INSCITY = INSCITY;
    }

    public String getINSST() {
        return INSST;
    }

    public void setINSST(String INSST) {
        this.INSST = INSST;
    }

    public String getINSZIP() {
        return INSZIP;
    }

    public void setINSZIP(String INSZIP) {
        this.INSZIP = INSZIP;
    }

    public String getAGENTNAME() {
        return AGENTNAME;
    }

    public void setAGENTNAME(String AGENTNAME) {
        this.AGENTNAME = AGENTNAME;
    }

    public String getAGENTCITY() {
        return AGENTCITY;
    }

    public void setAGENTCITY(String AGENTCITY) {
        this.AGENTCITY = AGENTCITY;
    }

    public String getAGENTSTREET() {
        return AGENTSTREET;
    }

    public void setAGENTSTREET(String AGENTSTREET) {
        this.AGENTSTREET = AGENTSTREET;
    }

    public String getAGENTST() {
        return AGENTST;
    }

    public void setAGENTST(String AGENTST) {
        this.AGENTST = AGENTST;
    }

    public String getAGENTCODE() {
        return AGENTCODE;
    }

    public void setAGENTCODE(String AGENTCODE) {
        this.AGENTCODE = AGENTCODE;
    }

    public String getAGENTZIP() {
        return AGENTZIP;
    }

    public void setAGENTZIP(String AGENTZIP) {
        this.AGENTZIP = AGENTZIP;
    }

    public String getPOLCANDATE() {
        return POLCANDATE;
    }

    public void setPOLCANDATE(String POLCANDATE) {
        this.POLCANDATE = POLCANDATE;
    }

    public String getCOPHONE() {
        return COPHONE;
    }

    public void setCOPHONE(String COPHONE) {
        this.COPHONE = COPHONE;
    }

    public String getAGENTEMAIL() {
        return AGENTEMAIL;
    }

    public void setAGENTEMAIL(String AGENTEMAIL) {
        this.AGENTEMAIL = AGENTEMAIL;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
