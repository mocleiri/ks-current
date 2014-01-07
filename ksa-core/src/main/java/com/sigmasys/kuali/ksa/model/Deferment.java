package com.sigmasys.kuali.ksa.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@Entity
@DiscriminatorValue(TransactionTypeValue.DEFERMENT_CODE)
public class Deferment extends Credit {

    /**
     * All deferments are set with an expiration date. If the date passes, then the deferment is expired, and the payment application system will
     * remove the allocation of the deferment, and the charge it is applied to will become due.
     */
    private Date expirationDate;

    /**
     * This is the amount of the deferment when it is created.
     */
    private BigDecimal originalAmount;


    @Transient
    public TransactionTypeValue getTransactionTypeValue() {
        return TransactionTypeValue.DEFERMENT;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRATION_DATE")
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Column(name = "ORIG_AMOUNT")
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    /**
     * Indicates that the deferment has gone through a process called "expireDeferment"
     */
    @Transient
    public boolean isExpired() {
        return (getStatus() != null && getStatus() == TransactionStatus.EXPIRED);
    }

}

