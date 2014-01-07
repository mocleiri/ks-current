package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.annotation.Url;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.TransactionTransfer;
import com.sigmasys.kuali.ksa.model.TransferType;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The transfer transaction service is a set of methods that take care of transferring transactions
 * from one account to another, or, in the case of payment billing, within the account,
 * breaking a group of transactions into payments with different due dates.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Url(TransactionTransferService.SERVICE_URL)
@WebService(serviceName = TransactionTransferService.SERVICE_NAME, portName = TransactionTransferService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
public interface TransactionTransferService {

    String SERVICE_URL = "transactionTransfer.webservice";
    String SERVICE_NAME = "TransactionTransferService";
    String PORT_NAME = SERVICE_NAME + "Port";


    /**
     * Generates Transfer Group ID
     *
     * @return UUID String value
     */
    String generateTransferGroupId();

    /**
     * Retrieves TransferType instance from the persistent store by ID.
     *
     * @param transferTypeId TransferType ID
     * @return TransferType instance
     */
    TransferType getTransferType(Long transferTypeId);

    /**
     * Creates a new instance of TransferType and persists it in the persistent store.
     *
     * @param glTypeId    General Ledger Type ID
     * @param code        TransferType code
     * @param name        TransferType name
     * @param description TransferType description
     * @return TransferType instance
     */
    TransferType createTransferType(Long glTypeId, String code, String name, String description);

    /**
     * Persists TransferType instance in the persistent store.
     *
     * @param transferType TransferType instance
     * @return TransferType ID
     */
    Long persistTransferType(TransferType transferType);

    /**
     * Removes TransferType instance from the persistent store by ID.
     *
     * @param transferTypeId TransferType ID
     * @return true, if the transfer type has successfully been deleted, false - otherwise
     */
    boolean deleteTransferType(Long transferTypeId);

    /**
     * Returns all transfer types existing in the persistent store.
     *
     * @return list of TransferType instances
     */
    List<TransferType> getTransferTypes();


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
    TransactionTransfer transferTransaction(Long transactionId,
                                            String transactionTypeId,
                                            Long transferTypeId,
                                            String accountId,
                                            BigDecimal amount,
                                            Date effectiveDate,
                                            Date recognitionDate,
                                            String memoText,
                                            String statementPrefix,
                                            String transactionTypeMask);

    /**
     * Persists TransactionTransfer in the persistent store
     *
     * @param transactionTransfer TransactionTransfer instance
     * @return TransactionTransfer ID
     */
    Long persistTransactionTransfer(TransactionTransfer transactionTransfer);

    /**
     * Returns all transaction transfer objects for the given group ID.
     *
     * @param transferGroupId Transfer Group ID
     * @return list of TransactionTransfer instances
     */
    List<TransactionTransfer> getTransactionTransfersByGroupId(String transferGroupId);

    /**
     * Retrieves TransactionTransfer instance by ID from the persistent store.
     *
     * @param transactionTransferId Transaction Transfer ID
     * @return TransactionTransfer instance
     */
    TransactionTransfer getTransactionTransfer(Long transactionTransferId);

    /**
     * Sets the transfer group ID for each transaction transfer specified by the list of IDs.
     *
     * @param transferGroupId        Transfer group ID
     * @param transactionTransferIds list of transaction transfer IDs
     */
    void setTransferGroup(String transferGroupId, List<Long> transactionTransferIds);

    /**
     * Sets the rollup specified by ID for the destination transaction of all transaction transfers from the specified group.
     *
     * @param transferGroupId Transfer Group ID
     * @param rollupId        Rollup ID
     */
    void setRollupForTransferGroup(String transferGroupId, Long rollupId);

    /**
     * This method is used to reverse a previously made transaction transfer. This allows the system to dynamically
     * restore the charges and credits to an originating account without the user having to look up those values.
     *
     * @param transactionTransferId TransactionTransfer ID
     * @param memoText              Memo text
     * @param reversalAmount        Reversal amount
     * @return TransactionTransfer instance
     */
    TransactionTransfer reverseTransactionTransfer(Long transactionTransferId, String memoText, BigDecimal reversalAmount);

    /**
     * This method is used to reverse a previously made transaction transfer in the original transfer amount.
     * This allows the system to dynamically  restore the charges and credits to an originating account without
     * the user having to look up those values.
     *
     * @param transactionTransferId TransactionTransfer ID
     * @param memoText              Memo text
     * @return TransactionTransfer instance
     */
    @WebMethod(exclude = true)
    TransactionTransfer reverseTransactionTransfer(Long transactionTransferId, String memoText);

    /**
     * Reverses transaction transfers for the entire group of transfers specified by ID.
     *
     * @param transferGroupId        Transfer Group ID
     * @param memoText               Memo text
     * @param allowLockedAllocations Indicates whether the locked allocations are allowed,
     *                               if there any locked allocations and false throws an unchecked exception
     * @return list of the transaction transfers from the group
     */
    List<TransactionTransfer> reverseTransferGroup(String transferGroupId, String memoText, boolean allowLockedAllocations);

}
