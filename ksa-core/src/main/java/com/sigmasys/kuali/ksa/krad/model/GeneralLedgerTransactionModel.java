package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.GlOperationType;
import com.sigmasys.kuali.ksa.model.GlTransaction;
import com.sigmasys.kuali.ksa.model.Transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * This class represents a single row in a General Ledger Transaction subtable
 * of the GeneralLedger Account table.
 * .
 * User: Sergey
 * Date: 4/24/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralLedgerTransactionModel extends TransactionModel implements Serializable {

    /**
     * The General Ledger Transaction.
     */
    private GlTransaction glTransaction;

    /**
     * The KSA Transaction.
     */
    private Transaction ksaTransaction;


    /**
     * Creates an empty GeneralLedgerTransactionModel.
     */
    public GeneralLedgerTransactionModel() {}

    /**
     * Creates a new GeneralLedgerTransactionModel with the given KSA Transaction.
     *
     * @param ksaTransaction    A KSA Transaction.
     */
    public GeneralLedgerTransactionModel(Transaction ksaTransaction) {
        super(ksaTransaction);

        this.ksaTransaction = ksaTransaction;
    }

    public GlTransaction getGlTransaction() {
        return glTransaction;
    }

    public void setGlTransaction(GlTransaction glTransaction) {
        this.glTransaction = glTransaction;
    }

    public Transaction getKsaTransaction() {
        return ksaTransaction;
    }

    public void setKsaTransaction(Transaction ksaTransaction) {
        this.ksaTransaction = ksaTransaction;
    }

    public Date getTransactionDate() {
        return glTransaction.getDate();
    }

    public BigDecimal getOriginalAmount() {
        return ksaTransaction.getAmount();
    }

    public BigDecimal getGlAmount() {
        return glTransaction.getAmount();
    }

    public String getDescription () {
        return ksaTransaction.getStatementText();
    }

    public GlOperationType getOperationType() {
        return glTransaction.getGlOperation();
    }

    public String getOperationTypeString() {
        return glTransaction.getGlOperation().toString();
    }

    public String getOriginatingAccountId() {
        return ksaTransaction.getAccountId();
    }
}

