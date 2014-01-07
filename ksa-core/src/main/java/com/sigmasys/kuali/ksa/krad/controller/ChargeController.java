package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.ChargeForm;
import com.sigmasys.kuali.ksa.krad.form.PaymentForm;
import com.sigmasys.kuali.ksa.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by: dmulderink on 9/28/12 at 7:56 AM
 */
@Controller
@RequestMapping(value = "/chargeView")
public class ChargeController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(ChargeController.class);

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected ChargeForm createInitialForm(HttpServletRequest request) {
        ChargeForm form = new ChargeForm();
        form.setStatusMessage("");
        String userId = request.getParameter("userId");

        if (userId != null) {

            Account account = accountService.getFullAccount(userId);

            if (account == null) {
                String errMsg = "Cannot find Account by ID = " + userId;
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            form.setAccount(account);

        } /*else {
         String errMsg = "'userId' request parameter cannot be null";
         logger.error(errMsg);
         throw new IllegalStateException(errMsg);
      }*/

        return form;

    }

    /**
     * @param form
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") ChargeForm form, HttpServletRequest request) {

        // do get stuff...

        String viewId = request.getParameter("viewId");
        // example user1
        String userId = request.getParameter("userId");

        logger.info("View: " + viewId + " User: " + userId);

        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("'userId' request parameter must be specified");
        }

        // abbreviated payment initialization
        Account account = accountService.getFullAccount(userId);
        String accountId = account.getId();

        Charge charge = new Charge();
        charge.setAccount(account);
        charge.setAccountId(accountId);
        charge.setEffectiveDate(new Date());
        form.setCharge(charge);

        return getUIFModelAndView(form);
    }

    /**
     * @param form
     * @param result
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=submit")
    @Transactional(readOnly = false)
    public ModelAndView submit(@ModelAttribute("KualiForm") ChargeForm form, BindingResult result,
                               HttpServletRequest request, HttpServletResponse response) {
        // do submit stuff...

        String viewId = request.getParameter("viewId");
        // example user1
        String userId = request.getParameter("userId");

        logger.info("View: " + viewId + " User: " + userId);
        if (saveCharge(form)) {
            initCharge(form);
        }

        return getUIFModelAndView(form);
    }

    /**
     * Retrieves the transaction type.
     *
     * @param form PaymentForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=getTransactionType")
    public ModelAndView getTransactionType(@ModelAttribute("KualiForm") ChargeForm form) {

        String ttId = form.getChargeTransactionTypeId();

        form.setTransactionType(null);
        TransactionType type = null;

        try {
            form.setTransactionTypeMessage("");
            type = transactionService.getTransactionType(ttId, new Date());
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            form.setTransactionTypeMessage("Invalid Transaction Type");
        }

        if (type != null && type instanceof DebitType) {

            form.setTransactionType((DebitType) type);

        }

        return getUIFModelAndView(form);
    }


    private boolean saveCharge(ChargeForm form) {
        boolean saveResult = false;
        String statusMsg = "";
        Charge charge = form.getCharge();
        charge.setAccount(form.getAccount());

        String typeIdString = form.getChargeTransactionTypeId();
        Date effectiveDate = charge.getEffectiveDate();
        if (effectiveDate == null) {
            effectiveDate = new Date();
        }

        BigDecimal amount = charge.getAmount();
        if (amount == null) {
            statusMsg = "Amount must be a numerical value";
            form.setStatusMessage(statusMsg);
            logger.error(statusMsg);
            return saveResult;
        }

        int compareResult = amount.compareTo(BigDecimal.ZERO);
        if (compareResult <= 0) {
            statusMsg = "Amount must be a positive value";
            form.setStatusMessage(statusMsg);
            logger.error(statusMsg);
            return saveResult;
        }

        TransactionType tt;
        try {
            tt = transactionService.getTransactionType(typeIdString, effectiveDate);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            form.setStatusMessage(e.getMessage());
            return saveResult;
        }

        if (tt == null) {
            // Error handler here.
            statusMsg = "Invalid Transaction Type";
            form.setStatusMessage(statusMsg);
            logger.error(statusMsg);
            return saveResult;
        } else if (!(tt instanceof DebitType)) {
            statusMsg = "Transaction Type must be a charge type";
            form.setStatusMessage(statusMsg);
            logger.error(statusMsg);
            return saveResult;
        }

        try {

            charge = (Charge) transactionService.createTransaction(typeIdString, charge.getAccount().getId(), effectiveDate, charge.getAmount());

            if (charge.getId() != null) {
                form.setCharge(charge);
                statusMsg = tt.getDescription() + " charge saved";
                form.setStatusMessage(statusMsg);
                logger.info(statusMsg);
                saveResult = true;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            form.setStatusMessage(e.getMessage());
        }

        return saveResult;
    }

    private void initCharge(ChargeForm form) {

        // abbreviated payment initialization
        Account account = form.getAccount();
        String accountId = account.getId();

        Charge charge = new Charge();
        charge.setAccount(account);
        charge.setAccountId(accountId);
        charge.setEffectiveDate(new Date());
        form.setCharge(charge);
    }
}
