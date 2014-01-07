package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.krad.util.AuditTooltipUtil;
import com.sigmasys.kuali.ksa.model.Information;
import com.sigmasys.kuali.ksa.model.InformationAccessLevel;

import java.util.Date;


public class InformationModel {

    private Information information;

    public InformationModel() {

    }

    public InformationModel(Information information) {
        this.information = information;
    }

    public Information getParentEntity() {
        return information;
    }

    public void setParentEntity(Information parentEntity) {
        this.information = parentEntity;
    }

    public Long getId() {
        return information.getId();
    }

    public String getText() {
        return information.getText();
    }

    public void setText(String text) {
        information.setText(text);
    }

    public String getDisplayValue() {
        return information.getDisplayValue();
    }

    public Date getEffectiveDate() {
        return information.getEffectiveDate();
    }

    public void setEffectiveDate(Date date) {
        information.setEffectiveDate(date);
    }

    public Date getExpirationDate() {
        return information.getExpirationDate();
    }

    public void setExpirationDate(Date date) {
        information.setExpirationDate(date);
    }

    public String getAccessLevel() {
        InformationAccessLevel level = information.getAccessLevel();
        return (level != null) ? level.getName() : "";
    }

    public String getAuditTooltip() {
        return AuditTooltipUtil.getAuditTooltip(information);
    }

    public String getTransactionName() {
        if(this.getParentEntity().getTransaction() != null) {
            return this.getParentEntity().getTransaction().getTransactionType().getDescription();
        }
        return "";
    }

    public Long getTransactionId() {
        if(this.getParentEntity().getTransaction() != null) {
            return this.getParentEntity().getTransaction().getId();
        }
        return null;
    }

}
