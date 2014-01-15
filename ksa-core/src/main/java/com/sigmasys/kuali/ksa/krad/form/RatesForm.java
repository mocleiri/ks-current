package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.krad.model.RateModel;
import com.sigmasys.kuali.ksa.model.fm.Rate;

import java.util.ArrayList;
import java.util.List;

/**
 * The form object behind the "Rates" page.
 * User: Sergey
 * Date: 12/4/13
 * Time: 10:44 PM
 */
public class RatesForm extends AbstractViewModel {

    // Filter fields
    private String academicTimePeriod;
    private String rateCatalogCode;
    private String rateCode;
    private String rateSubcode;


    private List<RateModel> rates;

    public String getAcademicTimePeriod() {
        return academicTimePeriod;
    }

    public void setAcademicTimePeriod(String academicTimePeriod) {
        this.academicTimePeriod = academicTimePeriod;
    }

    public String getRateCatalogCode() {
        return rateCatalogCode;
    }

    public void setRateCatalogCode(String rateCatalogCode) {
        this.rateCatalogCode = rateCatalogCode;
    }

    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }

    public String getRateSubcode() {
        return rateSubcode;
    }

    public void setRateSubcode(String rateSubcode) {
        this.rateSubcode = rateSubcode;
    }

    public List<RateModel> getRates() {
        if(rates == null) {
            rates = new ArrayList<RateModel>();
        }
        return rates;
    }

    public void setRates(List<RateModel> rates) {
        this.rates = rates;
    }
}
