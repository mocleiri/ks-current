package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.ThirdPartyAccount;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyPlan;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyPlanMember;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyTransferDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: tbornholtz
 * Date: 8/28/13
 */
public class ThirdPartyPlanModel {

    private ThirdPartyPlan parent;

    private List<ThirdPartyAllowableChargeModel> thirdPartyAllowableCharges;

    private List<ThirdPartyMemberModel> planMembers;
    private List<ThirdPartyPlanMember> queuedMembers;
    private List<ThirdPartyTransferDetail> reversedMembers;

    public ThirdPartyPlan getParent() {
        if (parent == null) {
            parent = new ThirdPartyPlan();
        }
        return parent;
    }

    public void setParent(ThirdPartyPlan parent) {
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

    public ThirdPartyAccount getThirdPartyAccount() {
        return getParent().getThirdPartyAccount();
    }

    public void setThirdPartyAccount(ThirdPartyAccount thirdPartyAccount) {
        getParent().setThirdPartyAccount(thirdPartyAccount);
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

    public Date getEffectiveDate() {
        return getParent().getEffectiveDate();
    }

    public void setEffectiveDate(Date effectiveDate) {
        getParent().setEffectiveDate(effectiveDate);
    }

    public Date getRecognitionDate() {
        return getParent().getRecognitionDate();
    }

    public void setRecognitionDate(Date recognitionDate) {
        getParent().setRecognitionDate(recognitionDate);
    }

    public String getTransferType() {
        return getParent().getTransferType().getId().toString();
    }

    public void setTransferType(String type) {
        getParent().getTransferType().setId(Long.parseLong(type));
    }

    public List<ThirdPartyAllowableChargeModel> getThirdPartyAllowableCharges() {
        if (thirdPartyAllowableCharges == null) {
            thirdPartyAllowableCharges = new ArrayList<ThirdPartyAllowableChargeModel>();
        }
        return thirdPartyAllowableCharges;
    }

    public void setThirdPartyAllowableCharges(List<ThirdPartyAllowableChargeModel> thirdPartyAllowableCharges) {
        this.thirdPartyAllowableCharges = thirdPartyAllowableCharges;
    }

    public List<ThirdPartyMemberModel> getPlanMembers() {
        if(planMembers == null){
            planMembers = new ArrayList<ThirdPartyMemberModel>();
        }
        return planMembers;
    }

    public void setPlanMembers(List<ThirdPartyMemberModel> planMembers) {
        this.planMembers = planMembers;
    }

    public List<ThirdPartyPlanMember> getQueuedMembers() {
        if (queuedMembers == null) {
            queuedMembers = new ArrayList<ThirdPartyPlanMember>();
        }
        return queuedMembers;
    }

    public void setQueuedMembers(List<ThirdPartyPlanMember> queuedMembers) {
        this.queuedMembers = queuedMembers;
    }

    public List<ThirdPartyTransferDetail> getReversedMembers() {
        if(reversedMembers == null) {
            reversedMembers = new ArrayList<ThirdPartyTransferDetail>();
        }
        return reversedMembers;
    }

    public void setReversedMembers(List<ThirdPartyTransferDetail> reversedMembers) {
        this.reversedMembers = reversedMembers;
    }
}
