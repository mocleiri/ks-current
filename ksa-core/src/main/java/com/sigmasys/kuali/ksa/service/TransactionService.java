package com.sigmasys.kuali.ksa.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.sigmasys.kuali.ksa.annotation.Url;
import com.sigmasys.kuali.ksa.exception.GlTransactionFailedException;
import com.sigmasys.kuali.ksa.model.*;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Transaction service declares business operations on Transaction and related objects.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Url(TransactionService.SERVICE_URL)
@WebService(serviceName = TransactionService.SERVICE_NAME, portName = TransactionService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
public interface TransactionService {

    String SERVICE_URL = "transaction.webservice";
    String SERVICE_NAME = "TransactionService";
    String PORT_NAME = SERVICE_NAME + "Port";


    /**
     * Creates and persists a new charge based on the given parameters.
     *
     * @param debitTypeId   The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                      based on the effective date
     * @param userId        Account ID
     * @param effectiveDate Charge Effective Date
     * @param amount        Charge amount
     * @return Charge instance
     */
    Charge createCharge(String debitTypeId, String userId, Date effectiveDate, BigDecimal amount);

    /**
     * Creates and persists a new payment based on the given parameters.
     *
     * @param creditTypeId  The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                      based on the effective date
     * @param userId        Account ID
     * @param effectiveDate Payment Effective Date
     * @param amount        Payment amount
     * @return Payment instance
     */
    Payment createPayment(String creditTypeId, String userId, Date effectiveDate, BigDecimal amount);

    /**
     * Creates and persists a new deferment based on the given parameters.
     *
     * @param creditTypeId   The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                       based on the effective date
     * @param userId         Account ID
     * @param effectiveDate  Deferment Effective Date
     * @param expirationDate Deferment Expiration Date
     * @param amount         Deferment amount
     * @return Deferment instance
     */
    Deferment createDeferment(String creditTypeId, String userId, Date effectiveDate, Date expirationDate, BigDecimal amount);

    /**
     * Creates and persists a new transaction based on the given parameters
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param userId            Account ID
     * @param effectiveDate     Transaction Effective Date
     * @param amount            Transaction amount
     * @return new Transaction instance
     */
    @WebMethod(exclude = true)
    Transaction createTransaction(String transactionTypeId, String userId, Date effectiveDate, BigDecimal amount);

    /**
     * Creates a new transaction based on the given parameters
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param userId            Account ID
     * @param effectiveDate     Transaction Effective date
     * @param recognitionDate   Transaction Recognition date
     * @param amount            Transaction amount
     * @return new Transaction instance
     */
    @WebMethod(exclude = true)
    Transaction createTransaction(String transactionTypeId, String userId, Date effectiveDate,
                                  Date recognitionDate, BigDecimal amount);

    /**
     * Creates a new transaction based on the given parameters
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param externalId        Transaction external ID
     * @param userId            Account ID
     * @param effectiveDate     Transaction Effective Date
     * @param expirationDate    Used for deferments only
     * @param amount            Transaction amount
     * @return new Transaction instance
     */
    Transaction createTransaction(String transactionTypeId, String externalId, String userId,
                                  Date effectiveDate, Date expirationDate, BigDecimal amount);

    /**
     * Creates a new transaction based on the given parameters
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param externalId        Transaction External ID
     * @param userId            Account ID
     * @param effectiveDate     Transaction Effective Date
     * @param recognitionDate   Transaction Recognition Date
     * @param expirationDate    Used for deferments only
     * @param amount            Transaction amount
     * @param overrideBlocks    indicates whether the account blocks must be overridden
     * @return new Transaction instance
     */
    @WebMethod(exclude = true)
    Transaction createTransaction(String transactionTypeId, String externalId, String userId,
                                  Date effectiveDate, Date recognitionDate, Date expirationDate,
                                  BigDecimal amount, boolean overrideBlocks);

    /**
     * Returns the transaction type instance for the given transaction type ID and effective date
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param effectiveDate     Transaction Effective Date
     * @return TransactionType instance
     */
    TransactionType getTransactionType(String transactionTypeId, Date effectiveDate);

    /**
     * Returns the transaction type instance for the given TransactionTypeId instance
     *
     * @param transactionTypeId TransactionTypeId instance
     * @return TransactionType instance
     */
    @WebMethod(exclude = true)
    TransactionType getTransactionType(TransactionTypeId transactionTypeId);

    /**
     * Returns the list of transaction type instances for the given string pattern.
     *
     * @param pattern    String containing characters within the code, name or description
     * @param entityType Class instance of TransactionType subclass
     * @return List of TransactionType instances
     */
    @WebMethod(exclude = true)
    <T extends TransactionType> List<T> getTransactionTypesByNamePattern(String pattern, Class<T> entityType);

    /**
     * Returns the list of transaction type instances for the given string pattern and effective date.
     *
     * @param pattern       String containing characters within the code, name or description
     * @param entityType    Class instance of TransactionType subclass
     * @param effectiveDate Date for which the transaction types are active
     * @return List of TransactionType instances
     */
    @WebMethod(exclude = true)
    <T extends TransactionType> List<T> getTransactionTypesByNamePattern(String pattern,
                                                                         Class<T> entityType,
                                                                         Date effectiveDate);

    /**
     * Creates a new debit type based on the given parameters.
     *
     * @param creditTypeId Transaction Type ID
     * @param name         Transaction Type name
     * @param startDate    Transaction type start date
     * @param priority     Priority integer value
     * @param description  Default statement text
     * @return a new DebitType instance
     */
    DebitType createDebitType(String creditTypeId, String name, Date startDate, int priority, String description);

    /**
     * Creates a new credit type based on the given parameters.
     *
     * @param creditTypeId Transaction Type ID
     * @param name         Transaction Type name
     * @param startDate    Transaction type start date
     * @param priority     Priority integer value
     * @param description  Default statement text
     * @return a new CreditType instance
     */
    CreditType createCreditType(String creditTypeId, String name, Date startDate, int priority, String description);

    /**
     * Creates a new debit sub-type based on the given parameters.
     * The original debit type must exist prior to its sub-type creation.
     *
     * @param debitTypeId ID of the existing Debit Type
     * @param startDate   Debit Type start date
     * @return a new DebitType instance
     */
    DebitType createDebitSubType(String debitTypeId, Date startDate);

    /**
     * Creates a new credit sub-type based on the given parameters.
     * The original credit type must exist prior to its sub-type creation.
     *
     * @param creditTypeId ID of the existing Credit Type
     * @param startDate    Credit Type start date
     * @return a new CreditType instance
     */
    CreditType createCreditSubType(String creditTypeId, Date startDate);

    /**
     * Checks if the transaction type exists.
     *
     * @param transactionTypeId Transaction Type ID
     * @return "true" if the transaction type exists, false - otherwise
     */
    boolean transactionTypeExists(String transactionTypeId);

    /**
     * Assigns the general ledger type to the transaction specified by IDs.
     *
     * @param transactionId Transaction ID
     * @param glTypeId      GL Type ID
     */
    void setGeneralLedgerType(Long transactionId, Long glTypeId);

    /**
     * Returns Transaction by ID
     *
     * @param id Transaction ID
     * @return Transaction instance
     */
    Transaction getTransaction(Long id);

    /**
     * Returns Charge by ID
     *
     * @param id Charge ID
     * @return Charge instance
     */
    Charge getCharge(Long id);

    /**
     * Returns Payment by ID
     *
     * @param id Payment ID
     * @return Payment instance
     */
    Payment getPayment(Long id);

    /**
     * Returns Deferment by ID
     *
     * @param id Deferment ID
     * @return Deferment instance
     */
    Deferment getDeferment(Long id);

    /**
     * Returns all transactions sorted by ID
     *
     * @return List of transactions
     */
    @WebMethod(exclude = true)
    List<Transaction> getTransactions();

    /**
     * Returns all transactions sorted by ID
     *
     * @return List of transactions
     */
    @WebMethod(exclude = true)
    List<Transaction> getTransactions(String userId);

    /**
     * Returns all transactions by account ID and date range
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of transactions
     */
    List<Transaction> getTransactions(String userId, Date fromDate, Date toDate);

    /**
     * Returns all charges by account ID and date range
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of charges
     */
    List<Charge> getCharges(String userId, Date fromDate, Date toDate);

    /**
     * Returns all payments by account ID and date range
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of payments
     */
    List<Payment> getPayments(String userId, Date fromDate, Date toDate);

    /**
     * Returns all deferments by account ID and date range
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of deferments
     */
    List<Deferment> getDeferments(String userId, Date fromDate, Date toDate);

    /**
     * Returns all transactions by account ID, date range and statuses
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @param statuses Array of transaction statuses
     * @return List of transactions
     */
    @WebMethod(exclude = true)
    List<Transaction> getTransactions(String userId, Date fromDate, Date toDate, TransactionStatus... statuses);

    /**
     * Returns all charges by account ID
     *
     * @param userId Account ID
     * @return List of all charges by account ID
     */
    @WebMethod(exclude = true)
    List<Charge> getCharges(String userId);

    /**
     * Returns all charges sorted by ID
     *
     * @return List of all charges
     */
    @WebMethod(exclude = true)
    List<Charge> getCharges();

    /**
     * Returns all payments sorted by ID
     *
     * @return List of all payments
     */
    @WebMethod(exclude = true)
    List<Payment> getPayments();

    /**
     * Returns all payments by account ID
     *
     * @param userId Account ID
     * @return List of all payments by account ID
     */
    @WebMethod(exclude = true)
    List<Payment> getPayments(String userId);

    /**
     * Returns all deferments sorted by ID
     *
     * @return List of all deferments
     */
    @WebMethod(exclude = true)
    List<Deferment> getDeferments();

    /**
     * Returns all deferments by account ID
     *
     * @param userId Account ID
     * @return List of all deferments by account ID
     */
    @WebMethod(exclude = true)
    List<Deferment> getDeferments(String userId);

    /**
     * Persists the transaction in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param transaction Transaction instance
     * @return Transaction ID
     */
    Long persistTransaction(Transaction transaction);

    /**
     * Persists the transaction type in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param transactionType TransactionType instance
     * @return Transaction Type ID
     */
    TransactionTypeId persistTransactionType(TransactionType transactionType);

    /**
     * Removes the transaction from the database.
     *
     * @param id Transaction ID
     * @return true if the Transaction entity has been deleted
     */
    boolean deleteTransaction(Long id);

    /**
     * Checks if the first transaction can pay the second transaction.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @return true if transactionId1 can pay transactionId2, false - otherwise
     */
    @WebMethod(exclude = true)
    boolean canPay(Long transactionId1, Long transactionId2);

    /**
     * Checks if the first transaction can pay the second transaction.
     *
     * @param transaction1 First transaction
     * @param transaction2 Second transaction
     * @return true if transaction1 can pay transaction2, false - otherwise
     */
    @WebMethod(exclude = true)
    boolean canPay(Transaction transaction1, Transaction transaction2);

    /**
     * Checks if the first transaction can pay the second transaction.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param priority       priority
     * @return true if transactionId1 can pay transactionId2, false - otherwise
     */
    @WebMethod(exclude = true)
    boolean canPay(Long transactionId1, Long transactionId2, int priority);

    /**
     * Checks if the first transaction can pay the second transaction.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param priorityFrom   lower priority boundary
     * @param priorityTo     upper priority boundary
     * @return true if transactionId1 can pay transactionId2, false - otherwise
     */
    boolean canPay(Long transactionId1, Long transactionId2, int priorityFrom, int priorityTo);

    /**
     * This will allocate the value of amount on the transaction. A check will
     * be made to ensure that the allocated amount is equal to or less than the
     * localAmount, less any lockedAllocationAmount. The expectation is that
     * this method will only be called by the payment application module.
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param amount         amount of money to be allocated
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    @WebMethod(exclude = true)
    CompositeAllocation createAllocation(Long transactionId1, Long transactionId2, BigDecimal amount);

    /**
     * This will allocate the value of amount on the transaction. A check will
     * be made to ensure that the allocated amount is equal to or less than the
     * localAmount, less any lockedAllocationAmount. The expectation is that
     * this method will only be called by the payment application module.
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param amount         amount of money to be allocated
     * @param isQueued       indicates whether the GL transaction should be in Q or W status
     * @return a new allocation
     */
    CompositeAllocation createAllocation(Long transactionId1, Long transactionId2, BigDecimal amount, boolean isQueued);

    /**
     * Creates an allocation between two transactions with the specified parameters.
     *
     * @param transaction1     First transaction
     * @param transaction2     Second transaction
     * @param newAmount        amount to be allocated
     * @param isQueued         indicates whether the GL transaction should be in Q or W status
     * @param locked           indicates whether the allocation should be locked or unlocked
     * @param internallyLocked indicates whether the allocation should be internally locked or unlocked
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    @WebMethod(exclude = true)
    CompositeAllocation createAllocation(Transaction transaction1, Transaction transaction2, BigDecimal newAmount,
                                         boolean isQueued, boolean locked, boolean internallyLocked);

    /**
     * This will allocate a locked amount on the transaction. A check will be
     * made to ensure that the lockedAmount and the allocateAmount don't exceed
     * the ledgerAmount of the transaction. Setting an amount as locked prevents
     * the payment application system from reallocating the balance elsewhere.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param amount         amount of money to be allocated
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    @WebMethod(exclude = true)
    CompositeAllocation createLockedAllocation(Long transactionId1, Long transactionId2, BigDecimal amount);


    /**
     * This will allocate a locked amount on the transaction. A check will be
     * made to ensure that the lockedAmount and the allocateAmount don't exceed
     * the ledgerAmount of the transaction. Setting an amount as locked prevents
     * the payment application system from reallocating the balance elsewhere.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param amount         amount of money to be allocated
     * @param isQueued       indicates whether the GL transaction should be in Q or W status
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    @WebMethod(exclude = true)
    CompositeAllocation createLockedAllocation(Long transactionId1, Long transactionId2, BigDecimal amount, boolean isQueued);

    /**
     * This will allocate a locked amount on the transaction. A check will be
     * made to ensure that the lockedAmount and the allocateAmount don't exceed
     * the ledgerAmount of the transaction. Setting an amount as locked prevents
     * the payment application system from reallocating the balance elsewhere.
     *
     * @param transactionId1   transaction1 ID
     * @param transactionId2   transaction2 ID
     * @param amount           amount of money to be allocated
     * @param isQueued         indicates whether the GL transaction should be in Q or W status
     * @param internallyLocked indicates whether the allocation should be internally locked or not
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    CompositeAllocation createLockedAllocation(Long transactionId1, Long transactionId2, BigDecimal amount,
                                               boolean isQueued, boolean internallyLocked);

    /**
     * Removes all allocations (locked and non-locked) associated with the given transaction.
     * <p/>
     *
     * @param transactionId transaction ID
     * @return list of generated GL transactions
     */
    List<GlTransaction> removeAllAllocations(Long transactionId);

    /**
     * Removes non-locked allocations associated with the given transaction.
     * <p/>
     *
     * @param transactionId transaction ID
     * @return list of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> removeAllocations(Long transactionId);

    /**
     * Removes non-locked allocations associated with the given transaction list.
     * <p/>
     *
     * @param transactions list of transactions for which allocations have to be removed
     * @return list of generated GL transactions
     */
    @WebMethod(exclude = true)
    public List<GlTransaction> removeAllocations(List<Transaction> transactions);

    /**
     * Retrieves all allocations associated with the given transaction by ID.
     * <p/>
     *
     * @param transactionId Transaction ID
     * @return list of allocations
     */
    List<Allocation> getAllocations(Long transactionId);

    /**
     * Removes all allocations (locked and non-locked) associated with the given Account ID
     * <p/>
     *
     * @param userId Account ID
     * @return list of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> removeAllAllocations(String userId);

    /**
     * Removes all allocations (locked and non-locked) associated with the given transaction list.
     * <p/>
     *
     * @param transactions list of transactions for which allocations have to be removed
     * @return list of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> removeAllAllocations(List<Transaction> transactions);

    /**
     * Removes allocation between two given transactions
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @return list of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> removeAllocation(Long transactionId1, Long transactionId2);

    /**
     * Removes allocation between two given transactions
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param isQueued       indicates whether the GL transaction should be in Q or W status
     * @return list of generated GL transactions
     */
    List<GlTransaction> removeAllocation(Long transactionId1, Long transactionId2, boolean isQueued);

    /**
     * Removes locked allocation between two given transactions
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @return list of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> removeLockedAllocation(Long transactionId1, Long transactionId2);

    /**
     * Removes locked allocation between two given transactions
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param isQueued       indicates whether the GL transaction should be in Q or W status
     * @return list of generated GL transactions
     */
    List<GlTransaction> removeLockedAllocation(Long transactionId1, Long transactionId2, boolean isQueued);

    /**
     * This method is used to apply “obvious” payments to their reversal. Under normal circumstances, this will not be needed,
     * as reversals created inside of KSA will automatically be locked together. However, after an import from an external system,
     * this allocation may not exist. This method is provided to ensure that transactions that are obviously designed to be together,
     * are allocated together. “Obvious” means they are entirely unallocated, have the same amounts,
     * but one is negated, and they have the same transaction type.
     * <p/>
     *
     * @param accountId Account ID
     * @param isQueued  indicates whether the GL transaction should be in Q or W status
     * @return list of generated GL transactions
     */
    List<GlTransaction> allocateReversals(String accountId, boolean isQueued);

    /**
     * This method is used to apply “obvious” payments to their reversal. Under normal circumstances, this will not be needed,
     * as reversals created inside of KSA will automatically be locked together. However, after an import from an external system,
     * this allocation may not exist. This method is provided to ensure that transactions that are obviously designed to be together,
     * are allocated together. “Obvious” means they are entirely unallocated, have the same amounts,
     * but one is negated, and they have the same transaction type.
     * <p/>
     *
     * @param accountId Account ID
     * @return list of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> allocateReversals(String accountId);

    /**
     * An overridden version of allocateReversals() that takes a list of transactions as an argument.
     *
     * @param transactions list of transactions
     * @param isQueued     indicates whether the GL transaction should be in Q or W status
     * @return list of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> allocateReversals(List<Transaction> transactions, boolean isQueued);

    /**
     * An overridden version of allocateReversals() that takes a list of transactions as an argument.
     *
     * @param transactions list of transactions
     * @return list of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> allocateReversals(List<Transaction> transactions);

    /**
     * Makes effective all failed transactions for which GL entries have not been generated yet.
     *
     * @return the number of effective transactions
     */
    int makeFailedTransactionsEffective();

    /**
     * Makes effective all transactions for which GL entries have not been generated yet.
     *
     * @param forceEffective indicates whether it has to be forced
     * @return the number of effective transactions
     */
    int makeAllTransactionsEffective(boolean forceEffective);

    /**
     * Moves a transaction from a pre-effective state to an effective state. Once a transaction is effective, its
     * general ledger entries are created. In certain cases, a transaction might be moved to an effective state
     * before its effective date, in which case, forceEffective is passed as true.
     *
     * @param transactionId  transaction ID
     * @param forceEffective indicates whether it has to be forced
     * @return true if the transaction has been made effective, false - otherwise
     * @throws GlTransactionFailedException
     */
    boolean makeEffective(Long transactionId, boolean forceEffective) throws GlTransactionFailedException;

    /**
     * If the reverse method is called, the system will generate a negative
     * transaction for the type of the original transaction. A memo transaction
     * will be generated, and the transactions will be locked together. Subject
     * to user customization, the transactions may be marked as hidden. (likely
     * that credits will not be hidden, debits will.) A charge to an account may
     * be reversed when a mistake is made, or a refund is issued. A payment may
     * be reversed when a payment bounces, or for some other reason is entered
     * on to the account and is not payable.
     *
     * @param transactionId  Transaction ID
     * @param memoText       Text of the memo to be created
     * @param reversalAmount Reversal amount
     * @return a created reversal transaction
     */
    @WebMethod(exclude = true)
    Transaction reverseTransaction(Long transactionId, String memoText, BigDecimal reversalAmount);


    /**
     * If the reverse method is called, the system will generate a negative
     * transaction for the type of the original transaction. A memo transaction
     * will be generated, and the transactions will be locked together. Subject
     * to user customization, the transactions may be marked as hidden. (likely
     * that credits will not be hidden, debits will.) A charge to an account may
     * be reversed when a mistake is made, or a refund is issued. A payment may
     * be reversed when a payment bounces, or for some other reason is entered
     * on to the account and is not payable.
     *
     * @param transactionId   Transaction ID
     * @param memoText        Text of the memo to be created
     * @param reversalAmount  Reversal amount
     * @param statementPrefix Statement prefix that will be added to the existing Transaction statement
     * @return a created reversal transaction
     */
    @WebMethod(exclude = true)
    Transaction reverseTransaction(Long transactionId, String memoText, BigDecimal reversalAmount, String statementPrefix);

    /**
     * If the reverse method is called, the system will generate a negative
     * transaction for the type of the original transaction. A memo transaction
     * will be generated, and the transactions will be locked together. Subject
     * to user customization, the transactions may be marked as hidden. (likely
     * that credits will not be hidden, debits will.) A charge to an account may
     * be reversed when a mistake is made, or a refund is issued. A payment may
     * be reversed when a payment bounces, or for some other reason is entered
     * on to the account and is not payable.
     *
     * @param transactionId   Transaction ID
     * @param memoText        Text of the memo to be created
     * @param reversalAmount  Reversal amount
     * @param statementPrefix Statement prefix that will be added to the existing Transaction statement
     * @param reversalStatus  Transaction status of the reversal (can be null)
     * @return a created reversal transaction
     */
    @WebMethod(exclude = true)
    Transaction reverseTransaction(Long transactionId, String memoText, BigDecimal reversalAmount,
                                   String statementPrefix, TransactionStatus reversalStatus);


    /**
     * If the reverse method is called, the system will generate a negative
     * transaction for the type of the original transaction. A memo transaction
     * will be generated, and the transactions will be locked together. Subject
     * to user customization, the transactions may be marked as hidden. (likely
     * that credits will not be hidden, debits will.) A charge to an account may
     * be reversed when a mistake is made, or a refund is issued. A payment may
     * be reversed when a payment bounces, or for some other reason is entered
     * on to the account and is not payable.
     *
     * @param transactionId             Transaction ID
     * @param reversalTransactionTypeId Transaction Type ID of the reverse transaction
     * @param memoText                  Text of the memo to be created
     * @param reversalAmount            Reversal amount
     * @param statementPrefix           Statement prefix that will be added to the existing Transaction statement
     * @param reversalStatus            Transaction status of the reversal (can be null)
     * @return a created reversal transaction
     */
    Transaction reverseTransaction(Long transactionId, String reversalTransactionTypeId, String memoText,
                                   BigDecimal reversalAmount, String statementPrefix, TransactionStatus reversalStatus);


    /**
     * A deferment may be expired automatically (when the date of the deferment
     * passes) or be expired manually but the system, either through user
     * intervention, or by a payment being received on the account that removes
     * the need for the deferment. If, for example, an account is paid in full,
     * the deferment would have to be expired, otherwise a credit balance would
     * technically occur on the account.
     */
    void expireDeferment(Long defermentId);


    /**
     * Expires all deferments on the account whose expiration date has passed
     *
     * @param userId Account ID
     */
    @WebMethod(exclude = true)
    void expireDeferments(String userId);

    /**
     * Returns the transaction type for the given transaction type ID
     *
     * @param transactionTypeId The first part of TransactionTypeId PK
     * @return a subclass of TransactionType
     */
    @WebMethod(exclude = true)
    <T extends TransactionType> Class<T> getTransactionTypeClass(String transactionTypeId);

    /**
     * Determines if the transaction is allowed for the given account ID, transaction type and effective date
     *
     * @param accountId         Account ID
     * @param transactionTypeId Transaction Type ID
     * @param effectiveDate     Effective Date
     * @return true/false
     */
    boolean isTransactionAllowed(String accountId, String transactionTypeId, Date effectiveDate);

    /**
     * Returns the list of matching transactions for the given name pattern.
     *
     * @param pattern Statement text pattern
     * @return List of Transaction instances
     */
    List<Transaction> findTransactionsByStatementPattern(String pattern);

    /**
     * The logic of this is very similar to reverseTransaction(), except a partial write off is allowed, and only
     * credits can be written off. Also, the institution can choose to write off charges to a different general
     * ledger account, instead of the original, permitting the writing off to a general "bad debt" account, if they
     * so choose.
     *
     * @param transactionId     Transaction ID
     * @param transactionTypeId TransactionType ID
     * @param memoText          Memo test
     * @param statementPrefix   Transaction statement prefix
     * @return a write-off transaction instance
     */
    Transaction writeOffTransaction(Long transactionId, TransactionTypeId transactionTypeId, String memoText,
                                    String statementPrefix);

    /**
     * The logic of this is very similar to reverseTransaction(), except a partial write off is allowed, and only
     * credits can be written off. Also, the institution can choose to write off charges to a different general
     * ledger account, instead of the original, permitting the writing off to a general "bad debt" account, if they
     * so choose.
     *
     * @param transactionId   Transaction ID
     * @param memoText        Memo test
     * @param statementPrefix Transaction statement prefix
     * @return a write-off transaction instance
     */
    @WebMethod(exclude = true)
    Transaction writeOffTransaction(Long transactionId, String memoText, String statementPrefix);


    /**
     * Checks if Transactions that meet the specified search criteria exist.
     *
     * @param accountId         Account ID.
     * @param transactionTypeId Transaction Type ID.
     * @return <code>true</code> if at least one Transaction of the given type for the given account exists.
     */
    @WebMethod(exclude = true)
    boolean transactionExists(String accountId, String transactionTypeId);

    /**
     * Checks if Transactions that meet the specified search criteria exist.
     *
     * @param accountId         Account ID.
     * @param transactionTypeId Transaction Type ID.
     * @param effectiveDateFrom Transaction Effective Date beginning range (inclusive).
     * @param effectiveDateTo   Transaction Effective Date end range (inclusive).
     * @return <code>true</code> if at least one Transaction of the given type for the given account
     *         with the Effective Dates that fall into the specified range exists.
     */
    @WebMethod(exclude = true)
    boolean transactionExists(String accountId, String transactionTypeId, Date effectiveDateFrom, Date effectiveDateTo);

    /**
     * Checks if Transactions that meet the specified search criteria exist.
     *
     * @param accountId         Account ID.
     * @param transactionTypeId Transaction Type ID.
     * @param amountFrom        Amount of a Transaction begging of a search range.
     * @param amountTo          Amount of a Transaction end of a search range.
     * @return <code>true</code> if at least one Transaction of the given type, given amount for the given account exists.
     */
    @WebMethod(exclude = true)
    boolean transactionExists(String accountId, String transactionTypeId, BigDecimal amountFrom, BigDecimal amountTo);

    /**
     * Checks if Transactions that meet the specified search criteria exist.
     *
     * @param accountId         Account ID.
     * @param transactionTypeId Transaction Type ID.
     * @param amountFrom        Amount of a Transaction begging of a search range.
     * @param amountTo          Amount of a Transaction end of a search range.
     * @param effectiveDateFrom Transaction Effective Date beginning range (inclusive).
     * @param effectiveDateTo   Transaction Effective Date end range (inclusive).
     * @return <code>true</code> if at least one Transaction of the given type, given amount for the given account
     *         with the Effective Dates that fall into the specified range exists.
     */
    boolean transactionExists(String accountId, String transactionTypeId, BigDecimal amountFrom, BigDecimal amountTo, Date effectiveDateFrom, Date effectiveDateTo);

    /**
     * Creates and persist a new instance of CreditPermission class.
     *
     * @param creditTypeId       Credit type ID
     * @param allowableDebitType Allowable debit type mask (can be a regular expression)
     * @param priority           Priority value
     * @return new persistent CreditPermission instance
     */
    CreditPermission createCreditPermission(TransactionTypeId creditTypeId, String allowableDebitType, int priority);

    /**
     * Persists CreditPermission instance in the persistent store.
     *
     * @param creditPermission CreditPermission instance
     * @return CreditPermission ID
     */
    Long persistCreditPermission(CreditPermission creditPermission);

    /**
     * Removes CreditPermission instance from the persistent store by ID.
     *
     * @param creditPermissionId CreditPermission ID
     * @return true, if the credit permission has been deleted, false - otherwise
     */
    boolean deleteCreditPermission(Long creditPermissionId);

    /**
     * Returns a list of credit permissions for the given transaction type.
     *
     * @param transactionTypeId Transaction Type ID
     * @return list of CreditPermission instances
     */
    @WebMethod(exclude = true)
    List<CreditPermission> getCreditPermissions(TransactionTypeId transactionTypeId);

    /**
     * Returns a list of credit permissions for the given transaction type and priority range.
     *
     * @param transactionTypeId Transaction Type ID
     * @param priorityFrom      lower priority
     * @param priorityTo        upper priority
     * @return list of CreditPermission instances
     */
    List<CreditPermission> getCreditPermissions(TransactionTypeId transactionTypeId,
                                                Integer priorityFrom, Integer priorityTo);

    /**
     * Returns the unallocated amount of all transactions in the list for the given transaction type and
     * debit type mask
     *
     * @param transactions    a list of transactions
     * @param transactionType transaction type <code>TransactionTypeValue</code>(Charge, Payment or Deferment)
     * @param restricted      if "true" credit permissions with a mask != ".*" will be summarized,
     *                        otherwise the mask ".*" will be used
     * @return BigDecimal of the sum of the Restricted Payment amount
     */
    @WebMethod(exclude = true)
    BigDecimal getUnallocatedAmount(List<Transaction> transactions, TransactionTypeValue transactionType, boolean restricted);

    /**
     * Parses the cancellationRule and returns a map of dates and pairs of cancellation rule types with
     * the corresponding amount.
     *
     * @param cancellationRule The cancellation rule. The String value to be normalized:
     *                         DAYS values have to be replaced with DATE.
     * @return a map of dates and pairs of cancellation rule types and amount
     */
    @WebMethod(exclude = true)
    TreeMap<Date, List<Pair<CancellationRuleType, BigDecimal>>> parseCancellationRule(String cancellationRule);

    /**
     * Takes the cancellationRule and using the baseDate calculates the
     * appropriate dates to be stored in the actual transaction version of the cancellation rule.
     *
     * @param cancellationRule the cancellation rule
     * @param baseDate         the base date that is used for calculation by "DATE" values in the rule sentence
     * @return the modified cancellation rule with the updated dates
     */
    String calculateCancellationRule(String cancellationRule, Date baseDate);

    /**
     * Checks if the given cancellation rule is legal.
     *
     * @param cancellationRule the cancellation rule
     * @return true if the rule is valid or null, false - otherwise
     */
    boolean isCancellationRuleValid(String cancellationRule);


    /**
     * Using the cancellationRule, calculates the appropriate amount that can be cancelled
     * from a charge based on the current date.
     *
     * @param chargeId Charge ID
     * @return Cancellation amount
     */
    @WebMethod(exclude = true)
    BigDecimal getCancellationAmount(Long chargeId);

    /**
     * Using the cancellationRule, calculates the appropriate amount that can be cancelled
     * from a charge based on the given cancellation date.
     *
     * @param chargeId         Charge ID
     * @param cancellationDate Cancellation date
     * @return Cancellation amount
     */
    BigDecimal getCancellationAmount(Long chargeId, Date cancellationDate);


    /**
     * Cancels a charge by ID.
     *
     * @param chargeId Charge ID
     * @param memoText Memo text
     * @return Charge instance
     */
    Charge cancelCharge(Long chargeId, String memoText);

    /**
     * Creates a deferment using createTransaction() and the default contest payment type as the transaction type.
     * After that it creates a locked allocation between the deferment and charge.
     *
     * @param chargeId       Charge ID
     * @param expirationDate Deferment expiration date
     * @param memoText       Memo text
     * @return Charge instance
     */
    Charge contestCharge(Long chargeId, Date expirationDate, String memoText);

    /**
     * Performs the charge reversal and sets the status of the reversing transaction to DISCOUNTING.
     *
     * @param chargeId          Charge ID
     * @param transactionTypeId Transaction Type ID of the reversing transaction
     * @param amount            Charge amount
     * @param memoText          Memo text
     * @param statementPrefix   Transaction statement prefix
     * @return a discounted Charge instance
     */
    Charge discountCharge(Long chargeId, String transactionTypeId, BigDecimal amount, String memoText, String statementPrefix);

    /**
     * Bounces a payment by ID.
     *
     * @param paymentId Payment ID
     * @param memoText  Memo text
     * @return Payment instance
     */
    Payment bouncePayment(Long paymentId, String memoText);

    /**
     * Adds the list of tags to the transaction specified by Transaction ID.
     *
     * @param transactionId Transaction ID
     * @param tags          a list of tags
     * @return the updated transaction instance with tags
     */
    Transaction addTagsToTransaction(Long transactionId, List<Tag> tags);

    /**
     * Adds the list of tags to the transaction type specified by TransactionType ID.
     *
     * @param typeId Transaction ID
     * @param tags   a list of tags
     * @return the updated transaction type instance with tags
     */
    TransactionType addTagsToTransactionType(TransactionTypeId typeId, List<Tag> tags);

    /**
     * Removes the specified list of tags from the transaction type specified by TransactionType ID.
     *
     * @param typeId Transaction ID
     * @return the updated transaction type instance with tags
     */
    TransactionType removeTagsFromTransactionType(TransactionTypeId typeId, Long... tagIds);

    /**
     * Removes the specified list of tags from the transaction specified by Transaction ID.
     *
     * @param transactionId Transaction ID
     * @param tagIds        IDs of tags being removed
     * @return the updated transaction instance
     */
    Transaction removeTagsFromTransaction(Long transactionId, Long... tagIds);

    /**
     * Returns the number of all transactions associated with the given Transaction Type ID (code and sub-code)
     *
     * @param transactionTypeId TransactionType ID
     * @return the number of transactions
     */
    long getNumberOfTransactions(TransactionTypeId transactionTypeId);


    /**
     * This method is used to progressively undo allocations on a transaction to find an unallocated balance.
     *
     * @param transactionId Transaction ID
     * @param amount        Required balance
     * @return set of AllocationReversalType values
     */
    Set<AllocationReversalType> reverseAllocations(Long transactionId, BigDecimal amount);

    /**
     * Retrieves the list of Transaction objects from the persistent store by GL transaction ID.
     *
     * @param glTransactionId GL Transaction ID
     * @return list ofTransaction instances
     */
    List<Transaction> getTransactionsByGlTransactionId(Long glTransactionId);

    /**
     * Returns a list of potential refunds (payments) for specified Account IDs.
     *
     * @param accountIds Account IDs
     * @return list of Payment instances
     */
    @WebMethod(exclude = true)
    List<Payment> getPotentialRefunds(Set<String> accountIds);

    /**
     * Returns a list of potential refunds (payments) for specified Account IDs and date range.
     *
     * @param accountIds Account IDs
     * @param startDate  Start date
     * @param endDate    End date
     * @return list of Payment instances
     */
    @WebMethod(exclude = true)
    List<Payment> getPotentialRefunds(Set<String> accountIds, Date startDate, Date endDate);

    /**
     * Returns a list of potential refunds (payments) for specified Account IDs, date range and Tag IDs.
     *
     * @param accountIds Account IDs
     * @param startDate  Start date
     * @param endDate    End date
     * @param tagIds     Tag IDs
     * @return List of Payment instances
     */
    List<Payment> getPotentialRefunds(Set<String> accountIds, Date startDate, Date endDate, Set<Long> tagIds);

}
