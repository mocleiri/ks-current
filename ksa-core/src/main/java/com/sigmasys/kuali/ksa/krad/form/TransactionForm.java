package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.krad.model.InformationModel;
import com.sigmasys.kuali.ksa.krad.model.TransactionModel;
import com.sigmasys.kuali.ksa.krad.util.TransactionZeroBalanceKeyValues;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.Currency;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

public class TransactionForm extends AbstractViewModel {


    private String statusMessage;


    private Tree<Memo, String> memoTree = new Tree<Memo, String>();

    private List<TransactionModel> rollupTransactions;
    private List<TransactionModel> allTransactions;

    private TransactionModel currentTransaction;
    private List<TransactionModel> currrentTransactionAllocations = new ArrayList<TransactionModel>();

    private List<TransactionModel> deferments;

    private Boolean    showInternal = Boolean.FALSE;
    private Date       startingDate;
    private BigDecimal startingBalance = BigDecimal.ZERO;
    private Date       endingDate;
    private BigDecimal endingBalance = BigDecimal.ZERO;

    private BigDecimal chargeTotal;
    private BigDecimal paymentTotal;
    private BigDecimal defermentTotal;
    private BigDecimal allocatedTotal;
    private BigDecimal unallocatedTotal;

    private String newTag;
    private List<Tag> filterTags;

    private String       zeroBalanceDate;
    private Set<Date> zeroBalanceDates;


    // Not sure if any of these below this line are still used.


    // use this object as a query argument for matching transactions by student name
    private String studentLookupByName;

    private String selectedPersonName;

    // result set of matching persons and address postal information
    private List<Account> accountBrowseList;


    private List<Charge> chargeList;

    // result set of charges
    private List<Payment> paymentList;

    // a single charge
    private Charge charge;

    // a single payment
    private Payment payment;

    // Currency stuff
    private List<Currency> currencies;

    private Currency currency;

    private String code;

    private String currencyName;

    private String currencyDescription;

    // a list of activities
    private List<Activity> activities;

    /**
     * Get the student name
     * Possible uses is a query match for transactions
     * The value can be a partial matching name
     *
     * @return
     */
    public String getStudentLookupByName() {
        return studentLookupByName;
    }

    /**
     * Set the student lookup name
     * Possible uses is a query match for transactions
     * The value can be a partial matching name
     *
     * @param studentLookupByName
     */
    public void setStudentLookupByName(String studentLookupByName) {
        this.studentLookupByName = studentLookupByName;
    }

    /**
     * Get the selected person name
     *
     * @return
     */
    public String getSelectedPersonName() {
        return selectedPersonName;
    }

    /**
     * Set the selected person name
     *
     * @param selectedPersonName
     */
    public void setSelectedPersonName(String selectedPersonName) {
        this.selectedPersonName = selectedPersonName;
    }

    /**
     * Get the accountBrowseList
     * Encapsulates Person and Address model
     *
     * @return
     */
    public List<Account> getAccountBrowseList() {
        return accountBrowseList;
    }

    /**
     * Set the accountBrowseList
     * Encapsulates Person and Address model
     *
     * @param accountBrowseList
     */
    public void setAccountBrowseList(List<Account> accountBrowseList) {
        this.accountBrowseList = accountBrowseList;
    }

    /**
     * Get the list of Charges found via a selected Account
     *
     * @return
     */
    public List<Charge> getChargeList() {
        return chargeList;
    }

    /**
     * Set the list of Charges found via a selected Account
     *
     * @param chargeList
     */
    public void setChargeList(List<Charge> chargeList) {
        this.chargeList = chargeList;
    }

    /**
     * Get the list of Payments found via a selected Account
     *
     * @return
     */
    public List<Payment> getPaymentList() {
        return paymentList;
    }

    /**
     * Set the list of Payments found via a selected Account
     *
     * @param paymentList
     */
    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    /**
     * Get a charge object
     *
     * @return
     */
    public Charge getCharge() {
        return charge;
    }

    /**
     * Set a charge object
     *
     * @param charge
     */
    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    /**
     * Get a Payment object
     *
     * @return
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Set a Payment object
     *
     * @param payment
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    // Currency get/Set methods

    /**
     * Get the list of Currency objects
     *
     * @return
     */
    public List<Currency> getCurrencies() {
        return currencies;
    }

    /**
     * Set the list of Currency objects
     *
     * @param currencies
     */
    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    /**
     * Get the Currency model object
     *
     * @return
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Set teh Currency model object
     *
     * @param currency
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * Get the ISO symbol
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the ISO symbol
     *
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get the currencyName
     *
     * @return
     */
    public String getCurrencyName() {
        return currencyName;
    }

    /**
     * Set the Name
     *
     * @param currencyName
     */
    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    /**
     * Get the currency description
     *
     * @return
     */
    public String getCurrencyDescription() {
        return currencyDescription;
    }

    /**
     * Set  the currency description
     *
     * @param currencyDescription
     */
    public void setCurrencyDescription(String currencyDescription) {
        this.currencyDescription = currencyDescription;
    }

    /**
     * Get a list of activities
     *
     * @return
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * Set a list of activities
     *
     * @param activities
     */
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<TransactionModel> getRollupTransactions() {
        return rollupTransactions;
    }

    public void setRollupTransactions(List<TransactionModel> rollupTransactions) {
        this.rollupTransactions = rollupTransactions;
    }

    public List<TransactionModel> getAllTransactions() {
        return allTransactions;
    }

    public void setAllTransactions(List<TransactionModel> allTransactions) {
        this.allTransactions = allTransactions;
    }

    public BigDecimal getStartingBalance() {
        if (this.startingBalance == null) {
            this.startingBalance = BigDecimal.ZERO;
        }
        return startingBalance;
    }

    public String getFormattedStartingBalance() {
        NumberFormat percentFormat = NumberFormat.getCurrencyInstance();
        return percentFormat.format(this.getStartingBalance());
    }

    public void setStartingBalance(BigDecimal startingBalance) {
        this.startingBalance = startingBalance;
    }

    public BigDecimal getEndingBalance() {
        if (this.endingBalance == null) {
            this.endingBalance = BigDecimal.ZERO;
        }
        return endingBalance;
    }

    public void setEndingBalance(BigDecimal endingBalance) {
        this.endingBalance = endingBalance;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public List<TransactionModel> getDeferments() {
        if(deferments == null){
            return new ArrayList<TransactionModel>();
        }
        return deferments;
    }

    public void setDeferments(List<TransactionModel> deferments) {
        this.deferments = deferments;
    }

    public BigDecimal getChargeTotal() {
        if(this.chargeTotal == null){
            this.chargeTotal = BigDecimal.ZERO;
        }
        return chargeTotal;
    }

    public void setChargeTotal(BigDecimal chargeTotal) {
        this.chargeTotal = chargeTotal;
    }

    public void addChargeTotal(BigDecimal chargeTotal){
        this.chargeTotal = this.getChargeTotal().add(chargeTotal);
    }

    public BigDecimal getPaymentTotal() {
        if(this.paymentTotal == null){
            this.paymentTotal = BigDecimal.ZERO;
        }
        return paymentTotal;
    }

    public void setPaymentTotal(BigDecimal paymentTotal) {
        this.paymentTotal = paymentTotal;
    }

    public void addPaymentTotal(BigDecimal paymentTotal){
        this.paymentTotal = this.getPaymentTotal().add(paymentTotal);
    }

    public BigDecimal getDefermentTotal() {
        if(defermentTotal == null){
            defermentTotal = BigDecimal.ZERO;
        }
        return defermentTotal;
    }

    public void setDefermentTotal(BigDecimal defermentTotal) {
        this.defermentTotal = defermentTotal;
    }

    public void addDefermentTotal(BigDecimal defermentTotal){
        this.defermentTotal = this.getDefermentTotal().add(defermentTotal);
    }

    public BigDecimal getAllocatedTotal() {
        if(allocatedTotal == null){
            allocatedTotal = BigDecimal.ZERO;
        }
        return allocatedTotal;
    }

    public void setAllocatedTotal(BigDecimal allocatedTotal) {
        this.allocatedTotal = allocatedTotal;
    }

    public void addAllocatedTotal(BigDecimal allocatedTotal){
        this.allocatedTotal = this.getAllocatedTotal().add(allocatedTotal);
    }

    public void subtractAllocatedTotal(BigDecimal allocatedTotal){
        this.allocatedTotal = this.getAllocatedTotal().subtract(allocatedTotal);
    }

    public BigDecimal getUnallocatedTotal() {
        if(unallocatedTotal == null){
            unallocatedTotal = BigDecimal.ZERO;
        }
        return unallocatedTotal;
    }

    public void setUnallocatedTotal(BigDecimal unallocatedTotal) {
        this.unallocatedTotal = unallocatedTotal;
    }

    public void addUnallocatedTotal(BigDecimal unallocatedTotal){
        this.unallocatedTotal = this.getUnallocatedTotal().add(unallocatedTotal);
    }

    public void subtractUnallocatedTotal(BigDecimal unallocatedTotal){
        this.unallocatedTotal = this.getUnallocatedTotal().subtract(unallocatedTotal);
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

    public TransactionModel getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(TransactionModel currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    public List<TransactionModel> getCurrrentTransactionAllocations() {
        return currrentTransactionAllocations;
    }

    public void setCurrrentTransactionAllocations(List<TransactionModel> currrentTransactionAllocations) {
        this.currrentTransactionAllocations = currrentTransactionAllocations;
    }

    public Boolean getShowInternal() {
        return showInternal;
    }

    public void setShowInternal(Boolean showInternal) {
        this.showInternal = showInternal;
    }

    public Set<Date> getZeroBalanceDates() {
        if(zeroBalanceDates == null) {
            zeroBalanceDates = new HashSet<Date>();
        }
        return zeroBalanceDates;
    }

    public void setZeroBalanceDates(Set<Date> zeroBalanceDates) {
        this.zeroBalanceDates = zeroBalanceDates;
    }

    public KeyValuesFinder getZeroBalanceDateFinder() {
        // Don't cache the values finder or else new entries will not show when added

        TransactionZeroBalanceKeyValues keyValues = new TransactionZeroBalanceKeyValues();
        keyValues.setBlankOption(true);
        keyValues.setValues(new ArrayList<Date>(this.getZeroBalanceDates()));
        return keyValues;
    }


    public String getZeroBalanceDate() {
        return zeroBalanceDate;
    }

    public void setZeroBalanceDate(String zeroBalanceDate) {
        this.zeroBalanceDate = zeroBalanceDate;
    }
}
