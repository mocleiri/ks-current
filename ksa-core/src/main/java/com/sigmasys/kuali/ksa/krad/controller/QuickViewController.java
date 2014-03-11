package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.QuickViewForm;
import com.sigmasys.kuali.ksa.krad.model.MemoModel;
import com.sigmasys.kuali.ksa.krad.util.AccountUtils;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.fm.FeeManagementSession;
import com.sigmasys.kuali.ksa.model.fm.FeeManagementSessionStatus;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.InformationService;
import com.sigmasys.kuali.ksa.service.PaymentService;
import com.sigmasys.kuali.ksa.service.fm.FeeManagementService;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * TODO -> handle InformationAccessLevel logic
 * <p/>
 * Created by: dmulderink on 9/28/12 at 2:25 PM
 */
@Controller
@RequestMapping(value = "/quickView")
public class QuickViewController extends GenericSearchController {

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private InformationService informationService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private FeeManagementService fmService;


    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected QuickViewForm createInitialForm(HttpServletRequest request) {

        QuickViewForm form = new QuickViewForm();

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

        return form;
    }

    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") QuickViewForm form, HttpServletRequest request) {

        String viewId = request.getParameter("viewId");
        String pageId = request.getParameter("pageId");
        String userId = request.getParameter("userId");

        logger.info("View: " + viewId + " User: " + userId);

        if ("QuickView".equals(viewId)) {

            if (!accountService.accountExists(userId)) {
                throw new IllegalArgumentException("Unknown account for userId '" + userId + "'");
            }

            if (pageId == null) {
                pageId = "QuickViewPage";
            }

            if ("QuickViewPage".equals(pageId)) {

                populateForm(userId, form);

            } else if ("QuickViewAddMemoPage".equals(pageId)) {

                if (userId == null || userId.isEmpty()) {
                    throw new IllegalArgumentException("'userId' request parameter must be specified");
                }

                // create a Memo, set defaults for the view
                Account account = accountService.getFullAccount(userId);
                String accountId = account.getId();

                // don't create a persistent memo when adding a memo, just initialize one for use
                // rather persist when the user submits (button control) an insert on the memo
                Memo memo = new Memo();
                memo.setAccount(account);
                memo.setAccountId(accountId);
                memo.setEffectiveDate(new Date());
                memo.setText("");
                memo.setAccessLevel(new InformationAccessLevel());

                form.setMemoModel(memo);
            }
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=refresh")
    public ModelAndView refresh(@ModelAttribute("KualiForm") QuickViewForm form, HttpServletRequest request) {

        String viewId = request.getParameter("viewId");
        String userId = request.getParameter("actionParameters[userId]");

        logger.info("View: " + viewId + " User: " + userId);

        if ("QuickView".equals(viewId)) {

            if (!accountService.accountExists(userId)) {
                String errMsg = "Unknown account for userId '" + userId + "'";
                logger.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }

            populateForm(userId, form);
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=paymentApplication")
    public ModelAndView paymentApplication(@ModelAttribute("KualiForm") QuickViewForm form, HttpServletRequest request) {

        String accountId = request.getParameter("actionParameters[userId]");

        if (StringUtils.isNotBlank(accountId)) {
            try {
                paymentService.paymentApplication(accountId);
                GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Payments have been successfully applied");
            } catch (Exception e) {
                String errMsg = "'Failed to execute payments. " + e.getMessage();
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errMsg);
                logger.error(errMsg, e);
            }
        }

        populateForm(accountId, form);
        return getUIFModelAndView(form);
    }


    /**
     * Ageing debts.
     *
     * @param form Kuali form instance
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=ageDebt")
    public ModelAndView ageDebt(@ModelAttribute("KualiForm") QuickViewForm form) {

        String accountId = form.getAccount().getId();
        boolean ignoreDeferment = Boolean.parseBoolean(form.getIgnoreDeferment());

        if (StringUtils.isNotBlank(accountId)) {
            try {
                accountService.ageDebt(accountId, ignoreDeferment);
                GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Debts have been successfully aged");
            } catch (Exception e) {
                String errMsg = "'Failed to age debts. " + e.getMessage();
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errMsg);
                logger.error(errMsg, e);
            }
        }

        populateForm(accountId, form);
        return getUIFModelAndView(form);
    }

    /**
     * Fee assessment.
     *
     * @param form Kuali form instance
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=assessFees")
    public ModelAndView assessFees(@ModelAttribute("KualiForm") QuickViewForm form) {

        String accountId = form.getAccount().getId();

        if (StringUtils.isNotBlank(accountId)) {

            try {

                FeeManagementSession fmSession = fmService.getOldestFeeManagementSession(accountId, FeeManagementSessionStatus.CURRENT);

                if (fmSession != null) {
                    fmService.processFeeManagementSession(fmSession.getId());
                    GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Fees have been successfully assessed");
                } else {
                    String errMsg = "FeeManagementSession could not be found for the account '" + accountId + "'";
                    GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errMsg);
                    logger.error(errMsg);
                }

            } catch (Exception e) {
                String errMsg = "'Failed to asses fees. " + e.getMessage();
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errMsg);
                logger.error(errMsg, e);
            }
        }

        populateForm(accountId, form);
        return getUIFModelAndView(form);
    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=insertMemo")
    public ModelAndView insertMemo(@ModelAttribute("KualiForm") QuickViewForm form) {

        MemoModel memoModel = form.getNewMemoModel();

        String accountId = form.getAccount().getId();
        String memoText = memoModel.getText();

        String accessLevelCode = "DEF_MEMO_LEVEL_CD";

        Date effectiveDate = memoModel.getEffectiveDate();
        Date expirationDate = memoModel.getExpirationDate();

        try {

            Memo memo = informationService.createMemo(accountId, memoText, accessLevelCode, effectiveDate, expirationDate, null);

            Long persistResult = informationService.persistInformation(memo);

            if (persistResult >= 0) {

                String statusMsg = "Memo saved";

                GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, statusMsg);

                List<Memo> memos = informationService.getMemos(accountId);

                form.setMemoModels(memos);

                form.setNewMemoModel(null);

            } else {
                String failedMsg = "Failed to add memo. result code: " + persistResult.toString();
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, failedMsg);
            }

        } catch (Exception e) {
            String errMsg = "'Failed to add memo. " + e.getMessage();
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errMsg);
            logger.error(errMsg, e);
        }

        return getUIFModelAndView(form);
    }


    /**
     * Populate the form per business needs for a single account by the account identifier
     *
     * @param userId User ID
     * @param form   QuickViewForm
     */
    private void populateForm(String userId, QuickViewForm form) {

        boolean ignoreDeferment = Boolean.parseBoolean(form.getIgnoreDeferment());

        Account accountById = accountService.getFullAccount(userId);
        if (accountById == null) {
            String errMsg = "Cannot find Account with ID = " + userId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        form.setAccount(accountById);

        ChargeableAccount chargeableAccount = (ChargeableAccount) accountById;

        form.setCompositeDefaultPersonName(accountById.getCompositeDefaultPersonName());
        form.setCompositeDefaultPostalAddress(accountById.getCompositeDefaultPostalAddress());

        BigDecimal balance = accountService.getDueBalance(userId, ignoreDeferment);
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }

        BigDecimal future = accountService.getFutureBalance(userId, ignoreDeferment);
        if (future == null) {
            future = BigDecimal.ZERO;
        }

        BigDecimal deferment = accountService.getDeferredBalance(userId);
        if (deferment == null) {
            deferment = BigDecimal.ZERO;
        }

        // Aging

        Date lastAgeDate = chargeableAccount.getLateLastUpdate();
        form.setLastAgeDate(lastAgeDate);

        Currency currency = auditableEntityService.getCurrency("USD");
        form.setCurrency(currency);

        LatePeriod latePeriod = chargeableAccount.getLatePeriod();

        form.setDaysLate1(latePeriod.getDaysLate1() != null ? latePeriod.getDaysLate1().toString() : "30");
        form.setDaysLate2(latePeriod.getDaysLate2() != null ? latePeriod.getDaysLate2().toString() : "60");
        form.setDaysLate3(latePeriod.getDaysLate3() != null ? latePeriod.getDaysLate3().toString() : "90");

        BigDecimal pastDue = BigDecimal.ZERO;

        if (chargeableAccount.getAmountLate1() != null) {
            form.setAged30(chargeableAccount.getAmountLate1());
            pastDue = pastDue.add(chargeableAccount.getAmountLate1());
        } else {
            form.setAged30(BigDecimal.ZERO);
        }

        if (chargeableAccount.getAmountLate2() != null) {
            form.setAged60(chargeableAccount.getAmountLate2());
            pastDue = pastDue.add(chargeableAccount.getAmountLate2());
        } else {
            form.setAged60(BigDecimal.ZERO);
        }

        if (chargeableAccount.getAmountLate3() != null) {
            form.setAged90(chargeableAccount.getAmountLate3());
            pastDue = pastDue.add(chargeableAccount.getAmountLate3());
        } else {
            form.setAged90(BigDecimal.ZERO);
        }

        BigDecimal agedTotal = BigDecimal.ZERO;

        if (chargeableAccount.getAmountLate1() != null &&
                chargeableAccount.getAmountLate2() != null &&
                chargeableAccount.getAmountLate3() != null) {
            agedTotal = agedTotal.add(chargeableAccount.getAmountLate1());
            agedTotal = agedTotal.add(chargeableAccount.getAmountLate2());
            agedTotal = agedTotal.add(chargeableAccount.getAmountLate3());
        }

        form.setAgedTotal(agedTotal);

        form.setPastDueAmount(pastDue);
        form.setBalanceAmount(balance);
        form.setFutureAmount(future);

        if (deferment != null) {
            form.setDefermentAmount(deferment);
        }

        BigDecimal outstandingBalance = accountService.getOutstandingBalance(userId, ignoreDeferment);
        if (outstandingBalance == null) {
            outstandingBalance = BigDecimal.ZERO;
        }

        form.setOutstandingAmount(outstandingBalance);

        AccountUtils.populateTransactionHeading(form, userId);

        List<Memo> memos = informationService.getMemos(userId);

        Iterator<Memo> iterator = memos.iterator();
        Date today = new Date();
        while (iterator.hasNext()) {
            Date expire = iterator.next().getExpirationDate();
            if (expire != null && expire.before(today)) {
                iterator.remove();
            }
        }

        form.setMemoModels(memos);
    }
}
