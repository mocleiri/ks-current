package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.fm.Rate;
import com.sigmasys.kuali.ksa.model.fm.RateAmount;
import com.sigmasys.kuali.ksa.model.fm.RateCatalog;

import java.math.BigDecimal;

/**
 * User: tbornholtz
 * Date: 1/14/14
 */
public class RateModel {
    private Rate rate;
    private RateCatalog rateCatalog;

    public RateModel() {

    }

    public RateModel(Rate rate) {
        setRate(rate);
    }

    public Rate getRate() {
        if(rate == null) {
            rate = new Rate();
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
            return rate.getDefaultRateAmount().getAmount();
        }

        return rateCatalog.getMaxAmount();
    }

    public void setDefaultAmount(BigDecimal defaultAmount) {
        rate.getDefaultRateAmount().setAmount(defaultAmount);
    }

    public boolean getDefaultAmountReadOnly() {
        return (rateCatalog != null) && (rateCatalog.getMinAmount() != null) && (rateCatalog.getMaxAmount() != null) && (rateCatalog.getMinAmount().equals(rateCatalog.getMaxAmount()));
    }

    public String getTransactionTypeId() {
        RateAmount rateAmount = getRate().getDefaultRateAmount();
        if(rateAmount != null && rateAmount.getTransactionTypeId() != null) {
            return rateAmount.getTransactionTypeId();
        }
        if(rateAmount == null) {
            rateAmount = new RateAmount();
        }

        if(getRateCatalog() != null && getRateCatalog().isTransactionTypeFinal()) {

            rateAmount.setTransactionTypeId(getRateCatalog().getTransactionTypeId());
        }

        return rateAmount.getTransactionTypeId();
    }

    public void setTransactionTypeId(String transactionTypeId){
        getRate().getDefaultRateAmount().setTransactionTypeId(transactionTypeId);
    }

    public boolean getTransactionTypeIdReadOnly() {
        return (getRateCatalog() != null) && getRateCatalog().isTransactionTypeFinal();
    }
}
