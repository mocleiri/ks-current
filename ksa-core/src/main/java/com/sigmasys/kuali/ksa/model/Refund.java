package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * &nbsp;&nbsp;&nbsp;&nbsp;The refund class is used to record the issuance of refunds. Refunds may be provoked by a number of actions,
 * automated batch processed, user request, CSR request, but essentially, the system will go through a rule-based
 * procedure to calculate which unused payments are eligible for refunds, how they are eligible
 * (not all refunds happen in the same way, for example, check and cash payments may be refunded as a check,
 * credit card payments may be refunded to the credit card, etc.)
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Once the refund has been entered into this class, it will be picked up by the appropriate subsystem that will
 * generate the actual refund, which will generate the actual refund transaction, and create a locked
 * allocation between the refund and the payments that were refunded.
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;As a general principle, refunds are first entered into the REFUND table,
 * where they are checked, before interaction takes place to actually create the refund.
 * <p/>
 *
 * @author Michael Ivanov
 * @author Sergey Godunov
 * @version 1.1
 */
@SuppressWarnings("serial")
@Auditable
@Entity
@Table(name = "KSSA_REFUND")
public class Refund implements Identifiable {

    /**
     * Refund ID
     */
    private Long id;

    /**
     * Identifier of the transaction that is being refunded.
     */
    private Transaction transaction;

    /**
     * Value of the refund in system currency.
     */
    private BigDecimal amount;

    /**
     * Refund type
     */
    private RefundType refundType;

    /**
     * Depending on the type of refund, the attributes associated with
     * creating that refund. For example, for an ACH refund, the ACH bank
     * information needs to be passed.
     */
    private String attribute;

    /**
     * Refund statement
     */
    private String statement;

    /**
     * Entity identifier of the user who requested the refund. Likely values
     * will be the user who ran a refund job, or the student requesting their
     * own refund.
     */
    private Account requestedBy;

    /**
     * The user who authorized the refund.
     */
    private Account authorizedBy;

    /**
     * Date and time when the refund request was made.
     */
    private Date requestDate;

    /**
     * The date the refund was applied to the KSA account.
     */
    private Date refundDate;

    /**
     * The produced refund transaction.
     * This will be null at instantiation until the actual refund is produced.
     * Note that a number of transactions may have a single refundTransactionId,
     * as one large refund may be issued to cover several smaller credits.
     */
    private Transaction refundTransaction;

    /**
     * Refund status.
     * Transient, calculated enumerated attribute.
     */
    private RefundStatus status = RefundStatus.UNVERIFIED;

    /**
     * Refund status code.
     * Persistent attribute.
     * A refund may be Unverified, Verified, Failed, Cancelled or Refunded. Once a transaction is refunded, the refundTransactionId must be completed.
     * Set to U at instantiation.
     */
    private String statusCode = RefundStatus.UNVERIFIED_CODE;

    /**
     * System name created the refund
     */
    private String system;

    /**
     * Batch identifier
     */
    private String batchId;

    /**
     * References a RefundManifest object, which records how the refund was actually manifested.
     * For example, if the refund is given as a check, we will record the check number,
     * if an account refund, the transaction Id and the KSA account number.
     */
    private RefundManifest refundManifest;

    /**
     * If several refunds are issued under a single refund header (i.e. four transactions are refunded, but only one check is issued)
     * then the refund group of all the refunds will be the same. This will be a UUID.
     */
    private String refundGroup;


    @PostLoad
    protected void populateTransientFields() {
        status = (statusCode != null) ? EnumUtils.findById(RefundStatus.class, statusCode) : null;
    }

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID_FK", nullable = false)
    public Transaction getTransaction() {
        return transaction;
    }

    @Column(name = "AMOUNT", nullable = false, precision = 10, scale = 2)
    public BigDecimal getAmount() {
        return amount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFUND_TYPE_ID_FK")
    public RefundType getRefundType() {
        return refundType;
    }

    @Column(name = "ATTRIBUTE", length = 1000)
    public String getAttribute() {
        return attribute;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTED_BY_ID_FK")
    public Account getRequestedBy() {
        return requestedBy;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHORIZED_BY_ID_FK")
    public Account getAuthorizedBy() {
        return authorizedBy;
    }

    @Column(name = "REQUEST_DATE")
    public Date getRequestDate() {
        return requestDate;
    }

    @Column(name = "REFUND_DATE")
    public Date getRefundDate() {
        return refundDate;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "REFUND_TRANSACTION_ID_FK")
    public Transaction getRefundTransaction() {
        return refundTransaction;
    }

    @Column(name = "STATUS", length = 1)
    protected String getStatusCode() {
        return statusCode;
    }

    @Transient
    public RefundStatus getStatus() {
        return status;
    }

    @Column(name = "SYSTEM", length = 100)
    public String getSystem() {
        return system;
    }

    @Column(name = "BATCH_ID", length = 100)
    public String getBatchId() {
        return batchId;
    }

    @Column(name = "OVERRIDE_STATEMENT", length = 1024)
    public String getStatement() {
        return statement;
    }

    @Column(name = "REFUND_GROUP_ID", length = 100)
    public String getRefundGroup() {
        return refundGroup;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "REFUND_MANIFEST_ID_FK")
    public RefundManifest getRefundManifest() {
        return refundManifest;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setRefundType(RefundType refundType) {
        this.refundType = refundType;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setRequestedBy(Account requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setAuthorizedBy(Account authorizedBy) {
        this.authorizedBy = authorizedBy;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
        statusCode = status.getId();
    }

    protected void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        status = EnumUtils.findById(RefundStatus.class, statusCode);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public void setRefundGroup(String refundGroup) {
        this.refundGroup = refundGroup;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public void setRefundTransaction(Transaction refundTransaction) {
        this.refundTransaction = refundTransaction;
    }

    public void setRefundManifest(RefundManifest refundManifest) {
        this.refundManifest = refundManifest;
    }
}
