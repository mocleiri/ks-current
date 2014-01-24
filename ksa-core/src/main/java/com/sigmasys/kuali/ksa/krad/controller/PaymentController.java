package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.PaymentForm;
import com.sigmasys.kuali.ksa.krad.model.TransactionModel;
import com.sigmasys.kuali.ksa.krad.util.AuditableEntityKeyValuesFinder;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.Currency;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.InformationService;
import com.sigmasys.kuali.ksa.service.PaymentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * PaymentController
 *
 * @author Tim
 */
@Controller
@RequestMapping(value = "/paymentView")
public class PaymentController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(PaymentController.class);

    private static final String PAYMENT_VIEW = "PaymentView";
    private static final String ADD_PAYMENT_PAGE = "AddPaymentPage";
    private static final String ALLOCATE_PAYMENT_PAGE = "AllocatePaymentPage";

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InformationService informationService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected PaymentForm createInitialForm(HttpServletRequest request) {

        PaymentForm form = new PaymentForm();

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
        }

        return form;
    }

    /**
     * Default GET method
     *
     * @param form PaymentForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") PaymentForm form) {
        return getUIFModelAndView(form);
    }

    /**
     * Payment creating method
     *
     * @param form    PaymentForm
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=create")
    public ModelAndView create(@ModelAttribute("KualiForm") PaymentForm form, HttpServletRequest request) {

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
        form.setAccount(account);

        initPayment(form);
        return getUIFModelAndView(form);
    }

    /**
     * Retrieves the transaction type.
     *
     * @param form PaymentForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=getTransactionType")
    public ModelAndView getTransactionType(@ModelAttribute("KualiForm") PaymentForm form) {

        String ttId = form.getPaymentTransactionTypeId();

        form.setTransactionType(null);
        TransactionType type = null;

        try {
            form.setTransactionTypeMessage("");
            type = transactionService.getTransactionType(ttId, form.getPayment().getEffectiveDate());
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            form.setTransactionTypeMessage("Invalid Transaction Type: '" + ttId + " for effective date '" + form.getPayment().getEffectiveDate() + "'");
        }

        if (type != null && type instanceof CreditType) {

            form.setTransactionType((CreditType) type);

            String systemCurrency = java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode();
            form.setSystemCurrency(systemCurrency);

            Currency curr = auditableEntityService.getCurrency(systemCurrency);
            form.setCurrencyId(curr.getId().toString());
        }

        return getUIFModelAndView(form);
    }

    /**
     * Form submitting method
     *
     * @param form    PaymentForm
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=submit")
    @Transactional(readOnly = false)
    public ModelAndView submit(@ModelAttribute("KualiForm") PaymentForm form, HttpServletRequest request) {
        // do submit stuff...

        String viewId = request.getParameter("viewId");

        String userId = form.getAccount().getId();

        logger.info("View: " + viewId + " User: " + userId);

        if (!savePayment(form)) {
            return getUIFModelAndView(form);
        }

        TransactionModel model = form.getPayment();
        Payment payment = (Payment)model.getParentTransaction();

        if (payment != null && payment.getId() != null && form.isAgeAccount()) {
            accountService.ageDebt(payment.getAccount().getId(), false);
            GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Account Aged");

        }

        if(form.isRunPaymentApplication()) {
            if (userId != null && !userId.trim().isEmpty()) {
                try {
                    paymentService.paymentApplication(userId);

                    GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Payments successfully applied");
                } catch(Throwable e) {
                    GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getLocalizedMessage());
                    return getUIFModelAndView(form);
                }
            }
        }

        // determine next screen and set up data
        if(form.isAllocatePayment()){
            form.setPageId(ALLOCATE_PAYMENT_PAGE);
            List<TransactionModel> models = this.getTransactionModels(userId, payment);
            form.setAllocations(models);

        } else if(form.isAddAdditionalPayment()){

            form.setPageId(ADD_PAYMENT_PAGE);

            Properties props = new Properties();

            props.put("pageId", ADD_PAYMENT_PAGE);
            props.put("viewId", form.getViewId());
            props.put("methodToCall", "create");
            props.put("userId", userId);

            return performRedirect(form, "paymentView", props);


        } else {
            this.initPayment(form);
        }

        return getUIFModelAndView(form);
    }

    /**
     * Form submitting method
     *
     * @param form    PaymentForm
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=allocate")
    @Transactional(readOnly = false)
    public ModelAndView allocate(@ModelAttribute("KualiForm") PaymentForm form, HttpServletRequest request) {


        TransactionModel paymentModel = form.getPayment();
        Payment payment = (Payment)paymentModel.getParentTransaction();

        if(payment == null){
            String statusMsg = "Unknown payment.  Unable to process allocations.";
            GlobalVariables.getMessageMap().putError("", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg);

            return getUIFModelAndView(form);
        }

        BigDecimal total = BigDecimal.ZERO;

        List<TransactionModel> allocations = form.getAllocations();
        for(TransactionModel model : allocations){
            BigDecimal amount = model.getNewAllocation();

            if(amount != null){
                if(amount.compareTo(model.getUnallocatedAmount()) > 0){
                    String statusMsg = "Allocation amount of " + amount + " cannot be greater than the unallocated amount of " + model.getUnallocatedAmount() + " for " + model.getParentTransaction().getTransactionType().getDescription();
                    GlobalVariables.getMessageMap().putError(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
                }
                total = total.add(amount);
            }
        }

        // Total of allocations can't be greater than the payment amount
        if(total.compareTo(payment.getAmount()) > 0) {
            String statusMsg = "Allocations cannot add up to more than the payment amount";
            GlobalVariables.getMessageMap().putError(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg);

            return getUIFModelAndView(form);
        }

        if(!GlobalVariables.getMessageMap().hasErrors()) {
            for(TransactionModel model : allocations) {
                BigDecimal amount = model.getNewAllocation();
                if(amount != null && (amount.compareTo(BigDecimal.ZERO) > 0)) {
                    try{
                        transactionService.createLockedAllocation(payment.getId(), model.getParentTransaction().getId(), amount);
                    } catch(IllegalStateException e) {
                        GlobalVariables.getMessageMap().putError(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, e.getMessage());

                        return getUIFModelAndView(form);

                    }
                }
            }

            String userId = form.getAccount().getId();
            paymentService.paymentApplication(userId);
            List<TransactionModel> models = this.getTransactionModels(userId, payment);
            form.setAllocations(models);

            Payment pmt = transactionService.getPayment(payment.getId());
            form.setPayment(new TransactionModel(pmt));

            String statusMsg = "Payment successfully allocated";
            GlobalVariables.getMessageMap().putInfo(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);

            if(form.isAddAdditionalPayment()){
                this.initPayment(form);
                form.setPageId(ADD_PAYMENT_PAGE);
            }
        }

        return getUIFModelAndView(form);
    }

    public KeyValuesFinder getCurrencyOptionsFinder() {
        // Don't cache the values finder or else new entries will not show when added
        return new AuditableEntityKeyValuesFinder<Currency>(Currency.class, true);
    }

    public KeyValuesFinder getRollupOptionsFinder() {
        // Don't cache the values finder or else new entries will not show when added
        return new AuditableEntityKeyValuesFinder<Rollup>(Rollup.class);
    }

    private List<TransactionModel> getTransactionModels(String userId, Payment payment){

        List<Transaction> transactions = transactionService.getTransactions(userId);

        List<TransactionModel> models = new ArrayList<TransactionModel>(transactions.size());
        for (Transaction t : transactions) {
            if(transactionService.canPay(payment, t)){
                TransactionModel m = new TransactionModel(t);
                models.add(m);
            }
        }

        return models;
    }

    private boolean savePayment(PaymentForm form) {

        boolean saveResult = false;
        boolean errors = false;
        String statusMsg = "";
        TransactionModel model = form.getPayment();
        Payment payment = (Payment)model.getParentTransaction();
        payment.setAccount(form.getAccount());

        String typeIdString = form.getPaymentTransactionTypeId();
        Date effectiveDate = model.getEffectiveDate();
        if (effectiveDate == null) {
            effectiveDate = new Date();
        }

        // Because of the way that the fields are hidden and shown on the screen,
        // Check the currency of the native amount If that matches the system currency
        // then the native amount will contain the actual amount.
        BigDecimal amount = payment.getAmount();
        BigDecimal nativeAmount = payment.getNativeAmount();
        String currencyId = form.getCurrencyId();

        String systemCurrency = java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        Currency systemCurr = auditableEntityService.getCurrency(systemCurrency);
        String systemCurrencyId = systemCurr.getId().toString();

        if (currencyId == null || systemCurrencyId.equals(currencyId)) {
            amount = nativeAmount;
            nativeAmount = null;
        } else {
            Currency curr = auditableEntityService.getAuditableEntity(Long.parseLong(currencyId), Currency.class);
            payment.setCurrency(curr);
        }

        if (amount == null) {
            statusMsg = "Amount must be a numerical value";
            GlobalVariables.getMessageMap().putError(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg);
            errors = true;
        } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            statusMsg = "Amount must be a positive value";
            GlobalVariables.getMessageMap().putError(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg);
            errors = true;
        }

        TransactionType tt = null;

        try {
            tt = transactionService.getTransactionType(typeIdString, effectiveDate);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (tt == null) {
            // Error handler here.
            statusMsg = "Invalid Payment Type";
            GlobalVariables.getMessageMap().putError(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg);
            errors = true;
        } else if (!(tt instanceof CreditType)) {
            statusMsg = "Payment Type must be a credit type";
            GlobalVariables.getMessageMap().putError(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg);
            errors = true;
        }

        // Handle all error checking prior to this point.
        if (errors) {
            return saveResult;
        }

        try {

            payment = (Payment) transactionService.createTransaction(typeIdString, form.getExternalId(),
                    payment.getAccount().getId(), effectiveDate, model.getRecognitionDate(), null, amount, false);

            boolean columnsChanged = false;
            if (payment != null && payment.getId() != null) {

                if(model.getOriginationDate() != null) {
                    payment.setOriginationDate(model.getOriginationDate());
                    columnsChanged = true;
                }
                if(model.isInternal()) {
                    payment.setInternal(model.isInternal());
                    columnsChanged = true;
                }

                String statementText = model.getStatementText();
                if(statementText != null && (! "".equals(statementText)) && (! tt.getDescription().equals(statementText))) {
                    payment.setStatementText(model.getStatementText());
                    columnsChanged = true;
                }

                if(form.getRollupId() != null && ! "".equals(form.getRollupId())) {
                    Rollup rollup = auditableEntityService.getAuditableEntity(form.getRollupId(), Rollup.class);
                    payment.setRollup(rollup);
                    columnsChanged = true;
                }

                if (nativeAmount != null) {
                    payment.setAmount(amount);
                    Currency c = auditableEntityService.getAuditableEntity(new Long(currencyId), Currency.class);
                    payment.setCurrency(c);
                    payment.setNativeAmount(nativeAmount);

                    columnsChanged = true;
                }

                Memo memo = form.getMemoModel();
                if(memo != null){
                    String text = memo.getText();
                    if(text != null && !"".equals(text.trim())){
                        Long memoId = informationService.persistInformation(memo);
                        if(memoId != null){
                            informationService.associateWithTransaction(memoId, payment.getId());
                        }
                    }
                }

                if(columnsChanged) {
                    transactionService.persistTransaction(payment);
                }

                form.setPayment(new TransactionModel(payment));
                statusMsg = tt.getDescription() + " Payment saved";
                GlobalVariables.getMessageMap().putInfo(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
                logger.info(statusMsg);
                saveResult = true;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            GlobalVariables.getMessageMap().putError(PAYMENT_VIEW, RiceKeyConstants.ERROR_CUSTOM, e.getMessage());

        }

        return saveResult;
    }

    private void initPayment(PaymentForm form) {

        form.setPaymentTransactionTypeId("");
        form.setTransactionType(null);
        form.setTransactionTypeMessage("");


        // abbreviated payment initialization
        Account account = form.getAccount();
        String accountId = account.getId();

        form.setPayment(null);
        Payment payment = new Payment();
        payment.setAccount(account);
        payment.setAccountId(accountId);
        payment.setEffectiveDate(new Date());
        form.setPayment(new TransactionModel(payment));

        form.setCurrencyOptionsFinder(this.getCurrencyOptionsFinder());
        form.setRollupOptionsFinder(this.getRollupOptionsFinder());

        BigDecimal startingBalance = accountService.getBalance(form.getAccount().getId(), new Date());
        form.setEstimatedCurrentBalance(startingBalance);


    }
}
