package com.palminsurance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Audited
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditingEntity {

    @CreatedBy
    @Column(name = "created_by")
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @JsonIgnore
    private Date createdDate = new Date();

    @LastModifiedBy
    @Column(name = "updated_by")
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @ColumnDefault(value = "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    @JsonIgnore
    private Date lastModifiedDate = new Date();

    public AuditingEntity() {
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public String toString() {
        return "AuditingEntity [createdDate=" + createdDate + ", createdBy=" + createdBy + ", lastModifiedDate="
                + lastModifiedDate + ", lastModifiedBy=" + lastModifiedBy + "]";
    }
}
