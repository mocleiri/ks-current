package com.sigmasys.kuali.ksa.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * A super class for chargeable accounts
 *
 * @author Michael Ivanov
 */
@MappedSuperclass
public abstract class ChargeableAccount extends Account {

    /**
     * Outstanding balance
     */
    protected BigDecimal outstandingBalance;

    /**
     * Balance due
     */
    protected BigDecimal balanceDue;

    /**
     * Amount for DAYS_LATE1 period
     */
    protected BigDecimal amountLate1;

    /**
     * Amount for DAYS_LATE2 period
     */
    protected BigDecimal amountLate2;

    /**
     * Amount for DAYS_LATE3 period
     */
    protected BigDecimal amountLate3;

    /**
     * Amount for DAYS_LATE3 period
     */
    protected Date lateLastUpdate;


    @Column(name = "OUTSTANDING")
    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    @Column(name = "DUE")
    public BigDecimal getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    @Column(name = "LATE1")
    public BigDecimal getAmountLate1() {
        return amountLate1;
    }

    public void setAmountLate1(BigDecimal amountLate1) {
        this.amountLate1 = amountLate1;
    }

    @Column(name = "LATE2")
    public BigDecimal getAmountLate2() {
        return amountLate2;
    }

    public void setAmountLate2(BigDecimal amountLate2) {
        this.amountLate2 = amountLate2;
    }

    @Column(name = "LATE3")
    public BigDecimal getAmountLate3() {
        return amountLate3;
    }

    public void setAmountLate3(BigDecimal amountLate3) {
        this.amountLate3 = amountLate3;
    }

    @Column(name = "LATE_LAST_UPDATE")
    public Date getLateLastUpdate() {
        return lateLastUpdate;
    }

    public void setLateLastUpdate(Date lateLastUpdate) {
        this.lateLastUpdate = lateLastUpdate;
    }

}

