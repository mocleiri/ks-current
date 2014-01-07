package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.PaymentPlanForm;
import com.sigmasys.kuali.ksa.krad.model.*;
import com.sigmasys.kuali.ksa.krad.util.AuditableEntityKeyValuesFinder;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.pb.*;
import com.sigmasys.kuali.ksa.model.tp.*;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.PersistenceService;
import com.sigmasys.kuali.ksa.service.TransactionTransferService;
import com.sigmasys.kuali.ksa.service.pb.PaymentBillingService;
import com.sigmasys.kuali.ksa.service.tp.ThirdPartyTransferService;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping(value = "/paymentPlanView")
public class PaymentPlanController extends GenericSearchController {

    private static final String PAYMENT_PLAN_VIEW = "PaymentPlanView";
    private static final String TRANSFER_TYPE_FIELD = "PaymentPlanViewTransferType";
    private static final String RESPONSIBLE_ACCOUNT_FIELD = "PaymentPlanViewResponsibleAccount";
    private static final String RESPONSIBLE_ACCOUNT_SUGGEST = "PaymentPlanViewResponsibleAccountSuggest";

    private static final Log logger = LogFactory.getLog(PaymentPlanController.class);

    @Autowired
    private PaymentBillingService paymentBillingService;

    @Autowired
    private ThirdPartyTransferService thirdPartyTransferService;

    @Autowired
    private TransactionTransferService transactionTransferService;

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private PersistenceService persistenceService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected PaymentPlanForm createInitialForm(HttpServletRequest request) {
        PaymentPlanForm form = new PaymentPlanForm();
        String userId = request.getParameter("userId");

        if (userId != null) {

            Account account = accountService.getFullAccount(userId);

            if (account == null) {
                String errMsg = "Cannot find Account by ID = " + userId;
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            form.setAccount(account);

        }

        form.setTransferTypeOptionsFinder(this.getTransferTypeOptionsFinder());

        return form;
    }

    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") PaymentPlanForm form, BindingResult result,
                            HttpServletRequest request, HttpServletResponse response) {
        String pageId = request.getParameter("pageId");
        form.setRollupOptionsFinder(this.getRollupOptionsFinder());

        // Clean up the "new" objects to clear out any old data
        form.setNewPaymentBillingPlan(new PaymentBillingPlan());
        form.setNewThirdPartyPlan(new ThirdPartyPlan());

        if ("ManageThirdPartyPage".equals(pageId)) {
            populateThirdPartyForm(form);
        } else if ("ManagePaymentPlanPage".equals(pageId)) {
            populatePaymentBillingForm(form);
        }

        return getUIFModelAndView(form);
    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=getResponsibleAccount")
    public ModelAndView getResponsibleAccount(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {

        String accountString = request.getParameter("actionParameters[accountId]");

        if (accountString == null) {
            accountString = form.getResponsibleAccount();
        }

        Account account = accountService.getFullAccount(accountString);
        if (account instanceof ThirdPartyAccount) {
            form.setThirdPartyAccount((ThirdPartyAccount) account);
            form.setResponsibleAccountMessage("");
        } else {
            form.setResponsibleAccountMessage(accountString + " is not a valid Third Party Account");
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=filterThirdPartyAccount")
    public ModelAndView filterThirdPartyAccount(@ModelAttribute("KualiForm") PaymentPlanForm form) {
        String accountString = form.getFilterThirdPartyAccount();
        Account account = accountService.getFullAccount(accountString);
        if (account instanceof ThirdPartyAccount) {
            form.getFilterThirdPartyAccounts().add((ThirdPartyAccount) account);
        } else {
            String errorMessage = accountString + " is not a valid Third Party Account";
            GlobalVariables.getMessageMap().putError(RESPONSIBLE_ACCOUNT_SUGGEST, RiceKeyConstants.ERROR_CUSTOM, errorMessage);
        }

        populateThirdPartyForm(form);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=enrollThirdParty")
    public ModelAndView enrollThirdParty(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {

        String memberIdString = request.getParameter("actionParameters[memberId]");

        String planIdString = request.getParameter("actionParameters[planId]");
        Long planId = Long.parseLong(planIdString);

        try {
            thirdPartyTransferService.generateThirdPartyTransfer(planId, memberIdString, new Date());
            GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, memberIdString + " enrolled.");
        } catch (IllegalArgumentException e) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
        }

        populateThirdPartyForm(form);

        return getUIFModelAndView(form);

    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=enrollPaymentBilling")
    public ModelAndView enrollPaymentBilling(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {

        String memberIdString = request.getParameter("actionParameters[memberId]");

        String planIdString = request.getParameter("actionParameters[planId]");
        Long planId = Long.parseLong(planIdString);

        PaymentBillingPlan plan = paymentBillingService.getPaymentBillingPlan(planId);

        try {
            paymentBillingService.generatePaymentBillingTransfer(planId, memberIdString, plan.getMaxAmount(), new Date());
            GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, memberIdString + " enrolled.");
        } catch (IllegalArgumentException e) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
        }

        populatePaymentBillingForm(form);

        return getUIFModelAndView(form);

    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeFromThirdPartyQueue")
    public ModelAndView removeFromThirdPartyQueue(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {

        String memberIdString = request.getParameter("actionParameters[memberId]");

        String planIdString = request.getParameter("actionParameters[planId]");
        Long planId = Long.parseLong(planIdString);

        boolean result = thirdPartyTransferService.deleteThirdPartyPlanMember(planId, memberIdString);

        String message = "";
        if (result) {
            message = memberIdString + " removed from the plan";
        } else {
            message = memberIdString + " not removed from the plan";
        }
        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, message);

        populateThirdPartyForm(form);

        return getUIFModelAndView(form);

    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeFromPaymentBillingQueue")
    public ModelAndView removeFromPaymentBillingQueue(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {

        String memberIdString = request.getParameter("actionParameters[memberId]");

        String planIdString = request.getParameter("actionParameters[planId]");
        Long planId = Long.parseLong(planIdString);


        boolean result = paymentBillingService.deletePaymentBillingQueue(planId, memberIdString);

        String message = "";
        if (result) {
            message = memberIdString + " removed from the plan";
        } else {
            message = memberIdString + " not removed from the plan";
        }
        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, message);


        populatePaymentBillingForm(form);

        return getUIFModelAndView(form);

    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeThirdPartyAccount")
    public ModelAndView removeThirdPartyAccount(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {

        String accountString = request.getParameter("actionParameters[accountId]");

        List<ThirdPartyAccount> updatedAccounts = new ArrayList<ThirdPartyAccount>();

        // Don't remove while iterating through a list.
        for (ThirdPartyAccount account : form.getFilterThirdPartyAccounts()) {
            if (!account.getId().equals(accountString)) {
                updatedAccounts.add(account);
            }
        }

        form.setFilterThirdPartyAccounts(updatedAccounts);

        populateThirdPartyForm(form);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=reverseThirdPartyPlan")
    public ModelAndView reverseThirdPartyPlan(@ModelAttribute("KualiForm") PaymentPlanForm form, BindingResult result,
                                              HttpServletRequest request, HttpServletResponse response) {

        String planString = request.getParameter("actionParameters[planId]");
        Long planId;
        try {
            planId = Long.parseLong(planString);
        } catch (NumberFormatException e) {
            String errorMessage = planString + " is not a valid Third Party Plan Id";
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }

        if (planId == 0L) {
            String errorMessage = planString + " is not a valid Third Party Plan Id";
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }


        String transferDetailIdString = request.getParameter("actionParameters[transferDetailId]");
        Long transferDetailId;

        try {
            transferDetailId = Long.parseLong(transferDetailIdString);
        } catch (NumberFormatException e) {
            String errorMessage = transferDetailIdString + " is not a valid Third Party Transfer Detail Id";
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }

        if (transferDetailId == 0L) {
            String errorMessage = transferDetailIdString + " is not a valid Third Party Transfer Detail Id";
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }


        List<ThirdPartyPlanModel> planModels = form.getThirdPartyPlans();

        String memoText = null;

        ThirdPartyPlanModel planModel = null;

        for (ThirdPartyPlanModel plan : planModels) {
            if (planId.equals(plan.getParent().getId())) {
                planModel = plan;
                break;
            }
        }

        if (planModel == null) {
            String errorMessage = planString + " is not found in the list of Third Party Plans";
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }

        List<ThirdPartyMemberModel> memberModels = planModel.getPlanMembers();
        // Need to loop through and find the right one to get the memo text
        for (ThirdPartyMemberModel model : memberModels) {
            if (model.getTransferDetail() != null && transferDetailId.equals(model.getTransferDetail().getId())) {
                memoText = model.getMemo().getText();
                break;
            }
        }

        if (memoText == null || "".equals(memoText)) {
            String errorMessage = "Memo description is required when reversing a Third Party Payment Plan.  Plan not reversed.";
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }

        thirdPartyTransferService.reverseThirdPartyTransfer(transferDetailId, memoText);

        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Plan reversed");

        populateThirdPartyForm(form);

        return getUIFModelAndView(form);
    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=filterThirdPartyPlans")
    public ModelAndView filterThirdPartyPlans(@ModelAttribute("KualiForm") PaymentPlanForm form) {
        String planString = form.getFilterPlanName();
        Long planId = Long.parseLong(planString);

        ThirdPartyPlan plan = thirdPartyTransferService.getThirdPartyPlan(planId);

        if (plan != null) {
            form.getFilterThirdPartyPlans().add(plan);
        }

        populateThirdPartyForm(form);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=filterPaymentBillingPlans")
    public ModelAndView filterPaymentBillingPlans(@ModelAttribute("KualiForm") PaymentPlanForm form) {
        String planString = form.getFilterPlanName();
        Long planId = Long.parseLong(planString);

        PaymentBillingPlan plan = paymentBillingService.getPaymentBillingPlan(planId);

        if (plan != null) {
            form.getFilterPaymentBillingPlans().add(plan);
        }

        populatePaymentBillingForm(form);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=getPaymentBillingPlan")
    public ModelAndView getPaymentBillingPlan(@ModelAttribute("KualiForm") PaymentPlanForm form) {
        String planString = form.getPlanName();

        List<PaymentBillingPlan> plans = paymentBillingService.getPaymentBillingPlansByNamePattern(planString);

        if (plans != null && plans.size() > 0) {
            form.setPaymentBillingPlan(plans.get(0));
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=getThirdPartyPlan")
    public ModelAndView getThirdPartyPlan(@ModelAttribute("KualiForm") PaymentPlanForm form) {
        String planString = form.getPlanName();

        List<ThirdPartyPlan> plans = thirdPartyTransferService.getThirdPartyPlanByNamePattern(planString);

        if (plans != null && plans.size() > 0) {
            form.setBatchThirdPartyPlan(plans.get(0));
        } else {
            form.setBatchThirdPartyPlan(null);
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=batchPaymentBilling")
    public ModelAndView batchPaymentBilling(@ModelAttribute("KualiForm") PaymentPlanForm form) {
        PaymentBillingPlan plan = form.getPaymentBillingPlan();

        if (plan == null) {
            String planString = form.getPlanName();

            List<PaymentBillingPlan> plans = paymentBillingService.getPaymentBillingPlansByNamePattern(planString);

            if (plans != null && plans.size() > 0) {
                plan = plans.get(0);
            }

        }

        if (plan == null) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "No plan selected");
            return getUIFModelAndView(form);
        }

        String accounts = form.getBatchAccounts();

        List<String> accountList = Arrays.asList(accounts.split("[,\\s]+"));

        // validate them all.  If any are invalid then don't save
        List<String> invalidAccounts = new ArrayList<String>();


        for (String account : accountList) {
            boolean exists = accountService.accountExists(account);
            if (!exists) {
                invalidAccounts.add(account);
            }
        }

        if (invalidAccounts.size() > 0) {
            // one or more are invalid.
            String invalid = StringUtils.collectionToCommaDelimitedString(invalidAccounts);
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "The following accounts are not valid: " + invalid);
            return getUIFModelAndView(form);
        }

        Date today = new Date();
        int addedCount = 0;
        int errorCount = 0;
        for (String account : accountList) {
            try {
                paymentBillingService.createPaymentBillingQueue(plan.getId(), account, plan.getMaxAmount(), today, false);
                addedCount++;
            } catch (IllegalArgumentException e) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
                errorCount++;
            }
        }

        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, addedCount + " accounts added to " + plan.getName() + " plan");

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=batchThirdParty")
    public ModelAndView batchThirdParty(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {
        String action = request.getParameter("actionParameters[action]");
        boolean enroll = false;
        if ("enroll".equals(action)) {
            enroll = true;
        }

        String forceReversalString = form.getForceReversal();
        Boolean forceReversal = false;
        if ("on".equalsIgnoreCase(forceReversalString)) {
            forceReversal = true;
        }

        ThirdPartyPlan plan = form.getBatchThirdPartyPlan();

        if (plan == null || plan.getId() == null) {
            String planString = form.getPlanName();

            List<ThirdPartyPlan> plans = thirdPartyTransferService.getThirdPartyPlanByNamePattern(planString);

            if (plans != null && plans.size() > 1) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Multiple Third Party plans match name: " + planString);
                return getUIFModelAndView(form);
            } else if (plans != null && plans.size() == 1) {
                plan = plans.get(0);
            }
        }

        if (plan == null || plan.getId() == null) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "No valid plan selected");
            return getUIFModelAndView(form);
        }

        String accounts = form.getBatchAccounts();

        List<String> accountList = Arrays.asList(accounts.split("[,\\s]+"));

        // validate them all.  If any are invalid then don't save
        List<String> invalidAccounts = new ArrayList<String>();


        for (String account : accountList) {
            boolean exists = accountService.accountExists(account);
            if (!exists) {
                invalidAccounts.add(account);
            }
        }

        if (invalidAccounts.size() > 0) {
            // one or more are invalid.
            String invalid = StringUtils.collectionToCommaDelimitedString(invalidAccounts);
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "The following accounts are not valid: " + invalid);
            return getUIFModelAndView(form);
        }

        Date today = new Date();
        int newCount = 0;
        int enrollCount = 0;
        int reversalCount = 0;

        for (String account : accountList) {
            ThirdPartyPlanMember member = thirdPartyTransferService.getThirdPartyPlanMember(plan.getId(), account);

            if (member == null) {
                thirdPartyTransferService.createThirdPartyPlanMember(account, plan.getId(), 1);
                newCount++;
            }

            if (enroll) {
                ThirdPartyTransferDetail transferDetail = thirdPartyTransferService.getThirdPartyTransferDetail(plan.getId(), account);

                if (transferDetail == null) {
                    thirdPartyTransferService.generateThirdPartyTransfer(plan.getId(), account, new Date());
                    enrollCount++;
                } else if (forceReversal) {
                    thirdPartyTransferService.reverseThirdPartyTransfer(transferDetail.getId(), account);

                    thirdPartyTransferService.generateThirdPartyTransfer(plan.getId(), account, new Date());

                    reversalCount++;
                }
            }
        }

        StringBuilder message = new StringBuilder();
        message.append(newCount);
        message.append(" accounts added");
        if (enrollCount > 0) {
            message.append(", ");
            message.append(enrollCount);
            message.append(" accounts enrolled");
        }
        if (reversalCount > 0) {
            message.append(", ");
            message.append(reversalCount);
            message.append(" accounts reversed");
        }
        message.append(" for ");
        message.append(plan.getName());
        message.append(" plan");
        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, message.toString());

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeThirdPartyPlan")
    public ModelAndView removeThirdPartyPlan(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {

        String planString = request.getParameter("actionParameters[planId]");
        Long planId = Long.parseLong(planString);

        List<ThirdPartyPlan> updatedPlans = new ArrayList<ThirdPartyPlan>();

        // Don't remove while iterating through a list.
        for (ThirdPartyPlan plan : form.getFilterThirdPartyPlans()) {
            if (!plan.getId().equals(planId)) {
                updatedPlans.add(plan);
            }
        }

        form.setFilterThirdPartyPlans(updatedPlans);

        populateThirdPartyForm(form);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removePaymentBillingPlan")
    public ModelAndView removePaymentBillingPlan(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {

        String planString = request.getParameter("actionParameters[planId]");
        Long planId = Long.parseLong(planString);

        List<PaymentBillingPlan> updatedPlans = new ArrayList<PaymentBillingPlan>();

        // Don't remove while iterating through a list.
        for (PaymentBillingPlan plan : form.getFilterPaymentBillingPlans()) {
            if (!plan.getId().equals(planId)) {
                updatedPlans.add(plan);
            }
        }

        form.setFilterPaymentBillingPlans(updatedPlans);

        populatePaymentBillingForm(form);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=transactionTransferList")
    public ModelAndView transactionTransferList(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {

        String memberIdString = request.getParameter("actionParameters[memberId]");

        String planIdString = request.getParameter("actionParameters[planId]");
        Long planId = Long.parseLong(planIdString);

        String transferDetailIdString = request.getParameter("actionParameters[transferDetailId]");
        Long transferDetailId = Long.parseLong(transferDetailIdString);

        List<ThirdPartyPlanModel> plans = form.getThirdPartyPlans();
        ThirdPartyPlanModel currentPlan = null;
        for (ThirdPartyPlanModel plan : plans) {
            if (planId.equals(plan.getParent().getId())) {
                currentPlan = plan;
                break;
            }
        }

        if (currentPlan == null) {
            ThirdPartyPlan plan = thirdPartyTransferService.getThirdPartyPlan(planId);
            currentPlan = new ThirdPartyPlanModel();
            currentPlan.setParent(plan);
        }
        form.setThirdPartyPlan(currentPlan);


        ThirdPartyTransferDetail transferDetail = thirdPartyTransferService.getThirdPartyTransferDetail(transferDetailId);
        transferDetail.getDirectChargeAccount().getCompositeDefaultPersonName();

        // check that the member id passed in matches what's really in the database
        if (!memberIdString.equals(transferDetail.getDirectChargeAccount().getId())) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Transfer Detail doesn't match selected user.");
            return getUIFModelAndView(form);
        }

        TransactionTransferModel model = new TransactionTransferModel();
        model.setThirdPartyTransferDetail(transferDetail);
        String transferGroupId = transferDetail.getTransferGroupId();
        List<TransactionTransfer> transactionTransfers = transactionTransferService.getTransactionTransfersByGroupId(transferGroupId);

        model.setTransactionTransfers(transactionTransfers);

        form.setThirdPartyTransactionTransfer(model);

        form.setPageId("ViewTransactionTransferListPage");

        return getUIFModelAndView(form);
    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=reexecuteThirdParty")
    public ModelAndView reexecuteThirdParty(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {
        String planIdString = request.getParameter("actionParameters[planId]");
        Long planId;

        try {
            planId = Long.parseLong(planIdString);
        } catch (NumberFormatException e) {
            String errorMessage = planIdString + " is not a valid Third Party Plan";
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }

        String userId = request.getParameter("actionParameters[accountId]");

        thirdPartyTransferService.generateThirdPartyTransfer(planId, userId, new Date());
        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Plan re-executed for user " + userId);


        populateThirdPartyForm(form);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=reexecutePaymentBilling")
    public ModelAndView reexecutePaymentBilling(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {
        String planIdString = request.getParameter("actionParameters[planId]");
        Long planId;

        try {
            planId = Long.parseLong(planIdString);
        } catch (NumberFormatException e) {
            String errorMessage = planIdString + " is not a valid Payment Billing Plan";
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }

        PaymentBillingPlan plan = paymentBillingService.getPaymentBillingPlan(planId);

        String userId = request.getParameter("actionParameters[accountId]");

        try {
            paymentBillingService.generatePaymentBillingTransfer(planId, userId, plan.getMaxAmount(), new Date());
            GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Plan re-executed for user " + userId);
        } catch (RuntimeException e) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
            return getUIFModelAndView(form);
        }

        populatePaymentBillingForm(form);

        return getUIFModelAndView(form);
    }

    public KeyValuesFinder getTransferTypeOptionsFinder() {
        // Don't cache the values finder or else new entries will not show when added
        return new AuditableEntityKeyValuesFinder<TransferType>(TransferType.class);
    }


    private void populatePaymentBillingForm(PaymentPlanForm form) {
        List<PaymentBillingPlan> allPlans = new ArrayList<PaymentBillingPlan>();

        if (form.getFilterPaymentBillingPlans().size() == 0) {
            allPlans = paymentBillingService.getPaymentBillingPlans();
        } else {
            allPlans.addAll(form.getFilterPaymentBillingPlans());
        }

        List<PaymentBillingPlanModel> models = new ArrayList<PaymentBillingPlanModel>(allPlans.size());

        for (PaymentBillingPlan plan : allPlans) {

            PaymentBillingPlanModel model = new PaymentBillingPlanModel();
            model.setParent(plan);

            List<PaymentBillingQueue> paymentBillingQueues = paymentBillingService.getPaymentBillingQueuesByPlanId(plan.getId());

            for (PaymentBillingQueue paymentBillingQueue : paymentBillingQueues) {
                paymentBillingQueue.getDirectChargeAccount().getCompositeDefaultPersonName();
                PaymentBillingTransferDetail transferDetail = paymentBillingQueue.getTransferDetail();
                if (transferDetail == null) {
                    model.getQueuedMembers().add(paymentBillingQueue);
                } else if (PaymentBillingChargeStatus.ACTIVE.equals(transferDetail.getChargeStatus())) {
                    model.getPlanMembers().add(paymentBillingQueue);
                } else if (PaymentBillingChargeStatus.REVERSED.equals(transferDetail.getChargeStatus())) {
                    model.getReversedMembers().add(paymentBillingQueue);
                }
            }


            models.add(model);

        }

        form.setPaymentBillingPlans(models);
    }

    private void populateThirdPartyForm(PaymentPlanForm form) {
        List<ThirdPartyAccount> accounts = form.getFilterThirdPartyAccounts();
        List<ThirdPartyPlan> plans = form.getFilterThirdPartyPlans();

        List<ThirdPartyPlan> allPlans = new ArrayList<ThirdPartyPlan>();

        if ((accounts == null || accounts.size() == 0) && (plans == null || plans.size() == 0)) {
            allPlans = thirdPartyTransferService.getThirdPartyPlans();
        } else if (accounts == null || accounts.size() == 0) {
            allPlans = form.getFilterThirdPartyPlans();
        } else {
            // Accounts have something in them.  Plans might too.
            Set<String> users = new HashSet<String>();
            for (ThirdPartyAccount account : accounts) {
                users.add(account.getId());
            }

            List<ThirdPartyPlan> accountPlans = thirdPartyTransferService.getThirdPartyPlans(users);

            if (plans == null || plans.size() == 0) {
                allPlans = accountPlans;
            } else {
                // Make sure that only the accounts that are also within the plans are displayed

                for (ThirdPartyPlan plan : accountPlans) {
                    Long id = plan.getId();
                    for (ThirdPartyPlan filteredPlan : plans) {
                        if (filteredPlan.getId().equals(id)) {
                            allPlans.add(plan);
                            break;
                        }
                    }
                }
            }
        }

        List<ThirdPartyPlanModel> models = new ArrayList<ThirdPartyPlanModel>(allPlans.size());

        for (ThirdPartyPlan plan : allPlans) {

            ThirdPartyPlanModel model = new ThirdPartyPlanModel();
            model.setParent(plan);

            ThirdPartyAccount account = plan.getThirdPartyAccount();
            if (account == null) {
                logger.error("Plan: " + plan.getCode() + " has a null third party account");
            } else {
                account.getCompositeDefaultPersonName();
            }

            List<ThirdPartyAllowableCharge> allowableCharges = thirdPartyTransferService.getThirdPartyAllowableCharges(plan.getId());
            List<ThirdPartyAllowableChargeModel> allowableChargeModels = model.getThirdPartyAllowableCharges();
            for (ThirdPartyAllowableCharge charge : allowableCharges) {
                ThirdPartyAllowableChargeModel thirdPartyAllowableChargeModel = new ThirdPartyAllowableChargeModel();
                try {
                    BeanUtils.copyProperties(thirdPartyAllowableChargeModel, charge);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
                BigDecimal percentage = thirdPartyAllowableChargeModel.getMaxPercentage().divide(Constants.BIG_DECIMAL_HUNDRED);
                thirdPartyAllowableChargeModel.setMaxPercentage(percentage);
                allowableChargeModels.add(thirdPartyAllowableChargeModel);
            }

            model.setThirdPartyAllowableCharges(allowableChargeModels);

            List<ThirdPartyTransferDetail> transferDetails = thirdPartyTransferService.getThirdPartyTransfersByPlanId(plan.getId());

            for (ThirdPartyTransferDetail transferDetail : transferDetails) {
                ThirdPartyChargeStatus status = transferDetail.getChargeStatus();
                transferDetail.getDirectChargeAccount().getCompositeDefaultPersonName();

                if (ThirdPartyChargeStatus.ACTIVE.equals(status)) {
                    ThirdPartyMemberModel memberModel = new ThirdPartyMemberModel();
                    memberModel.setTransferDetail(transferDetail);
                    memberModel.setPlan(plan);

                    model.getPlanMembers().add(memberModel);
                } else if (ThirdPartyChargeStatus.REVERSED.equals(status)) {
                    model.getReversedMembers().add(transferDetail);
                }

            }

            List<ThirdPartyPlanMember> members = thirdPartyTransferService.getThirdPartyPlanMembers(plan.getId());

            for (ThirdPartyPlanMember member : members) {
                if (!member.isExecuted()) {
                    model.getQueuedMembers().add(member);
                    // Make sure it is loaded from the database
                    member.getDirectChargeAccount().getCompositeDefaultPersonName();
                }
            }


            models.add(model);

        }

        form.setThirdPartyPlans(models);

    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=insertPaymentBilling")
    public ModelAndView insertPaymentBilling(@ModelAttribute("KualiForm") PaymentPlanForm form) {

        boolean errors = false;

        PaymentBillingPlan plan = form.getNewPaymentBillingPlan();

        TransferType transferType = auditableEntityService.getAuditableEntity("PB", TransferType.class);

        if (plan == null) {
            plan = new PaymentBillingPlan();
        }
        if (plan.getCode() == null) {
            plan.setCode(form.getCode());
        }

        if (plan.getName() == null) {
            plan.setName(form.getName());
        }

        if (plan.getDescription() == null) {
            plan.setDescription(form.getDescription());
        }
        if (plan.getOpenPeriodStartDate() == null) {
            plan.setOpenPeriodStartDate(form.getOpenPeriodStartDate());
        }

        if (plan.getOpenPeriodEndDate() == null) {
            plan.setOpenPeriodEndDate(form.getOpenPeriodEndDate());
        }

        if (plan.getChargePeriodStartDate() == null) {
            plan.setChargePeriodStartDate(form.getChargePeriodStartDate());
        }

        if (plan.getChargePeriodEndDate() == null) {
            plan.setChargePeriodEndDate(form.getChargePeriodEndDate());
        }

        if (plan.getMaxAmount() == null) {
            plan.setMaxAmount(form.getMaxAmount());
        }

        plan.setGlCreationImmediate("Y".equals(form.getPostToGeneralLedger()));

        PaymentRoundingType roundingType = EnumUtils.findById(PaymentRoundingType.class, form.getNonRoundedPaymentType());
        plan.setPaymentRoundingType(roundingType);

        ScheduleType scheduleType = EnumUtils.findById(ScheduleType.class, form.getLateMembership());
        if (scheduleType == null) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Late Membership is a required field");
            errors = true;
        }
        plan.setScheduleType(scheduleType);

        plan.setTransferType(transferType);

        plan.setFlatFeeDebitTypeId(form.getFlatFeeTransactionType());
        plan.setVariableFeeDebitTypeId(form.getVariableFeeTransactionType());

        if (errors) {
            return getUIFModelAndView(form);
        }

        Long planId = paymentBillingService.persistPaymentBillingPlan(plan);
        plan.setId(planId);


        List<PaymentBillingAllowableChargeModel> allowableCharges = form.getPaymentBillingAllowableCharges();
        for (PaymentBillingAllowableCharge charge : allowableCharges) {
            paymentBillingService.createPaymentBillingAllowableCharge(planId, charge.getTransactionTypeMask(),
                    charge.getMaxAmount(), charge.getMaxPercentage().multiply(Constants.BIG_DECIMAL_HUNDRED),
                    charge.getPriority());
        }


        List<PaymentBillingDateModel> billingModels = form.getPaymentBillingDates();
        for (PaymentBillingDateModel model : billingModels) {
            String rollupIdString = model.getRollupId();
            Long rollupId = null;
            try {
                rollupId = Long.parseLong(rollupIdString);
            } catch (NumberFormatException e) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Rollup is required.  '" + rollupIdString + "' is not a valid rollup");
                errors = true;
            }
            if (!errors) {
                PaymentBillingDate newBillingDate = paymentBillingService.createPaymentBillingDate(planId, rollupId, model.getPercentage(), model.getEffectiveDate());
            }
        }

        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Payment Billing Plan saved.");

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=insertThirdParty")
    public ModelAndView insertThirdParty(@ModelAttribute("KualiForm") PaymentPlanForm form) {

        ThirdPartyPlan plan = form.getNewThirdPartyPlan();
        if (plan == null) {
            plan = new ThirdPartyPlan();
        }

        if (plan.getCode() == null) {
            plan.setCode(form.getCode());
        }

        if (plan.getName() == null) {
            plan.setName(form.getName());
        }

        if (plan.getDescription() == null) {
            plan.setDescription(form.getDescription());
        }

        if (plan.getOpenPeriodStartDate() == null) {
            plan.setOpenPeriodStartDate(form.getOpenPeriodStartDate());
        }

        if (plan.getOpenPeriodEndDate() == null) {
            plan.setOpenPeriodEndDate(form.getOpenPeriodEndDate());
        }

        if (plan.getChargePeriodStartDate() == null) {
            plan.setChargePeriodStartDate(form.getChargePeriodStartDate());
        }

        if (plan.getChargePeriodEndDate() == null) {
            plan.setChargePeriodEndDate(form.getChargePeriodEndDate());
        }

        if (plan.getMaxAmount() == null) {
            plan.setMaxAmount(form.getMaxAmount());
        }

        if (plan.getEffectiveDate() == null) {
            plan.setEffectiveDate(form.getEffectiveDate());
        }

        if (plan.getRecognitionDate() == null) {
            plan.setRecognitionDate(form.getRecognitionDate());
        }

        boolean errors = false;

        if (form.getThirdPartyAccount() == null || form.getThirdPartyAccount().getId() == null) {
            String errorMessage = "Invalid third party account";
            GlobalVariables.getMessageMap().putError(RESPONSIBLE_ACCOUNT_FIELD, RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            errors = true;
        }

        String accountId = null;
        if (plan.getThirdPartyAccount() != null) {
            accountId = plan.getThirdPartyAccount().getId();
        } else if (form.getThirdPartyAccount() != null) {
            accountId = form.getThirdPartyAccount().getId();
        }

        Long transferTypeId = null;
        if (form.getTransferType() != null) {
            transferTypeId = Long.parseLong(form.getTransferType());
        } else {
            String errorMessage = "Transfer Type is required";
            GlobalVariables.getMessageMap().putError(TRANSFER_TYPE_FIELD, RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            errors = true;
        }

        if (errors) {
            return getUIFModelAndView(form);
        }

        plan = thirdPartyTransferService.createThirdPartyPlan(plan.getCode(), plan.getName(), plan.getDescription(), transferTypeId, accountId,
                plan.getMaxAmount(), plan.getEffectiveDate(), plan.getRecognitionDate(), plan.getOpenPeriodStartDate(), plan.getOpenPeriodEndDate(),
                plan.getChargePeriodStartDate(), plan.getChargePeriodEndDate());

        for (ThirdPartyAllowableCharge charge : form.getThirdPartyAllowableCharges()) {
            thirdPartyTransferService.createThirdPartyAllowableCharge(plan.getId(), charge.getTransactionTypeMask(),
                    charge.getMaxAmount(), charge.getMaxPercentage().multiply(Constants.BIG_DECIMAL_HUNDRED),
                    charge.getPriority(), charge.getDistributionPlan());
        }

        String message = "Third Party Payment Plan saved";
        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, message);


        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=updateThirdParty")
    public ModelAndView updateThirdParty(@ModelAttribute("KualiForm") PaymentPlanForm form, HttpServletRequest request) {
        String planString = request.getParameter("actionParameters[planId]");
        Long planId = Long.parseLong(planString);

        List<ThirdPartyPlanModel> plans = form.getThirdPartyPlans();
        for (ThirdPartyPlanModel model : plans) {
            ThirdPartyPlan plan = model.getParent();
            if (plan != null && planId.equals(plan.getId())) {
                if (plan.getCode().length() > 20) {
                    plan.setCode(plan.getCode().substring(0, 20));
                }


                persistenceService.persistEntity(plan);

                GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Third Party Plan updated");
            }
        }


        return getUIFModelAndView(form);
    }

    public KeyValuesFinder getRollupOptionsFinder() {
        // Don't cache the values finder or else new entries will not show when added
        return new AuditableEntityKeyValuesFinder<Rollup>(Rollup.class);
    }


}
