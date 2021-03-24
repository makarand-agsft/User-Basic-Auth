package com.formz.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "fields")
public class Field extends AuditingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "field_label")
    private String fieldLabel;

    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "field")
    private List<FormField> formFields;

    @ManyToOne
    @JoinColumn(name = "parent_field_id")
    private Field parentField;

    @OneToMany(mappedBy = "parentField",fetch = FetchType.EAGER)
    private List<Field> childFields;

    public Field getParentField() {
        return parentField;
    }

    public void setParentField(Field parentField) {
        this.parentField = parentField;
    }

    public List<Field> getChildFields() {
        return childFields;
    }

    public void setChildFields(List<Field> childFields) {
        this.childFields = childFields;
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

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
