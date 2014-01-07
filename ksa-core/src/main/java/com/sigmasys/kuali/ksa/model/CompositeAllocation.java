package com.sigmasys.kuali.ksa.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Composite allocation model that holds references to Allocation and GlTransaction instances.
 *
 * @author Michael Ivanov
 */
public class CompositeAllocation implements Serializable {

    private Allocation allocation;

    private GlTransaction creditGlTransaction;

    private GlTransaction debitGlTransaction;


    public Allocation getAllocation() {
        return allocation;
    }

    public void setAllocation(Allocation allocation) {
        this.allocation = allocation;
    }

    public GlTransaction getCreditGlTransaction() {
        return creditGlTransaction;
    }

    public void setCreditGlTransaction(GlTransaction creditGlTransaction) {
        this.creditGlTransaction = creditGlTransaction;
    }

    public GlTransaction getDebitGlTransaction() {
        return debitGlTransaction;
    }

    public void setDebitGlTransaction(GlTransaction debitGlTransaction) {
        this.debitGlTransaction = debitGlTransaction;
    }

    public List<GlTransaction> getGlTransactions() {
        List<GlTransaction> glTransactions = new LinkedList<GlTransaction>();
        GlTransaction creditGlTransaction = getCreditGlTransaction();
        GlTransaction debitGlTransaction = getDebitGlTransaction();
        if (creditGlTransaction != null) {
            glTransactions.add(creditGlTransaction);
        }
        if (debitGlTransaction != null) {
            glTransactions.add(debitGlTransaction);
        }
        return glTransactions;
    }
}
