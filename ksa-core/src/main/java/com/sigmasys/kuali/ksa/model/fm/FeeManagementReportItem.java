package com.sigmasys.kuali.ksa.model.fm;

import java.math.BigDecimal;
import java.util.Date;

/**
 * FeeManagement report item.
 *
 * @author Sergey Godunov
 */
public class FeeManagementReportItem {

    /**
     * Type from the manifest (CHARGE, DISCOUNT, etc.)
     */
    private FeeManagementManifestType manifestType;

    /**
     * Effective date of the transaction.
     */
    private Date effectiveDate;

    /**
     * Transaction type identifier.
     */
    private String transactionTypeId;

    /**
     * Readable statement text.
     */
    private String statementText;

    /**
     * Amount of the transaction.
     */
    private BigDecimal amount;

    /**
     * If true, this charge was already on the student’s account (and is
     * therefore not included in the netImpact figure).
     */
    private Boolean alreadyCharged;


    public FeeManagementManifestType getManifestType() {
        return manifestType;
    }

    public void setManifestType(FeeManagementManifestType manifestType) {
        this.manifestType = manifestType;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(String transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public String getStatementText() {
        return statementText;
    }

    public void setStatementText(String statementText) {
        this.statementText = statementText;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getAlreadyCharged() {
        return alreadyCharged;
    }

    public void setAlreadyCharged(Boolean alreadyCharged) {
        this.alreadyCharged = alreadyCharged;
    }
}
