package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.pb.PaymentBillingPlan;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingQueue;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyAllowableCharge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: tbornholtz
 * Date: 8/28/13
 */
public class PaymentBillingPlanModel {

    private PaymentBillingPlan parent;

    private List<ThirdPartyAllowableCharge> thirdPartyAllowableCharges;

    private List<PaymentBillingQueue> queuedMembers;
    private List<PaymentBillingQueue> planMembers;
    private List<PaymentBillingQueue> reversedMembers;

    private List<TransactionTransferModel> transferDetails;

    public PaymentBillingPlan getParent() {
        if (parent == null) {
            parent = new PaymentBillingPlan();
        }
        return parent;
    }

    public void setParent(PaymentBillingPlan parent) {
        this.parent = parent;
    }

    public String getCode() {
        return getParent().getCode();
    }

    public void setCode(String code) {
        getParent().setCode(code);
    }

    public String getName() {
        return getParent().getName();
    }

    public void setName(String name) {
        getParent().setName(name);
    }

    public String getDescription() {
        return getParent().getDescription();
    }

    public void setDescription(String description) {
        getParent().setDescription(description);
    }

    public Date getOpenPeriodStartDate() {
        return getParent().getOpenPeriodStartDate();
    }

    public void setOpenPeriodStartDate(Date openPeriodStartDate) {
        getParent().setOpenPeriodStartDate(openPeriodStartDate);
    }

    public Date getOpenPeriodEndDate() {
        return getParent().getOpenPeriodEndDate();
    }

    public void setOpenPeriodEndDate(Date openPeriodEndDate) {
        getParent().setOpenPeriodEndDate(openPeriodEndDate);
    }

    public Date getChargePeriodStartDate() {
        return getParent().getChargePeriodStartDate();
    }

    public void setChargePeriodStartDate(Date chargePeriodStartDate) {
        getParent().setChargePeriodStartDate(chargePeriodStartDate);
    }

    public Date getChargePeriodEndDate() {
        return getParent().getChargePeriodEndDate();
    }

    public void setChargePeriodEndDate(Date chargePeriodEndDate) {
        getParent().setChargePeriodEndDate(chargePeriodEndDate);
    }

    public BigDecimal getMaxAmount() {
        return getParent().getMaxAmount();
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        getParent().setMaxAmount(maxAmount);
    }

    public String getTransferType() {
        return getParent().getTransferType().getId().toString();
    }

    public void setTransferType(String type) {
        getParent().getTransferType().setId(Long.parseLong(type));
    }

    public List<ThirdPartyAllowableCharge> getThirdPartyAllowableCharges() {
        if (thirdPartyAllowableCharges == null) {
            thirdPartyAllowableCharges = new ArrayList<ThirdPartyAllowableCharge>();
        }
        return thirdPartyAllowableCharges;
    }

    public void setThirdPartyAllowableCharges(List<ThirdPartyAllowableCharge> thirdPartyAllowableCharges) {
        this.thirdPartyAllowableCharges = thirdPartyAllowableCharges;
    }

    public List<TransactionTransferModel> getTransferDetails() {
        if(transferDetails == null) {
            transferDetails = new ArrayList<TransactionTransferModel>();
        }
        return transferDetails;
    }

    public void setTransferDetails(List<TransactionTransferModel> transferDetails) {
        this.transferDetails = transferDetails;
    }

    public List<PaymentBillingQueue> getQueuedMembers() {
        if(queuedMembers == null) {
            queuedMembers = new ArrayList<PaymentBillingQueue>();
        }
        return queuedMembers;
    }

    public void setQueuedMembers(List<PaymentBillingQueue> queuedMembers) {
        this.queuedMembers = queuedMembers;
    }

    public List<PaymentBillingQueue> getPlanMembers() {
        if(planMembers == null) {
            planMembers = new ArrayList<PaymentBillingQueue>();
        }
        return planMembers;
    }

    public void setPlanMembers(List<PaymentBillingQueue> planMembers) {
        this.planMembers = planMembers;
    }

    public List<PaymentBillingQueue> getReversedMembers() {
        if(reversedMembers == null) {
            reversedMembers = new ArrayList<PaymentBillingQueue>();
        }
        return reversedMembers;
    }

    public void setReversedMembers(List<PaymentBillingQueue> reversedMembers) {
        this.reversedMembers = reversedMembers;
    }
}
