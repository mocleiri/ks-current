package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Document model.
 * <p/>
 * User: mike
 * Date: 1/22/12
 * Time: 3:47 PM
 */
@Entity
@Table(name = "KSSA_DOCUMENT")
public class Document implements Identifiable {

    /**
     * Document ID
     */
    private Long id;

    /**
     * Document body
     */
    private String body;

    /**
     * Creation date
     */
    private Date creationDate;

    /**
     * Creator user ID
     */
    private String creatorId;

    /**
     * Editor user ID
     */
    private String editorId;

    /**
     * Timestamp
     */
    private Date lastUpdate;

    /**
     * Edit reason
     */
    private String editReason;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_DOC",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "DOCUMENT_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_DOC")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Lob
    @Column(name = "DOCUMENT")
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

    @Column(name = "EDIT_REASON", length = 512)
    public String getEditReason() {
        return editReason;
    }

    public void setEditReason(String editReason) {
        this.editReason = editReason;
    }
}
