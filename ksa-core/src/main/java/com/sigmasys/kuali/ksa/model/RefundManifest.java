package com.sigmasys.kuali.ksa.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sigmasys.kuali.ksa.annotation.Auditable;
import org.hibernate.annotations.GenericGenerator;

/**
 * Note that KSA is only responsible for the manifestation of account refunds
 * (that is, the credit that is made to an external party is most often handled
 * by another system (check writer, ACH processor, etc.) therefore these fields
 * will only be recorded if the external system can report back the identification
 * of the document/ transaction to KSA. In the case of an account refund,
 * KSA will store the appropriate values automatically.
 * It is advised in implementation that values stored in the otherIdentifier box are
 * prefixed with the type (for example PAYPAL:, etc.) in order to allow for future expansion.
 *
 * @author Sergey
 * @version 1.0
 */
@SuppressWarnings("serial")
@Entity
@Auditable
@Table(name = "KSSA_REFUND_MANIFEST")
public class RefundManifest implements Identifiable {

    /**
     * Refund manifest ID.
     */
    private Long id;

    /**
     * Open field, usually storing the check number issued to the user.
     */
    private String checkIdentifier;

    /**
     * If the refund is an account refund, then this will store the account identifier where the funds were sent.
     */
    private Account refundAccount;

    /**
     * If an account refund is made, this field will hold the transaction identifier for the credit to the other account.
     */
    private Transaction refundTransaction;

    /**
     * If the refund is made via electronic bank transfer (SWIFT, BACS, ACH, etc.) the identifier for the refund will be stored here.
     */
    private String bankTransferIdentifier;

    /**
     * If the refund is made back to a credit card or similar, the return identifier (authorization number) will be stored here.
     */
    private String creditCardIdentifier;

    /**
     * If some other refund is made that has an identifier, it will be stored here.
     */
    private String otherIdentifier;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "CHECK_IDENTIFIER", length = 45)
    public String getCheckIdentifier() {
        return checkIdentifier;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "REFUND_ACCOUNT_ID_FK")
    public Account getRefundAccount() {
        return refundAccount;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "REFUND_TRANSACTION_ID_FK")
    public Transaction getRefundTransaction() {
        return refundTransaction;
    }

    @Column(name = "BANK_TRANSFER_ID", length = 45)
    public String getBankTransferIdentifier() {
        return bankTransferIdentifier;
    }

    @Column(name = "CREDIT_CARD_ID", length = 45)
    public String getCreditCardIdentifier() {
        return creditCardIdentifier;
    }

    @Column(name = "OTHER_ID", length = 45)
    public String getOtherIdentifier() {
        return otherIdentifier;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCheckIdentifier(String checkIdentifier) {
        this.checkIdentifier = checkIdentifier;
    }

    public void setRefundAccount(Account refundAccount) {
        this.refundAccount = refundAccount;
    }

    public void setRefundTransaction(Transaction refundTransaction) {
        this.refundTransaction = refundTransaction;
    }

    public void setBankTransferIdentifier(String bankTransferIdentifier) {
        this.bankTransferIdentifier = bankTransferIdentifier;
    }

    public void setCreditCardIdentifier(String creditCardIdentifier) {
        this.creditCardIdentifier = creditCardIdentifier;
    }

    public void setOtherIdentifier(String otherIdentifier) {
        this.otherIdentifier = otherIdentifier;
    }
}
