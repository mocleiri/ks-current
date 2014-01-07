package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.krad.form.AbstractViewModel;
import com.sigmasys.kuali.ksa.krad.model.InformationModel;
import com.sigmasys.kuali.ksa.model.Information;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingPlan;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingQueue;
import com.sigmasys.kuali.ksa.service.InformationService;
import com.sigmasys.kuali.ksa.service.hold.HoldService;
import com.sigmasys.kuali.ksa.service.pb.PaymentBillingService;
import com.sigmasys.kuali.ksa.service.tp.ThirdPartyTransferService;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.hold.dto.AppliedHoldInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * User: tbornholtz
 * Date: 9/25/13
 */
public class AccountUtils {


    private static final Log logger = LogFactory.getLog(AccountUtils.class);

    private static HoldService holdService;

    private static InformationService informationService;

    private static PaymentBillingService paymentBillingService;

    private static ThirdPartyTransferService thirdPartyTransferService;

    public static void populateTransactionHeading(AbstractViewModel form, String userId) {
        if (holdService == null) {
            holdService = ContextUtils.getBean(HoldService.class);
        }

        if (informationService == null) {
            informationService = ContextUtils.getBean(InformationService.class);
        }

        if(paymentBillingService == null) {
            paymentBillingService = ContextUtils.getBean(PaymentBillingService.class);
        }

        if(thirdPartyTransferService == null) {
            thirdPartyTransferService = ContextUtils.getBean(ThirdPartyTransferService.class);
        }


        form.setAlertObjects(informationService.getAlerts(userId));
        form.setFlagObjects(informationService.getFlags(userId));
        //form.setMemos(informationService.getMemos(userId));
        form.setHolds(AccountUtils.getHolds(userId));

        List<PaymentBillingPlan> plans = paymentBillingService.getPaymentBillingPlansByAccountId(userId);
        List<PaymentBillingQueue> queues = paymentBillingService.getPaymentBillingQueues(userId, null);
        for(PaymentBillingQueue queue : queues) {
            plans.add(queue.getPlan());
        }
        form.setPaymentBillingPlansForTooltip(plans);
        form.setThirdPartyPlansForTooltip(thirdPartyTransferService.getThirdPartyPlansByMember(userId));

    }

    public static List<InformationModel> getHolds(String userId) throws RuntimeException {

        ContextInfo context = new ContextInfo();
        String effectiveUser = GlobalVariables.getUserSession().getActualPerson().getPrincipalId();
        context.setAuthenticatedPrincipalId(effectiveUser);

        List<InformationModel> models = new ArrayList<InformationModel>();

        try {

            if (holdService == null) {
                holdService = ContextUtils.getBean(HoldService.class);
            }

            List<AppliedHoldInfo> holds = holdService.getActiveAppliedHoldsByPerson(userId, context);

            for (AppliedHoldInfo hold : holds) {
                Information info = new Information();

                info.setEffectiveDate(hold.getEffectiveDate());
                info.setText(hold.getDescr().getPlain());
                InformationModel model = new InformationModel(info);
                models.add(model);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return models;
    }

}
