package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


/**
 * External statement model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_EXTERNAL_STATEMENT")
public class ExternalStatement implements Identifiable {

    /**
     * The unique identifier
     */
    private Long id;

    /**
     * BillRecord reference
     */
    private BillRecord billRecord;

    /**
     * MIME type of the statement
     */
    private String mimeType;

    /**
     * URI of the statement
     */
    private String uri;

    /**
     * Indicates whether the statement is directly available at the URI specified by "uri" property
     */
    private Boolean isDirectUri;

    /**
     * Creator ID
     */
    private String creatorId;

    /**
     * Editor ID
     */
    private String editorId;

    /**
     * Creation date
     */
    private Date creationDate;

    /**
     * Last update timestamp
     */
    private Date lastUpdate;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_RECORD_ID_FK")
    public BillRecord getBillRecord() {
        return billRecord;
    }

    public void setBillRecord(BillRecord billRecord) {
        this.billRecord = billRecord;
    }

    @Column(name = "MIME_TYPE", length = 100)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Column(name = "URI", length = 1000)
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_DIRECT_URI")
    public Boolean isDirectUri() {
        return isDirectUri != null ? isDirectUri : false;
    }

    public void setDirectUri(Boolean directUri) {
        isDirectUri = directUri;
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

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Column(name = "LAST_UPDATE")
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


}



