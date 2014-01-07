package com.sigmasys.kuali.ksa.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.sigmasys.kuali.ksa.model.PostalAddress;
import com.sigmasys.kuali.ksa.model.Refund;
import com.sigmasys.kuali.ksa.model.RefundStatus;
import com.sigmasys.kuali.ksa.model.RefundType;
import com.sigmasys.kuali.ksa.jaxb.Ach;

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
public interface RefundService {

    /**
     * Creates on refund for a single payment.
     *
     * @param paymentId Payment ID
     * @return a new Refund instance
     */
    Refund checkForRefund(Long paymentId);

    /**
     * Creates on refund for a single payment.
     *
     * @param paymentId    Payment ID
     * @param refundAmount Refund amount
     * @return a new Refund instance
     */
    Refund checkForRefund(Long paymentId, BigDecimal refundAmount);

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
    List<Refund> checkForRefund(String accountId, Date dateFrom, Date dateTo);

    /**
     * A convenience methods that checks for unverified Refunds for each of the specified accounts in the specified date range.
     *
     * @param accountIds Student accounts for which to look for unverified refunds.
     * @param dateFrom   Beginning of the Date range in which to search for refunds.
     * @param dateTo     End of the Date range in which to search for refunds.
     * @return List of unverified refunds for all specified accounts.
     */
    List<Refund> checkForRefund(List<String> accountIds, Date dateFrom, Date dateTo);

    /**
     * For each account in the system, creates a list of Refunds within the specified date range.
     *
     * @param dateFrom Beginning of the Date range in which to search for refunds.
     * @param dateTo   End of the Date range in which to search for refunds.
     * @return List of unverified refunds for all accounts in the given date range.
     */
    List<Refund> checkForRefunds(Date dateFrom, Date dateTo);

    /**
     * For each account in the system, searches for refunds for the entire life.
     *
     * @return List of all Refunds for all accounts in the system for the entire life.
     */
    List<Refund> checkForRefunds();

    /**
     * This actually creates the refund transaction, in addition to allocating the refund to the original charge.
     * It marks the refund object as refunded.
     *
     * @param refundId ID of a Refund object.
     * @return The Refund object.
     */
    Refund performRefund(Long refundId);

    /**
     * This actually creates the refund transaction as a part of a batch transaction, in addition to allocating the refund to the original charge.
     * It marks the refund object as refunded.
     *
     * @param refundId ID of a Refund object.
     * @param batch    ID of a batch transaction.
     * @return The Refund object.
     */
    Refund performRefund(Long refundId, String batch);

    /**
     * Performs refund validation.
     * Sets the refundStatus to {@link RefundStatus#VERIFIED} and the authorizedBy to the current user.
     *
     * @param refundId Refund ID
     * @return Refund instance
     */
    Refund validateRefund(Long refundId);

    /**
     * Performs refund validation. Alters the refund amount.
     * Sets the refundStatus to {@link RefundStatus#VERIFIED} and the authorizedBy to the current user.
     *
     * @param refundId Refund ID
     * @param amount   Amount to set on the Refund object.
     * @return Refund instance
     */
    Refund validateRefundWithAmount(Long refundId, BigDecimal amount);

    /**
     * Performs a refund to a student account. Calls <code>performRefund</code> for the actual refund action.
     *
     * @param refundId ID of a refund to apply to a student account.
     * @return The <code>Refund</code> object that was generated during this process.
     */
    Refund doAccountRefund(Long refundId);

    /**
     * Performs a batched refund to a student account. Calls <code>performRefund</code> for the actual refund action.
     *
     * @param refundId ID of a refund to apply to a student account.
     * @param batch    Batch of transactions.
     * @return The <code>Refund</code> object that was generated during this process.
     */
    Refund doAccountRefund(Long refundId, String batch);

    /**
     * Go through the Refund objects in a batch. For each validated refund with type set to account refund
     * (account.refund.type). For each one that is found, call doAccountRefund (refundId, batch).
     *
     * @param batch ID of a transaction batch.
     * @return A <code>List</code> of <code>Refund</code> objects created as a result of this operation.
     */
    List<Refund> doAccountRefunds(String batch);

    /**
     * Performs refund as a check.
     *
     * @param refundId  Refund identifier.
     * @param checkDate Date on the issued check.
     * @param checkMemo Memo section on the issued check.
     * @return String An XML form of the issued check.
     */
    String doCheckRefund(Long refundId, Date checkDate, String checkMemo);

    /**
     * Performs a batch refund as a check.
     *
     * @param refundId  Refund identifier.
     * @param batch     ID of a transaction batch.
     * @param checkDate Date on the issued check.
     * @param checkMemo Memo section on the issued check.
     * @return String An XML form of the issued check.
     * @see RefundService#produceCheckXml(String, String, PostalAddress, BigDecimal, Date, String)
     */
    String doCheckRefund(Long refundId, String batch, Date checkDate, String checkMemo);

    /**
     * Performs check type refunds as a part of a batch.
     *
     * @param batch     Batch of transactions.
     * @param checkDate Date on the refund checks.
     * @param checkMemo Memo section on the refund checks.
     * @return A batch of checks in form of an XML.
     * @see RefundService#produceCheckXml(String, String, PostalAddress, BigDecimal, Date, String)
     */
    String doCheckRefunds(String batch, Date checkDate, String checkMemo);

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
     * @see RefundService#produceCheckXml(String, String, PostalAddress, BigDecimal, Date, String)
     */
    String produceCheckXml(String identifier, String payee, PostalAddress postalAddress, BigDecimal amount, Date checkDate, String memo);

    /**
     * Performs refund as a bank account credit.
     *
     * @param refundId Refund identifier.
     * @return String An XML form of the bank account information (Ach).
     * @see RefundService#produceAchTransmissionXml(Ach, BigDecimal, String)
     */
    String doAchRefund(Long refundId);

    /**
     * Performs a batch refund as a bank account credit.
     *
     * @param refundId Refund identifier.
     * @param batch    ID of a transaction batch.
     * @return String An XML form of the bank account information (Ach).
     * @see RefundService#produceAchTransmissionXml(Ach, BigDecimal, String)
     */
    String doAchRefund(Long refundId, String batch);

    /**
     * Performs bank account credit type refunds as a part of a batch.
     * Go through the Refund objects. For each validated refund with type set to ach refund (refund.ach.type).
     * For each one that is found, call doAchRefund (refundId, batch).
     *
     * @param batch Batch of transactions.
     * @return String An XML form of a batch of the bank account informations (Ach).
     * @see RefundService#produceAchTransmissionXml(Ach, BigDecimal, String)
     */
    String doAchRefunds(String batch);

    /**
     * Produces a bank account transmission as an XML document.
     *
     * @param ach       <code>Ach</code> that contains bank account information.
     * @param amount    Amount to be transmitted to the bank account.
     * @param reference Reference information.
     * @return Bank account transmission as an XML document.
     */
    String produceAchTransmissionXml(Ach ach, BigDecimal amount, String reference);

    /**
     * Checks if a refund rule is valid.
     * Refer to the "Process Diagrams" for the details.
     *
     * @param refundRule A refund rule.
     * @return boolean Whether the specified refund rule is valid.
     */
    boolean isRefundRuleValid(String refundRule);

    /**
     * Performs refund cancellation.
     * Reverses the associated <code>Transaction</code> and sets the appropriate refund status.
     * Refer to the "Process Diagrams" document for the refund cancellation logic.
     *
     * @param refundId ID of a <code>Refund</code> to be cancelled.
     * @param memo     An explanation of the reasons for refund cancellation.
     * @return <code>Refund</code> that was cancelled.
     */
    Refund cancelRefund(Long refundId, String memo);

    /**
     * Retrieves the refund type by code.
     *
     * @param refundTypeCode Credit type ID.
     * @return RefundType instance
     */
    RefundType getRefundType(String refundTypeCode);

    /**
     * Deletes a <code>RefundType</code> from the persistent storage.
     *
     * @param refundType A <code>RefundType</code> to delete from the storage.
     */
    boolean deleteRefundType(RefundType refundType);

    /**
     * Returns a List of Refunds for the given Account ID.
     *
     * @param userId ID of an Account for which to get its Refunds.
     * @return A List of all Refund objects linked to the given Account ID.
     */
    List<Refund> getAccountRefunds(String userId);

    /**
     * Returns all Refunds linked to the Account with the given ID and which fall in the specified date range.
     *
     * @param accountId Account which Refunds to find.
     * @param dateFrom  Start of the date range.
     * @param dateTo    End of the date range.
     * @return List of Refunds in the date range for the given Account.
     */
    List<Refund> getAccountRefunds(String accountId, Date dateFrom, Date dateTo);

    /**
     * Returns all Refunds belonging to the same group with the specified Group ID (UUID).
     *
     * @param groupId Refund Group id.
     * @return A List of <code>Refund</code>s belonging to the Group with the given ID.
     */
    List<Refund> getRefundGroup(String groupId);

    /**
     * Returns Refunds for all specified Accounts.
     *
     * @param accountIds Accounts for which to return Refunds.
     * @return Refunds for the given Accounts.
     */
    List<Refund> getAccountRefunds(Set<String> accountIds);

    /**
     * Returns Refunds for all specified Accounts and date range.
     *
     * @param accountIds Account IDs for which to return Refunds.
     * @param dateFrom   Start of the date range.
     * @param dateTo     End of the date range.
     * @return Refunds for the given Accounts.
     */
    List<Refund> getAccountRefunds(Set<String> accountIds, Date dateFrom, Date dateTo);
}
