package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.GlRecognitionPeriod;

import java.util.Date;

/**
 * User: tbornholtz
 * Date: 1/28/14
 */
public class GlRecognitionPeriodModel extends AuditableEntityModel {

    public GlRecognitionPeriodModel() {

    }

    public GlRecognitionPeriodModel(GlRecognitionPeriod parent) {
        parentEntity = parent;
    }

    @Override
    public Long getId() {
        return getParent().getId();
    }

    public Date getStartDate() {
        return getParent().getStartDate();
    }

    public void setStartDate(Date date) {
        getParent().setStartDate(date);
    }

    public Date getEndDate() {
        return getParent().getEndDate();
    }

    public void setEndDate(Date date) {
        getParent().setEndDate(date);
    }

    public String getFiscalYear() {
        Integer fiscalYear = getParent().getFiscalYear();
        return fiscalYear == null ? "" : fiscalYear.toString();
    }

    public void setFiscalYear(String year) {
        try {
            Integer yearInt = new Integer(year);
            getParent().setFiscalYear(yearInt);
        } catch(RuntimeException e) {
            // NumberFormatException or NullPointerException here.
            // Both mean it isn't a number and we're not going to save it.
        }

    }

    private GlRecognitionPeriod getParent() {
        if(parentEntity == null) {
            parentEntity = new GlRecognitionPeriod();
        }
        return (GlRecognitionPeriod)parentEntity;
    }

}
