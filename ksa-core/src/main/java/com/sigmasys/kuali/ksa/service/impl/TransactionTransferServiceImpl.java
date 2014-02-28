package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.exception.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.*;
import com.sigmasys.kuali.ksa.util.TransactionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * Transaction Transfer Service implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("transactionTransferService")
@Transactional(readOnly = true)
@WebService(serviceName = TransactionTransferService.SERVICE_NAME, portName = TransactionTransferService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
@SuppressWarnings("unchecked")
public class TransactionTransferServiceImpl extends GenericPersistenceService implements TransactionTransferService {

    private static final Log logger = LogFactory.getLog(TransactionTransferServiceImpl.class);

    private static final String TRANSACTION_TRANSFER_SELECT = " select t from TransactionTransfer t " +
            " left outer join fetch t.sourceTransaction " +
            " left outer join fetch t.destTransaction " +
            " left outer join fetch t.offsetTransaction " +
            " left outer join fetch t.sourceReciprocalTransaction " +
            " left outer join fetch t.destReciprocalTransaction " +
            " left outer join fetch t.transferType ";

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private GeneralLedgerService generalLedgerService;

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private AccountService accountService;


    /**
     * Generates Transfer Group ID
     *
     * @return UUID String value
     */
    @Override
    public String generateTransferGroupId() {
        return UUID.randomUUID().toString();
    }


    /**
     * Retrieves TransferType instance from the persistent store by ID.
     *
     * @param transferTypeId TransferType ID
     * @return TransferType instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSFER_TYPE)
    public TransferType getTransferType(Long transferTypeId) {

        Query query = em.createQuery("select t from TransferType t " +
                " left outer join fetch t.generalLedgerType " +
                " where t.id = :id");

        query.setParameter("id", transferTypeId);

        List<TransferType> transferTypes = query.getResultList();
        return CollectionUtils.isNotEmpty(transferTypes) ? transferTypes.get(0) : null;
    }


    /**
     * Creates a new instance of TransferType and persists it in the persistent store.
     *
     * @param glTypeId    General Ledger Type ID
     * @param code        TransferType code
     * @param name        TransferType name
     * @param description TransferType description
     * @return TransferType instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_TRANSFER_TYPE)
    public TransferType createTransferType(Long glTypeId, String code, String name, String description) {

        if (StringUtils.isBlank(code)) {
            String errMsg = "TransferType code is required";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        GeneralLedgerType glType = generalLedgerService.getGeneralLedgerType(glTypeId);
        if (glType == null) {
            String errMsg = "General Ledger Type with ID = " + glTypeId + " does not exist";
            logger.error(errMsg);
            throw new InvalidGeneralLedgerTypeException(errMsg);
        }

        // Cresting a new instance of TransferType
        TransferType transferType = new TransferType();

        transferType.setGeneralLedgerType(glType);
        transferType.setCode(code);
        transferType.setName(name);
        transferType.setDescription(description);

        // Persisting the new instance of TransferType
        auditableEntityService.persistAuditableEntity(transferType);

        return transferType;
    }

    /**
     * Persists TransferType instance in the persistent store.
     *
     * @param transferType TransferType instance
     * @return TransferType ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_TRANSFER_TYPE)
    public Long persistTransferType(TransferType transferType) {
        return auditableEntityService.persistAuditableEntity(transferType);
    }

    /**
     * Removes TransferType instance from the persistent store by ID.
     *
     * @param transferTypeId TransferType ID
     * @return true, if the transfer type has successfully been deleted, false - otherwise
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_TRANSFER_TYPE)
    public boolean deleteTransferType(Long transferTypeId) {
        return auditableEntityService.deleteAuditableEntity(transferTypeId, TransferType.class);
    }

    /**
     * Returns all transfer types existing in the persistent store.
     *
     * @return list of TransferType instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSFER_TYPE)
    public List<TransferType> getTransferTypes() {

        Query query = em.createQuery("select t from TransferType t " +
                " left outer join fetch t.generalLedgerType " +
                " order by t.id desc");

        return query.getResultList();
    }


    /**
     * This method transfers responsibility for a transaction from one account to another and
     * does this by issuing a negative transaction on the original account to wipe out its value
     * which is done via the reverseTransaction() method. Then it creates a new transaction on the new account.
     * Amount is passed, allowing only part of a transaction to be moved to a new account.
     * For example, a sponsor may agree to pay 80% of a student’s tuition charges,
     * therefore only 80% of the tuition charge would be transferred.
     * <p/>
     *
     * @param transactionId       Transaction ID
     * @param transactionTypeId   TransactionType ID
     * @param transferTypeId      TransferType ID
     * @param accountId           Account ID
     * @param amount              Transaction amount being transferred
     * @param effectiveDate       Effective Date
     * @param recognitionDate     Recognition Date
     * @param memoText            Memo text
     * @param statementPrefix     Statement prefix
     * @param transactionTypeMask Transaction type mask
     * @return TransactionTransfer instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_TRANSACTION_TRANSFER)
    public TransactionTransfer transferTransaction(Long transactionId,
                                                   String transactionTypeId,
                                                   Long transferTypeId,
                                                   String accountId,
                                                   BigDecimal amount,
                                                   Date effectiveDate,
                                                   Date recognitionDate,
                                                   String memoText,
                                                   String statementPrefix,
                                                   String transactionTypeMask) {

        Account account = accountService.getFullAccount(accountId);
        if (account == null) {
            String errMsg = "Account '" + transactionId + "' does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        if (amount == null) {
            amount = transaction.getAmount();
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            String errMsg = "Transaction amount cannot be null or 0";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        if (effectiveDate == null) {
            effectiveDate = transaction.getEffectiveDate();
        }

        if (recognitionDate == null) {
            recognitionDate = transaction.getRecognitionDate();
        }

        TransferType transferType = getTransferType(transferTypeId);
        if (transferType == null) {
            String errMsg = "Transfer type with ID = " + transactionTypeId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        TransactionType transactionType;
        if (transactionTypeId != null) {
            transactionType = transactionService.getTransactionType(transactionTypeId, recognitionDate);
            if (!(transactionType instanceof DebitType && transaction.getTransactionType() instanceof DebitType) &&
                    !(transactionType instanceof CreditType && transaction.getTransactionType() instanceof CreditType)) {
                String errMsg = "Transactions must have the same type, both credit or debit";
                logger.error(errMsg);
                throw new InvalidTransactionTypeException(errMsg);
            }
        } else {
            transactionType = transaction.getTransactionType();
        }

        // Clearing all non-locked allocations
        transactionService.removeAllocations(transaction.getId());

        // Updating transaction from the persistent store
        transaction = transactionService.getTransaction(transactionId);

        // Checking if the amount is less or equal to the transaction unallocated amount
        if (amount.compareTo(transaction.getUnallocatedAmount()) > 0) {
            String errMsg = "Transferred amount should be less or equal to transaction unallocated amount";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        // Checking if the new transaction is allowed
        if (!transactionService.isTransactionAllowed(accountId, transactionType.getId().getId(), effectiveDate)) {
            String errMsg = "Transaction is not allowed for account = " + accountId +
                    " and effective date = " + effectiveDate;
            logger.error(errMsg);
            throw new TransactionNotAllowedException(errMsg);
        }

        // Checking if the existing transaction is allowed
        if (!transactionService.isTransactionAllowed(transaction.getAccountId(),
                transaction.getTransactionType().getId().getId(), transaction.getEffectiveDate())) {
            String errMsg = "Transaction is not allowed for account = " + transaction.getAccountId() +
                    " and effective date = " + transaction.getEffectiveDate();
            logger.error(errMsg);
            throw new TransactionNotAllowedException(errMsg);
        }

        // Creating a transaction reversal with TRANSFERRING status
        Transaction reverseTransaction = transactionService.reverseTransaction(transactionId,
                transactionType.getId().getId(), memoText, amount, statementPrefix, TransactionStatus.TRANSFERRING);

        // Creating a new transaction
        Transaction newTransaction = transactionService.createTransaction(transactionTypeId, accountId, effectiveDate,
                recognitionDate, amount);

        newTransaction.setGeneralLedgerType(transferType.getGeneralLedgerType());
        newTransaction.setOriginationDate(transaction.getOriginationDate());
        newTransaction.setDocument(transaction.getDocument());
        newTransaction.setRollup(transaction.getRollup());

        Set<Tag> transactionTags = transaction.getTags();

        if (CollectionUtils.isNotEmpty(transactionTags)) {
            newTransaction.setTags(new HashSet<Tag>(transactionTags));
        }

        Account studentAccount = transaction.getAccount();

        String prefix = studentAccount.getDefaultPersonName().getDisplayValue() + " (" + studentAccount.getId() + "): ";

        newTransaction.setStatementText(prefix + transaction.getStatementText());

        // If both transactions are payments -> copy some additional properties from one to another
        if (newTransaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT &&
                transaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT) {

            Payment newPayment = (Payment) newTransaction;
            Payment payment = (Payment) transaction;

            newPayment.setRefundable(payment.isRefundable());
            newPayment.setRefundRule(payment.getRefundRule());
            newPayment.setClearDate(payment.getClearDate());
        }

        // Creating a new TransactionTransfer object and persisting it
        TransactionTransfer transactionTransfer = new TransactionTransfer();
        transactionTransfer.setCreationDate(new Date());
        transactionTransfer.setTransferType(transferType);
        transactionTransfer.setTransferAmount(amount);
        transactionTransfer.setTransactionTypeMask(transactionTypeMask);
        transactionTransfer.setSourceTransaction(transaction);
        transactionTransfer.setOffsetTransaction(reverseTransaction);
        transactionTransfer.setDestTransaction(newTransaction);
        transactionTransfer.setReversalStatus(ReversalStatus.NOT_REVERSED);

        persistEntity(transactionTransfer);

        return transactionTransfer;
    }

    /**
     * Persists TransactionTransfer in the persistent store
     *
     * @param transactionTransfer TransactionTransfer instance
     * @return TransactionTransfer ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_TRANSACTION_TRANSFER)
    public Long persistTransactionTransfer(TransactionTransfer transactionTransfer) {
        return persistEntity(transactionTransfer);
    }

    /**
     * Returns all transaction transfer objects for the given group ID.
     *
     * @param transferGroupId Transfer Group ID
     * @return list of TransactionTransfer instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION_TRANSFER)
    public List<TransactionTransfer> getTransactionTransfersByGroupId(String transferGroupId) {

        if (StringUtils.isBlank(transferGroupId)) {
            String errMsg = "Transfer group ID must not be blank";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Query query = em.createQuery(TRANSACTION_TRANSFER_SELECT + " where t.groupId = :groupId order by t.id desc");

        query.setParameter("groupId", transferGroupId);

        return query.getResultList();
    }

    /**
     * Retrieves TransactionTransfer instance by ID from the persistent store.
     *
     * @param transactionTransferId Transaction Transfer ID
     * @return TransactionTransfer instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION_TRANSFER)
    public TransactionTransfer getTransactionTransfer(Long transactionTransferId) {

        Query query = em.createQuery(TRANSACTION_TRANSFER_SELECT + " where t.id = :id");

        query.setParameter("id", transactionTransferId);

        List<TransactionTransfer> transfers = query.getResultList();

        return CollectionUtils.isNotEmpty(transfers) ? transfers.get(0) : null;
    }

    /**
     * Sets the transfer group ID for each transaction transfer specified by the list of IDs.
     *
     * @param transferGroupId        Transfer group ID
     * @param transactionTransferIds list of transaction transfer IDs
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_TRANSACTION_TRANSFER)
    public void setTransferGroup(String transferGroupId, List<Long> transactionTransferIds) {

        if (CollectionUtils.isEmpty(transactionTransferIds)) {
            String errMsg = "List of transaction transfer IDs must not be empty";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Query query = em.createQuery("update TransactionTransfer set groupId = :groupId where id in (:ids)");

        query.setParameter("groupId", transferGroupId);
        query.setParameter("ids", transactionTransferIds);

        query.executeUpdate();
    }

    /**
     * Sets the rollup specified by ID for the destination transaction of all transaction transfers from the specified group.
     *
     * @param transferGroupId Transfer Group ID
     * @param rollupId        Rollup ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_TRANSACTION_TRANSFER)
    public void setRollupForTransferGroup(String transferGroupId, Long rollupId) {

        Rollup rollup = auditableEntityService.getAuditableEntity(rollupId, Rollup.class);
        if (rollup == null) {
            String errMsg = "Rollup with ID = " + rollupId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        List<TransactionTransfer> transactionTransfers = getTransactionTransfersByGroupId(transferGroupId);

        if (CollectionUtils.isNotEmpty(transactionTransfers)) {
            for (TransactionTransfer transactionTransfer : transactionTransfers) {
                Transaction destTransaction = transactionTransfer.getDestTransaction();
                if (destTransaction != null) {
                    destTransaction.setRollup(rollup);
                }
            }
        }

    }

    protected TransactionTransfer reverseTransactionTransfer(TransactionTransfer transactionTransfer, String memoText,
                                                             BigDecimal reversalAmount) {

        if (reversalAmount == null || BigDecimal.ZERO.compareTo(reversalAmount) == 0) {
            String errMsg = "Reversal amount cannot be null or 0";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (transactionTransfer.getReversalStatus() != ReversalStatus.NOT_REVERSED) {
            String errMsg = "TransactionTransfer (ID=" + transactionTransfer.getId() + ") has already been reversed";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        Transaction offsetTransaction = transactionTransfer.getOffsetTransaction();
        if (offsetTransaction == null) {
            String errMsg = "TransactionTransfer (ID=" + transactionTransfer.getId() + ") requires Offset Transaction";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        Transaction destTransaction = transactionTransfer.getDestTransaction();

        BigDecimal unallocatedAmount = destTransaction.getUnallocatedAmount().setScale(2, RoundingMode.HALF_DOWN);

        if (reversalAmount.setScale(2, RoundingMode.HALF_DOWN).compareTo(unallocatedAmount) > 0) {

            // Removing all regular allocations which the transaction is involved in
            transactionService.removeAllAllocations(destTransaction.getId());

            // Updating the transaction instance
            destTransaction = transactionService.getTransaction(destTransaction.getId());

            // Checking the unallocated amount again
            unallocatedAmount = destTransaction.getUnallocatedAmount().setScale(2, RoundingMode.HALF_DOWN);

            if (reversalAmount.setScale(2, RoundingMode.HALF_DOWN).compareTo(unallocatedAmount) > 0) {
                logger.info("Transaction ID = " + destTransaction.getId() + ", reversal amount = " +
                        TransactionUtils.formatAmount(reversalAmount));
                logger.info("Transaction ID = " + destTransaction.getId() + ", unallocated amount = " +
                        TransactionUtils.formatAmount(unallocatedAmount));
                String errMsg = "Reversal amount cannot be greater than transaction unallocated amount";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            transactionTransfer.setDestTransaction(destTransaction);
        }

        // Creating a new reversal for the destination transaction
        Transaction destReciprocalTransaction =
                transactionService.reverseTransaction(destTransaction.getId(), memoText, reversalAmount, null);

        // Creating a new reversal for the offset transaction
        Transaction sourceReciprocalTransaction =
                transactionService.reverseTransaction(offsetTransaction.getId(), memoText, reversalAmount, null);

        transactionTransfer.setSourceReciprocalTransaction(sourceReciprocalTransaction);
        transactionTransfer.setDestReciprocalTransaction(destReciprocalTransaction);
        transactionTransfer.setReversalAmount(reversalAmount);

        boolean isPartialAmount = (reversalAmount.compareTo(transactionTransfer.getTransferAmount()) < 0);

        // Setting the reversal status
        transactionTransfer.setReversalStatus(isPartialAmount ? ReversalStatus.PARTIALLY_REVERSED : ReversalStatus.REVERSED);

        return transactionTransfer;
    }

    /**
     * This method is used to reverse a previously made transaction transfer. This allows the system to dynamically
     * restore the charges and credits to an originating account without the user having to look up those values.
     *
     * @param transactionTransferId TransactionTransfer ID
     * @param memoText              Memo text
     * @param reversalAmount        Reversal amount
     * @return TransactionTransfer instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REVERSE_TRANSACTION_TRANSFER)
    public TransactionTransfer reverseTransactionTransfer(Long transactionTransferId, String memoText, BigDecimal reversalAmount) {

        TransactionTransfer transactionTransfer = getTransactionTransfer(transactionTransferId);
        if (transactionTransfer == null) {
            String errMsg = "TransactionTransfer does not exist with ID = " + transactionTransferId;
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        return reverseTransactionTransfer(transactionTransfer, memoText, reversalAmount);
    }


    /**
     * This method is used to reverse a previously made transaction transfer in the original transfer amount.
     * This allows the system to dynamically  restore the charges and credits to an originating account without
     * the user having to look up those values.
     *
     * @param transactionTransferId TransactionTransfer ID
     * @param memoText              Memo text
     * @return TransactionTransfer instance
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REVERSE_TRANSACTION_TRANSFER)
    public TransactionTransfer reverseTransactionTransfer(Long transactionTransferId, String memoText) {

        TransactionTransfer transactionTransfer = getTransactionTransfer(transactionTransferId);
        if (transactionTransfer == null) {
            String errMsg = "TransactionTransfer does not exist with ID = " + transactionTransferId;
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        return reverseTransactionTransfer(transactionTransfer, memoText, transactionTransfer.getTransferAmount());
    }

    /**
     * Reverses transaction transfers for the entire group of transfers specified by ID.
     *
     * @param transferGroupId        Transfer Group ID
     * @param memoText               Memo text
     * @param allowLockedAllocations Indicates whether the locked allocations are allowed,
     *                               if there any locked allocations and false throws an unchecked exception
     * @return list of the transaction transfers from the group
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REVERSE_TRANSACTION_TRANSFER)
    public List<TransactionTransfer> reverseTransferGroup(String transferGroupId, String memoText, boolean allowLockedAllocations) {

        List<TransactionTransfer> transactionTransfers = getTransactionTransfersByGroupId(transferGroupId);

        if (CollectionUtils.isNotEmpty(transactionTransfers)) {

            // Generating a new transfer group ID
            String reversalGroupId = generateTransferGroupId();

            for (TransactionTransfer transactionTransfer : transactionTransfers) {

                Transaction destTransaction = transactionTransfer.getDestTransaction();

                BigDecimal lockedAllocatedAmount = destTransaction.getLockedAllocatedAmount();
                if (lockedAllocatedAmount == null) {
                    lockedAllocatedAmount = BigDecimal.ZERO;
                }

                if (!allowLockedAllocations) {
                    if (lockedAllocatedAmount.compareTo(BigDecimal.ZERO) != 0) {
                        String errMsg = "Cannot reverse transaction group if one or more destination transactions " +
                                " have any allocated amount, destination transaction ID = " + destTransaction.getId();
                        logger.error(errMsg);
                        throw new IllegalStateException(errMsg);
                    }
                }

                BigDecimal amount = destTransaction.getAmount();
                if (amount == null) {
                    amount = BigDecimal.ZERO;
                }

                BigDecimal reversalAmount = amount.subtract(lockedAllocatedAmount);

                // Creating a transaction transfer reversal
                reverseTransactionTransfer(transactionTransfer, memoText, reversalAmount);

                // Setting the new reversal group ID
                transactionTransfer.setGroupId(reversalGroupId);
            }

            return transactionTransfers;
        }

        return Collections.emptyList();
    }

}