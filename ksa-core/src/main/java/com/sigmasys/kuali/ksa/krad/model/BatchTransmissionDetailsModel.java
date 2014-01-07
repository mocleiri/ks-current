package com.sigmasys.kuali.ksa.krad.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a single Batch transmission displayed in the "Batch Details" page.
 *
 * User: Sergey
 * Date: 4/27/13
 * Time: 11:45 PM
 */
public class BatchTransmissionDetailsModel extends BatchTransmissionModel {

    /**
     * A list of All Other GL Accounts.
     */
    private List<GeneralLedgerAccountModel> allOtherGlAccounts;


    /**
     * The total of all Accrual GL Accounts on the "Batch Details" page.
     */
    private BigDecimal accrualGlAccountsTotal;

    /**
     * The total of all non-Accrual GL Accounts on the "Batch Details" page.
     */
    private BigDecimal allOtherGlAccountsTotal;


    /**
     * Returns the list of Accrual GL Accounts. Reuses the superclass' list of GL Accounts.
     *
     * @return The list of Accrual GL Accounts.
     */
    public List<GeneralLedgerAccountModel> getAccrualGlAccounts() {
        return super.getGlAccountSublist();
    }

    /**
     * Returns the list of All Other GL Accounts. Guaranteed not null
     *
     * @return The List of All Other GL Accounts.
     */
    public List<GeneralLedgerAccountModel> getAllOtherGlAccounts() {
        if (allOtherGlAccounts == null) {
            allOtherGlAccounts = new ArrayList<GeneralLedgerAccountModel>();
        }

        return allOtherGlAccounts;
    }

    public void setAccrualGlAccounts(List<GeneralLedgerAccountModel> accrualGlAccounts) {
        super.setGlAccountSublist(accrualGlAccounts);
    }

    public void setAllOtherGlAccounts(List<GeneralLedgerAccountModel> allOtherGlAccounts) {
        this.allOtherGlAccounts = allOtherGlAccounts;
    }

    public BigDecimal getAccrualGlAccountsTotal() {
        return accrualGlAccountsTotal;
    }

    public void setAccrualGlAccountsTotal(BigDecimal accrualGlAccountsTotal) {
        this.accrualGlAccountsTotal = accrualGlAccountsTotal;
    }

    public BigDecimal getAllOtherGlAccountsTotal() {
        return allOtherGlAccountsTotal;
    }

    public void setAllOtherGlAccountsTotal(BigDecimal allOtherGlAccountsTotal) {
        this.allOtherGlAccountsTotal = allOtherGlAccountsTotal;
    }
}
