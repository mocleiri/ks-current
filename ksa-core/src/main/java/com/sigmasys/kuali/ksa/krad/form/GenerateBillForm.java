package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.model.Rollup;

import java.util.*;

public class GenerateBillForm extends AbstractViewModel {

    private String billAccountId;
    private String billMessage;
    private Date billDate;
    private Date startDate;
    private Date endDate;

    private List<Rollup> rollupsOnSameDate;
    private List<Rollup> rollupsOnSameStatement;
    private String newSameDayRollup;
    private String newSameStatementRollup;

    private Boolean showOnlyUnbilledTransactions;
    private Boolean showDeferments;
    private Boolean showDependents;
    private Boolean showInternalTransactions;
    private Boolean runPaymentApplication;


    public String getBillMessage() {
        return billMessage;
    }

    public void setBillMessage(String billMessage) {
        this.billMessage = billMessage;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Rollup> getRollupsOnSameDate() {
        if(rollupsOnSameDate == null) {
            rollupsOnSameDate = new ArrayList<Rollup>();
        }
        return rollupsOnSameDate;
    }

    public void setRollupsOnSameDate(List<Rollup> rollupsOnSameDate) {
        this.rollupsOnSameDate = rollupsOnSameDate;
    }

    public List<Rollup> getRollupsOnSameStatement() {
        if(rollupsOnSameStatement == null) {
            rollupsOnSameStatement = new ArrayList<Rollup>();
        }
        return rollupsOnSameStatement;
    }

    public void setRollupsOnSameStatement(List<Rollup> rollupsOnSameStatement) {
        this.rollupsOnSameStatement = rollupsOnSameStatement;
    }

    public Boolean getShowOnlyUnbilledTransactions() {
        return showOnlyUnbilledTransactions;
    }

    public void setShowOnlyUnbilledTransactions(Boolean showOnlyUnbilledTransactions) {
        this.showOnlyUnbilledTransactions = showOnlyUnbilledTransactions;
    }

    public Boolean getShowDeferments() {
        return showDeferments;
    }

    public void setShowDeferments(Boolean showDeferments) {
        this.showDeferments = showDeferments;
    }

    public Boolean getShowDependents() {
        return showDependents;
    }

    public void setShowDependents(Boolean showDependents) {
        this.showDependents = showDependents;
    }

    public Boolean getShowInternalTransactions() {
        return showInternalTransactions;
    }

    public void setShowInternalTransactions(Boolean showInternalTransactions) {
        this.showInternalTransactions = showInternalTransactions;
    }

    public Boolean getRunPaymentApplication() {
        return runPaymentApplication;
    }

    public void setRunPaymentApplication(Boolean runPaymentApplication) {
        this.runPaymentApplication = runPaymentApplication;
    }

    public String getBillAccountId() {
        return billAccountId;
    }

    public void setBillAccountId(String billAccountId) {
        this.billAccountId = billAccountId;
    }

    public String getNewSameDayRollup() {
        return newSameDayRollup;
    }

    public void setNewSameDayRollup(String newSameDayRollup) {
        this.newSameDayRollup = newSameDayRollup;
    }

    public String getNewSameStatementRollup() {
        return newSameStatementRollup;
    }

    public void setNewSameStatementRollup(String newSameStatementRollup) {
        this.newSameStatementRollup = newSameStatementRollup;
    }
}
