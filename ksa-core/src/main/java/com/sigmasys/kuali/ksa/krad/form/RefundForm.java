package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.krad.model.PotentialRefundModel;
import com.sigmasys.kuali.ksa.krad.model.RefundModel;
import com.sigmasys.kuali.ksa.krad.model.RequestPotentialRefundSummaryModel;
import com.sigmasys.kuali.ksa.model.Tag;

import java.util.Date;
import java.util.List;

/**
 * The form object behind the Batch and Account Refund screens.
 */
public class RefundForm extends AbstractViewModel {

    /*
    Lookup Account and Tag support.
     */
    private String newAccount;
    private String newTag;

    /**
     * A list of Tags to filter Transactions on.
     */
    private List<Tag> filterTags;

    /**
     * Refund status to filter on.
     * A <code>null</code> refund status means "Show All Status Types".
     */
    private String filterRefundStatusCode;

    /**
     * A list of PotentialRefundModel objects.
     */
    private List<PotentialRefundModel> potentialRefunds;

    /**
     * A list of all Refunds displayed on the "Refund Status" tab.
     */
    private List<RefundModel> allRefunds;

    /**
     * Start of the filtering date range. (Optional)
     */
    private Date filterDateFrom;

    /**
     * End of the filter date range
     */
    private Date filterDateTo;

    /**
     * Whether to run the Payment Application.
     */
    private boolean runPaymentApplication;

    /**
     * Type of the Date range to find transactions.
     * @see com.sigmasys.kuali.ksa.krad.util.DateRangeFilterKeyValuesFinder
     */
    private String dateRangeType;

    /**
     * Potential Refund request summary.
     */
    private RequestPotentialRefundSummaryModel requestRefundSummary;

    /**
     * Whether this form is a for a batch Refund.
     */
    private boolean isBatch;


    public boolean isBatch() {
        return isBatch;
    }

    public void setBatch(boolean batch) {
        isBatch = batch;
    }

    public String getNewAccount() {
        return newAccount;
    }

    public void setNewAccount(String newAccount) {
        this.newAccount = newAccount;
    }

    public String getNewTag() {
        return newTag;
    }

    public void setNewTag(String newTag) {
        this.newTag = newTag;
    }

    public String getFilterRefundStatusCode() {
        return filterRefundStatusCode;
    }

    public void setFilterRefundStatusCode(String refundStatusCode) {
        this.filterRefundStatusCode = refundStatusCode;
    }

    public List<PotentialRefundModel> getPotentialRefunds() {
        return potentialRefunds;
    }

    public void setPotentialRefunds(List<PotentialRefundModel> potentialRefunds) {
        this.potentialRefunds = potentialRefunds;
    }

    public Date getFilterDateFrom() {
        return filterDateFrom;
    }

    public void setFilterDateFrom(Date filterDateFrom) {
        this.filterDateFrom = filterDateFrom;
    }

    public Date getFilterDateTo() {
        return filterDateTo;
    }

    public void setFilterDateTo(Date filterDateTo) {
        this.filterDateTo = filterDateTo;
    }

    public List<RefundModel> getAllRefunds() {
        return allRefunds;
    }

    public void setAllRefunds(List<RefundModel> allRefunds) {
        this.allRefunds = allRefunds;
    }

    public boolean isRunPaymentApplication() {
        return runPaymentApplication;
    }

    public void setRunPaymentApplication(boolean runPaymentApplication) {
        this.runPaymentApplication = runPaymentApplication;
    }

    public String getDateRangeType() {
        return dateRangeType;
    }

    public void setDateRangeType(String dateRangeType) {
        this.dateRangeType = dateRangeType;
    }

    public RequestPotentialRefundSummaryModel getRequestRefundSummary() {
        return requestRefundSummary;
    }

    public void setRequestRefundSummary(RequestPotentialRefundSummaryModel requestRefundSummary) {
        this.requestRefundSummary = requestRefundSummary;
    }

    public List<Tag> getFilterTags() {
        return filterTags;
    }

    public void setFilterTags(List<Tag> filterTags) {
        this.filterTags = filterTags;
    }
}
