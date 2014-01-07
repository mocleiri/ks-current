package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.service.TransactionTypeVisitor;
import com.sigmasys.kuali.ksa.util.EnumUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * CreditType
 * Credit types define the clearing period for a transaction, refund preferences,
 * charges that can be paid by the credit, etc.
 * <p/>
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(TransactionType.CREDIT_TYPE)
public class CreditType extends TransactionType {

    private Integer clearPeriod;

    private String refundRule;

    private Boolean isRefundable;

    private String authorizationText;

    private String unallocatedGlAccount;

    private GlOperationType unallocatedGlOperation;

    private String glOperationCode;


    @Override
    @Transient
    @XmlTransient
    public String getTypeValue() {
        return TransactionType.CREDIT_TYPE;
    }

    /**
     * Allows TransactionTypeVisitor to access this CreditType
     *
     * @param visitor TransactionTypeVisitor instance
     */
    public void accept(TransactionTypeVisitor visitor) {
        visitor.visit(this);
    }

    @PostLoad
    protected void populateTransientFields() {
        unallocatedGlOperation = (glOperationCode != null) ? EnumUtils.findById(GlOperationType.class, glOperationCode) : null;
    }


    @Column(name = "REFUND_RULE", length = 2000)
    public String getRefundRule() {
        return refundRule;
    }

    public void setRefundRule(String refundRule) {
        this.refundRule = refundRule;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_REFUNDABLE")
    public Boolean isRefundable() {
        return isRefundable != null ? isRefundable : false;
    }

    public void setRefundable(Boolean isRefundable) {
        this.isRefundable = isRefundable;
    }

    @Column(name = "AUTH_TXT", length = 1000)
    public String getAuthorizationText() {
        return authorizationText;
    }

    public void setAuthorizationText(String authorizationText) {
        this.authorizationText = authorizationText;
    }

    @Column(name = "CLEAR_PERIOD")
    public Integer getClearPeriod() {
        return clearPeriod;
    }

    public void setClearPeriod(Integer clearPeriod) {
        this.clearPeriod = clearPeriod;
    }

    @Column(name = "UNALLOCATED_GL_ACCOUNT", length = 45)
    public String getUnallocatedGlAccount() {
        return unallocatedGlAccount;
    }

    public void setUnallocatedGlAccount(String unallocatedGlAccount) {
        this.unallocatedGlAccount = unallocatedGlAccount;
    }

    @Column(name = "UNALLOCATED_GL_OPERATION", length = 1)
    protected String getGlOperationCode() {
        return glOperationCode;
    }

    protected void setGlOperationCode(String glOperationCode) {
        this.glOperationCode = glOperationCode;
        unallocatedGlOperation = EnumUtils.findById(GlOperationType.class, glOperationCode);
    }

    @Transient
    public GlOperationType getUnallocatedGlOperation() {
        return unallocatedGlOperation;
    }

    public void setUnallocatedGlOperation(GlOperationType unallocatedGlOperation) {
        this.unallocatedGlOperation = unallocatedGlOperation;
        glOperationCode = unallocatedGlOperation.getId();
    }
}
