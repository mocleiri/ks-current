package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.util.EnumUtils;

import javax.persistence.*;

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

    private String authorizationText;

    private String unallocatedGlAccount;

    private GlOperationType unallocatedGlOperation;

    private String glOperationCode;


    @PrePersist
    void populateDBFields() {
        glOperationCode = (unallocatedGlOperation != null) ? unallocatedGlOperation.getId() : null;
    }

    @PostLoad
    void populateTransientFields() {
        unallocatedGlOperation = (glOperationCode != null) ?
                EnumUtils.findById(GlOperationType.class, glOperationCode) : null;
    }


    @Column(name = "REFUND_RULE", length = 2000)
    public String getRefundRule() {
        return refundRule;
    }

    public void setRefundRule(String refundRule) {
        this.refundRule = refundRule;
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
    }

    @Transient
    public GlOperationType getUnallocatedGlOperation() {
        return unallocatedGlOperation;
    }

    public void setUnallocatedGlOperation(GlOperationType unallocatedGlOperation) {
        this.unallocatedGlOperation = unallocatedGlOperation;
    }
}
