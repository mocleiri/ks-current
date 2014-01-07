package com.sigmasys.kuali.ksa.model.fm;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * This class is used to return information to a calling system when a what-if call is made. This data is not
 * persisted. It is used only as the data structure to send the message back to the caller.
 *
 * @author Sergey Godunov
 */
public class FeeManagementReportInfo {

    /**
     * User ID of the student account.
     */
    private String accountId;

    /**
     * Time and date stamp of the assessment.
     */
    private Date creationDate;

    /**
     * List of feeManagementReportItem objects.
     */
    private List<FeeManagementReportItem> reportItems;

    /**
     * Financial impact on the account. How much more or less this
     * assessment would cost the student.
     */
    private BigDecimal netImpact;


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<FeeManagementReportItem> getReportItems() {
        return reportItems;
    }

    public void setReportItems(List<FeeManagementReportItem> reportItems) {
        this.reportItems = reportItems;
    }

    public BigDecimal getNetImpact() {
        return netImpact;
    }

    public void setNetImpact(BigDecimal netImpact) {
        this.netImpact = netImpact;
    }
}
