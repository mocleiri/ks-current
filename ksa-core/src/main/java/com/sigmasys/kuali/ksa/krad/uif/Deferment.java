package com.sigmasys.kuali.ksa.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(TransactionTypeValue.DEFERMENT_CODE)
public class Deferment extends Credit {

    /**
     * All deferments are set with an expiration date. If the date passes, then the deferment is expired, and the payment application system will
     * remove the allocation of the deferment, and the charge it is applied to will become due.
     */
    private Date expirationDate;

    /**
     * Indicates that the deferment has gone through a process called "expireDeferment"
     */
    private Boolean isExpired;

    @Transient
    public TransactionTypeValue getTransactionTypeValue() {
        return TransactionTypeValue.DEFERMENT;
    }


    @Column(name = "EXPIRATION_DATE")
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_EXPIRED")
    public Boolean isExpired() {
        return isExpired != null ? isExpired : false;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }
}

