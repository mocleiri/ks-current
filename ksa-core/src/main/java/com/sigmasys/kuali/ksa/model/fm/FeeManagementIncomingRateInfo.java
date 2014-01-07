package com.sigmasys.kuali.ksa.model.fm;

/**
 * The FeeManagementIncomingRateInfo object is used to store the details of Rate from the registration system.
 * When this is translated into a session, the Rate will be converted to the appropriate ID.
 *
 * @author Sergey Godunov
 */
public class FeeManagementIncomingRateInfo {

    /**
     * Code for the attached Rate.
     */
    private String rateCode;

    /**
     * Sub-code for the attached rate.
     */
    private String rateSubCode;


    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }

    public String getRateSubCode() {
        return rateSubCode;
    }

    public void setRateSubCode(String rateSubCode) {
        this.rateSubCode = rateSubCode;
    }
}
