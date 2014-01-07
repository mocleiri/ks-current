package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * This entity class describes a type of a <code>Refund</code> stored in the "refundType" attribute of that class.
 * <p/>
 *
 * @author Michael Ivanov
 * @author Sergey Godunov
 * @version 1.1
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "KSSA_REFUND_TYPE")
public class RefundType extends AuditableEntity<Long> {

    /**
     * The transaction type for refunds that are made by this type. This is a debit type ("charge"), as it deducts the over-payment from the account.
     */
    private String debitTypeId;

    /**
     * For refund types that send the refund back to a KSA account ("Account Refunds", "Payoff Refunds")
     * this is the credit type ("payment") that will be applied to the receiving account.
     * For most refund types where the money is sent.
     */
    private String creditTypeId;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "CREDIT_TYPE_ID", length = 20)
    public String getCreditTypeId() {
        return creditTypeId;
    }

    public void setCreditTypeId(String creditTypeId) {
        this.creditTypeId = creditTypeId;
    }

    @Column(name = "DEBIT_TYPE_ID", length = 20)
    public String getDebitTypeId() {
        return debitTypeId;
    }

    public void setDebitTypeId(String debitTypeId) {
        this.debitTypeId = debitTypeId;
    }
}
