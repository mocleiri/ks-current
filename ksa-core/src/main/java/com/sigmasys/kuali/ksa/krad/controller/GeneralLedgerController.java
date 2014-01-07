package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.ReportReconciliationForm;
import com.sigmasys.kuali.ksa.krad.model.BatchTransmissionDetailsModel;
import com.sigmasys.kuali.ksa.krad.model.BatchTransmissionModel;
import com.sigmasys.kuali.ksa.krad.model.GeneralLedgerAccountModel;
import com.sigmasys.kuali.ksa.krad.model.GeneralLedgerTransactionModel;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.util.ErrorUtils;
import com.sigmasys.kuali.ksa.util.TransactionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableDouble;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

/**
 * GeneralLedgerController.
 *
 * @author Sergey Godunov
 */
@Controller
@RequestMapping(value = "/generalLedger")
public class GeneralLedgerController extends ReportReconciliationController {

    private static final Log logger = LogFactory.getLog(GeneralLedgerController.class);

    @Autowired
    private AuditableEntityService auditableEntityService;


    /**
     * Displays the initial page.
     *
     * @param form GeneralLedger form.
     * @return ModelAndView for the initial page.
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, params = "methodToCall=displayGeneralLedger")
    public ModelAndView displayGeneralLedger(@ModelAttribute("KualiForm") ReportReconciliationForm form) {
        // At this time, we actively pre-generate the model for the view because KRAD is unable
        // to remember the tab selection or use asynchronous requests to load the data on-demand.
        searchForPriorBatches(form);
        searchForPendingTransactions(form);

        return getUIFModelAndView(form);
    }

    /**
     * Generates a list of Prior Batch Transactions for a GL Account and starts download.
     *
     * @param form    The form object.
     * @param batchId Batch ID.
     * @return null because we want to stay on the same page when download starts.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=downloadPriorBatchTransactions")
    public ModelAndView downloadPriorBatchTransactions(@ModelAttribute("KualiForm") ReportReconciliationForm form,
                                                       HttpServletResponse response, @RequestParam("batchId") String batchId) throws Exception {
        // Retrieve a list of Transactions for a Batch:
        String batchTransactionXML = null;
        String error = null;

        // Get the form from the ReportService:
        try {
            batchTransactionXML = transactionExportService.exportTransactionsForBatch(batchId);
        } catch (Exception e) {
            error = String.format("<h2>Error generating Prior Batch report. See log file for details.</h2><p><h4>%s</h4>",
                    StringUtils.defaultString(ErrorUtils.getMessage(e), "Contact your administrator for details."));
            logger.error(error, e);
        }

        // If the form content is available, start download:
        if (StringUtils.isNotEmpty(batchTransactionXML)) {
            // Start download:
            String reportFileName = generatePriorBatchReportFileName(batchId);

            doDownload(batchTransactionXML, reportFileName, "application/xml", response);
        } else {
            // Write an error message into the HTTP response:
            PrintWriter out = response.getWriter();
            out.println(error);
        }

        return null;
    }

    /**
     * Called to display the Batch Details page when a Batch ID is clicked in the Prior Batch table.
     *
     * @param form    The form object.
     * @param batchId ID of the batch to display its details.
     * @return ModelAndView.
     * @throws Exception
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=displayBatchDetails")
    public ModelAndView displayBatchDetails(@ModelAttribute("KualiForm") ReportReconciliationForm form,
                                            @RequestParam("batchId") String batchId) throws Exception {
        // Get the Batch details:
        searchForBatchDetails(form, batchId);

        return getUIFModelAndView(form);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Helper Methods.
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Searches for Prior Batches and populates the form object.
     *
     * @param form The form object.
     */
    private void searchForPriorBatches(ReportReconciliationForm form) {

        // Find all GL Transmissions in all statuses:
        List<GlTransmission> glTransmissions = generalLedgerService.getGlTransmissionsByStatuses(GlTransmissionStatus.TRANSMITTED);

        // Create a BatchTransmissionModel List:
        MutableDouble grandTotal = new MutableDouble(0);
        List<BatchTransmissionModel> batchTransmissions = createBatchTransmissionList(glTransmissions, grandTotal);

        // Set objects on the form:
        form.setPriorBatchTransmissions(batchTransmissions);
        form.setAllPriorBatchTotal(TransactionUtils.getFormattedAmount(new BigDecimal(grandTotal.doubleValue())));
    }

    /**
     * Searches for Pending transactions under GL Accounts and records them in the form.
     *
     * @param form The form object.
     */
    private void searchForPendingTransactions(ReportReconciliationForm form) {

        // Create a GeneralLedgerAccountModel List:
        MutableDouble grandTotal = new MutableDouble(0);
        List<GeneralLedgerAccountModel> glAccounts = createPendingTransactionGlAccountsList(grandTotal);

        // Make sure that we're not displaying negative numbers on the screen.
        // As per Paul,
        //  if Amount > 0 then credit
        //  if amount < 0 then debit of abs(#)
        for(GeneralLedgerAccountModel model : glAccounts) {
            if(model.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                model.setOperationType(GlOperationType.DEBIT);
                model.setTotalAmount(model.getTotalAmount().abs());
            } else {
                model.setOperationType(GlOperationType.CREDIT);
            }
        }


        // Set objects on the form:
        form.setGlAccountsPending(glAccounts);
        form.setAllGlAccountsTotal(TransactionUtils.getFormattedAmount(new BigDecimal(grandTotal.doubleValue())));
    }

    /**
     * Searches for details for a particular Batch.
     * Populates the form object with the details.
     *
     * @param form The form object.
     */
    private void searchForBatchDetails(ReportReconciliationForm form, String batchId) {

        // Get GL Transmissions for the given batch:
        List<GlTransmission> glTransmissions = generalLedgerService.getGlTransmissionsForBatch(batchId, GlTransmissionStatus.TRANSMITTED);
        List<GlTransaction> glTransactions = generalLedgerService.getGlTransactionsForBatch(batchId);

        // Create a BatchTransmissionModel list, which should contain only one element:
        MutableDouble grandTotal = new MutableDouble(0);
        List<BatchTransmissionModel> batchTransmissions = createBatchTransmissionList(glTransmissions, grandTotal);

        // Set the objects on the form:
        if (CollectionUtils.isNotEmpty(batchTransmissions)) {
            // Create a BatchTransmissionDetailsModel object:
            BatchTransmissionDetailsModel batchDetails = createBatchTransmissionDetails(batchTransmissions.get(0), glTransactions);

            form.setDisplayedBatch(batchDetails);
        } else {
            // Set an empty object:
            form.setDisplayedBatch(new BatchTransmissionDetailsModel());
        }
    }

    /**
     * Creates a List of all BatchTransmissionModel objects that contain
     * their related GL Account Types.
     *
     * @param grandTotalAmount The total for all batches. This is an OUT parameter.
     * @return a List of all BatchTransmissionModel objects.
     */
    private List<BatchTransmissionModel> createBatchTransmissionList(List<GlTransmission> glTransmissions, MutableDouble grandTotalAmount) {

        // Create a temporary Map of Batch IDs mapped to BatchTransmissionModel objects:
        Map<String, BatchTransmissionModel> result = new HashMap<String, BatchTransmissionModel>();

        // Declare, but not fetch yet, a list of all GL Types:
        List<GeneralLedgerType> generalLedgerTypes = null;

        // Go through the list of GLTransmissions
        for (GlTransmission glTransmission : glTransmissions) {

            // Get the batch ID from the current transmission:
            String batchId = glTransmission.getBatchId();

            if (StringUtils.isNotEmpty(batchId)) {

                // Initialize the list of GL Types if needed:
                if (generalLedgerTypes == null) {
                    generalLedgerTypes = auditableEntityService.getAuditableEntities(GeneralLedgerType.class);
                }

                // See if the current GlTransmission is already mapped to a BatchTransmissionModel:
                BatchTransmissionModel batchTransmission = result.get(batchId);

                // If not mapped yet, create a new BatchTransmissionModel, set attributes and map it:
                if (batchTransmission == null) {
                    batchTransmission = new BatchTransmissionModel();
                    batchTransmission.setBatchId(batchId);
                    batchTransmission.setTransmissionDate(glTransmission.getDate());
                    batchTransmission.setGlAccountSublist(new ArrayList<GeneralLedgerAccountModel>());
                    batchTransmission.setTotalAmount(new BigDecimal(0));
                    result.put(batchId, batchTransmission);
                }

                // Add a GL Account to the sub-list:
                addGlAccountModel(glTransmission, generalLedgerTypes, batchTransmission,
                        batchTransmission.getGlAccountSublist(), grandTotalAmount);
            }
        }

        return new ArrayList<BatchTransmissionModel>(result.values());
    }

    /**
     * Searches for a GL Type in the given list by its GL Account ID.
     *
     * @param generalLedgerTypes A list of GL Types.
     * @param glAccountId        ID of a GL Account.
     * @return A matching GL Type or <code>null</code> if no match can be found.
     */
    private GeneralLedgerType findGlType(List<GeneralLedgerType> generalLedgerTypes, String glAccountId) {

        for (GeneralLedgerType glType : generalLedgerTypes) {
            if (StringUtils.equalsIgnoreCase(glType.getGlAccountId(), glAccountId)) {
                return glType;
            }
        }

        return null;
    }

    /**
     * Creates a new GeneralLedgerAccountModel object from the given GlTransmission
     * and adds it to the given list. Or if one wiht a matching GL Account ID and Operation
     * Type already exists in the list, adds or subtracts the amount.
     *
     * @param glTransmission     A GlTransmission.
     * @param generalLedgerTypes A list of all existing GlTypes.
     * @param glAccountModels    A list of GeneralLedgerAccountModel objects belonging to a BatchTransmissionModel.
     * @param grandTotalAmount   The grand total for all Batch Transmissions.
     * @return A new GeneralLedgerAccountModel object created from the given GlTransmission.
     */
    private GeneralLedgerAccountModel addGlAccountModel(GlTransmission glTransmission,
                                                        List<GeneralLedgerType> generalLedgerTypes, BatchTransmissionModel batchTransmission,
                                                        List<GeneralLedgerAccountModel> glAccountModels, MutableDouble grandTotalAmount) {
        // Try to find a matching GL Type:
        String glAccountId = glTransmission.getGlAccountId();
        GeneralLedgerType glType = findGlType(generalLedgerTypes, glAccountId);
        String glAccountName = (glType != null)
                ? StringUtils.defaultIfBlank(glType.getDescription(), glType.getName()) : glAccountId;
        GlOperationType glOperationType = glTransmission.getGlOperation();
        if(glOperationType == null && glType != null) {
                glOperationType = glType.getGlOperationOnCharge();
        }

        GeneralLedgerAccountModel glAccount = null;

        // Try to find an existing GeneralLedgerAccountModel in the list:
        for (GeneralLedgerAccountModel glAccountModel : glAccountModels) {
            if (StringUtils.equals(glAccountId, glAccountModel.getGlAccountId())
                    && (glOperationType == glAccountModel.getOperationType())) {
                glAccount = glAccountModel;
                break;
            }
        }

        // Create a new GeneralLedgerAccountModel object if an existing one is not found:
        if (glAccount == null) {
            glAccount = new GeneralLedgerAccountModel();
            glAccount.setGlAccountId(glAccountId);
            glAccount.setName(glAccountName);
            glAccount.setOperationType(glOperationType);
            glAccount.setTotalAmount(new BigDecimal(0));
            glAccount.setGlType(glType);
            glAccountModels.add(glAccount);
        }

        // Adjust the amount of the GeneralLedgerAccountModel object:
        BigDecimal adjustmentAmount = (glOperationType == GlOperationType.CREDIT)
                ? glTransmission.getAmount() : glTransmission.getAmount().negate();

        glAccount.setTotalAmount(glAccount.getTotalAmount().add(glTransmission.getAmount()));

        // Adjust the total amount for the GlTransmission and the Grand Total:
        if(glOperationType == GlOperationType.DEBIT) {
            batchTransmission.setTotalDebitAmount(batchTransmission.getTotalDebitAmount().add(glTransmission.getAmount()));
        } else {
            batchTransmission.setTotalCreditAmount(batchTransmission.getTotalCreditAmount().add(glTransmission.getAmount()));
        }

        batchTransmission.setTotalAmount(batchTransmission.getTotalAmount().add(adjustmentAmount));
        grandTotalAmount.add(adjustmentAmount);

        return glAccount;
    }

    /**
     * Generates a name for a Prior Batch transaction report file.
     *
     * @param batchId Batch ID
     * @return Batch transaction report file name.
     */
    private String generatePriorBatchReportFileName(String batchId) {
        return String.format("%s-General_Ledger_Transactions.xml", batchId);
    }

    /**
     * Creates a list of GeneralLedgerAccountModel that contain related Pending GL Transactions.
     *
     * @param grandTotalAmount The grand total amount of all GL Accounts to populate after this method ends.
     * @return A list of GeneralLedgerAccountModel object for display.
     */
    private List<GeneralLedgerAccountModel> createPendingTransactionGlAccountsList(MutableDouble grandTotalAmount) {

        // Find all GL Transactions in the "Pending" status ('Q'):
        List<GlTransaction> pendingTransactions = generalLedgerService.getGlTransactionsByStatus(GlTransactionStatus.QUEUED);

        // Create a temporary Map of GL Account Ids mapped to GeneralLedgerAccountModel objects:
        Map<String, GeneralLedgerAccountModel> glAccountMap = new HashMap<String, GeneralLedgerAccountModel>();

        // Declare, but not fetch yet, a list of all GL Types:
        List<GeneralLedgerType> generalLedgerTypes = null;

        // Go through the list of all GL Transactions:
        for (GlTransaction glTransaction : pendingTransactions) {
            // Get the GL Account ID from the current GL Transaction:
            String glAccountId = glTransaction.getGlAccountId();

            if (StringUtils.isNotBlank(glAccountId)) {
                // Initialize the list of GL Types if needed:
                if (generalLedgerTypes == null) {
                    generalLedgerTypes = auditableEntityService.getAuditableEntities(GeneralLedgerType.class);
                }

                // Check if the current GL Account ID is already mapped to a GeneralLedgerAccountModel object:
                GeneralLedgerAccountModel glAccountModel = glAccountMap.get(glAccountId);

                // If not mapped yet, create a new GeneralLedgerAccountModel object and map it:
                if (glAccountModel == null) {
                    // Find a matching GL Type if one can be found:
                    GeneralLedgerType glType = findGlType(generalLedgerTypes, glAccountId);
                    String glAccountName = (glType != null)
                            ? StringUtils.defaultIfBlank(glType.getDescription(), glType.getName()) : glAccountId;
                    GlOperationType glOperationType = (glType != null)
                            ? glType.getGlOperationOnCharge() : glTransaction.getGlOperation();

                    glAccountModel = new GeneralLedgerAccountModel();
                    glAccountModel.setGlAccountId(glAccountId);
                    glAccountModel.setName(glAccountName);
                    glAccountModel.setOperationType(glOperationType);
                    glAccountModel.setTotalAmount(new BigDecimal(0));
                    glAccountModel.setGlType(glType);
                    glAccountMap.put(glAccountId, glAccountModel);
                }

                // Add a GL Transaction to the GL Account:
                addGlTransactionModel(glAccountModel, glTransaction, grandTotalAmount, true);
            }
        }

        return new ArrayList<GeneralLedgerAccountModel>(glAccountMap.values());
    }

    /**
     * Adds a GL Transaction to the given GeneralLedgerAccountModel object.
     * Adjusts the GL Account amount and the Grand total amount accordingly.
     * According to the logic, only those GL Transactions that point to a SINGLE
     * KSA Transactions are hyperlinked for a light-box view.
     *
     * @param glAccountModel       A GL Account to add a GL Transaction to.
     * @param glTransaction        A GL Transaction to add to the GL Account.
     * @param grandTotalAmount     The grand total amount for all GL Accounts.
     * @param adjustGlAccountTotal Optionally, GL Account Total adjustment can be turned on or off.
     */
    private void addGlTransactionModel(GeneralLedgerAccountModel glAccountModel, GlTransaction glTransaction,
                                       MutableDouble grandTotalAmount, boolean adjustGlAccountTotal) {

        // Get the list of KSA Transactions from the GL Transaction:
        List<Transaction> ksaTransactions = transactionService.getTransactionsByGlTransactionId(glTransaction.getId());

        Transaction transaction;

        // If GL Transaction is linked to a single KSA Transaction, fetch its details:
        if (ksaTransactions.size() == 1) {
            // Prefetch associated properties:
            transaction = ksaTransactions.get(0);
        } else {
            // Create a scarcely populated substitute KSA Transaction:
            transaction = createMinimalKsaTransactionForView(glTransaction, ksaTransactions);
        }

        // Create a new GeneralLedgerTransactionModel object:
        GeneralLedgerTransactionModel glTransactionModel = new GeneralLedgerTransactionModel(transaction);

        // Link to the GL Account model:
        glTransactionModel.setGlTransaction(glTransaction);
        glAccountModel.getGlTransactions().add(glTransactionModel);

        // Adjust the total for the GL Account and the Grand Total amount:
        BigDecimal adjustmentAmount = (glTransaction.getGlOperation() == GlOperationType.CREDIT)
                ? glTransaction.getAmount() : glTransaction.getAmount().negate();

        grandTotalAmount.add(adjustmentAmount);

        if (adjustGlAccountTotal) {
            glAccountModel.setTotalAmount(glAccountModel.getTotalAmount().add(adjustmentAmount));
        }
    }

    /**
     * Creates a scarcely populated KSA Transaction, which is enough for the view.
     * Assigns no statement text, so it's not hyperlinked in the view.
     * Calculates the total amount of all Transactions.
     *
     * @param glTransaction   GL Transaction, the owner of KSA Transactions.
     * @param ksaTransactions A list of KSA Transactions linked to the GL Transaction.
     * @return A scarcely populated KSA Transaction for the view.
     */
    private Transaction createMinimalKsaTransactionForView(GlTransaction glTransaction,
                                                           List<Transaction> ksaTransactions) {

        // Calculate the total amount of all KSA Transactions:
        MutableDouble totalAmount = new MutableDouble(0);

        final boolean isCredit = (glTransaction.getGlOperation() == GlOperationType.CREDIT);

        String originatingAccountId = null;

        for (Transaction ksaTransaction : ksaTransactions) {

            BigDecimal adjustmentAmount = isCredit ? ksaTransaction.getAmount() : ksaTransaction.getAmount().negate();

            totalAmount.add(adjustmentAmount);
            originatingAccountId = ksaTransaction.getAccountId();
        }

        // Create a minimal KSA Transaction, which is enough for the view:
        Transaction ksaTransaction = new Transaction() {
            @Override
            public TransactionTypeValue getTransactionTypeValue() {
                return isCredit ? TransactionTypeValue.PAYMENT : TransactionTypeValue.CHARGE;
            }
        };

        ksaTransaction.setAmount(new BigDecimal(totalAmount.doubleValue()));
        ksaTransaction.setAccountId(originatingAccountId);

        return ksaTransaction;
    }

    /**
     * Creates a BatchTransmissionDetailsModel object from a BatchTransmissionModel object.
     *
     * @param batchTransmission A BatchTransmissionModel object.
     * @return A BatchTransmissionDetailsModel object created.
     */
    private BatchTransmissionDetailsModel createBatchTransmissionDetails(BatchTransmissionModel batchTransmission,
                                                                         List<GlTransaction> glTransactions) {
        // Create a new BatchTransmissionDetailsModel object:
        BatchTransmissionDetailsModel batchDetails = new BatchTransmissionDetailsModel();

        // Separate GL Accounts into Accrual and All Other GL Accounts:
        separateGlAccountsForDetails(batchTransmission, batchDetails, glTransactions);

        // Set the attributes of the Details object from the given Transmission object:
        batchDetails.setBatchId(batchTransmission.getBatchId());
        batchDetails.setTransmissionDate(batchTransmission.getTransmissionDate());
        batchDetails.setTotalAmount(batchTransmission.getTotalAmount());

        return batchDetails;
    }

    /**
     * Separates GL Accounts of the given Transmission object into Accrual and All Other GL Accounts
     * and sets them on the Details object. Calculates totals for both groups of GL Accounts.
     *
     * @param batchTransmission A BatchTransmissionModel object.
     * @param batchDetails      A BatchTransmissionDetailsModel object.
     */
    private void separateGlAccountsForDetails(BatchTransmissionModel batchTransmission, BatchTransmissionDetailsModel batchDetails,
                                              List<GlTransaction> glTransactions) {

        // Define two lists of GL Accounts and totals for each group:
        List<GeneralLedgerAccountModel> accrualGlAccount = new LinkedList<GeneralLedgerAccountModel>();
        List<GeneralLedgerAccountModel> allOtherGlAccount = new LinkedList<GeneralLedgerAccountModel>();
        MutableDouble accrualGlAccountsTotal = new MutableDouble(0);
        MutableDouble allOtherGlAccountsTotal = new MutableDouble(0);

        // Create a Map of GlTransactions by GL Account IDs they belong to:
        Map<String, List<GlTransaction>> batchGlTransactions = createGlAccountTransactionMap(glTransactions);

        // Iterate through the list of all GL Accounts and separate them:
        List<GeneralLedgerAccountModel> glAccounts = batchTransmission.getGlAccountSublist();

        for (GeneralLedgerAccountModel glAccount : glAccounts) {
            // If a GL Account has a GL Type, it's an Accrual GL Account:
            if (glAccount.getGlType() != null) {
                accrualGlAccount.add(glAccount);
                addGlTransactionsForBatchDetails(glAccount, batchGlTransactions, accrualGlAccountsTotal);
            } else {
                allOtherGlAccount.add(glAccount);
                addGlTransactionsForBatchDetails(glAccount, batchGlTransactions, allOtherGlAccountsTotal);
            }
        }

        // Set the attributes of the Details object:
        batchDetails.setAccrualGlAccounts(accrualGlAccount);
        batchDetails.setAllOtherGlAccounts(allOtherGlAccount);
        batchDetails.setAccrualGlAccountsTotal(new BigDecimal(accrualGlAccountsTotal.doubleValue()));
        batchDetails.setAllOtherGlAccountsTotal(new BigDecimal(allOtherGlAccountsTotal.doubleValue()));
    }

    /**
     * Create a Map of GlTransactions by GL Account IDs they belong to.
     *
     * @param glTransactions A list of GlTransactions.
     * @return A Map of GlTransactions mapped to their GL Account IDs.
     */
    private Map<String, List<GlTransaction>> createGlAccountTransactionMap(List<GlTransaction> glTransactions) {

        // Create the resulting Map:
        Map<String, List<GlTransaction>> result = new HashMap<String, List<GlTransaction>>();

        // Iterate through the list of Gl Transactions and record them under their GL Account IDs:
        for (GlTransaction glTransaction : glTransactions) {
            // Get the mapped List:
            String glAccountId = glTransaction.getTransmission().getGlAccountId();
            List<GlTransaction> mappedGlTransactions = result.get(glAccountId);

            // If the GL Account ID hasn't been mapped yet, create a new List and map it:
            if (mappedGlTransactions == null) {
                mappedGlTransactions = new ArrayList<GlTransaction>();
                result.put(glAccountId, mappedGlTransactions);
            }

            // Add the current GL Transaction to the list of mapped ones:
            mappedGlTransactions.add(glTransaction);
        }

        return result;
    }

    /**
     * Adds GeneralLedgerTransactionModel objects to the given GeneralLedgerAccountModel object
     * for a sub-table display.
     *
     * @param glAccount           A GeneralLedgerAccountModel object.
     * @param batchGlTransactions A Map of GlTransactions in a batch by GL Account Id.
     * @param totalAmount         The total amount for each group of GL Accounts.
     */
    private void addGlTransactionsForBatchDetails(GeneralLedgerAccountModel glAccount,
                                                  Map<String, List<GlTransaction>> batchGlTransactions, MutableDouble totalAmount) {

        // Add GL Transaction model objects to the GL Account model object:
        String glAccountId = glAccount.getGlAccountId();
        List<GlTransaction> glAccountTransactions = batchGlTransactions.get(glAccountId);

        if (CollectionUtils.isNotEmpty(glAccountTransactions)) {
            for (GlTransaction glTransaction : glAccountTransactions) {
                addGlTransactionModel(glAccount, glTransaction, totalAmount, false);
            }
        }

    }
}
