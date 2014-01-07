package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.pb.PaymentBillingDate;

/**
 * User: tbornholtz
 * Date: 10/3/13
 */
public class PaymentBillingDateModel extends PaymentBillingDate {

    private String rollupId;


    public String getRollupId() {
        return rollupId;
    }

    public void setRollupId(String rollupId) {
        this.rollupId = rollupId;
    }
}
