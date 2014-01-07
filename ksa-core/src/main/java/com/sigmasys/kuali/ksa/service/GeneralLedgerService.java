package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.annotation.Url;
import com.sigmasys.kuali.ksa.exception.GlTransactionFailedException;
import com.sigmasys.kuali.ksa.model.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * General Ledger service declares business operations on GL Transactions and related objects
 * <p/>
 *
 * @author Michael Ivanov
 */
@Url(GeneralLedgerService.SERVICE_URL)
@WebService(serviceName = GeneralLedgerService.SERVICE_NAME, portName = GeneralLedgerService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
public interface GeneralLedgerService {

    String SERVICE_URL = "generalLedger.webservice";
    String SERVICE_NAME = "GeneralLedgerService";
    String PORT_NAME = SERVICE_NAME + "Port";


    /**
     * Creates a new general ledger type.
     *
     * @param code                GL type code
     * @param name                GL type name
     * @param description         GL type description
     * @param glAccountId         GL Account ID
     * @param glOperationOnCharge GL operation on charge
     * @return GeneralLedgerType instance
     */
    GeneralLedgerType createGeneralLedgerType(String code, String name, String description, String glAccountId,
                                              GlOperationType glOperationOnCharge);

    /**
     * Persists GeneralLedgerType instance in the persistent store.
     *
     * @param glType GL type
     * @return GL type ID
     */
    Long persistGeneralLedgerType(GeneralLedgerType glType);

    /**
     * Returns all general ledger types existing in KSA in chronological order.
     *
     * @return list of GeneralLedgerType instances
     */
    List<GeneralLedgerType> getGeneralLedgerTypes();


    /**
     * Creates a new general ledger transaction based on the given parameters
     *
     * @param transactionId ID of the corresponding transaction
     * @param glAccountId   General ledger account ID
     * @param amount        Transaction amount
     * @param operationType GL operation type
     * @param statement     GL transaction statement
     * @param isQueued      Set status to Q unless isQueued is passed and is false, in which case, set status to W
     * @return new GL Transaction instance
     * @throws GlTransactionFailedException
     */
    @WebMethod(exclude = true)
    GlTransaction createGlTransaction(Long transactionId,
                                      String glAccountId,
                                      BigDecimal amount,
                                      GlOperationType operationType,
                                      String statement,
                                      boolean isQueued) throws GlTransactionFailedException;

    /**
     * Creates a new general ledger transaction based on the given parameters
     *
     * @param transactionId ID of the corresponding transaction
     * @param glAccountId   General ledger account ID
     * @param amount        Transaction amount
     * @param operationType GL operation type
     * @param statement     GL transaction statement
     * @return new GL Transaction instance
     * @throws GlTransactionFailedException
     */
    @WebMethod(exclude = true)
    GlTransaction createGlTransaction(Long transactionId,
                                      String glAccountId,
                                      BigDecimal amount,
                                      GlOperationType operationType,
                                      String statement) throws GlTransactionFailedException;

    /**
     * Persists GL Transaction in the persistent store
     *
     * @param glTransaction GlTransaction instance
     * @return GL Transaction ID
     */
    Long persistGlTransaction(GlTransaction glTransaction);


    /**
     * Summarizes the general ledger transactions for the given GL transaction list
     *
     * @param transactions List of general ledger transactions
     * @return the modified list of GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> summarizeGlTransactions(List<GlTransaction> transactions);

    /**
     * Returns the general ledger type instance by code.
     *
     * @param glTypeCode General Ledger type code
     * @return GeneralLedgerType instance
     */
    GeneralLedgerType getGeneralLedgerType(String glTypeCode);

    /**
     * Returns the general ledger type instance by ID.
     *
     * @param glTypeId General Ledger type ID
     * @return GeneralLedgerType instance
     */
    @WebMethod(exclude = true)
    GeneralLedgerType getGeneralLedgerType(Long glTypeId);

    /**
     * Returns the default general ledger mode.
     *
     * @return GeneralLedgerMode instance
     */
    GeneralLedgerMode getDefaultGeneralLedgerMode();

    /**
     * Returns the default general ledger type instance for the given code.
     *
     * @return GeneralLedgerType instance
     */
    GeneralLedgerType getDefaultGeneralLedgerType();

    /**
     * Creates and returns the list og GL transmissions for export based on the given parameters.
     *
     * @param fromDate           Start date, can be null
     * @param toDate             End date, can be null
     * @param isEffectiveDate    indicates whether Transaction effective date should be used if true,
     *                           otherwise - recognition date
     * @param recognitionPeriods an array of recognition period codes
     * @return list of GL transmissions
     */
    @WebMethod(exclude = true)
    List<GlTransmission> createGlTransmissions(Date fromDate, Date toDate, boolean isEffectiveDate,
                                               String... recognitionPeriods);

    /**
     * Retrieves all GL transmissions with the statuses for export by batch ID.
     *
     * @param batchId  GL transmission batch ID
     * @param statuses Status of GL Transmissions to retrieve.
     * @return list of GlTransmission instances
     */
    @WebMethod(exclude = true)
    List<GlTransmission> getGlTransmissionsForBatch(String batchId, GlTransmissionStatus... statuses);


    /**
     * Validates the GL account number.
     *
     * @param glAccount GL account number
     * @return true if the GL account is valid, false - otherwise.
     */
    boolean isGlAccountValid(String glAccount);

    /**
     * Validates the list of GL Breakdowns.
     *
     * @param glBreakdowns list of AbstractGlBreakdown instances
     * @return true if the list of GL Breakdowns is valid, false - otherwise
     */
    boolean isGlBreakdownValid(List<? extends AbstractGlBreakdown> glBreakdowns);

    /**
     * Retrieves all GL transactions for the given GL transaction date range and GL account ID
     * sorted by dates in ascending order.
     *
     * @param startDate   GL Transaction start date
     * @param endDate     GL Transaction end date
     * @param glAccountId GL Account ID
     * @return list of GlTransaction instances
     */
    List<GlTransaction> getGlTransactions(Date startDate, Date endDate, String glAccountId);

    /**
     * Retrieves all GL transactions for the given GL transaction date range sorted by dates in ascending order.
     *
     * @param startDate GL Transaction start date
     * @param endDate   GL Transaction end date
     * @return list of GlTransaction instances
     */
    @WebMethod(exclude = true)
    List<GlTransaction> getGlTransactions(Date startDate, Date endDate);

    /**
     * Retrieves all GL transactions for the given GL transaction status.
     *
     * @param status GL Transaction status
     * @return list of GlTransaction instances
     */
    @WebMethod(exclude = true)
    List<GlTransaction> getGlTransactionsByStatus(GlTransactionStatus status);

    /**
     * Retrieves GL transactions that belong to the specified Batch.
     *
     * @param batchId ID of a Batch which GL Transactions to retrieve.
     * @return list of GL Transactions belonging to the Batch.
     */
    @WebMethod(exclude = true)
    List<GlTransaction> getGlTransactionsForBatch(String batchId);

    /**
     * Retrieves all GL Transmissions with the specified statuses.
     *
     * @param statuses Statuses of GL Transmissions to retrieve.
     * @return A list of GL Transmissions with the specified statuses.
     */
    @WebMethod(exclude = true)
    List<GlTransmission> getGlTransmissionsByStatuses(GlTransmissionStatus... statuses);

    /**
     * Creates a list of GlBatchBaseline objects that contain some summary information about transactions
     * and their amounts.
     *
     * @param batchId Transmission batch ID generated during transaction export
     * @return list of GlBatchBaseline instances
     */
    List<GlBatchBaseline> createGlBaselineAmounts(String batchId);

    /**
     * Retrieves the previously persisted GlBatchBaseline objects that contain some summary information about
     * transactions and their amounts by Batch ID.
     *
     * @param batchId Transmission batch ID generated during transaction export
     * @return list of GlBatchBaseline instances
     */
    List<GlBatchBaseline> getGlBaselineAmounts(String batchId);

    /**
     * Returns all GL Breakdown Overrides associated with the specified transaction.
     *
     * @param transactionId Transaction ID
     * @return list of GlBreakdownOverride instances
     */
    List<GlBreakdownOverride> getGlBreakdownOverrides(Long transactionId);

    /**
     * Associates the transaction specified by ID with the list of GlBreakdownOverride instances.
     *
     * @param transactionId        Transaction ID
     * @param glBreakdownOverrides list of GlBreakdownOverride instances
     * @return list of GlBreakdownOverride IDs
     */
    List<Long> createGlBreakdownOverrides(Long transactionId, List<GlBreakdownOverride> glBreakdownOverrides);

    /**
     * This method persists new GL breakdowns and associates them with the given GL and transaction types.
     * It also provides validation of the breakdowns.
     *
     * @param glTypeId          GL type ID
     * @param transactionTypeId Transaction type ID
     * @param breakdowns        a list of GL breakdowns
     * @return a list of GL breakdown IDs
     */
    List<Long> createGlBreakdowns(Long glTypeId, TransactionTypeId transactionTypeId, List<GlBreakdown> breakdowns);

    /**
     * Retrieves a list of GL Breakdowns for the given transaction type ID.
     *
     * @param transactionTypeId TransactionType ID
     * @return list of GlBreakdown instances
     */
    List<GlBreakdown> getGlBreakdowns(TransactionTypeId transactionTypeId);

    /**
     * Retrieves a list of GL Breakdowns or Breakdown Overrides (if Transaction is overridden)
     * for the given Transaction instance.
     *
     * @param transaction Transaction instance
     * @return list of GlBreakdown or GlBreakdownOverride instances
     */
    List<AbstractGlBreakdown> getGlBreakdowns(Transaction transaction);

    /**
     * Retrieves a list of all failed GL transactions.
     *
     * @return list of FailedGlTransaction instances
     */
    List<FailedGlTransaction> getFailedGlTransactions();

    /**
     * Retrieves a list of failed GL transactions for the given Account ID.
     *
     * @param accountId Account ID
     * @return list of FailedGlTransaction instances
     */
    List<FailedGlTransaction> getFailedGlTransactionsForAccount(String accountId);

    /**
     * Retrieves a list of failed GL transactions for the given Transaction's effective start and end dates.
     *
     * @param startDate Transaction effective start date
     * @param endDate   Transaction effective end date
     * @return list of FailedGlTransaction instances
     */
    List<FailedGlTransaction> getFailedGlTransactionsForDates(Date startDate, Date endDate);

}
