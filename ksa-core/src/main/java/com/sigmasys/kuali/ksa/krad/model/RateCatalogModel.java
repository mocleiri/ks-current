package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.fm.RateCatalog;

/**
 * User: tbornholtz
 * Date: 1/9/14
 */
public class RateCatalogModel {
    private RateCatalog rateCatalog;

    private String rateTypeId;

    public RateCatalogModel() {
    }

    public RateCatalogModel(RateCatalog rateCatalog) {
        setRateCatalog(rateCatalog);
    }

    public RateCatalog getRateCatalog() {
        if(rateCatalog == null) {
            this.rateCatalog = new RateCatalog();
        }
        return rateCatalog;
    }

    public void setRateCatalog(RateCatalog rateCatalog) {
        this.rateCatalog = rateCatalog;
        this.rateTypeId = rateCatalog.getRateType().getId().toString();
    }


    public String getRateTypeId() {
        return rateTypeId;
    }

    public void setRateTypeId(String rateTypeId) {
        this.rateTypeId = rateTypeId;
    }

    public Boolean getTransactionTypeFinal() {
        return getRateCatalog().isTransactionTypeFinal();
    }

    public void setTransactionTypeFinal(Boolean typeFinal) {
        getRateCatalog().setTransactionTypeFinal(typeFinal);
    }

    public Boolean getRecognitionDateLocked() {
        return getRateCatalog().isRecognitionDateDefinable();
    }

    public void setRecognitionDateLocked(Boolean locked) {
        getRateCatalog().setRecognitionDateDefinable(locked);
    }

    public Boolean getLimitAmountFinal() {
        return getRateCatalog().isLimitAmountFinal();
    }

    public void setLimitAmountFinal(Boolean limitAmountFinal) {
        getRateCatalog().setLimitAmountFinal(limitAmountFinal);
    }

    public Boolean getIsLimitAmount() {
        return getRateCatalog().isLimitAmount();
    }

    public void setIsLimitAmount(Boolean limitAmount) {
        getRateCatalog().setLimitAmount(limitAmount);
    }

}


