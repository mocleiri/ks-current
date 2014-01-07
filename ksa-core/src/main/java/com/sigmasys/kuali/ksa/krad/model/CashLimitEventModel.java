package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.AccountProtectedInfo;
import com.sigmasys.kuali.ksa.model.CashLimitEvent;
import com.sigmasys.kuali.ksa.model.Payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * A model object for the "Combined Cash Limit" collection of CashLimitEvents.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 11/5/13
 * Time: 3:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class CashLimitEventModel implements Serializable {

    /**
     * A CashLimitEvent object.
     */
    private CashLimitEvent cashLimitEvent;

    /**
     * Event date formatted as a String
     */
    private String eventDateString;

    /**
     * Whether this row is marked selected.
     */
    private boolean selected;

    /**
     * CashLimitEvent status string for display.
     */
    private String statusString;

    /**
     * Total amount of Payments.
     */
    private BigDecimal totalPayments;

    /**
     * Total amount of Allocations
     */
    private BigDecimal totalAllocations;

    /**
     * Birth date of the Account holder.
     */
    private String dateOfBirthString;

    /**
     * Account holder
     */
    private Account account;

    /**
     * Account Protected Information.
     */
    private AccountProtectedInfo accountProtectedInfo;

    /**
     * A list of Payments belonging to the CashLimitEvent object.
     */
    private List<Payment> payments;



    public CashLimitEvent getCashLimitEvent() {
        return cashLimitEvent;
    }

    public void setCashLimitEvent(CashLimitEvent cashLimitEvent) {
        this.cashLimitEvent = cashLimitEvent;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public String getEventDateString() {
        return eventDateString;
    }

    public void setEventDateString(String eventDateString) {
        this.eventDateString = eventDateString;
    }

    public BigDecimal getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(BigDecimal totalPayments) {
        this.totalPayments = totalPayments;
    }

    public BigDecimal getTotalAllocations() {
        return totalAllocations;
    }

    public void setTotalAllocations(BigDecimal totalAllocations) {
        this.totalAllocations = totalAllocations;
    }

    public AccountProtectedInfo getAccountProtectedInfo() {
        return accountProtectedInfo;
    }

    public void setAccountProtectedInfo(AccountProtectedInfo accountProtectedInfo) {
        this.accountProtectedInfo = accountProtectedInfo;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getDateOfBirthString() {
        return dateOfBirthString;
    }

    public void setDateOfBirthString(String dateOfBirthString) {
        this.dateOfBirthString = dateOfBirthString;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
