package com.sigmasys.kuali.ksa.krad.form;

//import com.sigmasys.kuali.ksa.krad.model.PaymentBillingDateModel;

import com.sigmasys.kuali.ksa.krad.model.*;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.ThirdPartyAccount;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingPlan;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyPlan;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: tbornholtz
 * Date: 8/16/13
 */
public class PaymentPlanForm extends AbstractViewModel {

    private Account account;

    private List<ThirdPartyAllowableChargeModel> thirdPartyAllowableCharges;
    private List<PaymentBillingAllowableChargeModel> paymentBillingAllowableCharges;

    private String filterPlanName;
    private List<ThirdPartyPlan> filterThirdPartyPlans;
    private List<PaymentBillingPlan> filterPaymentBillingPlans;

    private List<PaymentBillingDateModel> paymentBillingDates;

    private String filterThirdPartyAccount;
    private List<ThirdPartyAccount> filterThirdPartyAccounts;

    private List<ThirdPartyPlanModel> thirdPartyPlans;
    private ThirdPartyPlan batchThirdPartyPlan;

    private List<PaymentBillingPlanModel> paymentBillingPlans;
    private PaymentBillingPlan paymentBillingPlan;

    private String planName;
    private String batchAccounts;
    private String forceReversal;

    private ThirdPartyPlanModel thirdPartyPlan;
    private TransactionTransferModel thirdPartyTransactionTransfer;

    private String responsibleAccount;
    private String responsibleAccountMessage;
    private String transferType;

    private KeyValuesFinder transferTypeOptionsFinder;
    private KeyValuesFinder rollupOptionsFinder;


    // Fields shared with both new Third Party Plan and new Payment Billing Plan
    private String code;
    private String name;
    private String description;


    // Fields for a new Third Party plan
    private ThirdPartyPlan     newThirdPartyPlan;
    private ThirdPartyAccount thirdPartyAccount;
    private Date openPeriodStartDate;
    private Date openPeriodEndDate;
    private Date chargePeriodStartDate;
    private Date chargePeriodEndDate;
    private BigDecimal maxAmount;
    private Date effectiveDate;
    private Date recognitionDate;

    // Fields for a new Payment Billing Plan
    private PaymentBillingPlan newPaymentBillingPlan;

    private String flatFeeTransactionType;
    private String variableFeeTransactionType;
    private String nonRoundedPaymentType;
    private String postToGeneralLedger;
    private String lateMembership;
    private String prefixStatement;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public PaymentBillingPlan getNewPaymentBillingPlan() {
        if(newPaymentBillingPlan == null){
            newPaymentBillingPlan = new PaymentBillingPlan();
        }
        return newPaymentBillingPlan;
    }

    public void setNewPaymentBillingPlan(PaymentBillingPlan newPaymentBillingPlan) {
        this.newPaymentBillingPlan = newPaymentBillingPlan;
    }

    public String getResponsibleAccount() {
        return responsibleAccount;
    }

    public void setResponsibleAccount(String responsibleAccount) {
        this.responsibleAccount = responsibleAccount;
    }

    public String getResponsibleAccountMessage() {
        if(responsibleAccount == null){
            responsibleAccountMessage = "No responsible account specified";
        }
        return responsibleAccountMessage;
    }

    public void setResponsibleAccountMessage(String responsibleAccountMessage) {
        this.responsibleAccountMessage = responsibleAccountMessage;
    }

    public ThirdPartyPlan getNewThirdPartyPlan() {
        if(newThirdPartyPlan == null){
            newThirdPartyPlan = new ThirdPartyPlan();
        }
        return newThirdPartyPlan;
    }

    public void setNewThirdPartyPlan(ThirdPartyPlan newThirdPartyPlan) {
        this.newThirdPartyPlan = newThirdPartyPlan;
    }

    public List<ThirdPartyAllowableChargeModel> getThirdPartyAllowableCharges() {
        if(thirdPartyAllowableCharges == null){
            thirdPartyAllowableCharges = new ArrayList<ThirdPartyAllowableChargeModel>();
        }
        return thirdPartyAllowableCharges;
    }

    public void setThirdPartyAllowableCharges(List<ThirdPartyAllowableChargeModel> thirdPartyAllowableCharges) {
        this.thirdPartyAllowableCharges = thirdPartyAllowableCharges;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public List<ThirdPartyPlan> getFilterThirdPartyPlans() {
        if(this.filterThirdPartyPlans == null){
            this.filterThirdPartyPlans = new ArrayList<ThirdPartyPlan>();
        }
        return filterThirdPartyPlans;
    }

    public void setFilterThirdPartyPlans(List<ThirdPartyPlan> filterThirdPartyPlans) {
        this.filterThirdPartyPlans = filterThirdPartyPlans;
    }

    public String getFilterPlanName() {
        return filterPlanName;
    }

    public void setFilterPlanName(String filterPlanName) {
        this.filterPlanName = filterPlanName;
    }

    public String getFilterThirdPartyAccount() {
        return filterThirdPartyAccount;
    }

    public void setFilterThirdPartyAccount(String filterThirdPartyAccount) {
        this.filterThirdPartyAccount = filterThirdPartyAccount;
    }

    public List<ThirdPartyAccount> getFilterThirdPartyAccounts() {
        if(this.filterThirdPartyAccounts == null){
            this.filterThirdPartyAccounts = new ArrayList<ThirdPartyAccount>();
        }
        return filterThirdPartyAccounts;
    }

    public void setFilterThirdPartyAccounts(List<ThirdPartyAccount> filterThirdPartyAccounts) {
        this.filterThirdPartyAccounts = filterThirdPartyAccounts;
    }

    public List<ThirdPartyPlanModel> getThirdPartyPlans() {
        if(thirdPartyPlans == null) {
            thirdPartyPlans = new ArrayList<ThirdPartyPlanModel>();
        }
        return thirdPartyPlans;
    }

    public void setThirdPartyPlans(List<ThirdPartyPlanModel> thirdPartyPlans) {
        this.thirdPartyPlans = thirdPartyPlans;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ThirdPartyAccount getThirdPartyAccount() {
        if(thirdPartyAccount == null){
            thirdPartyAccount = new ThirdPartyAccount();
        }
        return thirdPartyAccount;
    }

    public void setThirdPartyAccount(ThirdPartyAccount thirdPartyAccount) {
        this.thirdPartyAccount = thirdPartyAccount;
    }

    public Date getOpenPeriodStartDate() {
        return openPeriodStartDate;
    }

    public void setOpenPeriodStartDate(Date openPeriodStartDate) {
        this.openPeriodStartDate = openPeriodStartDate;
    }

    public Date getOpenPeriodEndDate() {
        return openPeriodEndDate;
    }

    public void setOpenPeriodEndDate(Date openPeriodEndDate) {
        this.openPeriodEndDate = openPeriodEndDate;
    }

    public Date getChargePeriodStartDate() {
        return chargePeriodStartDate;
    }

    public void setChargePeriodStartDate(Date chargePeriodStartDate) {
        this.chargePeriodStartDate = chargePeriodStartDate;
    }

    public Date getChargePeriodEndDate() {
        return chargePeriodEndDate;
    }

    public void setChargePeriodEndDate(Date chargePeriodEndDate) {
        this.chargePeriodEndDate = chargePeriodEndDate;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getRecognitionDate() {
        return recognitionDate;
    }

    public void setRecognitionDate(Date recognitionDate) {
        this.recognitionDate = recognitionDate;
    }

    public KeyValuesFinder getTransferTypeOptionsFinder() {
        return transferTypeOptionsFinder;
    }

    public void setTransferTypeOptionsFinder(KeyValuesFinder transferTypeOptionsFinder) {
        this.transferTypeOptionsFinder = transferTypeOptionsFinder;
    }

    public ThirdPartyPlanModel getThirdPartyPlan() {
        return thirdPartyPlan;
    }

    public void setThirdPartyPlan(ThirdPartyPlanModel thirdPartyPlan) {
        this.thirdPartyPlan = thirdPartyPlan;
    }

    public List<PaymentBillingAllowableChargeModel> getPaymentBillingAllowableCharges() {
        if(paymentBillingAllowableCharges == null) {
            paymentBillingAllowableCharges = new ArrayList<PaymentBillingAllowableChargeModel>();
        }
        return paymentBillingAllowableCharges;
    }

    public void setPaymentBillingAllowableCharges(List<PaymentBillingAllowableChargeModel> paymentBillingAllowableCharges) {
        this.paymentBillingAllowableCharges = paymentBillingAllowableCharges;
    }

    public List<PaymentBillingDateModel> getPaymentBillingDates() {
        if(paymentBillingDates == null) {
            paymentBillingDates = new ArrayList<PaymentBillingDateModel>();
        }
        return paymentBillingDates;
    }

    public void setPaymentBillingDates(List<PaymentBillingDateModel> paymentBillingDates) {
        this.paymentBillingDates = paymentBillingDates;
    }

    public KeyValuesFinder getRollupOptionsFinder() {
        return rollupOptionsFinder;
    }

    public void setRollupOptionsFinder(KeyValuesFinder rollupOptionsFinder) {
        this.rollupOptionsFinder = rollupOptionsFinder;
    }

    public BigDecimal getFlatFeeAmount() {
        return newPaymentBillingPlan.getFlatFeeAmount();
    }

    public void setFlatFeeAmount(BigDecimal flatFeeAmount) {
        getNewPaymentBillingPlan().setFlatFeeAmount(flatFeeAmount);
    }

    public String getFlatFeeTransactionType() {
        return flatFeeTransactionType;
    }

    public void setFlatFeeTransactionType(String flatFeeTransactionType) {
        this.flatFeeTransactionType = flatFeeTransactionType;
    }

    public BigDecimal getVariableFeePercentage() {
        return getNewPaymentBillingPlan().getVariableFeeAmount();
    }

    public void setVariableFeePercentage(BigDecimal variableFeePercentage) {
        getNewPaymentBillingPlan().setVariableFeeAmount(variableFeePercentage);
    }

    public String getVariableFeeTransactionType() {
        return variableFeeTransactionType;
    }

    public void setVariableFeeTransactionType(String variableFeeTransactionType) {
        this.variableFeeTransactionType = variableFeeTransactionType;
    }

    public BigDecimal getMinFeeAmount() {
        return getNewPaymentBillingPlan().getMinFeeAmount();
    }

    public void setMinFeeAmount(BigDecimal minimumAmount) {
        getNewPaymentBillingPlan().setMinFeeAmount(minimumAmount);
    }

    public BigDecimal getMaxFeeAmount() {
        return getNewPaymentBillingPlan().getMaxFeeAmount();
    }

    public void setMaxFeeAmount(BigDecimal maximumAmount) {
        getNewPaymentBillingPlan().setMaxFeeAmount(maximumAmount);
    }

    public Integer getRoundingFactor() {
        return getNewPaymentBillingPlan().getRoundingFactor();
    }

    public void setRoundingFactor(Integer roundingFactor) {
        getNewPaymentBillingPlan().setRoundingFactor(roundingFactor);
    }

    public String getNonRoundedPaymentType() {
        return nonRoundedPaymentType;
    }

    public void setNonRoundedPaymentType(String nonRoundedPaymentType) {
        this.nonRoundedPaymentType = nonRoundedPaymentType;
    }

    public String getPostToGeneralLedger() {
        return postToGeneralLedger;
    }

    public void setPostToGeneralLedger(String postToGeneralLedger) {
        this.postToGeneralLedger = postToGeneralLedger;
    }

    public String getLateMembership() {
        return lateMembership;
    }

    public void setLateMembership(String lateMembership) {
        this.lateMembership = lateMembership;
    }

    public String getPrefixStatement() {
        return getNewPaymentBillingPlan().getStatementPrefix();
    }

    public void setPrefixStatement(String prefixStatement) {
        getNewPaymentBillingPlan().setStatementPrefix(prefixStatement);
    }

    public List<PaymentBillingPlan> getFilterPaymentBillingPlans() {
        if(filterPaymentBillingPlans == null) {
            filterPaymentBillingPlans = new ArrayList<PaymentBillingPlan>();
        }
        return filterPaymentBillingPlans;
    }

    public void setFilterPaymentBillingPlans(List<PaymentBillingPlan> filterPaymentBillingPlans) {
        this.filterPaymentBillingPlans = filterPaymentBillingPlans;
    }

    public List<PaymentBillingPlanModel> getPaymentBillingPlans() {
        if(paymentBillingPlans == null) {
            paymentBillingPlans = new ArrayList<PaymentBillingPlanModel>();
        }
        return paymentBillingPlans;
    }

    public void setPaymentBillingPlans(List<PaymentBillingPlanModel> paymentBillingPlans) {
        this.paymentBillingPlans = paymentBillingPlans;
    }

    public PaymentBillingPlan getPaymentBillingPlan() {
        return paymentBillingPlan;
    }

    public boolean isPaymentBillingPlanValid() {
        return (paymentBillingPlan != null);
    }

    public void setPaymentBillingPlan(PaymentBillingPlan paymentBillingPlan) {
        this.paymentBillingPlan = paymentBillingPlan;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getBatchAccounts() {
        return batchAccounts;
    }

    public void setBatchAccounts(String batchAccounts) {
        this.batchAccounts = batchAccounts;
    }

    public ThirdPartyPlan getBatchThirdPartyPlan() {
        return batchThirdPartyPlan;
    }

    public void setBatchThirdPartyPlan(ThirdPartyPlan batchThirdPartyPlan) {
        this.batchThirdPartyPlan = batchThirdPartyPlan;
    }

    public TransactionTransferModel getThirdPartyTransactionTransfer() {
        return thirdPartyTransactionTransfer;
    }

    public void setThirdPartyTransactionTransfer(TransactionTransferModel thirdPartyTransactionTransfer) {
        this.thirdPartyTransactionTransfer = thirdPartyTransactionTransfer;
    }

    public String getForceReversal() {
        return forceReversal;
    }

    public void setForceReversal(String forceReversal) {
        this.forceReversal = forceReversal;
    }
}
