package com.formz.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "form")
public class Form extends AuditingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "label")
    private String label;

    @Column(name = "version")
    private Double version;

    @OneToMany(mappedBy = "form")
    private List<FormField> formFields;

    @OneToMany(mappedBy = "form")
    private List<FormPage> formPage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public List<FormPage> getFormPage() {
        return formPage;
    }

    public void setFormPage(List<FormPage> formPage) {
        this.formPage = formPage;
    }

    public List<FormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<FormField> formFields) {
        this.formFields = formFields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
