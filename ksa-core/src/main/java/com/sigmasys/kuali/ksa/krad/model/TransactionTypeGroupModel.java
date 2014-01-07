package com.sigmasys.kuali.ksa.krad.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: tbornholtz
 * Date: 3/25/13
 */
public class TransactionTypeGroupModel {

    private List<TransactionTypeModel> transactionTypes;

    private TransactionTypeModel currentTransactionType;

    public void addTransactionType(TransactionTypeModel entity) {

        if(transactionTypes == null){
            transactionTypes = new ArrayList<TransactionTypeModel>();
        }
        transactionTypes.add(entity);

        // there can be only one with the null end date.  That's the current one.
        if(currentTransactionType == null || entity.getTransactionType().getEndDate() == null){
            currentTransactionType = entity;
        } else {
            Date currentEndDate = currentTransactionType.getTransactionType().getEndDate();
            Date newEndDate = entity.getTransactionType().getEndDate();

            if(currentEndDate != null && newEndDate.after(currentEndDate)){
                currentTransactionType = entity;
            }
        }
    }


    public List<TransactionTypeModel> getTransactionTypes() {
        if(transactionTypes == null){
            transactionTypes = new ArrayList<TransactionTypeModel>();
        }
        return transactionTypes;
    }

    public void setTransactionTypes(List<TransactionTypeModel> transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public TransactionTypeModel getCurrentTransactionType() {
        return currentTransactionType;
    }

    public void setCurrentTransactionType(TransactionTypeModel currentTransactionType) {
        this.currentTransactionType = currentTransactionType;
    }
}
