package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.Tag;

/**
 * User: tbornholtz
 * Date: 9/1/13
 */
public class TransactionTagModel {

    private Long transactionId;
    private Tag tag;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
