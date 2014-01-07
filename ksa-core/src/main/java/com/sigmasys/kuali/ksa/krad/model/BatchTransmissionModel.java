package com.sigmasys.kuali.ksa.krad.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class encapsulates information about GlTransmissions that happened
 * as a part of the same batch, ie. share the same Batch ID.
 *
 * User: Sergey
 * Date: 3/12/13
 * Time: 1:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class BatchTransmissionModel implements Serializable {

    /**
     * Batch ID.
     */
    private String batchId;

    /**
     * Batch transmission date.
     */
    private Date transmissionDate;

    /**
     * Total Batch transmission amount.
     */
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal totalCreditAmount = BigDecimal.ZERO;
    private BigDecimal totalDebitAmount = BigDecimal.ZERO;

    /**
     * A Batch's sub-list of GL Accounts.
     */
    private List<GeneralLedgerAccountModel> glAccountSublist;


    public List<GeneralLedgerAccountModel> getGlAccountSublist() {
        if (glAccountSublist == null) {
            glAccountSublist = new ArrayList<GeneralLedgerAccountModel>();
        }

        return glAccountSublist;
    }

    public void setGlAccountSublist(List<GeneralLedgerAccountModel> glAccountSublist) {
        this.glAccountSublist = glAccountSublist;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Date getTransmissionDate() {
        return transmissionDate;
    }

    public void setTransmissionDate(Date transmissionDate) {
        this.transmissionDate = transmissionDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public BigDecimal getTotalDebitAmount() {
        return totalDebitAmount;
    }

    public void setTotalDebitAmount(BigDecimal totalDebitAmount) {
        this.totalDebitAmount = totalDebitAmount;
    }
}
