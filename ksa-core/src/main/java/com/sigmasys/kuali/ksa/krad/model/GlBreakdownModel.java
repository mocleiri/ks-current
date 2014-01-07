package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.krad.util.HighPrecisionPercentageFormatter;

import java.math.BigDecimal;


public class GlBreakdownModel {

    // Theoretically it is possible to get KRAD to validate the breakdown without
    // converting it to a string here and to use this formatter directly in the XML view
    // This current implementation is more complicated than it needs to be but the hope is
    // that this whole class can go away and use the facilities of KRAD directly.
    private HighPrecisionPercentageFormatter formatter = new HighPrecisionPercentageFormatter();

    private String operation;

    private String glAccount;

    private BigDecimal breakdown;

    public String getBreakdownString(){
        return (String)formatter.format(getBreakdown());
    }

    public void setBreakdownString(String str){
            setBreakdown((BigDecimal)formatter.convertToObject(str));
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }

    public BigDecimal getBreakdown() {
        if(breakdown == null){
            breakdown = BigDecimal.ZERO;
        }
        return breakdown;
    }

    public void setBreakdown(BigDecimal breakdown) {
        this.breakdown = breakdown;
    }
}
