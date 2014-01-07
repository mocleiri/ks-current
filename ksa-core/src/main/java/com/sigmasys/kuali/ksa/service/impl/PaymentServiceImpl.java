package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.exception.UserNotFoundException;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.*;
import com.sigmasys.kuali.ksa.service.brm.BrmContext;
import com.sigmasys.kuali.ksa.service.brm.BrmService;
import com.sigmasys.kuali.ksa.util.TransactionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.math.BigDecimal;
import java.util.*;


/**
 * Payment service JPA implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("paymentService")
@Transactional(timeout = 3600)
@WebService(serviceName = PaymentService.SERVICE_NAME, portName = PaymentService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
@SuppressWarnings("unchecked")
public class PaymentServiceImpl extends GenericPersistenceService implements PaymentService {

    private static final Log logger = LogFactory.getLog(PaymentServiceImpl.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BrmService brmService;

    @Autowired
    private AccountService accountService;


    /**
     * An overridden version of applyPayments() that takes a list of transactions and isQueued parameter as arguments
     *
     * @param transactions List of transactions
     * @param isQueued     Indicates whether the generated GL transactions should be put in a queue or not
     * @return List of generated GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.APPLY_PAYMENTS)
    public List<GlTransaction> applyPayments(List<Transaction> transactions, boolean isQueued) {
        return applyPayments(transactions, BigDecimal.valueOf(Long.MAX_VALUE), isQueued);
    }

    /**
     * An overridden version of applyPayments() that takes a list of transactions as an argument.
     *
     * @param transactions List of transactions
     * @return List of generated GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.APPLY_PAYMENTS)
    public List<GlTransaction> applyPayments(List<Transaction> transactions) {
        return applyPayments(transactions, true);
    }

    /**
     * An overridden version of applyPayments() that takes a list of transactions and maxAmount as arguments.
     *
     * @param transactions List of transactions
     * @param maxAmount    Maximum amount allowed
     * @return List of generated GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.APPLY_PAYMENTS)
    public List<GlTransaction> applyPayments(List<Transaction> transactions, BigDecimal maxAmount) {
        return applyPayments(transactions, maxAmount, true);
    }

    /**
     * This method takes the list that has been manipulated by the other payment application filters.
     * The system will then iterate through the list and apply payments, following simple payment logic.
     * Note that when a payment is encountered, the priority in the permissibleDebitArray will be used.
     * Where a charge is encountered, the next available payment will be applied (that is allowed to pay the charge).
     * Under normal circumstances, payments will be first in the list, unless the user wants to override this behavior.
     * If maximumAmount is passed, then the payment application will not allocate more than that amount.
     * If isQueued is set as false, the general ledger transactions that are created will be put into status of
     * Waiting, so they will not be transmitted to the general ledger until this status is set to Queued.
     * This will usually be done by passing the list of general ledger transactions to the
     * summarizeGeneralLedgerTransactions() method.
     *
     * @param transactions List of transactions
     * @param maxAmount    Maximum amount allowed
     * @param isQueued     Indicates whether the generated GL transactions should be put in a queue or not
     * @return List of generated GL transactions
     */
    @Override
    @PermissionsAllowed(Permission.APPLY_PAYMENTS)
    public List<GlTransaction> applyPayments(List<Transaction> transactions, BigDecimal maxAmount, boolean isQueued) {

        BigDecimal remainingAmount = maxAmount;

        List<GlTransaction> glTransactions = new LinkedList<GlTransaction>();

        List<CreditPermission> creditPermissions = Collections.emptyList();

        for (Transaction transaction : transactions) {

            if (transaction.getStatus() == TransactionStatus.ACTIVE) {

                Long transactionId = transaction.getId();

                TransactionTypeId transactionTypeId = transaction.getTransactionType().getId();

                // Check if it is Credit
                TransactionTypeValue transactionType = transaction.getTransactionTypeValue();

                boolean isCredit = (transactionType == TransactionTypeValue.PAYMENT
                        || transactionType == TransactionTypeValue.DEFERMENT);

                if (isCredit) {
                    // Assuming that credit permissions are sorted by priorities in descending order
                    creditPermissions = transactionService.getCreditPermissions(transactionTypeId);
                    if (creditPermissions == null) {
                        creditPermissions = Collections.emptyList();
                    }
                }

                int i = 0;

                do {

                    Integer currentPriority = null;
                    if (isCredit && i < creditPermissions.size()) {
                        // Getting the next priority from the sorted credit permissions
                        currentPriority = creditPermissions.get(i++).getPriority();
                    }

                    for (Transaction transaction1 : transactions) {

                        if (remainingAmount != null && remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                            // We have exceeded the limit and just need to return the result
                            return glTransactions;
                        }

                        Long transactionId1 = transaction1.getId();

                        // We have to exclude the same transaction
                        if (!transactionId.equals(transactionId1)) {

                            // Check if the transactions can pay one another
                            boolean canPay = false;

                            if (isCredit && currentPriority != null) {
                                // Credit
                                canPay = transactionService.canPay(transactionId, transactionId1, currentPriority);
                            } else if (!isCredit) {
                                // Debit
                                canPay = transactionService.canPay(transactionId, transactionId1);
                            }

                            if (canPay) {

                                BigDecimal amount = transaction.getUnallocatedAmount();

                                logger.info("First transaction ID = " + transactionId + ", unallocated amount = "
                                        + TransactionUtils.formatAmount(amount) + ", transaction amount = " +
                                        TransactionUtils.formatAmount(transaction.getAmount()));

                                BigDecimal amount1 = transaction1.getUnallocatedAmount();

                                logger.info("Second transaction ID = " + transactionId1 + ", unallocated amount = " +
                                        TransactionUtils.formatAmount(amount1) + ", transaction amount = " +
                                        TransactionUtils.formatAmount(transaction1.getAmount()));

                                if (amount.compareTo(BigDecimal.ZERO) > 0 && amount1.compareTo(BigDecimal.ZERO) > 0) {

                                    BigDecimal minAmount = (amount.compareTo(amount1) <= 0) ? amount : amount1;

                                    BigDecimal amountToAllocate;

                                    if (remainingAmount != null) {
                                        amountToAllocate = remainingAmount.compareTo(minAmount) > 0 ? minAmount : remainingAmount;
                                    } else {
                                        amountToAllocate = minAmount;
                                    }

                                    logger.info("Amount to allocate = " + TransactionUtils.formatAmount(amountToAllocate));

                                    CompositeAllocation allocation = transactionService.createAllocation(transaction,
                                            transaction1, amountToAllocate, isQueued, false, false);

                                    logger.info("Created allocation between " +
                                            transaction.getTransactionTypeValue() + "(" +
                                            transactionId + ") and " + transaction1.getTransactionTypeValue() +
                                            "(" + transactionId1 + ") transactions. Allocated amount = " +
                                            TransactionUtils.formatAmount(allocation.getAllocation().getAmount()));

                                    // Adding new GL transactions to the list
                                    glTransactions.addAll(allocation.getGlTransactions());

                                    if (remainingAmount != null) {
                                        remainingAmount = remainingAmount.subtract(amountToAllocate);
                                    }
                                }
                            }
                        }
                    }
                } while (isCredit && i < creditPermissions.size());
            }
        }

        return glTransactions;
    }

    /**
     * Calls the rules set for payment application.
     * Many other services can be used and will be useful to payment application,
     * including a direct creation of an allocation if needed.
     * However, the majority of use cases should be possible by filtering the lists as needed
     * and passing them to the automatic applyPayments() method.
     * This method will create a TransactionList object containing all the unallocated transactions (of any value)
     * for this accountId, ignoring all expired deferments (isExpired = true) and pass this object to the rules engine.
     *
     * @param userId Account ID
     * @return List of generated GL transactions
     */
    @Override
    @PermissionsAllowed(Permission.RUN_PAYMENT_APPLICATION)
    public List<GlTransaction> paymentApplication(String userId) {

        Account account = accountService.getFullAccount(userId);
        if (account == null) {
            String errMsg = "Account with ID = " + userId + " does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        // Calling BrmService with payment application rules
        BrmContext brmContext = new BrmContext();
        brmContext.setAccount(account);

        Map<String, Object> globalParams = new HashMap<String, Object>();
        globalParams.put("resultList", new LinkedList<GlTransaction>());

        brmContext.setGlobalVariables(globalParams);

        brmService.fireRules(Constants.BRM_PA_RULE_SET_NAME, brmContext);

        return (List<GlTransaction>) globalParams.get("resultList");
    }

}