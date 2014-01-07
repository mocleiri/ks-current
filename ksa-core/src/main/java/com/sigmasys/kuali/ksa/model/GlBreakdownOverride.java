package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;


/**
 * General Ledger Breakdown Override model.
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(GlBreakdownOverrideType.TRANSACTION_CODE)
public class GlBreakdownOverride extends AbstractGlBreakdownOverride {

    /**
     * Reference to Transaction
     */
    private Transaction transaction;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID_FK")
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
	


