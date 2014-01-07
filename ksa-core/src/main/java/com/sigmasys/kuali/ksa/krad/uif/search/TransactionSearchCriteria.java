package com.sigmasys.kuali.ksa.model.search;

import com.sigmasys.kuali.ksa.model.TransactionTypeValue;

import java.util.Date;
import java.util.List;

/**
 * This class is used as a criteria in the search of Transaction entities.
 *
 * @author Michael Ivanov
 *         Date: 4/8/12
 *         Time: 10:46 PM
 */
public class TransactionSearchCriteria extends SearchCriteria {

    private Date fromEffectiveDate;
    private Date toEffectiveDate;
    private String accountId;
    private List<TransactionTypeValue> transactionTypes;

    public Date getFromEffectiveDate() {
        return fromEffectiveDate;
    }

    public void setFromEffectiveDate(Date fromEffectiveDate) {
        this.fromEffectiveDate = fromEffectiveDate;
    }

    public Date getToEffectiveDate() {
        return toEffectiveDate;
    }

    public void setToEffectiveDate(Date toEffectiveDate) {
        this.toEffectiveDate = toEffectiveDate;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<TransactionTypeValue> getTransactionTypes() {
        return transactionTypes;
    }

    public void setTransactionTypes(List<TransactionTypeValue> transactionTypes) {
        this.transactionTypes = transactionTypes;
    }
}
