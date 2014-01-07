package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.krad.model.PaymentBillingTransferDetailModel;
import com.sigmasys.kuali.ksa.krad.model.ThirdPartyMemberModel;
import com.sigmasys.kuali.ksa.model.Memo;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingPlan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//import com.sigmasys.kuali.ksa.krad.model.ThirdPartyMemberModel;

/**
 * User: tbornholtz
 * Date: 8/16/13
 */
public class UserPaymentPlanForm extends AbstractViewModel {

    private String addPlanName;
    private BigDecimal maxRequestedAmount;

    private List<ThirdPartyMemberModel> thirdPartyMembers;

    private List<PaymentBillingPlan> paymentBillingPlans;

    private List<PaymentBillingTransferDetailModel> paymentBillingTransferDetails;

    private Memo changeMemo;

    public String getAddPlanName() {
        return addPlanName;
    }

    public void setAddPlanName(String addPlanName) {
        this.addPlanName = addPlanName;
    }

    public List<ThirdPartyMemberModel> getThirdPartyMembers() {
        if(thirdPartyMembers == null) {
            thirdPartyMembers = new ArrayList<ThirdPartyMemberModel>();
        }
        return thirdPartyMembers;
    }

    public void setThirdPartyMembers(List<ThirdPartyMemberModel> thirdPartyMembers) {
        this.thirdPartyMembers = thirdPartyMembers;
    }

    public BigDecimal getMaxRequestedAmount() {
        return maxRequestedAmount;
    }

    public void setMaxRequestedAmount(BigDecimal maxRequestedAmount) {
        this.maxRequestedAmount = maxRequestedAmount;
    }

    public List<PaymentBillingPlan> getPaymentBillingPlans() {
        if(paymentBillingPlans == null) {
            paymentBillingPlans = new ArrayList<PaymentBillingPlan>();
        }
        return paymentBillingPlans;
    }

    public void setPaymentBillingPlans(List<PaymentBillingPlan> paymentBillingPlans) {
        this.paymentBillingPlans = paymentBillingPlans;
    }

    public List<PaymentBillingTransferDetailModel> getPaymentBillingTransferDetails() {
        if(paymentBillingTransferDetails == null) {
            paymentBillingTransferDetails = new ArrayList<PaymentBillingTransferDetailModel>();
        }
        return paymentBillingTransferDetails;
    }

    public void setPaymentBillingTransferDetails(List<PaymentBillingTransferDetailModel> paymentBillingTransferDetails) {
        this.paymentBillingTransferDetails = paymentBillingTransferDetails;
    }

    public Memo getChangeMemo() {
        if(changeMemo == null) {
            changeMemo = new Memo();
        }
        return changeMemo;
    }

    public void setChangeMemo(Memo changeMemo) {
        this.changeMemo = changeMemo;
    }
}
