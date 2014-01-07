package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Transaction Transfer model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_TRANSACTION_TRANSFER")
public class TransactionTransfer implements Identifiable {

    /**
     * Transaction Transfer ID
     */
    private Long id;

    /**
     * Creation timestamp
     */
    private Date creationDate;

    /**
     * Source transaction
     */
    private Transaction sourceTransaction;

    /**
     * Destination transaction
     */
    private Transaction destTransaction;

    /**
     * Offset transaction
     */
    private Transaction offsetTransaction;

    /**
     * Source reciprocal transaction
     */
    private Transaction sourceReciprocalTransaction;

    /**
     * Destination reciprocal transaction
     */
    private Transaction destReciprocalTransaction;

    /**
     * Transfer type
     */
    private TransferType transferType;

    /**
     * Amount of the original transfer. In many cases, the entire amount of the transaction will be transferred,
     * but this field keeps track of the amount in case a partial amount is passed.
     */
    private BigDecimal transferAmount;

    /**
     * If the transaction is reversed, then the amount of the reversal is stored here.
     * This may or may not be equal to the transfer amount, but it cannot be higher.
     */
    private BigDecimal reversalAmount;

    /**
     * UUID used to group together groups of transfers into a logical group.
     * For example, if eight different types of transaction are covered under a third-party agreement,
     * there would be group of eight transactions moved over to the third party account,
     * but they would all have the same group identifier, so they could be handled as a group.
     */
    private String groupId;

    /**
     * If this transfer is based on a transaction type mask matching, then the mask is stored here.
     */
    private String transactionTypeMask;

    /**
     * Reversal status
     */
    protected ReversalStatus reversalStatus;

    /**
     * Reversal status code
     */
    protected String reversalStatusCode;


    @PostLoad
    protected void populateTransientFields() {
        reversalStatus = (reversalStatusCode != null) ? EnumUtils.findById(ReversalStatus.class, reversalStatusCode) : null;
    }


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SRC_TRANSACTION_ID_FK", nullable = false)
    public Transaction getSourceTransaction() {
        return sourceTransaction;
    }

    public void setSourceTransaction(Transaction sourceTransaction) {
        this.sourceTransaction = sourceTransaction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEST_TRANSACTION_ID_FK", nullable = false)
    public Transaction getDestTransaction() {
        return destTransaction;
    }

    public void setDestTransaction(Transaction destTransaction) {
        this.destTransaction = destTransaction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFSET_TRANSACTION_ID_FK")
    public Transaction getOffsetTransaction() {
        return offsetTransaction;
    }

    public void setOffsetTransaction(Transaction offsetTransaction) {
        this.offsetTransaction = offsetTransaction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SRC_RECIP_TRANSACTION_ID_FK")
    public Transaction getSourceReciprocalTransaction() {
        return sourceReciprocalTransaction;
    }

    public void setSourceReciprocalTransaction(Transaction sourceReciprocalTransaction) {
        this.sourceReciprocalTransaction = sourceReciprocalTransaction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEST_RECIP_TRANSACTION_ID_FK")
    public Transaction getDestReciprocalTransaction() {
        return destReciprocalTransaction;
    }

    public void setDestReciprocalTransaction(Transaction destReciprocalTransaction) {
        this.destReciprocalTransaction = destReciprocalTransaction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSFER_TYPE_ID_FK")
    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    @Column(name = "TRANSFER_AMOUNT", nullable = false)
    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    @Column(name = "REVERSAL_AMOUNT")
    public BigDecimal getReversalAmount() {
        return reversalAmount;
    }

    public void setReversalAmount(BigDecimal reversalAmount) {
        this.reversalAmount = reversalAmount;
    }

    @Column(name = "GROUP_ID", length = 100)
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name = "TRANSACTION_TYPE_MASK", length = 500)
    public String getTransactionTypeMask() {
        return transactionTypeMask;
    }

    public void setTransactionTypeMask(String transactionTypeMask) {
        this.transactionTypeMask = transactionTypeMask;
    }

    @Column(name = "REVERSAL_STATUS", length = 1, nullable = false)
    protected String getReversalStatusCode() {
        return reversalStatusCode;
    }

    protected void setReversalStatusCode(String reversalStatusCode) {
        this.reversalStatusCode = reversalStatusCode;
        reversalStatus = EnumUtils.findById(ReversalStatus.class, reversalStatusCode);
    }

    @Transient
    public ReversalStatus getReversalStatus() {
        return reversalStatus;
    }

    public void setReversalStatus(ReversalStatus reversalStatus) {
        this.reversalStatus = reversalStatus;
        reversalStatusCode = reversalStatus.getId();
    }

}
