package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.annotation.Url;
import com.sigmasys.kuali.ksa.model.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.math.BigDecimal;
import java.util.List;

/**
 * Payment service provides KSA payment functionality which is partially rule-based.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Url(PaymentService.SERVICE_URL)
@WebService(serviceName = PaymentService.SERVICE_NAME, portName = PaymentService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
public interface PaymentService {

    String SERVICE_URL = "payment.webservice";
    String SERVICE_NAME = "PaymentService";
    String PORT_NAME = SERVICE_NAME + "Port";


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
    List<GlTransaction> applyPayments(List<Transaction> transactions, BigDecimal maxAmount, boolean isQueued);

    /**
     * An overridden version of applyPayments() that takes a list of transactions and isQueued parameter as arguments
     *
     * @param transactions List of transactions
     * @param isQueued     Indicates whether the generated GL transactions should be put in a queue or not
     * @return List of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> applyPayments(List<Transaction> transactions, boolean isQueued);

    /**
     * An overridden version of applyPayments() that takes a list of transactions and maxAmount as arguments.
     *
     * @param transactions List of transactions
     * @param maxAmount    Maximum amount allowed
     * @return List of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> applyPayments(List<Transaction> transactions, BigDecimal maxAmount);

    /**
     * An overridden version of applyPayments() that takes a list of transactions as an argument
     *
     * @param transactions List of transactions
     * @return List of generated GL transactions
     */
    @WebMethod(exclude = true)
    List<GlTransaction> applyPayments(List<Transaction> transactions);

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
    List<GlTransaction> paymentApplication(String userId);

}
