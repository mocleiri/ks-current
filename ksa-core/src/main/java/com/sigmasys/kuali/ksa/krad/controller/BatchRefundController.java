package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.RefundForm;
import com.sigmasys.kuali.ksa.krad.model.PotentialRefundModel;
import com.sigmasys.kuali.ksa.krad.model.RefundModel;
import com.sigmasys.kuali.ksa.krad.model.RequestPotentialRefundSummaryModel;
import com.sigmasys.kuali.ksa.krad.util.DateRangeFilterKeyValuesFinder;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.PaymentService;
import com.sigmasys.kuali.ksa.service.RefundService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static java.lang.String.format;

/**
 * This controller serves requests from the "Batch Refunds" view.
 */
@Controller
@RequestMapping(value = "/batchRefundView")
public class BatchRefundController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(BatchRefundController.class);

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private PaymentService paymentService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected RefundForm createInitialForm(HttpServletRequest request) {
        RefundForm form = new RefundForm();

        // Set the defaults:
        Calendar calendar = Calendar.getInstance();

        form.setDateRangeType(DateRangeFilterKeyValuesFinder.ALL);
        form.setFilterDateTo(calendar.getTime());
        calendar.add(Calendar.YEAR, -1);
        form.setFilterDateFrom(calendar.getTime());
        form.setPotentialRefunds(new ArrayList<PotentialRefundModel>());
        form.setAllRefunds(new ArrayList<RefundModel>());
        form.setAccounts(new ArrayList<Account>());
        form.setBatch(true);

        return form;
    }

    /**
     * Account filtering support callback.
     * @param form The form object.
     * @return ModelAndView.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=filterAccounts")
    public ModelAndView filterAccounts(@ModelAttribute("KualiForm") RefundForm form) {

        String newFilterAccountId = form.getNewAccount();
        Account newFilterAccount = accountService.getFullAccount(newFilterAccountId);
        List<Account> filterAccounts = form.getAccounts();

        if (filterAccounts == null) {
            filterAccounts = new ArrayList<Account>();
            form.setAccounts(filterAccounts);
        }

        if ((newFilterAccount != null) && !filterAccounts.contains(newFilterAccount)) {
            filterAccounts.add(newFilterAccount);
        }

        // Refresh the model:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Remove" icon is clicked next to a filter Account.
     *
     * @param form      The form object.
     * @param accountId ID of the account to be removed from the filter.
     * @return ModelAndView
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeFilterAccount")
    public ModelAndView removeFilterAccount(@ModelAttribute("KualiForm") RefundForm form,
                                            @RequestParam("accountId") String accountId) throws Exception {

        // Find the filter Account with the specified ID and remove it from filtering:
        List<Account> filterAccounts = form.getAccounts();

        if (filterAccounts != null) {

            for (Account filterAccount : filterAccounts) {
                if (StringUtils.equals(filterAccount.getId(), accountId)) {
                    filterAccounts.remove(filterAccount);

                    break;
                }
            }
        }

        // Refresh the model:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }

    /**
     * Tag filtering supprot callback.
     * @param form The form object.
     * @return ModelAndView.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=filterTags")
    public ModelAndView filterTags(@ModelAttribute("KualiForm") RefundForm form) throws Exception {

        String newFilterTagCode = form.getNewTag();
        Tag newFilterTag = auditableEntityService.getAuditableEntity(newFilterTagCode, Tag.class);
        List<Tag> filterTags = form.getFilterTags();

        if (filterTags == null) {
            filterTags = new ArrayList<Tag>();
            form.setFilterTags(filterTags);
        }

        if (newFilterTag != null) {
            filterTags.add(newFilterTag);
        }

        // Refresh the model:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Remove" icon is clicked next to a filter Tag.
     *
     * @param form  The form object.
     * @param tagId ID of the account to be removed from the filter.
     * @return ModelAndView
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeFilterTag")
    public ModelAndView removeFilterTag(@ModelAttribute("KualiForm") RefundForm form,
                                            @RequestParam("tagId") Long tagId) throws Exception {

        // Find the filter Tag with the specified ID and remove it from filtering:
        List<Tag> filterTags = form.getFilterTags();

        if ((filterTags != null) && (tagId != null)) {

            for (Tag filterTag : filterTags) {
                if (filterTag.getId().compareTo(tagId) == 0) {
                    filterTags.remove(filterTag);

                    break;
                }
            }
        }

        // Refresh the model:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }

    /**
     * Called when the form is initially opened.
     *
     * @param form The form object associated with the Account Refund page.
     * @return ModelAndView for the page.
     * @throws Exception If any errors occur.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=displayInitialPage")
    public ModelAndView displayInitialPage(@ModelAttribute("KualiForm") RefundForm form) throws Exception {
        // Find all refunds:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Search" button is pressed. Performs Refund search and populates
     * the form object to be displayed on the screen.
     *
     * @param form The form object associated with the Account Refund page.
     * @return ModelAndView for the page.
     * @throws Exception If there are any errors search for Refunds.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=searchForRefunds")
    public ModelAndView searchForRefunds(@ModelAttribute("KualiForm") RefundForm form) throws Exception {
        // Refresh the view with the updated filtering criteria:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }

    /**
     * This method is called when the "Request Refund" button on the "Potential Refunds" tab is pressed.
     *
     * @param form   The form object associated with the Account Refund page.
     * @return ModelAndView.
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=requestRefund")
    public ModelAndView requestRefund(@ModelAttribute("KualiForm") RefundForm form) throws Exception {

        // Run the Payment Application for each Account ID, if selected:
        if (form.isRunPaymentApplication()) {
            List<Account> filterAccounts = form.getAccounts();

            if (filterAccounts != null) {
                for (Account account : form.getAccounts()) {
                    paymentService.paymentApplication(account.getId());
                }
            }
        }

        // Process Refund request for all selected Potential Refunds:
        processRequestPotentialRefund(form);

        // Refresh the model:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Verify Selected Refunds" button is pressed.
     *
     * @param form The form object.
     * @return The ModelAndView for the page.
     * @throws Exception If there are any errors.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=verifySelectedRefunds")
    public ModelAndView verifySelectedRefunds(@ModelAttribute("KualiForm") RefundForm form) throws Exception {

        // Get the selected Refunds:
        List<RefundModel> selectedRefunds = getSelectedRefunds(form);

        for (RefundModel refundModel : selectedRefunds) {
            // Verify each refund:
            try {
                refundService.validateRefund(refundModel.getRefund().getId());
            } catch (Exception e) {
                logger.error("Error validating a Refund with ID " + refundModel.getRefund().getId(), e);
            }
        }

        // Refresh the model:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Cancel Selected Refunds" button is pressed.
     *
     * @param form The form object.
     * @return The ModelAndView for the page.
     * @throws Exception If there are any errors.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=cancelSelectedRefunds")
    public ModelAndView cancelSelectedRefunds(@ModelAttribute("KualiForm") RefundForm form) throws Exception {

        // Get the selected Refunds:
        List<RefundModel> selectedRefunds = getSelectedRefunds(form);

        for (RefundModel refundModel : selectedRefunds) {
            // Cancel each refund:
            try {
                refundService.cancelRefund(refundModel.getRefund().getId(), "User cancellation");
            } catch (Exception e) {
                logger.error("Error canceling a Refund with ID " + refundModel.getRefund().getId(), e);
            }
        }

        // Refresh the model:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=verifySingleRefund")
    public ModelAndView verifySingleRefund(@ModelAttribute("KualiForm") RefundForm form,
                                           @RequestParam("verificationRefundId") Long refundId) throws Exception {


        // Go through all Refunds and find one with ID refundId:
        List<RefundModel> allRefunds = form.getAllRefunds();

        for (RefundModel refund : allRefunds) {

            if(refund.getId().compareTo(refundId) == 0) {

                // Validate the refund and adjust the status:
                adjustRefundStatus(refund);
            }
        }

        // Refresh the model:
        findAllRefunds(form);

        return getUIFModelAndView(form);
    }


    /* ********************************************************************
   *
   * Helper methods.
   *
   * ********************************************************************/


    /**
     * Searches for all Refunds and compiles a list of PotentialRefundModel and Refund objects.
     * Sets the lists of Potential and all Refunds on the given form object.
     *
     * @param form The form object.
     */
    private void findAllRefunds(RefundForm form) {

        Date filterDateFrom = null;
        Date filterDateTo = null;

        if (StringUtils.equals(form.getDateRangeType(), DateRangeFilterKeyValuesFinder.RANGE)) {
            filterDateFrom = form.getFilterDateFrom();
            filterDateTo = form.getFilterDateTo();
        }

        // Create model objects to display in the tables:
        List<PotentialRefundModel> potentialRefundModels = createPotentialRefundList(form, filterDateFrom, filterDateTo);
        List<RefundModel> refundModels = createRefundModelList(form, filterDateFrom, filterDateTo);

        form.setAllRefunds(refundModels);
        form.setPotentialRefunds(potentialRefundModels);
    }

    /**
     * Creates a list of RefundModel objects to be displayed in the "Refund Status" table.
     *
     * @param form      The form object.
     * @param dateFrom  Filtering date from.
     * @param dateTo    Filtering date to.
     * @return list of RefundModel instances
     */
    private List<RefundModel> createRefundModelList(RefundForm form, Date dateFrom, Date dateTo) {

        // Get all Refunds for all Accounts:
        Set<String> filterAccountIds = getFilterAccountIds(form);
        List<RefundModel> refundModels = new ArrayList<RefundModel>();

        if ((filterAccountIds != null) && !filterAccountIds.isEmpty()) {

            // Get Refunds for the selected Account IDs:
            List<Refund> refunds = refundService.getAccountRefunds(filterAccountIds, dateFrom, dateTo);

            if (refunds != null) {

                String filterRefundStatusCode = form.getFilterRefundStatusCode();
                boolean allRefundsMatch = StringUtils.isBlank(filterRefundStatusCode);

                for (Refund refund : refunds) {

                    if (allRefundsMatch || filterRefundStatusCode.equals(refund.getStatus().getId())) {

                        // Create a new RefundModel object:
                        RefundModel refundModel = createRefundModel(refund);

                        refundModels.add(refundModel);
                    }
                }
            }
        }

        return refundModels;
    }

    /**
     * Creates a RefundModel from a Refund object.
     *
     * @param refund A Refund to create a RefundModel out of.
     * @return RefundModel that uses the given Refund.
     */
    private RefundModel createRefundModel(Refund refund) {
        // Create a new RefundModel object:
        RefundModel refundModel = new RefundModel(refund.getTransaction());
        RefundManifest manifest = refund.getRefundManifest();

        refundModel.setRefund(refund);
        refundModel.setRefundStatusDisplay(refund.getStatus().toString());

        // Set Refund Verification object:
        RefundModel.RefundVerification refundVerification = new RefundModel.RefundVerification();

        refundVerification.setAmount(refund.getAmount());
        refundModel.setRefundVerification(refundVerification);

        // Set requesting and authorizing person names:
        if (refund.getAuthorizedBy() != null) {
            refundModel.setAuthorizedByName(refund.getAuthorizedBy().getDefaultPersonName().getDisplayValue());
        }

        if (refund.getRequestedBy() != null) {
            refundModel.setRequestedByName(refund.getRequestedBy().getDefaultPersonName().getDisplayValue());
        }

        // Set the Refund Manifest to a new empty object if one does not exist:
        refundModel.setRefundManifest((manifest != null) ? manifest : new RefundManifest());

        // Add Refund Group:
        List<RefundModel> refundGroup = createRefundGroupModelList(refund);

        refundModel.setRefundGroup(refundGroup);

        return refundModel;
    }

    /**
     * Creates a list of related Refunds in the same group as the given one.
     * The resulting list is of RefundModel to be able to display on screen.
     *
     * @param refund A refund to find other refunds in the same group.
     * @return A list of RefundModel objects in the same group.
     */
    private List<RefundModel> createRefundGroupModelList(Refund refund) {

        List<RefundModel> groupRefundModels = new ArrayList<RefundModel>();

        // Check if the given Refund has a Group ID:
        if (StringUtils.isNotBlank(refund.getRefundGroup())) {

            // Get Refunds in the same group:
            List<Refund> refundGroup = refundService.getRefundGroup(refund.getRefundGroup());
            Long argRefundId = refund.getId();

            // Create RefundModels. Exclude the argument Refund:
            for (Refund groupRefund : refundGroup) {
                if (groupRefund.getId().compareTo(argRefundId) != 0) {
                    // Create a new RefundModel object:
                    RefundModel groupRefundModel = createRefundModel(groupRefund);

                    groupRefundModels.add(groupRefundModel);
                }
            }
        }

        return groupRefundModels;
    }

    /**
     * Creates a list of PotentialRefundModel objects to be displayed in the "Refund Status" table.
     *
     * @param dateFrom Filtering date from.
     * @param dateTo   Filtering date to.
     * @return list of potential refunds
     */
    private List<PotentialRefundModel> createPotentialRefundList(RefundForm form, Date dateFrom, Date dateTo) {

        // Get Payments for the current account within the specified date range.
        Set<String> filterAccountIds = getFilterAccountIds(form);
        List<PotentialRefundModel> potentialRefundModels = new ArrayList<PotentialRefundModel>();

        if ((filterAccountIds != null) && !filterAccountIds.isEmpty()) {

            Set<Long> filterTagIds = getFilterTagIds(form);
            List<Payment> payments = transactionService.getPotentialRefunds(filterAccountIds, dateFrom, dateTo, filterTagIds);

            for (Payment payment : payments) {
                potentialRefundModels.add(new PotentialRefundModel(payment));
            }
        }

        return potentialRefundModels;
    }

    /**
     * Returns a List of filter Account IDs of the given form object.
     * @param form The form object.
     * @return A List of filter Account IDs.
     */
    private Set<String> getFilterAccountIds(RefundForm form) {

        List<Account> filterAccounts = form.getAccounts();
        Set<String> filterAccountIds = null;

        if ((filterAccounts != null) && !filterAccounts.isEmpty()) {

            filterAccountIds = new HashSet<String>(filterAccounts.size());

            for (Account account : filterAccounts) {
                filterAccountIds.add(account.getId());
            }
        }

        return filterAccountIds;
    }

    /**
     * Returns a S of IDs of the currently selected Tags.
     *
     * @param form The form object.
     * @return a set  Tag
     */
    private Set<Long> getFilterTagIds(RefundForm form) {

        List<Tag> filterTags = form.getFilterTags();
        Set<Long> filterTagIds = null;

        if ((filterTags != null) && !filterTags.isEmpty()) {

            filterTagIds = new HashSet<Long>(filterTags.size());

            for (Tag filterTag : filterTags) {
                filterTagIds.add(filterTag.getId());
            }
        }

        return filterTagIds;
    }

    /**
     * Processes the "Request Refund" action for all selected Potential Refunds.
     *
     * @param form The form object.
     */
    private void processRequestPotentialRefund(RefundForm form) {

        // Figure out the eligibility for a Refund Request:
        List<PotentialRefundModel> selectedPotentialRefunds = getSelectedPotentialRefunds(form);
        List<PotentialRefundModel> eligibleForRefund = new ArrayList<PotentialRefundModel>();
        List<PotentialRefundModel> notEligibleForRefund = new ArrayList<PotentialRefundModel>();

        for (PotentialRefundModel potentialRefund : selectedPotentialRefunds) {

            Long paymentId = potentialRefund.getId();

            try {

                // Generate a Refund:
                Refund refund = refundService.checkForRefund(paymentId, potentialRefund.getRefundAmount());

                // If Refund generated without errors, add to eligible:
                if (refund != null) {
                    logger.info(format("Successfully generated a Refund for Payment with ID %d", paymentId));
                    eligibleForRefund.add(potentialRefund);
                    potentialRefund.setGeneratedRefund(refund);
                } else {
                    // If no Refund was generated, log an error and add to ineligible:
                    logger.error(format("Error generating refund for Payment ID %d", paymentId));
                    notEligibleForRefund.add(potentialRefund);
                }
            } catch (Exception e) {
                // Log an error and add to ineligible:
                logger.error(format("Error generating refund for Payment ID %d: " + e.getMessage(), paymentId), e);
                notEligibleForRefund.add(potentialRefund);
            }
        }

        // Set the eligible an ineligible Potential Refunds:
        RequestPotentialRefundSummaryModel summary = form.getRequestRefundSummary();

        summary.setEligiblePotentialRefunds(eligibleForRefund);
        summary.setNotEligiblePotentialRefunds(notEligibleForRefund);

        // Calculate the totals for the Request Refund summary:
        calculateRefundRequestSummaryTotals(summary);
    }

    /**
     * Returns only the selected potential refund list.
     *
     * @param form The form
     * @return Only the selected Potential Refunds.
     */
    List<PotentialRefundModel> getSelectedPotentialRefunds(RefundForm form) {

        List<PotentialRefundModel> allPotentialRefunds = form.getPotentialRefunds();
        List<PotentialRefundModel> selectedRefunds = new LinkedList<PotentialRefundModel>();

        if (allPotentialRefunds != null) {
            for (PotentialRefundModel refund : allPotentialRefunds) {
                if (refund.isSelected()) {
                    selectedRefunds.add(refund);
                }
            }
        }

        return selectedRefunds;
    }

    /**
     * Returns only the selected refunds from the "Refund Status" tab.
     *
     * @param form The form object.
     * @return Only the selected Refunds.
     */
    List<RefundModel> getSelectedRefunds(RefundForm form) {

        List<RefundModel> allRefunds = form.getAllRefunds();
        List<RefundModel> selectedRefunds = new ArrayList<RefundModel>();

        if (allRefunds != null) {
            for (RefundModel refund : allRefunds) {
                if (refund.isSelected()) {
                    selectedRefunds.add(refund);
                }
            }
        }

        return selectedRefunds;
    }

    /**
     * Calculates totals for the Refund Request summary.
     *
     * @param summary The summary object.
     */
    private void calculateRefundRequestSummaryTotals(RequestPotentialRefundSummaryModel summary) {

        // All totals:
        BigDecimal totalEligiblePayment = BigDecimal.ZERO;
        BigDecimal totalEligibleAllocated = BigDecimal.ZERO;
        BigDecimal totalEligibleUnallocated = BigDecimal.ZERO;
        BigDecimal totalEligibleRefundAmount = BigDecimal.ZERO;
        BigDecimal totalNotEligiblePayment = BigDecimal.ZERO;
        BigDecimal totalNotEligibleAllocated = BigDecimal.ZERO;
        BigDecimal totalNotEligibleUnallocated = BigDecimal.ZERO;
        BigDecimal totalNotEligibleRefundAmount = BigDecimal.ZERO;

        // Go through the list of Eligible Refunds:
        for (PotentialRefundModel refund : summary.getEligiblePotentialRefunds()) {
            // Adjust the totals:
            totalEligibleAllocated = safeAdd(totalEligibleAllocated, refund.getAllocatedAmount());
            totalEligiblePayment = safeAdd(totalEligiblePayment, refund.getPaymentAmount());
            totalEligibleRefundAmount = safeAdd(totalEligibleRefundAmount, refund.getRefundAmount());
            totalEligibleUnallocated = safeAdd(totalEligibleUnallocated, refund.getUnallocatedAmount());
        }

        // Set the Eligible totals:
        summary.setTotalEligibleAllocated(totalEligibleAllocated);
        summary.setTotalEligiblePayment(totalEligiblePayment);
        summary.setTotalEligibleUnallocated(totalEligibleUnallocated);
        summary.setTotalEligibleRefundAmount(totalEligibleRefundAmount);

        // Go through the list of Ineligible Refunds:
        for (PotentialRefundModel refund : summary.getNotEligiblePotentialRefunds()) {
            // Adjust the totals:
            totalNotEligibleAllocated = safeAdd(totalNotEligibleAllocated, refund.getAllocatedAmount());
            totalNotEligiblePayment = safeAdd(totalNotEligiblePayment, refund.getPaymentAmount());
            totalNotEligibleRefundAmount = safeAdd(totalNotEligibleRefundAmount, refund.getRefundAmount());
            totalNotEligibleUnallocated = safeAdd(totalNotEligibleUnallocated, refund.getUnallocatedAmount());
        }

        // Set the Not Eligible totals:
        summary.setTotalNotEligibleAllocated(totalNotEligibleAllocated);
        summary.setTotalNotEligiblePayment(totalNotEligiblePayment);
        summary.setTotalNotEligibleRefundAmount(totalNotEligibleRefundAmount);
        summary.setTotalNotEligibleUnallocated(totalNotEligibleUnallocated);
    }

    /**
     * Safely adds one BigDecimal to another. Ignores null.
     *
     * @param bd1 First operand
     * @param bd2 Second operand
     * @return Result of addition.
     */
    private static BigDecimal safeAdd(BigDecimal bd1, BigDecimal bd2) {
        if ((bd1 != null) && (bd2 != null)) {
            bd1 = bd1.add(bd2);
        }
        return bd1;
    }

    /**
     * Adjusts refund's status based on the RefundVerification stored in the RefundModel object.
     *
     * @param refundModel A model object.
     */
    private void adjustRefundStatus(RefundModel refundModel) {

        // Make sure the Refund Verification object exists:
        RefundModel.RefundVerification refundVerification = refundModel.getRefundVerification();

        if (refundVerification == null) {
            String error = "Refund cannot be verified. No associated Refund Verification object found.";

            logger.error(error);
            throw new IllegalArgumentException(error);
        }

        // If Refund is for Verification, validate the Amount first:
        if (RefundStatus.VERIFIED_CODE.equals(refundVerification.getStatusCode())) {
            // Check that the requested refund amount doesn't exceed the original refund amount:
            BigDecimal requestedAmount = refundVerification.getAmount();

            if (requestedAmount == null) {
                String error = "Refund amount must be indicated.";

                logger.error("Refund amount must be indicated.");
                throw new IllegalStateException(error);
            } else if (requestedAmount.compareTo(refundModel.getRefund().getAmount()) > 0) {
                String error = String.format("Refund amount specified exceed the allowed maximum of %d",
                        refundModel.getRefund().getAmount().doubleValue());

                logger.error(error);
                throw new IllegalStateException(error);
            }

            // Verify the refund:
            refundService.validateRefund(refundModel.getRefund().getId());
        } else if (RefundStatus.CANCELED_CODE.equals(refundVerification.getStatusCode())) {

            // Simply cancel the refund. Ignore the amount:
            refundService.cancelRefund(refundModel.getRefund().getId(), refundVerification.getOverrideDescription());
        }

    }


}
