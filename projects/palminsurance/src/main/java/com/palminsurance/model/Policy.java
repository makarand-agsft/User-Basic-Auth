package com.palminsurance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "policy")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String policyNo;

    @JsonBackReference
    @OneToMany(mappedBy = "policy")
    private List<Agent> agents;
    @JsonBackReference

    @OneToMany(mappedBy = "policy")
    private List<Insured> insureds;

    @JsonBackReference
    @OneToMany(mappedBy = "policy")
    private List<Suspense> suspenses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    public List<Insured> getInsureds() {
        return insureds;
    }

    public void setInsureds(List<Insured> insureds) {
        this.insureds = insureds;
    }

    public List<Suspense> getSuspenses() {
        return suspenses;
    }

    public void setSuspenses(List<Suspense> suspenses) {
        this.suspenses = suspenses;
    }
}
