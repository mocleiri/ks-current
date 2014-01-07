package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.model.CreditType;
import com.sigmasys.kuali.ksa.model.DebitType;
import com.sigmasys.kuali.ksa.service.TransactionTypeVisitor;

/**
 * TransactionTypeVisitor's default implementation that stores transaction types in class member variables.
 *
 * @author Michael Ivanov
 */
public class SimpleTransactionTypeVisitor implements TransactionTypeVisitor {

    private CreditType creditType;
    private DebitType debitType;

    private SimpleTransactionTypeVisitor() {
    }

    public static SimpleTransactionTypeVisitor getInstance() {
        return new SimpleTransactionTypeVisitor();
    }

    @Override
    public void visit(CreditType creditType) {
        this.creditType = creditType;
    }

    @Override
    public void visit(DebitType debitType) {
        this.debitType = debitType;
    }

    public CreditType getCreditType() {
        return creditType;
    }

    public DebitType getDebitType() {
        return debitType;
    }

}
