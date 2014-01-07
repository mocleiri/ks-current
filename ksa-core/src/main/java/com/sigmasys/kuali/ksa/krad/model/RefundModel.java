package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.Refund;
import com.sigmasys.kuali.ksa.model.RefundManifest;
import com.sigmasys.kuali.ksa.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class represents a single row in the "Refund Status" table on the Account Refund or Batch Refund screen.
 *
 * User: Sergey
 * Date: 5/18/13
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefundModel extends TransactionModel {

    /**
     * Associated refund.
     */
    private Refund refund;

    /**
     * Refund Manifest associated with the Refund.
     */
    private RefundManifest refundManifest;

    /**
     * Refunds in the same group if any.
     */
    private List<RefundModel> refundGroup;

    /**
     * Name of the user, who authorized the Refund.
     */
    private String authorizedByName;

    /**
     * Name of the user, who requested the Refund.
     */
    private String requestedByName;

    /**
     * Whether this Refund is selected for Verification or Cancellation.
     */
    private boolean selected;

    /**
     * Refund Verification object.
     */
    private RefundVerification refundVerification;



    public RefundModel() {}

    public RefundModel(Transaction transaction) {
        super(transaction);
    }

    /**
     * The displayable value of the refund status.
     */
    private String refundStatusDisplay;


    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getRefundStatusDisplay() {
        return refundStatusDisplay;
    }

    public void setRefundStatusDisplay(String refundStatusDisplay) {
        this.refundStatusDisplay = refundStatusDisplay;
    }

    public RefundManifest getRefundManifest() {
        return refundManifest;
    }

    public void setRefundManifest(RefundManifest refundManifest) {
        this.refundManifest = refundManifest;
    }

    public List<RefundModel> getRefundGroup() {
        return refundGroup;
    }

    public void setRefundGroup(List<RefundModel> refundGroup) {
        this.refundGroup = refundGroup;
    }

    public String getAuthorizedByName() {
        return authorizedByName;
    }

    public void setAuthorizedByName(String authorizedByName) {
        this.authorizedByName = authorizedByName;
    }

    public String getRequestedByName() {
        return requestedByName;
    }

    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
    }

    public RefundVerification getRefundVerification() {
        return refundVerification;
    }

    public void setRefundVerification(RefundVerification refundVerification) {
        this.refundVerification = refundVerification;
    }

    public static class RefundVerification {
        String statusCode;
        BigDecimal amount;
        String overrideDescription;

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getOverrideDescription() {
            return overrideDescription;
        }

        public void setOverrideDescription(String overrideDescription) {
            this.overrideDescription = overrideDescription;
        }
    }

}
