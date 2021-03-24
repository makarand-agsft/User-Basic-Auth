package com.formz.model;

import javax.persistence.*;

@Entity
@Table(name = "form_field")
public class FormField extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "form_id",referencedColumnName = "id")
    private Form form;

    @ManyToOne
    @JoinColumn(name = "field_id",referencedColumnName = "id")
    private Field field;

    @Column(name = "required")
    private Boolean required;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
