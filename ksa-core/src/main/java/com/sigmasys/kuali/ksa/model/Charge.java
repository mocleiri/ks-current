package com.sigmasys.kuali.ksa.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(TransactionTypeValue.CHARGE_CODE)
public class Charge extends Debit {

    private String cancellationRule;


    @Column(name = "CANCELLATION_RULE", length = 2000)
    public String getCancellationRule() {
        return cancellationRule;
    }

    public void setCancellationRule(String cancellationRule) {
        this.cancellationRule = cancellationRule;
    }

    @Transient
    public TransactionTypeValue getTransactionTypeValue() {
       return TransactionTypeValue.CHARGE;
    }


}

