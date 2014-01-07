package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.krad.model.BatchTransmissionDetailsModel;
import com.sigmasys.kuali.ksa.krad.model.BatchTransmissionModel;
import com.sigmasys.kuali.ksa.krad.model.GeneralLedgerAccountModel;
import com.sigmasys.kuali.ksa.model.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by: dmulderink on 10/6/12 at 2:23 PM
 */
public class ReportReconciliationForm extends AbstractViewModel {

   private Account account;


    /**
     * All table rows containing information about GL Accounts
     * that have Pending Transactions.
     */
    private List<GeneralLedgerAccountModel> glAccountsPending;

    /**
     * All Batch Transmissions.
     */
    private List<BatchTransmissionModel> priorBatchTransmissions;


    /**
     * Formatted total amount for all GL Accounts with Pending Transactions.
     */
    private BigDecimal allGlAccountsTotal;

    /**
     * Formatted total amount for all Prior Batches.
     */
    private BigDecimal allPriorBatchTotal;

    /**
     * A Batch, which details are being displayed in the "Batch Details" page.
     */
    private BatchTransmissionDetailsModel displayedBatch;


    /**
     * Returns the ID of the Batch being displayed in the "Batch Details" page.
     *
     * @return ID of the displayed Batch.
     */
    public String getDisplayedBatchId() {
        return (displayedBatch != null) ? displayedBatch.getBatchId() : "";
    }

    public BatchTransmissionDetailsModel getDisplayedBatch() {
        return displayedBatch;
    }

    public void setDisplayedBatch(BatchTransmissionDetailsModel displayedBatch) {
        this.displayedBatch = displayedBatch;
    }

    public List<GeneralLedgerAccountModel> getGlAccountsPending() {
        return glAccountsPending;
    }

    public void setGlAccountsPending(List<GeneralLedgerAccountModel> glAccountsPending) {
        this.glAccountsPending = glAccountsPending;
    }

    public List<BatchTransmissionModel> getPriorBatchTransmissions() {
        return priorBatchTransmissions;
    }

    public void setPriorBatchTransmissions(List<BatchTransmissionModel> priorBatchTransmissions) {
        this.priorBatchTransmissions = priorBatchTransmissions;
    }

    public BigDecimal getAllGlAccountsTotal() {
        return allGlAccountsTotal;
    }

    public void setAllGlAccountsTotal(BigDecimal allGlAccountsTotal) {
        this.allGlAccountsTotal = allGlAccountsTotal;
    }

    public BigDecimal getAllPriorBatchTotal() {
        return allPriorBatchTotal;
    }

    public void setAllPriorBatchTotal(BigDecimal allPriorBatchTotal) {
        this.allPriorBatchTotal = allPriorBatchTotal;
    }

    public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }
}
