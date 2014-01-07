package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.util.EnumUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;


/**
 * General ledger transaction model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_GL_TRANSACTION")
public class GlTransaction implements Identifiable {

    /**
     * The unique identifier
     */
    private Long id;

    /**
     * Transactions associated with this GL transaction
     */
    private Set<Transaction> transactions;

    /**
     * Transmission of the current GL transaction
     */
    private GlTransmission transmission;

    /**
     * General ledger account ID
     */
    private String glAccountId;

    /**
     * Transaction date
     */
    private Date date;

    /**
     * Transaction amount
     */
    private BigDecimal amount;

    /**
     * Generated text
     */
    private String description;

    private GlTransactionStatus status;

    private String statusCode;

    /**
     * GL operation type. Can be 'C' or 'D'
     */
    private GlOperationType glOperation;

    private String glOperationCode;


    @PrePersist
    void populateDBFields() {
        statusCode = (status != null) ? status.getId() : null;
        glOperationCode = (glOperation != null) ? glOperation.getId() : null;
    }

    @PostLoad
    void populateTransientFields() {
        status = (statusCode != null) ? EnumUtils.findById(GlTransactionStatus.class, statusCode) : null;
        glOperation = (glOperationCode != null) ? EnumUtils.findById(GlOperationType.class, glOperationCode) : null;
    }


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_GL_TRANSACTION",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "GL_TRANSACTION_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_GL_TRANSACTION")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_GL_TRANS_TRANSACTION",
            joinColumns = {
                    @JoinColumn(name = "GL_TRANSACTION_ID_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "TRANSACTION_ID_FK")
            }
    )
    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSMISSION_ID_FK")
    public GlTransmission getTransmission() {
        return transmission;
    }

    public void setTransmission(GlTransmission transmission) {
        this.transmission = transmission;
    }

    @Column(name = "GL_ACCOUNT_ID", length = 45)
    public String getGlAccountId() {
        return glAccountId;
    }

    public void setGlAccountId(String glAccountId) {
        this.glAccountId = glAccountId;
    }

    @Column(name = "TRANSACTION_DATE")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "AMOUNT")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "GENERATED_TEXT", length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Column(name = "STATUS", length = 1)
    protected String getStatusCode() {
        return statusCode;
    }

    @Transient
    public GlTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(GlTransactionStatus status) {
        this.status = status;
    }

    @Column(name = "GL_OPERATION", length = 1)
    protected String getGlOperationCode() {
        return glOperationCode;
    }

    protected void setGlOperationCode(String glOperationCode) {
        this.glOperationCode = glOperationCode;
    }

    @Transient
    public GlOperationType getGlOperation() {
        return glOperation;
    }

    public void setGlOperation(GlOperationType glOperation) {
        this.glOperation = glOperation;
    }

}
	


