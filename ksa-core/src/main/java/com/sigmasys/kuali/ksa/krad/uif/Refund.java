package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;
import com.sigmasys.kuali.ksa.util.EnumUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Refund model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Auditable
@Entity
@Table(name = "KSSA_REFUND")
public class Refund implements Identifiable {

    /**
     * Refund ID
     */
    private Long id;

    /**
     * Refund type
     */
    private RefundType refundType;

    /**
     * Timestamp
     */
    private Date creationDate;

    /**
     * Request date
     */
    private Date requestDate;

    /**
     * Transaction associated with this refund
     */
    private Transaction transaction;

    /**
     * Refund attribute
     */
    private String attribute;

    /**
     * Refund statement
     */
    private String statement;

    /**
     * Account requested the refund
     */
    private Account requester;

    /**
     * Account authorized the refund
     */
    private Account authorizer;


    /**
     * System name created the refund
     */
    private String system;

    /**
     * Batch identifier
     */
    private String batchId;

    /**
     * Refund status
     */
    private RefundStatus status;

    /**
     * Refund status code
     */
    private String statusCode;


    @PrePersist
    void populateDBFields() {
        statusCode = (status != null) ? status.getId() : null;
    }

    @PostLoad
    void populateTransientFields() {
        status = (statusCode != null) ? EnumUtils.findById(RefundStatus.class, statusCode): null;
    }

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_REFUND",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "REFUND_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_REFUND")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFUND_TYPE_ID_FK")
    public RefundType getRefundType() {
        return refundType;
    }

    public void setRefundType(RefundType refundType) {
        this.refundType = refundType;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Column(name = "REQUEST_DATE")
    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID_FK", unique = true, nullable = false)
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Column(name = "ATTRIBUTE", length = 1024)
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Column(name = "STATEMENT", length = 1024)
    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTER_ID_FK")
    public Account getRequester() {
        return requester;
    }

    public void setRequester(Account requester) {
        this.requester = requester;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHORIZER_ID_FK")
    public Account getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(Account authorizer) {
        this.authorizer = authorizer;
    }

    @Column(name = "SYSTEM", length = 100)
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Column(name = "BATCH_ID", length = 100)
    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Transient
    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    @Column(name = "STATUS", length = 1)
    protected String getStatusCode() {
        return statusCode;
    }

    protected void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
