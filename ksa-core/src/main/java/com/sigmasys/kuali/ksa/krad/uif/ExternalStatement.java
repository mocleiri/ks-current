package com.sigmasys.kuali.ksa.model;

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
     * Unique key used by third parties
     */
    private String uniqueKey;


    /**
     * MIME type of the statement
     */
    private String mimeType;

    /**
     * Start date
     */
    private Date fromDate;

    /**
     * End date
     */
    private Date toDate;

    /**
     * URI of the statement
     */
    private String uri;

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

    /**
     * Boolean that tells the system whether the URI should act as a redirect,
     * or if the system should fetch the file at the location and send it to the user.
     */
    private Boolean isRedirect;

    /**
     * Reference to CREDIT type
     */
    private Account account;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_EXT_STATEMENT",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "EXT_STATEMENT_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_EXT_STATEMENT")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "MIME_TYPE", length = 45)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Column(name = "UNIQUE_KEY", length = 256)
    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    @Column(name = "FROM_DATE")
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @Column(name = "TO_DATE")
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Column(name = "URI", length = 256)
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_REDIRECT")
    public Boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(Boolean redirect) {
        isRedirect = redirect;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACNT_ID_FK")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
	


