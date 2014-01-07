package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;

import javax.persistence.*;
import java.util.Date;

/**
 * AuditableEntity.
 * It provides basic information about who/when created/updated the entity
 * <p/>
 *
 * @author Michael Ivanov
 */
@Auditable
@MappedSuperclass
public abstract class AuditableEntity implements Identifiable {

    /**
     * Entity ID
     */
    protected Long id;

    /**
     * Entity code
     */
    protected String code;

    /**
     * Entity name
     */
    private String name;

    /**
     * Entity description
     */
    private String description;

    /**
     * Creator user ID
     */
    private String creatorId;

    /**
     * Editor user ID
     */
    private String editorId;

    /**
     * Creation timestamp
     */
    private Date creationDate;

    /**
     * Update timestamp
     */
    private Date lastUpdate;


    @Transient
    public abstract Long getId();

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "CODE", length = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "NAME", length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "CREATOR_ID", length = 45)
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Column(name = "EDITOR_ID", length = 45)
    public String getEditorId() {
        return editorId;
    }

    public void setEditorId(String editorId) {
        this.editorId = editorId;
    }

    @Column(name = "LAST_UPDATE")
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column(name = "DESCRIPTION", length = 2000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", editorId='" + editorId + '\'' +
                ", creationDate=" + creationDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
