package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.krad.form.TransactionForm;
import com.sigmasys.kuali.ksa.krad.model.AllocationModel;
import com.sigmasys.kuali.ksa.krad.model.InformationModel;
import com.sigmasys.kuali.ksa.krad.model.TransactionModel;
import com.sigmasys.kuali.ksa.krad.util.AccountUtils;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.InformationService;
import com.sigmasys.kuali.ksa.service.PaymentService;
import com.sigmasys.kuali.ksa.service.RefundService;
import com.sigmasys.kuali.ksa.util.TransactionUtils;
import org.apache.commons.lang.time.DateUtils;
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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/transactionView")
public class TransactionController extends GenericSearchController {

    private static final String TRANSACTION_VIEW = "TransactionView";
    private static final String ALLOCATE_TRANSACTION_PAGE = "AllocateTransactionPage";
    private static final String FILTER_TAG_FIELD = "transactionPageFilterTags";

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private InformationService informationService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private ConfigService configService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected TransactionForm createInitialForm(HttpServletRequest request) {
        TransactionForm form = new TransactionForm();

        String start = configService.getParameter(Constants.KSA_TRANSACTION_DEFAULT_START_DATE);
        String end = configService.getParameter(Constants.KSA_TRANSACTION_DEFAULT_END_DATE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            form.setStartingDate(sdf.parse(start));
            form.setEndingDate(sdf.parse(end));
        } catch (Exception e) {
            //log it but no big deal, the code will still work
            logger.error("Error parsing configuration parameters for transaction start and end dates.", e);
        }

        return form;
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=save")
    public ModelAndView save(@ModelAttribute("KualiForm") TransactionForm form) {
        auditableEntityService.persistAuditableEntity(form.getCurrency());
        return getUIFModelAndView(form);
    }


    /**
     * @param form    TransactionForm
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView get(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {

        Timestamp start = new Timestamp((new Date()).getTime());

        // just for the transactions by person page
        String pageId = request.getParameter("pageId");
        if (pageId == null) {
            pageId = "ViewTransactions";
        }

        String userId = request.getParameter("userId");

        if (userId == null) {
            // Error out here
            form.setStatusMessage("No userId passed to method");
            return getUIFModelAndView(form);
        }

        form.setAccount(accountService.getFullAccount(userId));

        if ("ViewTransactions".equals(pageId) || "RollUpTransactions".equals(pageId) || "RunningBalanceTransactions".equals(pageId)) {

            populateForm(form);


        } else if ("ViewAlerts".equals(pageId)) {
            form.setAlertObjects(informationService.getAlerts(userId));
        } else if ("ViewFlags".equals(pageId)) {
            form.setFlagObjects(informationService.getFlags(userId));
        } else if ("ViewMemos".equals(pageId)) {
            //form.setMemos(informationService.getMemos(userId));
        } else if ("ViewHolds".equals(pageId)) {
            form.setHolds(AccountUtils.getHolds(userId));
        }

        logger.info("TJB: Transaction Page Start: " + start + " End: " + new Timestamp((new Date()).getTime()));

        return getUIFModelAndView(form);
    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=filterTags")
    public ModelAndView filterTags(@ModelAttribute("KualiForm") TransactionForm form) {
        String newTag = form.getNewTag();

        Tag tag = auditableEntityService.getAuditableEntity(newTag, Tag.class);

        if (tag == null) {
            List<Tag> searchTags = auditableEntityService.getAuditableEntitiesByNamePattern(newTag, Tag.class);

            if (searchTags.size() == 0) {
                String errorMessage = "'" + newTag + "' is not a valid tag";
                GlobalVariables.getMessageMap().putError(FILTER_TAG_FIELD, RiceKeyConstants.ERROR_CUSTOM, errorMessage);
                return getUIFModelAndView(form);
            } else if (searchTags.size() > 1) {
                String errorMessage = "'" + newTag + "' matches multiple tags, please select one tag at a time to add";
                GlobalVariables.getMessageMap().putError(FILTER_TAG_FIELD, RiceKeyConstants.ERROR_CUSTOM, errorMessage);
                return getUIFModelAndView(form);
            }

            // There can be only one
            tag = searchTags.get(0);
        }

        List<Tag> tags = form.getFilterTags();
        if (tags == null) {
            tags = new ArrayList<Tag>();
            form.setFilterTags(tags);
        }
        if (tag != null) {
            tags.add(tag);
        }

        form.setNewTag("");
        populateForm(form);
        return getUIFModelAndView(form);
    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeTag")
    public ModelAndView removeTag(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {

        String tagString = request.getParameter("actionParameters[tagId]");
        Long tagId = Long.parseLong(tagString);
        if (tagId != null) {
            Tag tag = auditableEntityService.getAuditableEntity(tagId, Tag.class);

            List<Tag> tags = form.getFilterTags();
            if (tags == null) {
                tags = new ArrayList<Tag>();
                form.setFilterTags(tags);
            }
            if (tag != null) {

                this.removeTag(tags, tag);
                form.setFilterTags(tags);
            }

            form.setNewTag("");
        }
        populateForm(form);
        return getUIFModelAndView(form);
    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeTransactionTag")
    public ModelAndView removeTransactionTag(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {
        String tagString = request.getParameter("actionParameters[tagId]");
        String transactionIdString = request.getParameter("actionParameters[currentTagParent]");


        Long tagId = Long.parseLong(tagString);
        Long transactionId;
        try {
            transactionId = Long.parseLong(transactionIdString);
        } catch (NumberFormatException e) {
            String errorMessage = "Unable to find a transaction with the id '" + transactionIdString + "'";
            GlobalVariables.getMessageMap().putError(TRANSACTION_VIEW, RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }

        if (tagId != null && transactionId != null) {
            transactionService.removeTagsFromTransaction(transactionId, tagId);
            String message = "Tag removed from transaction";
            GlobalVariables.getMessageMap().putInfo(TRANSACTION_VIEW, RiceKeyConstants.ERROR_CUSTOM, message);

        }
        populateForm(form);
        return getUIFModelAndView(form);
    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addTransactionTag")
    public ModelAndView addTransactionTag(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {
        String transactionIdString = request.getParameter("actionParameters[currentParent]");

        Long transactionId;
        try {
            transactionId = Long.parseLong(transactionIdString);
        } catch (NumberFormatException e) {
            logger.error("Invalid transaction id passed to addTransactionTag: '" + transactionIdString + "'");
            return getUIFModelAndView(form);
        }

        // Need to loop through the form's 'allTransactions' to figure out which one we're talking about.
        for (TransactionModel tm : form.getAllTransactions()) {
            if (tm.getId().equals(transactionId)) {
                String tagString = tm.getNewTag();

                Tag tag = auditableEntityService.getAuditableEntity(tagString, Tag.class);
                List<Tag> tags = new ArrayList<Tag>();
                if (tag != null) {
                    tags.add(tag);
                    transactionService.addTagsToTransaction(transactionId, tags);
                }
                break;
            }
        }

        populateForm(form);
        return getUIFModelAndView(form);
    }

    private void populateForm(TransactionForm form) {

        Account act = form.getAccount();
        String userId = "";
        if (act != null) {
            userId = act.getId();
        } else {
            form.setStatusMessage("No account in form");
            return;
        }

        AccountUtils.populateTransactionHeading(form, userId);

        form.setZeroBalanceDates(new HashSet<Date>());

        Boolean showInternal = form.getShowInternal();
        Date startDate = form.getStartingDate();
        Date endDate = form.getEndingDate();
        String zeroBalanceDate = form.getZeroBalanceDate();

        Date actualStartDate = startDate;
        Date actualEndDate = endDate;

        if(zeroBalanceDate != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date zeroDate = null;
            try {
                if(! "1900-01-01".equals(zeroBalanceDate)) {
                    zeroDate = df.parse(zeroBalanceDate);
                }
            } catch(ParseException e) {
                logger.error("Error parsing zero balance date of '" + zeroBalanceDate + "'");
            }
            actualStartDate = zeroDate;
            form.setStartingDate(actualStartDate);
            actualEndDate = null;
        }

        form.setStartingBalance(accountService.getBalance(userId, startDate));

        //if(form.getStartingBalance().compareTo(BigDecimal.ZERO) <= 0) {
        //    form.getZeroBalanceDates().add(startDate);
        //}

        form.setChargeTotal(BigDecimal.ZERO);
        form.setPaymentTotal(BigDecimal.ZERO);
        form.setDefermentTotal(BigDecimal.ZERO);
        form.setAllocatedTotal(BigDecimal.ZERO);
        form.setUnallocatedTotal(BigDecimal.ZERO);

        // All transactions
        List<Transaction> transactions = transactionService.getTransactions(userId, actualStartDate, actualEndDate);

        List<Tag> tags = form.getFilterTags();

        if (tags != null && tags.size() > 0) {
            transactions = TransactionUtils.filterByTags(transactions, tags);
        }

        transactions = TransactionUtils.orderByEffectiveDate(transactions, true);

        List<TransactionModel> models = new ArrayList<TransactionModel>(transactions.size());
        for (Transaction t : transactions) {
            if (!showInternal && t.isInternal()) {
                continue;
            }

            Date effectiveDate = t.getEffectiveDate();
            if (actualStartDate == null || (effectiveDate.before(actualStartDate))) {
                actualStartDate = effectiveDate;
            }
            if (actualEndDate == null || (effectiveDate.after(actualEndDate))) {
                actualEndDate = effectiveDate;
            }

            TransactionModel m = new TransactionModel(t);

            // Set the list of allocations
            m.setAllocations(transactionService.getAllocations(t.getId()));

            m.setRefundAmount(t.getUnallocatedAmount());

            // Add the memos
            m.setMemos(informationService.getMemos(t.getId()));
            models.add(m);

            // Add appropriate Alerts
            for (InformationModel im : form.getAlerts()) {
                Alert a = (Alert) im.getParentEntity();
                Transaction alertTransaction = a.getTransaction();
                if (alertTransaction != null && alertTransaction.getId().equals(t.getId())) {
                    m.addAlert(a);
                }
            }

            // Add appropriate flags
            for (InformationModel im : form.getFlags()) {
                Flag f = (Flag) im.getParentEntity();
                Transaction flagTransaction = f.getTransaction();
                if (flagTransaction != null && flagTransaction.getId().equals(t.getId())) {
                    m.addFlag(f);
                }
            }
        }

        if (startDate == null) {
            form.setStartingDate(actualStartDate);
        }

        if (endDate == null) {
            form.setEndingDate(actualEndDate);
        }

        populateRollups(form, models);
    }


    /**
     * perform Payment Application.
     *
     * @param form Kuali form instance
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=paymentApplication")
    public ModelAndView paymentApplication(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {

        String accountId = request.getParameter("actionParameters[userId]");

        if (accountId != null && !accountId.trim().isEmpty()) {
            paymentService.paymentApplication(accountId);

            GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Payments successfully applied");

        }

        populateForm(form);
        return getUIFModelAndView(form);
    }

    /**
     * Attempt a refund on a transaction
     *
     * @param form Kuali form instance
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=refund")
    public ModelAndView refund(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {

        Long transactionId;
        String errorMessage = "";

        try {

            transactionId = new Long(request.getParameter("transactionId"));

        } catch (NumberFormatException e) {
            // Error here
            errorMessage = "Invalid Payment";
            //GlobalVariables.getMessageMap().putError("refundLightbox", RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            form.setMessage(errorMessage);

            return getUIFModelAndView(form);
        }


        // Need to loop through the transactions and find the one that matches this transaction id to make sure
        // it is a payment and get the amount (if any)

        BigDecimal amount = null;
        TransactionModel model = null;

        for (TransactionModel t : form.getAllTransactions()) {
            if (t.getParentTransaction().getId().equals(transactionId)) {
                model = t;
                if (t.getTransactionTypeValue() == TransactionTypeValue.PAYMENT) {
                    amount = t.getRefundAmount();
                } else {
                    // error here, Refunds must occur on payments
                    errorMessage = "Refunds can only apply to payments";
                    //GlobalVariables.getMessageMap().putError("refundLightbox_line0", RiceKeyConstants.ERROR_CUSTOM, errorMessage);
                    t.setMessage(errorMessage);
                    return getUIFModelAndView(form);
                }
            }
        }

        if (model == null) {
            // The row isn't in the collection at all.  Something major happened.
            errorMessage = "Invalid Transaction ID";
            GlobalVariables.getMessageMap().putError("TransactionView", RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }

        try {

            refundService.checkForRefund(transactionId, amount);
            String success = "Refund saved";
            //GlobalVariables.getMessageMap().putInfo("refundLightbox", RiceKeyConstants.ERROR_CUSTOM, success);
            model.setMessage(success);

        } catch (RuntimeException e) {
            errorMessage = e.getMessage();
            //GlobalVariables.getMessageMap().putError("refundLightbox", RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            model.setMessage(errorMessage);
        }

        return getUIFModelAndView(form);
    }

    /**
     * perform Payment Application.
     *
     * @param form Kuali form instance
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeAllocation")
    public ModelAndView removeAllocation(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {

        String transactionIdString = request.getParameter("actionParameters[transactionId]");
        String allocationIdString = request.getParameter("actionParameters[allocationId]");
        String lockedString = request.getParameter("actionParameters[locked]");

        Boolean locked = Boolean.parseBoolean(lockedString);


        String errorMessage = "";
        Long transactionId;
        Long allocationId;

        try {
            transactionId = Long.parseLong(transactionIdString);
            allocationId = Long.parseLong(allocationIdString);
        } catch (NumberFormatException e) {
            errorMessage = "Invalid ID passed to removeAllocation";
            GlobalVariables.getMessageMap().putError("transactionView", RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            return getUIFModelAndView(form);
        }

        if (locked) {
            // Save the memo attached to this.
            if (this.saveAllocationMemos(form, transactionId, allocationId)) {
                transactionService.removeLockedAllocation(transactionId, allocationId);
            } else {
                errorMessage = "Memo is required for locked allocations";
                GlobalVariables.getMessageMap().putError("TransactionView", RiceKeyConstants.ERROR_CUSTOM, errorMessage);
            }
        } else {
            transactionService.removeAllocation(transactionId, allocationId);
        }

        Properties props = new Properties();
        String refreshLocation = request.getParameter("refresh");
        if (refreshLocation == null) {
            refreshLocation = "transactionView";
        }

        if (refreshLocation.equals("quickView")) {
            props.put("viewId", "QuickView");

        } else {
            props.put("pageId", "ViewTransactions");
            props.put("viewId", "TransactionView");
        }

        props.put("methodToCall", "get");
        props.put("userId", form.getAccount().getId());

        return performRedirect(form, refreshLocation, props);
    }

    /**
     * Load the information needed for the allocation form
     *
     * @param form Kuali form instance
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=allocate")
    public ModelAndView allocate(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {

        String transactionIdString = request.getParameter("actionParameters[transactionId]");
        Long transactionId = Long.parseLong(transactionIdString);

        String userId = form.getAccount().getId();

        Transaction t = transactionService.getTransaction(transactionId);

        if (!t.getAccount().getId().equals(form.getAccount().getId())) {
            // This should never happen unless someone is messing with the URL and trying to see things
            throw new IllegalStateException("Transaction ID and User ID do not match: " + t.getAccount().getId() + " " + userId);
        }

        TransactionModel tm = new TransactionModel(t);
        form.setCurrentTransaction(tm);

        List<TransactionModel> allocations = this.getPossibleAllocations(userId, t);
        logger.info("Potential allocations size: " + allocations.size());
        form.setCurrrentTransactionAllocations(allocations);

        form.setPageId(ALLOCATE_TRANSACTION_PAGE);

        return getUIFModelAndView(form);
    }

    /**
     * Save the information from the allocation form
     *
     * @param form Kuali form instance
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=allocateTransaction")
    public ModelAndView allocateTransaction(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {

        TransactionModel currentTransaction = form.getCurrentTransaction();
        Transaction parent = currentTransaction.getParentTransaction();

        if (parent == null) {
            String statusMsg = "Unknown transaction.  Unable to process allocations.";
            GlobalVariables.getMessageMap().putError("", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg);

            return getUIFModelAndView(form);
        }

        BigDecimal total = BigDecimal.ZERO;

        List<TransactionModel> allocations = form.getCurrrentTransactionAllocations();
        for (TransactionModel model : allocations) {
            BigDecimal amount = model.getNewAllocation();

            if (amount != null) {
                if (amount.compareTo(model.getUnallocatedAmount()) > 0) {
                    String statusMsg = "Allocation amount of " + amount + " cannot be greater than the unallocated amount of " + model.getUnallocatedAmount() + " for " + model.getParentTransaction().getTransactionType().getDescription();
                    GlobalVariables.getMessageMap().putError(TRANSACTION_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
                }
                total = total.add(amount);
            }
        }

        // Total of allocations can't be greater than the payment amount
        if (total.compareTo(parent.getAmount()) > 0) {
            String statusMsg = "Allocations cannot add up to more than the payment amount";
            GlobalVariables.getMessageMap().putError(TRANSACTION_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg);

            return getUIFModelAndView(form);
        }

        if (!GlobalVariables.getMessageMap().hasErrors()) {
            for (TransactionModel model : allocations) {
                BigDecimal amount = model.getNewAllocation();
                if (amount != null && (amount.compareTo(BigDecimal.ZERO) > 0)) {
                    try {
                        transactionService.createLockedAllocation(parent.getId(), model.getParentTransaction().getId(), amount);
                    } catch (IllegalStateException e) {
                        logger.error("Total amount of " + total.toString() + " is greater than the payment amount of " + parent.getAmount() + ".  IllegalStateException: " + e.getMessage());
                        GlobalVariables.getMessageMap().putError(TRANSACTION_VIEW, RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
                    }
                }
            }

            String userId = form.getAccount().getId();

            List<TransactionModel> models = getPossibleAllocations(userId, parent);
            form.setCurrrentTransactionAllocations(models);

            Transaction t = transactionService.getTransaction(parent.getId());
            form.setCurrentTransaction(new TransactionModel(t));

            String statusMsg = "Transaction successfully allocated";
            GlobalVariables.getMessageMap().putInfo(TRANSACTION_VIEW, RiceKeyConstants.ERROR_CUSTOM, statusMsg);
        }

        return getUIFModelAndView(form);
    }

    /**
     * Load the information needed for the allocation form
     *
     * @param form Kuali form instance
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=reverseTransaction")
    public ModelAndView reverseTransaction(@ModelAttribute("KualiForm") TransactionForm form, HttpServletRequest request) {

        String transactionIdString = request.getParameter("actionParameters[transactionId]");
        String source = request.getParameter("actionParameters[source]");
        Long transactionId = null;

        try {
            transactionId = Long.parseLong(transactionIdString);
        } catch(NumberFormatException e) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Unable to find transaction '" + transactionIdString + "'");
            return getUIFModelAndView(form);
        }

        String userId = form.getAccount().getId();

        Transaction t = transactionService.getTransaction(transactionId);

        if (!t.getAccount().getId().equals(form.getAccount().getId())) {
            // This should never happen unless someone is messing with the URL and trying to see things
            throw new IllegalStateException("Transaction ID and User ID do not match: " + t.getAccount().getId() + " " + userId);
        }

        // Need to find the actual transaction object they were working on to get the reason code.
        TransactionModel modelToReverse = null;
        if("rollup".equals(source)) {
            for(TransactionModel model : form.getRollupTransactions()) {
                for(TransactionModel subModel : model.getSubTransactions()) {
                    if(transactionId.equals(subModel.getId())) {
                        modelToReverse = subModel;
                        break;
                    }
                }
                if(modelToReverse != null) {
                    break;
                }
            }
        } else {
            for(TransactionModel model : form.getAllTransactions()) {
                if(transactionId.equals(model.getId())) {
                    modelToReverse = model;
                    break;
                }
            }
        }

        if(modelToReverse == null) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Unable to find transaction '" + transactionIdString + "' in " + source + " list");
            return getUIFModelAndView(form);
        }

        String reason = modelToReverse.getReverseTransactionReason();
        if(reason == null || "".equals(reason)) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Reverse reason is required");
            return getUIFModelAndView(form);
        }

        try {
            Transaction reversedTransaction = transactionService.reverseTransaction(modelToReverse.getId(), modelToReverse.getReverseTransactionReason(), modelToReverse.getUnallocatedAmount());
            boolean internal = modelToReverse.getReverseTransactionInternalOnly();
            if(internal) {
                t.setInternal(true);
                transactionService.persistTransaction(t);

                reversedTransaction.setInternal(true);
                transactionService.persistTransaction(reversedTransaction);
            }
            GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Transaction reversed");
        } catch(IllegalArgumentException e) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
            return getUIFModelAndView(form);
        }

        populateForm(form);
        return getUIFModelAndView(form);
    }


    private boolean saveAllocationMemos(TransactionForm form, Long transactionId, Long allocationId) {

        List<TransactionModel> allTransactions = form.getAllTransactions();

        for (TransactionModel model : allTransactions) {
            if (transactionId.equals(model.getId()) || allocationId.equals(model.getId())) {
                List<AllocationModel> allocations = model.getAllocations();
                for (AllocationModel allocation : allocations) {
                    Memo memo = allocation.getMemoModel();
                    if (memo != null && memo.getText() != null) {
                        return informationService.createMemo(transactionId, memo.getText(), memo.getEffectiveDate(), memo.getExpirationDate(), null) != null;
                    }
                }

            }
        }

        return false;
    }

    private void populateRollups(TransactionForm form, List<TransactionModel> transactions) {

        List<TransactionModel> rollUpTransactionModelList = new ArrayList<TransactionModel>();
        List<TransactionModel> unGroupedTransactionModelList = new ArrayList<TransactionModel>();
        List<TransactionModel> defermentModelList = new ArrayList<TransactionModel>();

        TransactionModel nonRolledUp = null;

        BigDecimal balance = form.getStartingBalance();

        // Assuming that transactions are already passed into this method sorted in the proper way for the running balance.
        for (TransactionModel t : transactions) {

            Transaction parentTransaction = t.getParentTransaction();

            BigDecimal amount = parentTransaction.getAmount();

            BigDecimal allocated = parentTransaction.getAllocatedAmount();
            if (allocated == null) {
                allocated = BigDecimal.ZERO;
            }

            BigDecimal unallocated = parentTransaction.getUnallocatedAmount();

            if (parentTransaction instanceof Deferment) {
                defermentModelList.add(t);
                form.addDefermentTotal(amount);
                continue;
            }

            BigDecimal locked = parentTransaction.getLockedAllocatedAmount();
            if (locked == null) {
                locked = BigDecimal.ZERO;
            }

            if(amount.compareTo(BigDecimal.ZERO) < 0) {
                locked = locked.multiply(new BigDecimal(-1));
                unallocated = unallocated.multiply(new BigDecimal(-1));
            }

            // Check the balance before this transaction.  If it is zero then add it to the list
            if(balance.compareTo(BigDecimal.ZERO) <= 0) {
                Date dt = DateUtils.addDays(t.getEffectiveDate(), -1);
                form.getZeroBalanceDates().add(dt);
            }


            if (t.getParentTransaction() instanceof Charge) {
                form.addChargeTotal(amount);
                balance = balance.add(amount);
                form.addAllocatedTotal(allocated);
                form.addAllocatedTotal(locked);
                form.addUnallocatedTotal(unallocated);
            } else {

                form.addPaymentTotal(amount);
                form.subtractAllocatedTotal(allocated);
                form.subtractAllocatedTotal(locked);
                form.subtractUnallocatedTotal(unallocated);

                balance = balance.subtract(allocated).subtract(locked);

            }
            t.setRunningBalance(balance);
            unGroupedTransactionModelList.add(t);

            Rollup tmRollup = t.getRollup();
            if (tmRollup != null) {
                // Check if this rollup is already in there.
                boolean found = false;
                for (TransactionModel m : rollUpTransactionModelList) {
                    Rollup r = m.getRollup();
                    if (r.getId().equals(tmRollup.getId())) {
                        m.addSubTransaction(t);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    TransactionModel tm = new TransactionModel();
                    tm.setRollup(tmRollup);

                    rollUpTransactionModelList.add(tm);
                    tm.addSubTransaction(t);
                }
            } else {
                if (nonRolledUp == null) {
                    Rollup r = new Rollup();
                    r.setName("Other Transactions not in a Roll-up");

                    nonRolledUp = new TransactionModel();
                    nonRolledUp.setRollup(r);
                }
                nonRolledUp.addSubTransaction(t);
            }

        }

        form.setEndingBalance(balance);

        if (nonRolledUp != null) {
            rollUpTransactionModelList.add(nonRolledUp);
        }

        form.setRollupTransactions(rollUpTransactionModelList);
        form.setAllTransactions(unGroupedTransactionModelList);
        form.setDeferments(defermentModelList);
    }

    private void removeTag(List<Tag> tags, Tag tagToRemove) {
        for (Tag tag : new HashSet<Tag>(tags)) {
            if (tag.getCode().equals(tagToRemove.getCode())) {
                tags.remove(tag);
                break;
            }
        }
    }

    private List<TransactionModel> getPossibleAllocations(String userId, Transaction transaction) {

        List<Transaction> transactions = transactionService.getTransactions(userId);

        List<TransactionModel> models = new LinkedList<TransactionModel>();

        for (Transaction t : transactions) {
            if (transactionService.canPay(transaction, t) && (t.getUnallocatedAmount().compareTo(BigDecimal.ZERO) > 0)) {
                TransactionModel m = new TransactionModel(t);
                models.add(m);
            }
        }

        return models;
    }



}
