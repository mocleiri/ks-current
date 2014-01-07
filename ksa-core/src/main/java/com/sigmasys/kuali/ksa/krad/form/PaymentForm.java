package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.krad.model.TransactionModel;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.CreditType;
import com.sigmasys.kuali.ksa.model.Memo;
import com.sigmasys.kuali.ksa.model.Payment;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * Created by: dmulderink on 9/30/12 at 11:27 AM
 */
public class PaymentForm extends AbstractViewModel {

   private Account account;

   private TransactionModel payment;

   private String paymentTransactionTypeId;

   private CreditType transactionType;

   private String printReceipt;

   private String emailReceipt;

   private BigDecimal estimatedCurrentBalance;

   private BigDecimal estimatedPendingBalance;

   private String statusMessage;

   private String transactionTypeMessage;

    private List<TransactionModel> allocations;

    private String systemCurrency;
    private KeyValuesFinder currencyOptionsFinder;
    private KeyValuesFinder rollupOptionsFinder;
    private String currencyId;
    private String systemCurrencyId;
    private String rollupId;

    private Memo memoModel;
    private boolean ageAccount;
    private boolean allocatePayment;
    private boolean runPaymentApplication = true;
    private boolean addAdditionalPayment;


    /*
      Get / Set methods
    */


   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   public TransactionModel getPayment() {
       if(payment == null){
           payment = new TransactionModel(new Payment());
       }
      return payment;
   }

   public void setPayment(TransactionModel payment) {
      this.payment = payment;
   }

   public String getPaymentTransactionTypeId() {
      return paymentTransactionTypeId;
   }

   public void setPaymentTransactionTypeId(String paymentTransactionTypeId) {
      this.paymentTransactionTypeId = paymentTransactionTypeId;
   }

   public String getPrintReceipt() {
      return printReceipt;
   }

   public void setPrintReceipt(String printReceipt) {
      this.printReceipt = printReceipt;
   }

   public String getEmailReceipt() {
      return emailReceipt;
   }

   public void setEmailReceipt(String emailReceipt) {
      this.emailReceipt = emailReceipt;
   }

   public BigDecimal getEstimatedCurrentBalance() {
      return estimatedCurrentBalance;
   }

   public void setEstimatedCurrentBalance(BigDecimal estimatedCurrentBalance) {
      this.estimatedCurrentBalance = estimatedCurrentBalance;
   }

   public BigDecimal getEstimatedPendingBalance() {
      return estimatedPendingBalance;
   }

   public void setEstimatedPendingBalance(BigDecimal estimatedPendingBalance) {
      this.estimatedPendingBalance = estimatedPendingBalance;
   }

   public String getStatusMessage() {
      return statusMessage;
   }

   public void setStatusMessage(String statusMessage) {
      this.statusMessage = statusMessage;
   }

    public CreditType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(CreditType transactionType) {
        this.transactionType = transactionType;
    }

    public String getExternalId(){
        if(payment != null){
            return payment.getExternalId();
        }
        return "";
    }

    public void setExternalId(String externalId){
        if(this.payment != null){
            this.payment.setExternalId(externalId);
        }
    }


    public String getPaymentTransactionTypeId2() {
        if(this.transactionType != null){
            return this.transactionType.getId().getId();
        }
        return "";
    }

    public String getDescription() {
        if(this.transactionType != null){
            return transactionType.getDescription();
        }
        return "";
    }

    public String getRefundable() {
        if(this.transactionType != null){
            return transactionType.isRefundable() ? "Yes" : "No";
        }
        return "";
    }

    public String getDefaultClearingPeriod() {
        if(this.transactionType != null){
            return transactionType.getClearPeriod().toString() + " days";
        }
        return "";
    }

    public String getRefundRule(){
        if(this.transactionType != null){
            return transactionType.getRefundRule();
        }
        return "";
    }

    public String getAuthorizationText(){
        if(this.transactionType != null){
            return transactionType.getAuthorizationText();
        }
        return "";
    }


    public String getTransactionTypeMessage() {
        return transactionTypeMessage;
    }

    public void setTransactionTypeMessage(String transactionTypeMessage) {
        this.transactionTypeMessage = transactionTypeMessage;
    }

    public boolean isTransactionTypeValid() {
        if(transactionType == null){
            return false;
        }
        return true;
    }

    public String getSystemCurrency() {
        if(systemCurrency == null){
            systemCurrency = java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        }
        return systemCurrency;
    }

    public void setSystemCurrency(String systemCurrency) {
        this.systemCurrency = systemCurrency;
    }

    public KeyValuesFinder getCurrencyOptionsFinder() {
        return currencyOptionsFinder;
    }

    public void setCurrencyOptionsFinder(KeyValuesFinder currencyOptionsFinder) {
        this.currencyOptionsFinder = currencyOptionsFinder;
    }

    public KeyValuesFinder getRollupOptionsFinder() {
        return rollupOptionsFinder;
    }

    public void setRollupOptionsFinder(KeyValuesFinder rollupOptionsFinder) {
        this.rollupOptionsFinder = rollupOptionsFinder;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
        this.systemCurrencyId = currencyId;
    }

    public String getSystemCurrencyId(){
        return this.systemCurrencyId;
    }

    public Boolean getInternal(){
        return payment.isInternal();
    }

    public void setInternal(Boolean internal){
        payment.setInternal(internal);
    }

    public String getRollupId() {
        return rollupId;
    }

    public void setRollupId(String rollupId) {
        this.rollupId = rollupId;
    }

    public boolean isAgeAccount() {
        return ageAccount;
    }

    public void setAgeAccount(boolean ageAccount) {
        this.ageAccount = ageAccount;
    }

    public boolean isAllocatePayment() {
        return allocatePayment;
    }

    public void setAllocatePayment(boolean allocatePayment) {
        this.allocatePayment = allocatePayment;
    }

    public boolean isAddAdditionalPayment() {
        return addAdditionalPayment;
    }

    public void setAddAdditionalPayment(boolean addAdditionalPayment) {
        this.addAdditionalPayment = addAdditionalPayment;
    }

    public Memo getMemoModel() {
        return memoModel;
    }

    public void setMemoModel(Memo memoModel) {
        this.memoModel = memoModel;
    }

    public List<TransactionModel> getAllocations(){
        return this.allocations;
    }

    public void setAllocations(List<TransactionModel> models) {
        this.allocations = models;
    }

    public boolean isRunPaymentApplication() {
        return runPaymentApplication;
    }

    public void setRunPaymentApplication(boolean runPaymentApplication) {
        this.runPaymentApplication = runPaymentApplication;
    }
}
