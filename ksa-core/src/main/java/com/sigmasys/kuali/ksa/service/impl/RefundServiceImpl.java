package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.exception.*;
import com.sigmasys.kuali.ksa.jaxb.Ach;
import com.sigmasys.kuali.ksa.jaxb.BatchAch;
import com.sigmasys.kuali.ksa.jaxb.BatchCheck;
import com.sigmasys.kuali.ksa.jaxb.Check;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.*;
import com.sigmasys.kuali.ksa.util.CalendarUtils;
import com.sigmasys.kuali.ksa.util.JaxbUtils;
import com.sigmasys.kuali.ksa.util.RequestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.*;


/**
 * The refund service is the core of KSA-RM-RM. It handles the production of refunds from the system.
 * At its heart is the Refund object, which acts as a queue for refunds to be processed.
 * <p/>
 * The refund service will try to determine if a transaction can be paid as "cash"
 * (that is, without restrictions, not going to a third party, or being repaid in a specific way.)
 * If a refund (or part of a refund) can be paid as "cash" then it will be paid with the refund type as
 * defined in "refund.method" system property, which is a student-specific attribute, unless
 * the "override.refund.method" property is set to a different refund type, in which case, this takes precedence.
 * If neither are set, then the "default.refund.method" property is used.
 * <p/>
 * This allows a default method for all students (likely paper check) with students able to sign up for other
 * refund types (bank transfer, etc.) and it allows a counselor to override that choice in certain cases
 * (for example, they may require certain students to pick up their refund check at the office.)
 * <p/>
 *
 * @author Sergey Godunov
 * @author Michael Ivanov
 * @version 1.0
 */
@Service("refundService")
@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class RefundServiceImpl extends GenericPersistenceService implements RefundService {

    private static final Log logger = LogFactory.getLog(RefundServiceImpl.class);


    private static final String GET_REFUNDS_SELECT =
            "select r from Refund r " +
                    " left outer join fetch r.transaction t " +
                    " left outer join fetch r.refundType rtp " +
                    " left outer join fetch r.requestedBy rb " +
                    " left outer join fetch r.authorizedBy ab " +
                    " left outer join fetch r.refundTransaction rt " +
                    " left outer join fetch r.refundManifest rf ";


    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private AuditableEntityService auditableEntityService;


    protected Refund checkForRefund(Payment payment, Date refundRequestDate, Account refundRequestedBy, BigDecimal refundAmount) {

        // Get the refund rule:
        String refundRule = StringUtils.isNotBlank(payment.getRefundRule()) ?
                payment.getRefundRule() : ((CreditType) payment.getTransactionType()).getRefundRule();

        boolean processAsCash = StringUtils.isBlank(refundRule);

        if (isRefundRuleValid(refundRule)) {

            if (!processAsCash) {

                // Check the clearing period from the refund rule.
                // Add it to the effectiveDate of the transaction and compare to the current date:
                String clearingPeriodStr = StringUtils.substringBetween(refundRule, "(", ")");

                int clearingPeriod = Integer.parseInt(clearingPeriodStr);

                if (clearingPeriod != 0) {

                    Date effectiveDate = CalendarUtils.addCalendarDays(payment.getEffectiveDate(), clearingPeriod);

                    // Uncleared payments can only be credited to the same source:
                    if (effectiveDate.before(new Date())) {
                        processAsCash = true;
                    }
                }
            }

            payment.setStatus(TransactionStatus.REFUND_REQUESTED);

            // Create a new Refund instance
            return processAsCash ?
                    createCashRefund(payment, refundRequestDate, refundRequestedBy, refundAmount) :
                    createSourceRefund(payment, refundRequestDate, refundRequestedBy, refundRule, refundAmount);
        }

        String errMsg = "Refund rule '" + refundRule + "'' is invalid, Payment ID = " + payment.getId();
        logger.error(errMsg);
        throw new RefundFailedException(errMsg);
    }

    /**
     * Creates a list of unverified Refunds in the specified date range.
     * <p/>
     * Per Paul H's description (from "Process Diagrams").
     * It is expected that once the system has produced this refund list, an institution-specific set of rules
     * would then check for use cases specific to the school in question and ensure that the list is ready for
     * human validation. As an example, for an account refund (a refund from one KSA account to another KSA account)
     * the refundAttribute can be used to override the statement text of the refund. The rules engine could be used
     * to insert this text, based on institution-specific preferences.
     *
     * @param accountId The ID of an account for which to create a List of unverified Refunds.
     * @param dateFrom  Beginning of the Date range in which to search for refunds.
     * @param dateTo    End of the Date range in which to search for refunds.
     * @return List of unverified refunds.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REQUEST_REFUND)
    public List<Refund> checkForRefund(String accountId, Date dateFrom, Date dateTo) {

        // Get all payment transactions on account accountId, where effectiveDate > dateFrom and < dateTo:
        BigDecimal amountThreshold = BigDecimal.ZERO;

        String sql =
                "select p from Payment p where to_date(p.effectiveDate) between :dateFrom and :dateTo " +
                        " and p.refundable = true and to_date(p.clearDate) <= current_date() " +
                        " and p.statusCode = :statusCode " +
                        " and (p.amount - p.lockedAllocatedAmount - p.allocatedAmount <= :amountThreshold) " +
                        " and (p.refundRule is not null or p.transactionType.refundRule is not null)";

        Query query = em.createQuery(sql);

        query.setParameter("dateFrom", CalendarUtils.removeTime(dateFrom), TemporalType.DATE);
        query.setParameter("dateTo", CalendarUtils.removeTime(dateTo), TemporalType.DATE);
        query.setParameter("statusCode", TransactionStatus.ACTIVE.getId());
        query.setParameter("amountThreshold", amountThreshold);

        List<Payment> payments = query.getResultList();

        List<Refund> allRefunds = new ArrayList<Refund>(payments.size());

        Date refundRequestDate = new Date();

        Account refundRequestedBy = accountService.getOrCreateAccount(userSessionManager.getUserId());

        // Iterate through the Payments:
        for (Payment payment : payments) {
            BigDecimal refundAmount = payment.getUnallocatedAmount();
            allRefunds.add(checkForRefund(payment, refundRequestDate, refundRequestedBy, refundAmount));
        }

        return allRefunds;
    }

    /**
     * Creates on refund for a single payment.
     *
     * @param paymentId Payment ID
     * @return a new Refund instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REQUEST_REFUND)
    public Refund checkForRefund(Long paymentId) {

        Payment payment = transactionService.getPayment(paymentId);
        if (payment == null) {
            String errMsg = "Payment with ID = " + paymentId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        return checkForRefund(payment, payment.getUnallocatedAmount());
    }

    /**
     * Creates on refund for a single payment.
     *
     * @param paymentId    Payment ID
     * @param refundAmount Refund amount
     * @return a new Refund instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REQUEST_REFUND)
    public Refund checkForRefund(Long paymentId, BigDecimal refundAmount) {

        Payment payment = transactionService.getPayment(paymentId);
        if (payment == null) {
            String errMsg = "Payment with ID = " + paymentId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        return checkForRefund(payment, refundAmount);
    }

    protected Refund checkForRefund(Payment payment, BigDecimal refundAmount) {

        if (!payment.isRefundable()) {
            String errMsg = "Payment '" + payment.getId() + "' is not refundable";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        if (payment.getStatus() != TransactionStatus.ACTIVE) {
            String errMsg = "Payment '" + payment.getId() + "' must be ACTIVE";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        if (refundAmount.compareTo(payment.getUnallocatedAmount()) > 0) {
            String errMsg = "Refund amount cannot be greater than unallocated payment amount";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        Account refundRequestedBy = accountService.getOrCreateAccount(userSessionManager.getUserId());

        return checkForRefund(payment, new Date(), refundRequestedBy, refundAmount);
    }

    /**
     * A convenience methods that checks for unverified Refunds for each of the specified accountIds in the specified date range.
     *
     * @param accountIds Student accountIds for which to look for unverified refunds.
     * @param dateFrom   Beginning of the Date range in which to search for refunds.
     * @param dateTo     End of the Date range in which to search for refunds.
     * @return List of unverified refunds for all specified accountIds.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REQUEST_REFUND)
    public List<Refund> checkForRefund(List<String> accountIds, Date dateFrom, Date dateTo) {

        List<Refund> allRefunds = new LinkedList<Refund>();

        for (String accountId : accountIds) {
            allRefunds.addAll(checkForRefund(accountId, dateFrom, dateTo));
        }

        return allRefunds;
    }

    /**
     * For each account in the system, creates a list of Refunds within the specified date range.
     *
     * @param dateFrom Beginning of the Date range in which to search for refunds.
     * @param dateTo   End of the Date range in which to search for refunds.
     * @return List of unverified refunds for all accountIds in the given date range.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REQUEST_REFUND)
    public List<Refund> checkForRefunds(Date dateFrom, Date dateTo) {

        // Get all Accounts and IDs:
        Query query = em.createQuery("select a.id from Account a");

        List<String> accountIds = query.getResultList();

        // Get all Refunds:
        return checkForRefund(accountIds, dateFrom, dateTo);
    }

    /**
     * For each account in the system, searches for refunds for the entire life.
     *
     * @return List of all Refunds for all accountIds in the system for the entire life.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REQUEST_REFUND)
    public List<Refund> checkForRefunds() {
        return checkForRefunds(new Date(0), new Date(Long.MAX_VALUE));
    }

    /**
     * This actually creates the refund transaction, in addition to allocating the refund to the original charge.
     * It marks the refund object as refunded.
     *
     * @param refundId ID of a Refund object.
     * @return The Refund object.
     */
    @Override
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public Refund performRefund(Long refundId) {
        return performRefund(refundId, null);
    }

    /**
     * This actually creates the refund transaction as a part of a batch transaction, in addition to allocating the refund to the original charge.
     * It marks the refund object as refunded.
     *
     * @param refundId ID of a Refund object.
     * @param batch    ID of a batch transaction.
     * @return The Refund object.
     */
    @Override
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public Refund performRefund(Long refundId, String batch) {
        return performRefundInternal(refundId, batch, null);
    }

    /**
     * Performs refund validation.
     * Sets the refundStatus to {@link RefundStatus#VERIFIED} and the authorizedBy to the current user.
     *
     * @param refundId Refund ID
     * @return Refund instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.VALIDATE_REFUND)
    public Refund validateRefund(Long refundId) {
        return validateRefundWithAmount(refundId, null);
    }

    /**
     * Performs refund validation. Alters the refund amount.
     * Sets the refundStatus to {@link RefundStatus#VERIFIED} and the authorizedBy to the current user.
     *
     * @param refundId Refund ID
     * @param amount   Amount to set on the Refund object.
     * @return Refund instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.VALIDATE_REFUND)
    public Refund validateRefundWithAmount(Long refundId, BigDecimal amount) {
        // Get the Refund object by its identifier:
        Refund refund = getRefund(refundId, false);

        // Set the Refund status to VERIFIED:
        Account currentUserAccount = accountService.getOrCreateAccount(userSessionManager.getUserId());

        refund.setStatus(RefundStatus.VERIFIED);
        refund.setAuthorizedBy(currentUserAccount);

        // Alter the amount if it has been passed:
        if (amount != null) {
            refund.setAmount(amount);
        }

        persistEntity(refund);

        return refund;
    }

    /**
     * Performs a refund to a student account. Calls <code>performRefund</code> for the actual refund action.
     *
     * @param refundId ID of a refund to apply to a student account.
     * @return The <code>Refund</code> object that was generated during this process.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public Refund doAccountRefund(Long refundId) {
        return doAccountRefund(refundId, null);
    }

    /**
     * Performs a batched refund to a student account. Calls <code>performRefund</code> for the actual refund action.
     *
     * @param refundId ID of a refund to apply to a student account.
     * @param batch    Batch of transactions.
     * @return The <code>Refund</code> object that was generated during this process.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public Refund doAccountRefund(Long refundId, String batch) {

        // Get the Refund object and check that it's in the VERIFIED status:
        Refund refund = getRefund(refundId, true);

        // Check that it's an Account refund type using the Refund's "attribute" attribute:
        String refundRule = refund.getAttribute();

        if (StringUtils.isBlank(refundRule) || !StringUtils.startsWith(refundRule, "A")) {
            String errMsg = "Invalid Refund type for an Account refund. Refund rule (attribute) is [" + refundRule + "]";
            logger.error(errMsg);
            throw new InvalidRefundTypeException(errMsg);
        }

        // Validate the refund rule:
        if (!isRefundRuleValid(refundRule)) {

            // Check if the refund rule is invalid because of a non-existing Account:
            String[] refundRuleParams = StringUtils.substringsBetween(refundRule, "(", ")");

            if ((refundRuleParams != null) && (refundRuleParams.length == 2)) {
                String accountId = refundRuleParams[1];
                boolean accountExists = accountService.accountExists(accountId);

                if (!accountExists) {
                    // Fail the Refund:
                    refund.setStatus(RefundStatus.FAILED);

                    // Create an Activity to record this occurrence:
                    createInvalidAccountActivity(accountId);

                    throw new UserNotFoundException("Refund attribute contains an invalid Account identifier. Refund attribute was [" + refundRule + "]");
                }
            }

            // If, however, the refund rule is invalid for another reason, throw an exception:
            throw new InvalidRefundTypeException("Invalid Refund attribute [" + refundRule + "].");
        }

        // Perform refund:
        String accountId = StringUtils.substringsBetween(refundRule, "(", ")")[1];

        refund = performRefundInternal(refundId, batch, accountId);

        // Create another payment Transaction for the Account to be credited:
        String paymentTypeId = refund.getRefundType().getCreditTypeId();
        Transaction paymentTransaction = transactionService.createTransaction(paymentTypeId, accountId, new Date(), refund.getAmount());

        if (StringUtils.isNotBlank(refund.getStatement())) {
            paymentTransaction.setStatementText(refund.getStatement());
        }

        persistEntity(paymentTransaction);

        // Set the Refund system:

        refund.setSystem(configService.getParameter(Constants.REFUND_ACCOUNT_SYSTEM_NAME));

        // Add Refund Manifest:
        addAccountRefundManifest(refund, accountId, paymentTransaction);

        return refund;
    }

    /**
     * Go through the Refund objects in a batch. For each validated refund with type set to account refund
     * (account.refund.type). For each one that is found, call doAccountRefund (refundId, batch).
     *
     * @param batch ID of a transaction batch.
     * @return A <code>List</code> of <code>Refund</code> objects created as a result of this operation.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public List<Refund> doAccountRefunds(String batch) {

        String refundTypeId = configService.getParameter(Constants.REFUND_TYPE_ACCOUNT);
        if (StringUtils.isBlank(refundTypeId)) {
            String errMsg = "Configuration parameter '" + Constants.REFUND_TYPE_ACCOUNT + " is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }

        // Find all Refund object with the given value of "batch":
        String sql = "select r from Refund r where r.batchId = :batchId and r.status = :status and " +
                " r.refundType.debitTypeId = :refundTypeId";

        Query query = em.createQuery(sql)
                .setParameter("batchId", batch)
                .setParameter("status", RefundStatus.VERIFIED)
                .setParameter("refundTypeId", refundTypeId);
        List<Refund> match = query.getResultList();

        // For each matching Refund, perform account refund:
        for (Refund refund : match) {
            doAccountRefund(refund.getId(), batch);
        }

        return match;
    }

    /**
     * Performs refund as a check.
     *
     * @param refundId  Refund identifier.
     * @param checkDate Date on the issued check.
     * @param checkMemo Memo section on the issued check.
     * @return String An XML form of the issued check.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public String doCheckRefund(Long refundId, Date checkDate, String checkMemo) {
        return doCheckRefund(refundId, null, checkDate, checkMemo);
    }

    /**
     * Performs a batch refund as a check.
     *
     * @param refundId  Refund identifier.
     * @param batch     ID of a transaction batch.
     * @param checkDate Date on the issued check.
     * @param checkMemo Memo section on the issued check.
     * @return String An XML form of the issued check.
     * @see RefundService#produceCheckXml
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public String doCheckRefund(Long refundId, String batch, Date checkDate, String checkMemo) {
        // Check the flag to consolidate same account checks:
        boolean consolidateSameAccountRefunds = BooleanUtils.toBoolean(configService.getParameter(Constants.REFUND_CHECK_GROUP));
        Check check = doCheckRefundInternal(refundId, batch, checkDate, checkMemo, consolidateSameAccountRefunds);

        return JaxbUtils.toXml(check);
    }

    /**
     * Performs check type refunds as a part of a batch.
     *
     * @param batch     Batch of transactions.
     * @param checkDate Date on the refund checks.
     * @param checkMemo Memo section on the refund checks.
     * @return A batch of checks in form of an XML.
     * @see RefundService#produceCheckXml
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public String doCheckRefunds(String batch, Date checkDate, String checkMemo) {

        String refundTypeId = configService.getParameter(Constants.REFUND_TYPE_CHECK);
        if (StringUtils.isBlank(refundTypeId)) {
            String errMsg = "Configuration parameter '" + Constants.REFUND_TYPE_CHECK + " is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }

        // Find all VERIFIED Check Refunds in the batch:
        String sql = "select r from Refund r where r.batchId = :batchId and r.status = :status and " +
                " r.refundType.debitTypeId = :refundTypeId";

        Query query = em.createQuery(sql)
                .setParameter("batchId", batch)
                .setParameter("status", RefundStatus.VERIFIED)
                .setParameter("refundTypeId", refundTypeId);
        List<Refund> match = query.getResultList();

        // Create a new BatchCheck and a BatchControl:
        BatchCheck batchCheck = new BatchCheck();
        BatchCheck.BatchControl batchControl = new BatchCheck.BatchControl();
        BigDecimal totalValue = new BigDecimal(0);

        batchCheck.setBatchControl(batchControl);
        batchCheck.setBatchIdentifier(batch);
        batchControl.setNumberOfChecks(match.size());

        // For each matching Refund, perform refund and record its XML check:
        for (Refund refund : match) {
            Check check = doCheckRefundInternal(refund.getId(), batch, checkDate, checkMemo, false);

            batchCheck.getCheck().add(check);
            totalValue = totalValue.add(check.getAmount());
        }

        // Set the total value of checks:
        batchControl.setValueOfChecks(totalValue);

        // Marshal the batch check:
        return JaxbUtils.toXml(batchCheck);
    }

    /**
     * Produces an XML form of a refund check.
     *
     * @param identifier    Check identifier.
     * @param payee         To whom the check is made out to.
     * @param postalAddress Address of the payee.
     * @param amount        Amount on the check.
     * @param checkDate     Date on the check.
     * @param memo          Memo section on the check.
     * @return A refund check in an XML form.
     * @see RefundService#produceCheckXml
     */
    @Override
    @Transactional(readOnly = true)
    public String produceCheckXml(String identifier, String payee, PostalAddress postalAddress, BigDecimal amount,
                                  Date checkDate, String memo) {
        // Create a new Check object:
        Check check = produceCheckInternal(identifier, payee, postalAddress, amount, checkDate, memo);
        // Marshal the Check object:
        return JaxbUtils.toXml(check);
    }

    /**
     * Performs refund as a bank account credit.
     *
     * @param refundId Refund identifier.
     * @return String An XML form of the bank account information (Ach).
     * @see RefundService#produceAchTransmissionXml
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public String doAchRefund(Long refundId) {
        return doAchRefund(refundId, null);
    }

    /**
     * Performs a batch refund as a bank account credit.
     *
     * @param refundId Refund identifier.
     * @param batch    ID of a transaction batch.
     * @return String An XML form of the bank account information (Ach).
     * @see RefundService#produceAchTransmissionXml
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public String doAchRefund(Long refundId, String batch) {
        // Check the flag to consolidate same account checks:
        boolean consolidateSameAccountRefunds = BooleanUtils.toBoolean(configService.getParameter(Constants.REFUND_ACH_GROUP));
        Ach achTransmission = doAchRefundInternal(refundId, batch, consolidateSameAccountRefunds);

        return JaxbUtils.toXml(achTransmission);
    }

    /**
     * Performs bank account credit type refunds as a part of a batch.
     * Go through the Refund objects. For each validated refund with type set to ach refund (refund.ach.type).
     * For each one that is found, call doAchRefund (refundId, batch).
     *
     * @param batch Batch of transactions.
     * @return String An XML form of a batch of the bank account informations (Ach).
     * @see RefundService#produceAchTransmissionXml
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.PERFORM_REFUND)
    public String doAchRefunds(String batch) {

        String refundTypeId = configService.getParameter(Constants.REFUND_TYPE_ACH);
        if (StringUtils.isBlank(refundTypeId)) {
            String errMsg = "Configuration parameter '" + Constants.REFUND_TYPE_ACH + " is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }

        // Find all VERIFIED Ach Refunds in the batch:
        String sql = "select r from Refund r where r.batchId = :batchId and r.status = :status and " +
                " r.refundType.debitTypeId = :refundTypeId";

        Query query = em.createQuery(sql)
                .setParameter("batchId", batch)
                .setParameter("status", RefundStatus.VERIFIED)
                .setParameter("refundTypeId", refundTypeId);
        List<Refund> match = query.getResultList();

        // Create a new BatchAch and a BatchControl:
        BatchAch batchAch = new BatchAch();
        BatchAch.BatchControl batchControl = new BatchAch.BatchControl();
        BigDecimal totalValue = new BigDecimal(0);

        batchAch.setBatchControl(batchControl);
        batchAch.setBatchIdentifier(batch);
        batchControl.setNumberOfAchs(match.size());

        // For each matching Refund, perform refund and record its XML check:
        for (Refund refund : match) {
            Ach ach = doAchRefundInternal(refund.getId(), batch, false);

            batchAch.getAch().add(ach);
            totalValue = totalValue.add(ach.getAmount());
        }

        // Set the total value of checks:
        batchControl.setValueOfAchs(totalValue);

        // Marshal the batch check:
        return JaxbUtils.toXml(batchAch);
    }

    /**
     * Produces a bank account transmission as an XML document.
     *
     * @param ach       <code>Ach</code> that contains bank account information.
     * @param amount    Amount to be transmitted to the bank account.
     * @param reference Reference information.
     * @return Bank account transmission as an XML document.
     */
    @Override
    @Transactional(readOnly = true)
    public String produceAchTransmissionXml(Ach ach, BigDecimal amount, String reference) {
        // Create a new Ach:
        Ach achTransmission = produceAchTransmissionInternal(amount, reference, ach);
        return JaxbUtils.toXml(achTransmission);
    }

    /**
     * Checks if a refund rule is valid.
     * Refer to the "Process Diagrams" for the details.
     *
     * @param refundRule A refund rule.
     * @return boolean Whether the specified refund rule is valid.
     */
    @Override
    @Transactional(readOnly = true)
    @PermissionsAllowed(Permission.VALIDATE_REFUND)
    public boolean isRefundRuleValid(String refundRule) {

        if (StringUtils.isBlank(refundRule)) {
            return true;
        }

        // example A(100)(pheald) or S(45)
        // transactionService.isRefundRule(refundRule);
        // applies to credit types
        // drill down on the refund rule provided
        if (refundRule.startsWith("A") || refundRule.startsWith("S")) {
            // does the rule have a number 0 - 65535 after/in parenthesis
            int openRuleIndex = refundRule.indexOf("(");
            int closeRuleIndex = refundRule.indexOf(")");
            if ((openRuleIndex > 0) && (closeRuleIndex > 0) && (openRuleIndex < closeRuleIndex)) {
                try {
                    int value = Integer.valueOf(refundRule.substring(openRuleIndex + 1, closeRuleIndex));
                    if ((value < 0) || (value > 0xffff)) {
                        return false;
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return false;
                }
                if (refundRule.startsWith("S")) {
                    return true;
                } else {
                    // There should be a second parameter in parenthesis for "A" which appears to be the accountId
                    // which must exist in KSA
                    int openAccountIndex = refundRule.indexOf("(", closeRuleIndex + 1);
                    int closeAccountIndex = refundRule.indexOf(")", closeRuleIndex + 1);
                    if ((openAccountIndex > 0) && (closeAccountIndex > 0) && (openAccountIndex < closeAccountIndex)) {
                        try {
                            String accountId = refundRule.substring(openAccountIndex + 1, closeAccountIndex);
                            return (accountService.getOrCreateAccount(accountId) != null);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            return false;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Performs refund cancellation.
     * Reverses the associated <code>Transaction</code> and sets the appropriate refund status.
     * Refer to the "Process Diagrams" document for the refund cancellation logic.
     *
     * @param refundId ID of a <code>Refund</code> to be cancelled.
     * @param memo     An explanation of the reasons for refund cancellation.
     * @return <code>Refund</code> that was cancelled.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CANCEL_REFUND)
    public Refund cancelRefund(Long refundId, String memo) {

        // Get the Refund object:
        Refund refund = getRefund(refundId, false);
        RefundStatus refundStatus = refund.getStatus();

        switch (refundStatus) {
            case CANCELLED:
                throw new RefundCancelledException("Refund with ID [" + refundId + "] is already canceled");
            case FAILED:
                throw new RefundFailedException("Refund with ID [" + refundId + "] is already failed");
            case VERIFIED:
            case UNVERIFIED:
                refund.setStatus(RefundStatus.CANCELLED);
                persistEntity(refund);
                break;
            case ACTIVE:

                // Check all Refunds belonging to the same refund group, cancel them and reverse all Transactions.
                // If there is no refund group, cancel only the refund and reverse its Transaction:
                String refundGroup = refund.getRefundGroup();

                List<Refund> refundsToCancel = new LinkedList<Refund>();

                refundsToCancel.add(refund);

                if (StringUtils.isNotBlank(refundGroup)) {

                    // Get all Refunds with the same group ID:
                    String sql = "select r from Refund r " +
                            " inner join fetch r.transaction rt " +
                            " inner join fetch r.refundTransaction rrt " +
                            " where r.refundGroup = :refundGroup and r.id <> :id";

                    Query query = em.createQuery(sql).setParameter("refundGroup", refundGroup);
                    query.setParameter("id", refund.getId());

                    refundsToCancel.addAll(query.getResultList());
                }

                // Cancel all Refunds and reverse their Transactions:
                for (Refund refundToCancel : refundsToCancel) {
                    Transaction transaction = refundToCancel.getTransaction();
                    transaction.setStatus(TransactionStatus.ACTIVE);
                    Transaction refundTransaction = refundToCancel.getRefundTransaction();
                    BigDecimal amount = refund.getAmount();
                    transactionService.reverseTransaction(refundTransaction.getId(), memo, amount, "Refund Cancellation");
                    refundToCancel.setStatus(RefundStatus.CANCELLED);
                    persistEntity(refundToCancel);
                }
        }

        return refund;
    }

    /**
     * Retrieves the refund type by code.
     *
     * @param refundTypeCode Credit type ID.
     * @return RefundType instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_REFUND_TYPE)
    public RefundType getRefundType(String refundTypeCode) {
        return auditableEntityService.getAuditableEntity(refundTypeCode, RefundType.class);
    }


    /**
     * Deletes a <code>RefundType</code> from the persistent storage.
     *
     * @param refundType A <code>RefundType</code> to delete from the storage.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.EDIT_REFUND_TYPE)
    public boolean deleteRefundType(RefundType refundType) {
        return deleteEntity(refundType.getId(), RefundType.class);
    }

    /**
     * Returns all Refunds belonging to the same group with the specified Group ID (UUID).
     *
     * @param groupId Refund Group id.
     * @return A List of <code>Refund</code>s belonging to the Group with the given ID.
     */
    @Override
    @Transactional(readOnly = true)
    @PermissionsAllowed(Permission.READ_REFUND)
    public List<Refund> getRefundGroup(String groupId) {

        // Create a run a query to get all refunds in the same Group as the given one
        Query query = em.createQuery(GET_REFUNDS_SELECT + " where r.refundGroup = :refundGroup");

        query.setParameter("refundGroup", groupId);

        return query.getResultList();
    }

    /**
     * Returns a List of Refunds for the given Account ID.
     *
     * @param userId ID of an Account for which to get its Refunds.
     * @return A List of all Refund objects linked to the given Account ID.
     */
    @Override
    @Transactional(readOnly = true)
    @PermissionsAllowed(Permission.READ_REFUND)
    public List<Refund> getAccountRefunds(String userId) {
        return getAccountRefunds(userId, null, null);
    }

    /**
     * Returns all Refunds linked to the Account with the given ID and which fall in the specified date range.
     *
     * @param accountId   Account which Refunds to find.
     * @param dateFrom Start of the date range.
     * @param dateTo   End of the date range.
     * @return List of Refunds in the date range for the given Account.
     */
    @Override
    @Transactional(readOnly = true)
    @PermissionsAllowed(Permission.READ_REFUND)
    public List<Refund> getAccountRefunds(String accountId, Date dateFrom, Date dateTo) {

        Set<String> accountIds = new HashSet<String>(1);

        accountIds.add(accountId);

        return getAccountRefunds(accountIds, dateFrom, dateTo);
    }

    /**
     * Returns Refunds for all specified Accounts.
     *
     * @param accountIds Account IDs for which to return Refunds.
     * @return Refunds for the given Accounts.
     */
    @Override
    @PermissionsAllowed(Permission.READ_REFUND)
    public List<Refund> getAccountRefunds(Set<String> accountIds) {
        return getAccountRefunds(accountIds, null, null);
    }

    /**
     * Returns Refunds for all specified Accounts and date range.
     *
     * @param accountIds Account IDs for which to return Refunds.
     * @param dateFrom   Start of the date range.
     * @param dateTo     End of the date range.
     * @return Refunds for the given Accounts.
     */
    @Override
    @PermissionsAllowed(Permission.READ_REFUND)
    public List<Refund> getAccountRefunds(Set<String> accountIds, Date dateFrom, Date dateTo) {

        if (CollectionUtils.isEmpty(accountIds)) {
            String errMsg = "Account IDs are required";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        // Create a run a query to get all refunds with matching Account IDs
        StringBuilder sql = new StringBuilder(GET_REFUNDS_SELECT + " where t.account.id in (:accountIds) ");
        boolean hasDateFrom = (dateFrom != null);
        boolean hasDateTo = (dateTo != null);

        if (hasDateFrom) {
            sql.append(" and t.effectiveDate >= :dateFrom ");
        }

        if (hasDateTo) {
            sql.append(" and t.effectiveDate <= :dateTo ");
        }

        Query query = em.createQuery(sql.toString());

        query.setParameter("accountIds", new ArrayList<String>(accountIds));

        if (hasDateFrom) {
            query.setParameter("dateFrom", CalendarUtils.removeTime(dateFrom), TemporalType.DATE);
        }

        if (hasDateTo) {
            query.setParameter("dateTo", CalendarUtils.removeTime(dateTo), TemporalType.DATE);
        }

        return query.getResultList();
    }

    /* ****************************************************************
   *
   * Utility methods.
   *
   * ****************************************************************/

    /**
     * Performs a refund to a bank account. Returns the bank account transmission object.
     *
     * @param refundId                      ID of a Refund to process.
     * @param batch                         Batch of refunds.
     * @param consolidateSameAccountRefunds Whether to consolidate refunds into one transmission.
     * @return Ach transmission as a JAXB object.
     */
    private Ach doAchRefundInternal(Long refundId, String batch, boolean consolidateSameAccountRefunds) {
        // Get the Refund object:
        Refund refund = getRefund(refundId, true);

        // Check the refund is of an ACH type. Compare the value of the system setting to RefundType:
        String refundTypeId = refund.getRefundType().getDebitTypeId();

        String systemPropRefundType = configService.getParameter(Constants.REFUND_TYPE_ACH);
        if (StringUtils.isBlank(systemPropRefundType)) {
            String errMsg = "Configuration parameter '" + Constants.REFUND_TYPE_ACH + " is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }

        if (!StringUtils.equalsIgnoreCase(refundTypeId, systemPropRefundType)) {
            throw new InvalidRefundTypeException("Invalid Refund type for an Ach refund. Refund type is [" + refundTypeId + "].");
        }

        // Check that the account in question has bank type listed under protected information type of type refund.ach.banktype available.
        // If so, get the bank information.
        String accountId = refund.getTransaction().getAccountId();
        Ach ach;

        try {

            ach = accountService.getAch(accountId);

        } catch (Exception e) {

            // Mark refund as FAILED:
            refund.setStatus(RefundStatus.FAILED);
            persistEntity(refund);

            String errMsg = "No ACH information found for account " + accountId;

            logger.error(errMsg, e);

            throw new InvalidAchInformationException(errMsg);
        }

        // Perform either an individual or consolidate refund:
        List<Refund> allDueRefunds = new ArrayList<Refund>();
        String systemName = configService.getParameter(Constants.REFUND_ACH_SYSTEM_NAME);
        String achReference;
        BigDecimal amount;

        // Add the original Refund to the list of all Refunds:
        allDueRefunds.add(refund);

        if (consolidateSameAccountRefunds) {
            // Find all VALIDATED Ach Refunds for the same account, EXCEPT for the current Refund:
            String sql = "select r from Refund r where r.status = :refundStatus and " +
                    " r.transaction.accountId = :accountId and r.refundType.debitTypeId = :refundType and r.id <> :id";
            Query query = em.createQuery(sql)
                    .setParameter("refundStatus", RefundStatus.VERIFIED)
                    .setParameter("accountId", accountId)
                    .setParameter("refundType", refundTypeId)
                    .setParameter("id", refund.getId());
            String refundGroup = UUID.randomUUID().toString();

            allDueRefunds.addAll(query.getResultList());
            amount = new BigDecimal(0);
            achReference = refundGroup;

            // Get the new Rollup:
            String achRollupCode = configService.getParameter(Constants.REFUND_ACH_GROUP_ROLLUP);
            Rollup achRollup = auditableEntityService.getAuditableEntity(achRollupCode, Rollup.class);

            // Sum all due Refunds into one Ach transmission and perform refund:
            for (Refund dueRefund : allDueRefunds) {
                // Add the current refund amount to the total:
                amount = amount.add(dueRefund.getAmount());

                // Perform refund:
                dueRefund = performRefundInternal(dueRefund.getId(), batch, null);
                dueRefund.setSystem(systemName);
                dueRefund.setRefundGroup(refundGroup);
                dueRefund.getRefundTransaction().setRollup(achRollup);
                persistEntity(dueRefund.getRefundTransaction());
            }
        } else {
            // Produce a single refund Ach transmission:
            amount = refund.getAmount();
            achReference = refundId.toString();

            // Perform refund:
            refund = performRefundInternal(refund.getId(), batch, null);
            refund.setSystem(systemName);
        }

        // Produce an ACH transmission:
        Ach achTransmission = produceAchTransmissionInternal(amount, achReference, ach);

        // Add Refund Manifest:
        addAchRefundManifest(allDueRefunds, achTransmission);

        return achTransmission;
    }

    /**
     * Performs a Check refund. Optionally, consolidates all refunds for the same Account into one refund check.
     *
     * @param refundId                      Id of a Refund to perform a check refund on.
     * @param batch                         Batch ID.
     * @param checkDate                     Date of the refund check.
     * @param checkMemo                     Memo section on the refund check.
     * @param consolidateSameAccountRefunds Whether to consolidate refund checks into one large sum.
     * @return Refund check as a JAXB object.
     */
    private Check doCheckRefundInternal(Long refundId, String batch, Date checkDate, String checkMemo, boolean consolidateSameAccountRefunds) {

        // Get the Refund object:
        Refund refund = getRefund(refundId, true);

        // Check that this refund of of a Check type. Compare the value of the system setting to RefundType:
        String refundTypeId = refund.getRefundType().getDebitTypeId();

        String systemPropRefundType = configService.getParameter(Constants.REFUND_TYPE_CHECK);
        if (StringUtils.isBlank(systemPropRefundType)) {
            String errMsg = "Configuration parameter '" + Constants.REFUND_TYPE_CHECK + " is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }


        if (!StringUtils.equalsIgnoreCase(refundTypeId, systemPropRefundType)) {
            throw new InvalidRefundTypeException("Invalid Refund type for a Check refund. Refund type is [" + refundTypeId + "].");
        }

        // Perform either an individual refund or a consolidated check refund:
        List<Refund> allDueRefunds = new ArrayList<Refund>();
        String systemName = configService.getParameter(Constants.REFUND_CHECK_SYSTEM_NAME);
        String checkId;
        BigDecimal amount;

        // Add the original refund to the list of all Refunds:
        allDueRefunds.add(refund);

        // Work on Refunds. Check if we need to consolidate refund amounts:
        if (consolidateSameAccountRefunds) {
            // Find all VALIDATED cash/check Refunds for the same account, EXCEPT for the current Refund:
            String accountId = refund.getTransaction().getAccountId();
            String sql = "select r from Refund r where r.status = :refundStatus and r.transaction.accountId = :accountId and r.refundType.debitTypeId = :refundType and r.id <> :id";
            Query query = em.createQuery(sql)
                    .setParameter("refundStatus", RefundStatus.VERIFIED)
                    .setParameter("accountId", accountId)
                    .setParameter("refundType", refundTypeId)
                    .setParameter("id", refund.getId());

            allDueRefunds.addAll(query.getResultList());
            checkId = UUID.randomUUID().toString();
            amount = new BigDecimal(0);

            // Get the new Rollup:
            String checkRollupCode = configService.getParameter(Constants.REFUND_CHECK_GROUP_ROLLUP);
            if (StringUtils.isBlank(checkRollupCode)) {
                String errMsg = "Configuration parameter '" + Constants.REFUND_CHECK_GROUP_ROLLUP + " is required";
                logger.error(errMsg);
                throw new ConfigurationException(errMsg);
            }

            Rollup checkRollup = auditableEntityService.getAuditableEntity(checkRollupCode, Rollup.class);

            // Sum all due Refunds into one check and perform refund:
            for (Refund dueRefund : allDueRefunds) {
                // Add the current refund amount to the total:
                amount = amount.add(dueRefund.getAmount());

                // Perform refund:
                dueRefund = performRefundInternal(dueRefund.getId(), batch, null);
                dueRefund.setSystem(systemName);
                dueRefund.setRefundGroup(checkId);
                dueRefund.getRefundTransaction().setRollup(checkRollup);
                persistEntity(dueRefund.getRefundTransaction());
            }
        } else {
            // Produce a single refund check:
            checkId = refundId.toString() + ObjectUtils.toString(batch);
            amount = refund.getAmount();

            // Perform refund:
            refund = performRefundInternal(refund.getId(), batch, null);
            refund.setSystem(systemName);
        }

        // Produce an XML check:
        Account refundAccount = refund.getTransaction().getAccount();
        String payee = refundAccount.getDefaultPersonName().getDisplayValue();
        PostalAddress address = refundAccount.getDefaultPostalAddress();
        Check check = produceCheckInternal(checkId, payee, address, amount, checkDate, checkMemo);

        // Add Refund Manifest:
        addCheckRefundManifest(allDueRefunds, checkId);

        return check;
    }

    /**
     * Creates a <code>RefundManifest</code> for an Account refund and adds it to the specified <code>Refund</code>.
     *
     * @param refund            A <code>Refund</code> for which to add an Account refund manifest.
     * @param accountId         ID of an Account into which a refund was made.
     * @param refundTransaction Transaction that actually refunded the funds.
     */
    private void addAccountRefundManifest(Refund refund, String accountId, Transaction refundTransaction) {

        // Create an Account refund Manifest and add it to the Refund:
        RefundManifest refundManifest = new RefundManifest();

        Account refundAccount = accountService.getOrCreateAccount(accountId);

        refundManifest.setRefundAccount(refundAccount);
        refundManifest.setRefundTransaction(refundTransaction);
        persistEntity(refundManifest);
        refund.setRefundManifest(refundManifest);
        persistEntity(refund);
    }

    /**
     * Creates a <code>RefundManifest</code> for a Check refund and adds it to the specified <code>Refund</code>.
     *
     * @param allDueRefunds   Either a single or grouped <code>Refund</code> objects.
     * @param checkIdentifier Refund check identifier.
     */
    private void addCheckRefundManifest(List<Refund> allDueRefunds, String checkIdentifier) {
        // Create a Check refund manifest:
        RefundManifest refundManifest = new RefundManifest();

        refundManifest.setCheckIdentifier(checkIdentifier);
        persistEntity(refundManifest);

        // Add the manifest all Refunds and persist them:
        for (Refund refund : allDueRefunds) {
            refund.setRefundManifest(refundManifest);
            persistEntity(refund);
        }
    }

    /**
     * Creates a <code>RefundManifest</code> for a Bank transfer refund and adds it to the specified <code>Refund</code>.
     *
     * @param allDueRefunds   Either a single or grouped <code>Refund</code> objects.
     * @param achTransmission Bank transmission that defined the refund.
     */
    private void addAchRefundManifest(List<Refund> allDueRefunds, Ach achTransmission) {
        // Create an Ach refund manifest:
        RefundManifest refundManifest = new RefundManifest();
        String bankTransferIdentifier = achTransmission.getReference();

        refundManifest.setBankTransferIdentifier(bankTransferIdentifier);
        persistEntity(refundManifest);

        // Add the manifest all Refunds and persist them:
        for (Refund refund : allDueRefunds) {
            refund.setRefundManifest(refundManifest);
            persistEntity(refund);
        }
    }

    /**
     * Finds a <code>Refund</code> object. Optionally, asserts that it's in the VERIFIED state.
     *
     * @param refundId      ID of a <code>Refund</code> to be located.
     * @param checkVerified Flag to check if the <code>Refund</code> is in the VERIFIED state.
     * @return The <code>Refund</code> object with the matching ID.
     * @throws RefundNotFoundException    If no such <code>Refund</code> is found by the given ID.
     * @throws RefundNotVerifiedException If <code>checkVerified</code> flag is <code>true</code>
     *                                    and the <code>Refund</code> has a status different than {@link RefundStatus#VERIFIED}
     */
    private Refund getRefund(Long refundId, boolean checkVerified) {

        // Get the Refund object by its identifier:
        Query query = em.createQuery(GET_REFUNDS_SELECT + " where r.id = :refundId");

        query.setParameter("refundId", refundId);

        List<Refund> result = query.getResultList();

        Refund refund = CollectionUtils.isNotEmpty(result) ? result.get(0) : null;

        if (refund == null) {
            throw new RefundNotFoundException(refundId, "Refund with ID [" + refundId + "] was found in the system");
        }

        // Check the status of the Refund:
        if (checkVerified) {

            RefundStatus refundStatus = refund.getStatus();

            if (refundStatus != RefundStatus.VERIFIED) {
                throw new RefundNotVerifiedException("Refund with ID [" + refundId + "] is not verified");
            }
        }

        return refund;
    }

    /**
     * A utility method that performs refund and accepts an Account ID optionally.
     *
     * @param refundId  Refund ID.
     * @param batch     Batch ID.
     * @param accountId Optional Account ID. If null, the original Transaction's Account ID is used.
     * @return The Refund object.
     */
    private Refund performRefundInternal(Long refundId, String batch, String accountId) {

        // Get the Refund object and check that it's in the VERIFIED status:
        Refund refund = getRefund(refundId, true);

        // Create a charge transaction on the account referenced through the original transaction
        // Of type referenced in the refundType, of the amount of the refund:
        Transaction originalTransaction = refund.getTransaction();
        if (originalTransaction == null) {
            String errMsg = "Cannot find transaction being refunded, Refund ID = " + refundId;
            logger.error(errMsg);
            throw new InvalidRefundException(errMsg);
        }

        if (originalTransaction.getStatus() != TransactionStatus.REFUND_REQUESTED) {
            String errMsg = "Refund transaction must be in " + TransactionStatus.REFUND_REQUESTED + " status";
            logger.error(errMsg);
            throw new InvalidRefundException(errMsg);
        }

        Account account = originalTransaction.getAccount();

        String userId = StringUtils.isNotBlank(accountId) ? accountId : account.getId();

        String refundTransactionTypeId = refund.getRefundType().getDebitTypeId();

        Date effectiveDate = new Date();

        BigDecimal amount = refund.getAmount();

        // Creating a new refund transaction with the same amount
        Transaction refundTransaction = transactionService.createTransaction(refundTransactionTypeId, userId, effectiveDate, amount);

        // Creating a locked allocation between the refund and the original transaction in the amount of the refund
        transactionService.createLockedAllocation(originalTransaction.getId(), refundTransaction.getId(), refund.getAmount());

        // Updating the Refund object with the modified values:
        refund.setRefundDate(effectiveDate);
        refund.setRefundTransaction(refundTransaction);
        refund.setStatus(RefundStatus.ACTIVE);

        originalTransaction.setStatus(TransactionStatus.ACTIVE);
        originalTransaction.setOffset(true);

        if (StringUtils.isNotBlank(batch)) {
            refund.setBatchId(batch);
        }

        persistEntity(refund);

        return refund;
    }

    /**
     * Creates a Cash type Refund.
     *
     * @param payment     Original payment.
     * @param requestDate Date the new <code>Refund</code> is requested. Defaults to current date.
     * @param requestedBy User who requested the refund. Defaults to the current system user.
     * @return A newly created <code>Refund</code>.
     */
    private Refund createCashRefund(Payment payment, Date requestDate, Account requestedBy, BigDecimal refundAmount) {

        // Figure out the refund type method. Get the overridden value first:
        String refundTypeCode = configService.getParameter(Constants.REFUND_METHOD_OVERRIDE);

        if (StringUtils.isBlank(refundTypeCode)) {

            // If there is no overridden value, get the configured value:
            refundTypeCode = configService.getParameter(Constants.REFUND_TYPE_CASH);

            // If there is still no value, use the User preference:
            if (StringUtils.isBlank(refundTypeCode)) {
                refundTypeCode = userPreferenceService.getUserPreferenceValue(requestedBy.getId(), Constants.REFUND_TYPE_CASH);
            }
        }

        // If there is a valid Refund type, create a new Refund:
        if (StringUtils.isNotBlank(refundTypeCode)) {

            RefundType refundType = getRefundType(refundTypeCode);
            if (refundType == null) {
                String errMsg = "Refund type '" + refundTypeCode + " does not exist";
                logger.error(errMsg);
                throw new InvalidRefundTypeException(errMsg);
            }

            // Creating a new Refund
            Refund refund = new Refund();

            refund.setAmount(refundAmount);
            refund.setRequestDate(requestDate);
            refund.setRequestedBy(requestedBy);
            refund.setTransaction(payment);
            refund.setRefundType(refundType);

            persistEntity(refund);

            return refund;

        } else {
            String errMsg = "No default Refund Type code found in system configuration or User Preferences.";
            logger.error(errMsg);
            throw new InvalidRefundMethodException(errMsg);
        }
    }

    /**
     * Creates a <code>Refund</code> to the original payment source.
     *
     * @param payment     Original payment.
     * @param requestDate Date the new <code>Refund</code> is requested. Defaults to current date.
     * @param requestedBy User who requested the refund. Defaults to the current system user.
     * @param refundRule  Refund rule from the original payment.
     * @return A newly created <code>Refund</code>.
     */
    private Refund createSourceRefund(Payment payment, Date requestDate, Account requestedBy, String refundRule,
                                      BigDecimal refundAmount) {

        // Create a new Refund:
        String refundTypeCode = configService.getParameter(Constants.REFUND_TYPE_SOURCE);
        if (StringUtils.isBlank(refundTypeCode)) {
            String errMsg = "Configuration parameter '" + Constants.REFUND_TYPE_SOURCE + "' is required";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        RefundType refundType = getRefundType(refundTypeCode);
        if (refundType == null) {
            String errMsg = "Refund type '" + refundTypeCode + " does not exist";
            logger.error(errMsg);
            throw new InvalidRefundTypeException(errMsg);
        }

        // Creating a new Refund
        Refund refund = new Refund();

        refund.setAmount(refundAmount);
        refund.setRequestDate(requestDate);
        refund.setRequestedBy(requestedBy);
        refund.setTransaction(payment);
        refund.setRefundType(refundType);

        // If an Account refund, set the "attribute" to the Account ID:
        if (StringUtils.startsWith(refundRule, "A")) {
            String refundAccountId = StringUtils.substringsBetween(refundRule, "(", ")")[1];
            refund.setAttribute(refundAccountId);
        }

        persistEntity(refund);

        return refund;
    }


    /**
     * Creates an <code>Activity</code> that records an occurrence of an invalid <code>Account</code>
     *
     * @param accountId An invalid <code>Account</code> identifier.
     */
    private void createInvalidAccountActivity(String accountId) {
        Activity activity = new Activity();
        activity.setAccountId(accountId);
        activity.setEntityId(accountId);
        activity.setEntityType(Account.class.getSimpleName());
        activity.setIpAddress(RequestUtils.getClientIpAddress());
        activity.setLogDetail("Invalid account identifier [" + accountId + "] in processing Account Refunds.");
        activityService.persistActivity(activity);
    }

    /**
     * Produces an Ach transmission using the specified parameters.
     *
     * @param amount    Amount of transmission.
     * @param reference Reference info.
     * @param ach       Original Ach.
     * @return Newly created Ach transmission.
     */
    private Ach produceAchTransmissionInternal(BigDecimal amount, String reference, Ach ach) {

        // Validate the Amount:
        if (amount == null || amount.doubleValue() <= 0) {
            String errMsg = "ACH transmission amount must be a positive number";
            logger.error(errMsg);
            throw new InvalidAchInformationException(errMsg);
        }

        // Create a new Ach:
        Ach achTransmission = new Ach();

        achTransmission.setReference(reference);
        achTransmission.setAmount(amount);
        achTransmission.setRoutingNumber(ach.getRoutingNumber());
        achTransmission.setAccountNumber(ach.getAccountNumber());
        achTransmission.setAccountType(ach.getAccountType());

        return achTransmission;
    }

    /**
     * Creates a JAXB Check object.
     *
     * @param checkId       Check identifier.
     * @param payee         Payee
     * @param postalAddress Postal address on check.
     * @param amount        Amount of check to produce.
     * @param checkDate     Date on check.
     * @param memo          Memo section on check.
     * @return A new Check object.
     */
    private Check produceCheckInternal(String checkId, String payee, PostalAddress postalAddress, BigDecimal amount,
                                       Date checkDate, String memo) {
        // Create a new Check object:
        Check check = new Check();
        check.setIdentifier(checkId);
        check.setPayee(payee);
        check.setPostalAddress(convertToXMLAddress(postalAddress));
        check.setAmount(amount);
        check.setMemo(StringUtils.isNotBlank(memo) ? memo : null);
        check.setDate(produceXMLCheckDate(checkDate));
        return check;
    }


    /**
     * Converts a persistent entity <code>PostalAddress</code> into an XML type.
     *
     * @param address A persistent entity.
     * @return XML type.
     */
    private com.sigmasys.kuali.ksa.jaxb.PostalAddress convertToXMLAddress(PostalAddress address) {
        // Create a new XML Postal Address:
        com.sigmasys.kuali.ksa.jaxb.PostalAddress xmlAddress = new com.sigmasys.kuali.ksa.jaxb.PostalAddress();

        xmlAddress.setAddressLine1(address.getStreetAddress1());
        xmlAddress.setAddressLine2(address.getStreetAddress2());
        xmlAddress.setAddressLine3(address.getStreetAddress3());
        xmlAddress.setCity(address.getCity());
        xmlAddress.setCountryCode(address.getCountry());
        xmlAddress.setPostalCode(address.getPostalCode());
        xmlAddress.setStateCode(address.getState());

        return xmlAddress;
    }

    /**
     * Produces an XML type of a date from a check date. Discourages future checks converting them to today's date.
     *
     * @param checkDate Proposed check date.
     * @return XML check date no earlier than today.
     */
    private XMLGregorianCalendar produceXMLCheckDate(Date checkDate) {

        // If a future date, change to the current date:
        Date dateOnCheck = (checkDate.getTime() < System.currentTimeMillis()) ? checkDate : new Date();

        // Create a new calendar and convert to an XML calendar:
        GregorianCalendar c = new GregorianCalendar();

        c.setTime(dateOnCheck);

        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        return null;
    }
}
