package com.sigmasys.kuali.ksa.krad.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Encapsulates the data for a Request Potential Refund summary.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 10/28/13
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestPotentialRefundSummaryModel implements Serializable {

    /**
     * A list of Potential Refunds eligible for a Refunds request.
     */
    private List<PotentialRefundModel> eligiblePotentialRefunds;

    /**
     * A list of Potential Refunds not eligible for a Refunds request.
     */
    private List<PotentialRefundModel> notEligiblePotentialRefunds;

    /*
     * Totals.
     */
    private BigDecimal totalEligiblePayment;
    private BigDecimal totalEligibleAllocated;
    private BigDecimal totalEligibleUnallocated;
    private BigDecimal totalEligibleRefundAmount;
    private BigDecimal totalNotEligiblePayment;
    private BigDecimal totalNotEligibleAllocated;
    private BigDecimal totalNotEligibleUnallocated;
    private BigDecimal totalNotEligibleRefundAmount;

    public List<PotentialRefundModel> getEligiblePotentialRefunds() {
        return eligiblePotentialRefunds;
    }

    public void setEligiblePotentialRefunds(List<PotentialRefundModel> eligiblePotentialRefunds) {
        this.eligiblePotentialRefunds = eligiblePotentialRefunds;
    }

    public List<PotentialRefundModel> getNotEligiblePotentialRefunds() {
        return notEligiblePotentialRefunds;
    }

    public void setNotEligiblePotentialRefunds(List<PotentialRefundModel> notEligiblePotentialRefunds) {
        this.notEligiblePotentialRefunds = notEligiblePotentialRefunds;
    }

    public BigDecimal getTotalEligiblePayment() {
        return totalEligiblePayment;
    }

    public void setTotalEligiblePayment(BigDecimal totalEligiblePayment) {
        this.totalEligiblePayment = totalEligiblePayment;
    }

    public BigDecimal getTotalEligibleAllocated() {
        return totalEligibleAllocated;
    }

    public void setTotalEligibleAllocated(BigDecimal totalEligibleAllocated) {
        this.totalEligibleAllocated = totalEligibleAllocated;
    }

    public BigDecimal getTotalEligibleUnallocated() {
        return totalEligibleUnallocated;
    }

    public void setTotalEligibleUnallocated(BigDecimal totalEligibleUnallocated) {
        this.totalEligibleUnallocated = totalEligibleUnallocated;
    }

    public BigDecimal getTotalEligibleRefundAmount() {
        return totalEligibleRefundAmount;
    }

    public void setTotalEligibleRefundAmount(BigDecimal totalEligibleRefundAmount) {
        this.totalEligibleRefundAmount = totalEligibleRefundAmount;
    }

    public BigDecimal getTotalNotEligiblePayment() {
        return totalNotEligiblePayment;
    }

    public void setTotalNotEligiblePayment(BigDecimal totalNotEligiblePayment) {
        this.totalNotEligiblePayment = totalNotEligiblePayment;
    }

    public BigDecimal getTotalNotEligibleAllocated() {
        return totalNotEligibleAllocated;
    }

    public void setTotalNotEligibleAllocated(BigDecimal totalNotEligibleAllocated) {
        this.totalNotEligibleAllocated = totalNotEligibleAllocated;
    }

    public BigDecimal getTotalNotEligibleUnallocated() {
        return totalNotEligibleUnallocated;
    }

    public void setTotalNotEligibleUnallocated(BigDecimal totalNotEligibleUnallocated) {
        this.totalNotEligibleUnallocated = totalNotEligibleUnallocated;
    }

    public BigDecimal getTotalNotEligibleRefundAmount() {
        return totalNotEligibleRefundAmount;
    }

    public void setTotalNotEligibleRefundAmount(BigDecimal totalNotEligibleRefundAmount) {
        this.totalNotEligibleRefundAmount = totalNotEligibleRefundAmount;
    }
}
