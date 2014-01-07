package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.CashLimitParameter;

import java.math.BigDecimal;

/**
 * User: tbornholtz
 * Date: 8/6/13
 */
public class CashLimitParameterModel extends AuditableEntityModel {


    private String tagString;

    public CashLimitParameterModel() {}

    public CashLimitParameterModel(CashLimitParameter cashLimitParameter) {
        this.parentEntity = cashLimitParameter;
        this.tagString = cashLimitParameter.getTag().getCode();
    }

    public String getTagString() {
        return tagString;
    }

    public void setTagString(String tagString) {
        this.tagString = tagString;
    }

    public BigDecimal getLowerLimit() {
        return getParent().getLowerLimit();
    }

    public void setLowerLimit(BigDecimal lowerLimit) {
        getParent().setLowerLimit(lowerLimit);
    }

    public BigDecimal getUpperLimit() {
        return getParent().getUpperLimit();
    }

    public void setUpperLimit(BigDecimal lowerLimit) {
        getParent().setUpperLimit(lowerLimit);
    }

    public Boolean getActive() {
        return getParent().isActive();
    }

    public void setActive(Boolean active) {
        getParent().setActive(active);
    }

    public String getAuthorityName() {
        return getParent().getAuthorityName();
    }

    public void setAuthorityName(String authorityName) {
        getParent().setAuthorityName(authorityName);
    }

    public String getXmlElement() {
        return getParent().getXmlElement();
    }

    public void setXmlElement(String xmlElement) {
        getParent().setXmlElement(xmlElement);
    }

    private CashLimitParameter getParent(){
        if(this.parentEntity == null){
            this.parentEntity = new CashLimitParameter();
        }
        return (CashLimitParameter)parentEntity;
    }
}
