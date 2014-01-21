package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.fm.Rate;
import com.sigmasys.kuali.ksa.model.fm.RateAmount;
import com.sigmasys.kuali.ksa.model.fm.RateCatalog;
import com.sigmasys.kuali.ksa.model.fm.TransactionDateType;
import com.sigmasys.kuali.ksa.util.EnumUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: tbornholtz
 * Date: 1/14/14
 */
public class RateModel {
    private Rate rate;
    private RateCatalog rateCatalog;

    // Rolover specific fields
    private String rolloverAcademicPeriod;
    private String rolloverTransactionDateType;
    private Date rolloverTransactionDate;
    private Date rolloverRecognitionDate;

    public RateModel() {

    }

    public RateModel(Rate rate) {
        setRate(rate);
    }

    public Rate getRate() {
        if(rate == null) {
            rate = new Rate();
            rate.setDefaultRateAmount(new RateAmount());
        }
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public RateCatalog getRateCatalog() {
        return rateCatalog;
    }

    public void setRateCatalog(RateCatalog rateCatalog) {
        this.rateCatalog = rateCatalog;
    }

    public BigDecimal getDefaultAmount() {
        if(rateCatalog == null) {
            // undefined logic, return zero
            return BigDecimal.ZERO;
        }

        BigDecimal minAmount = rateCatalog.getMinAmount();
        BigDecimal maxAmount = rateCatalog.getMaxAmount();
        if(minAmount != null && maxAmount != null && minAmount.equals(maxAmount)) {
            return this.getDefaultRateAmount().getAmount();
        }

        return rateCatalog.getMaxAmount();
    }

    public void setDefaultAmount(BigDecimal defaultAmount) {
        this.getDefaultRateAmount().setAmount(defaultAmount);
    }

    public boolean getDefaultAmountReadOnly() {
        return (rateCatalog != null) && (rateCatalog.getMinAmount() != null) && (rateCatalog.getMaxAmount() != null) && (rateCatalog.getMinAmount().equals(rateCatalog.getMaxAmount()));
    }

    public String getTransactionTypeId() {
        RateAmount rateAmount = this.getDefaultRateAmount();
        if(rateAmount.getTransactionTypeId() != null) {
            return rateAmount.getTransactionTypeId();
        }

        if(getRateCatalog() != null && getRateCatalog().isTransactionTypeFinal()) {

            rateAmount.setTransactionTypeId(getRateCatalog().getTransactionTypeId());
        }

        return rateAmount.getTransactionTypeId();
    }

    public void setTransactionTypeId(String transactionTypeId){
        this.getDefaultRateAmount().setTransactionTypeId(transactionTypeId);
    }

    public boolean getTransactionTypeIdReadOnly() {
        return (getRateCatalog() != null) && getRateCatalog().isTransactionTypeFinal();
    }

    public String getRolloverAcademicPeriod() {
        return rolloverAcademicPeriod;
    }

    public void setRolloverAcademicPeriod(String rolloverAcademicPeriod) {
        this.rolloverAcademicPeriod = rolloverAcademicPeriod;
    }

    public String getRolloverTransactionDateType() {
        return rolloverTransactionDateType;
    }

    public void setRolloverTransactionDateType(String rolloverTransactionDateType) {
        this.rolloverTransactionDateType = rolloverTransactionDateType;
    }

    public Date getRolloverTransactionDate() {
        return rolloverTransactionDate;
    }

    public void setRolloverTransactionDate(Date rolloverTransactionDate) {
        this.rolloverTransactionDate = rolloverTransactionDate;
    }

    public Date getRolloverRecognitionDate() {
        return rolloverRecognitionDate;
    }

    public void setRolloverRecognitionDate(Date rolloverRecognitionDate) {
        this.rolloverRecognitionDate = rolloverRecognitionDate;
    }

    public List<RateAmount> getRateAmounts() {
        Set<RateAmount> rateAmounts = getRate().getRateAmounts();
        if(rateAmounts == null) {
            rateAmounts = new HashSet<RateAmount>();
            getRate().setRateAmounts(rateAmounts);
        }

        return new ArrayList<RateAmount>(rateAmounts);
    }

    public void setRateAmounts(List<RateAmount> rateAmounts) {
        getRate().setRateAmounts(new HashSet<RateAmount>(rateAmounts));
    }

    public boolean getIsLimitAmount() {
        return getRate().isLimitAmount();
    }

    public void setIsLimitAmount(boolean isLimitAmount) {
        getRate().setLimitAmount(isLimitAmount);
    }

    public String getTransactionDateType() {
        TransactionDateType transactionDateType = getRate().getTransactionDateType();
        if(transactionDateType == null) {
            return null;
        }
        return transactionDateType.getId();
    }

    public void setTransactionDateType(String transactionDateType) {
        getRate().setTransactionDateType(EnumUtils.findById(TransactionDateType.class, transactionDateType));
    }

    private RateAmount getDefaultRateAmount() {
        RateAmount amount = getRate().getDefaultRateAmount();
        if(amount == null) {
            amount = new RateAmount();
            amount.setUnits(1);
            getRate().setDefaultRateAmount(amount);

        }
        return amount;
    }



}
