package com.sigmasys.kuali.ksa.model;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Transient;

/*
 * Concrete payment class. 
 * 
 */

@Entity
@DiscriminatorValue(TransactionTypeValue.PAYMENT_CODE)
public class Payment extends Credit {

    /**
     * if set to true, the amount may be refunded to the student subject to any other clearing rules, for example, a check 10-day waiting period, etc.
     * This will be set to false in the case of tuition deposits, etc, which may not be refunded to a student, but may be allocated to charges on the account.
     */
    protected Boolean isRefundable;

    /**
     * Refund rule
     */
    private String refundRule;

    /**
     * the clearDate is the date after which a payment is considered refundable after bank processing.
     * It is generally inherited at the time of instantiation from the type of payment
     * as looked up in the TransactionType. For example, an institution may set the clearDate for a check as 10 days
     * after the receipt of the check, whereas cash is cleared
     * instantly. The refundability of a transaction based on this date does NOT affect the refund flag isRefundable,
     * which is set for transactions which are inherently non refundable,
     * such as tuition deposits, etc.
     */
    private Date clearDate;


    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_REFUNDABLE")
    public Boolean isRefundable() {
        return isRefundable != null ? isRefundable : false;
    }

    public void setRefundable(Boolean refundable) {
        isRefundable = refundable;
    }

    @Column(name = "REFUND_RULE", length = 2000)
    public String getRefundRule() {
        return refundRule;
    }

    public void setRefundRule(String refundRule) {
        this.refundRule = refundRule;
    }


    @Column(name = "CLEAR_DATE")
    public Date getClearDate() {
        return clearDate;
    }

    public void setClearDate(Date clearDate) {
        this.clearDate = clearDate;
    }

    @Transient
    public TransactionTypeValue getTransactionTypeValue() {
        return TransactionTypeValue.PAYMENT;
    }

    /*
    * Using the payment type and effective date, look up the priority of the payment for the payment application system.
    */
    @Transient
    public void getPriority() {
        // TODO
    }


    /*
      * Using the payment type and effective date, look up the types of debits that can be paid with this payment.
      * This will be used by the payment application system.
      */
    @Transient
    public void getAllowableCharges() {
        // TODO
    }


}

