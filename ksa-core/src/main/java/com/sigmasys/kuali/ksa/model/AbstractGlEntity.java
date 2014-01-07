package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.util.EnumUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Abstract General Ledger entity.
 *
 * @author Michael Ivanov
 */
@MappedSuperclass
public abstract class AbstractGlEntity implements Identifiable {

    /**
     * The unique identifier
     */
    protected Long id;

    /**
     * General ledger account ID
     */
    protected String glAccountId;

    /**
     * Transaction date
     */
    protected Date date;

    /**
     * Transmission amount
     */
    protected BigDecimal amount;

    /**
     * GL recognition period
     */
    protected GlRecognitionPeriod recognitionPeriod;

    /**
     * GL operation type. Can be 'C' or 'D'
     */
    protected GlOperationType glOperation;

    protected String glOperationCode;

    protected String statusCode;

    @PostLoad
    protected void populateTransientFields() {
        glOperation = (glOperationCode != null) ? EnumUtils.findById(GlOperationType.class, glOperationCode) : null;
    }

    @Transient
    public abstract Long getId();

    @Transient
    public abstract Date getDate();

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "GL_ACCOUNT_ID", length = 45)
    public String getGlAccountId() {
        return glAccountId;
    }

    public void setGlAccountId(String glAccountId) {
        this.glAccountId = glAccountId;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GL_RECOGNITION_PERIOD_ID_FK")
    public GlRecognitionPeriod getRecognitionPeriod() {
        return recognitionPeriod;
    }

    public void setRecognitionPeriod(GlRecognitionPeriod recognitionPeriod) {
        this.recognitionPeriod = recognitionPeriod;
    }

    @Column(name = "GL_OPERATION", length = 1)
    protected String getGlOperationCode() {
        return glOperationCode;
    }

    protected void setGlOperationCode(String glOperationCode) {
        this.glOperationCode = glOperationCode;
        glOperation = EnumUtils.findById(GlOperationType.class, glOperationCode);
    }

    protected void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Column(name = "STATUS", length = 1)
    protected String getStatusCode() {
        return statusCode;
    }

    @Transient
    public GlOperationType getGlOperation() {
        return glOperation;
    }

    public void setGlOperation(GlOperationType glOperation) {
        this.glOperation = glOperation;
        glOperationCode = glOperation.getId();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", glAccountId='" + glAccountId + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                ", recognitionPeriod=" + recognitionPeriod +
                ", glOperation=" + glOperation +
                ", glOperationCode='" + glOperationCode + '\'' +
                ", statusCode='" + statusCode + '\'' +
                '}';
    }
}
	


