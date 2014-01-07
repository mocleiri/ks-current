package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.model.Tag;
import com.sigmasys.kuali.ksa.model.TransactionStatus;

import java.util.Date;
import java.util.List;

/**
 * This class encapsulates the state of a Transaction Filter form.
 * Such a form has a capability to perform the following filtering:
 *
 * 1. Account filtering
 * 2. Date range filtering
 * 3. Tag filtering
 * 4. Transaction status filtering
 *
 * More can be added by subclasses as needed. Such as custom date range filtering.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 11/5/13
 * Time: 2:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionFilterForm extends AbstractViewModel {

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
     * Type of the Date range to find transactions.
     * @see com.sigmasys.kuali.ksa.krad.util.DateRangeFilterKeyValuesFinder
     */
    private String dateRangeType;

    /**
     * Start of the filtering date range. (Optional)
     */
    private Date filterDateFrom;

    /**
     * End of the filter date range
     */
    private Date filterDateTo;

    /**
     * Transaction status.
     */
    private TransactionStatus filterTransactionStatus;


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

    public List<Tag> getFilterTags() {
        return filterTags;
    }

    public void setFilterTags(List<Tag> filterTags) {
        this.filterTags = filterTags;
    }

    public String getDateRangeType() {
        return dateRangeType;
    }

    public void setDateRangeType(String dateRangeType) {
        this.dateRangeType = dateRangeType;
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

    public TransactionStatus getFilterTransactionStatus() {
        return filterTransactionStatus;
    }

    public void setFilterTransactionStatus(TransactionStatus filterTransactionStatus) {
        this.filterTransactionStatus = filterTransactionStatus;
    }
}
