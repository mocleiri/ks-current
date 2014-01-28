package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.RefundType;

/**
 * User: tbornholtz
 * Date: 1/27/14
 */
public class RefundTypeModel extends AuditableEntityModel {


    public RefundTypeModel() { }

    public RefundTypeModel(RefundType refundType) {
        this.parentEntity = refundType;
    }

    public String getDebitType() {
        return getParent().getDebitTypeId();
    }

    public void setDebitType(String debitType) {
        getParent().setDebitTypeId(debitType);
    }

    public String getCreditType() {
        return getParent().getCreditTypeId();
    }

    public void setCreditType(String creditType) {
        getParent().getCreditTypeId();
    }

    @Override
    public String getName() {
        return getParent().getName();
    }

    @Override
    public String getDescription() {
        return getParent().getDescription();
    }

    @Override
    public String getCode() {
        return getParent().getCode();
    }

    @Override
    public void setCode(String code) {
        getParent().setCode(code);
    }

    private RefundType getParent() {
        if(parentEntity == null) {
            parentEntity = new RefundType();
        }
        return (RefundType)parentEntity;
    }
}
