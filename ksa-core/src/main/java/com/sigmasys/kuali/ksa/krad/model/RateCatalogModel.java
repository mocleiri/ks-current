package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.KeyPair;
import com.sigmasys.kuali.ksa.model.fm.RateCatalog;

import java.util.ArrayList;
import java.util.List;

/**
 * User: tbornholtz
 * Date: 1/9/14
 */
public class RateCatalogModel {
    private RateCatalog rateCatalog;

    private List<KeyPair> keyPairs;

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
        setKeyPairs(new ArrayList<KeyPair>(rateCatalog.getKeyPairs()));
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

    public List<KeyPair> getKeyPairs() {
        if(keyPairs == null) {
            keyPairs = new ArrayList<KeyPair>();
        }
        return keyPairs;
    }

    public void setKeyPairs(List<KeyPair> keyPairs) {
        /*if(keyPairs != null) {
            if(keyPairs.size() == 0) {
                keyPairs.add(new KeyPair());
            } else {
                KeyPair kp = keyPairs.get(0);
                if(kp != null && kp.getId() != null) {
                    keyPairs.add(new KeyPair());
                }
            }
        } */
        this.keyPairs = keyPairs;
    }
}


