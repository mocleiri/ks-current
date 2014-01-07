package com.sigmasys.kuali.ksa.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(TransactionTypeValue.CHARGE_CODE)
public class Charge extends Debit {

    @Transient
    public TransactionTypeValue getTransactionTypeValue() {
       return TransactionTypeValue.CHARGE;
    }


}

