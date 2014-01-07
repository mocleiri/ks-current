package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Batch receipt model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_BATCH_RECEIPT")
public class BatchReceipt extends AccountIdAware implements Identifiable {

    /**
     * The unique identifier
     */
    private Long id;

    /**
     * External ID
     */
    private String externalId;

    /**
     * The batch date
     */
    private Date batchDate;

    /**
     * The receipt date
     */
    private Date receiptDate;

    /**
     * Account
     */
    private Account account;

    /**
     * Total number of transactions in the batch
     */
    private Integer numberOfTransactions;

    /**
     * Total volume of transactions in the batch
     */
    private BigDecimal totalVolume;

    /**
     * The number of accepted transactions in the batch
     */
    private Integer numberOfAcceptedTransactions;

    /**
     * Total debit of accepted transactions
     */
    private BigDecimal debitOfAcceptedTransactions;

    /**
     * Total credit of accepted transactions
     */
    private BigDecimal creditOfAcceptedTransactions;


    /**
     * The number of rejected transactions in the batch
     */
    private Integer numberOfRejectedTransactions;

    /**
     * Total volume of rejected transactions
     */
    private BigDecimal volumeOfRejectedTransactions;

    /**
     * Reference to incoming XML document
     */
    private XmlDocument incomingXml;

    /**
     * Reference to outgoing XML document
     */
    private XmlDocument outgoingXml;


    private BatchReceiptStatus status;

    private String statusCode;


    @PostLoad
    protected void populateTransientFields() {
        status = (statusCode != null) ? EnumUtils.findById(BatchReceiptStatus.class, statusCode) : null;
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

    @Column(name = "EXTERNAL_ID")
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Column(name = "BATCH_DATE")
    public Date getBatchDate() {
        return batchDate;
    }

    public void setBatchDate(Date batchDate) {
        this.batchDate = batchDate;
    }

    @Column(name = "RECEIPT_DATE")
    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACNT_ID_FK")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Column(name = "TOTAL_TRANS")
    public Integer getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(Integer numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    @Column(name = "TOTAL_VOLUME")
    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    @Column(name = "TOTAL_ACCEPTED")
    public Integer getNumberOfAcceptedTransactions() {
        return numberOfAcceptedTransactions;
    }

    public void setNumberOfAcceptedTransactions(Integer numberOfAcceptedTransactions) {
        this.numberOfAcceptedTransactions = numberOfAcceptedTransactions;
    }

    @Column(name = "TOTAL_ACCEPTED_DEBITS")
    public BigDecimal getDebitOfAcceptedTransactions() {
        return debitOfAcceptedTransactions;
    }

    public void setDebitOfAcceptedTransactions(BigDecimal debitOfAcceptedTransactions) {
        this.debitOfAcceptedTransactions = debitOfAcceptedTransactions;
    }

    @Column(name = "TOTAL_ACCEPTED_CREDITS")
    public BigDecimal getCreditOfAcceptedTransactions() {
        return creditOfAcceptedTransactions;
    }

    public void setCreditOfAcceptedTransactions(BigDecimal creditOfAcceptedTransactions) {
        this.creditOfAcceptedTransactions = creditOfAcceptedTransactions;
    }

    @Column(name = "TOTAL_REJECTED")
    public Integer getNumberOfRejectedTransactions() {
        return numberOfRejectedTransactions;
    }

    public void setNumberOfRejectedTransactions(Integer numberOfRejectedTransactions) {
        this.numberOfRejectedTransactions = numberOfRejectedTransactions;
    }

    @Column(name = "TOTAL_VOLUME_REJECTED")
    public BigDecimal getVolumeOfRejectedTransactions() {
        return volumeOfRejectedTransactions;
    }

    public void setVolumeOfRejectedTransactions(BigDecimal volumeOfRejectedTransactions) {
        this.volumeOfRejectedTransactions = volumeOfRejectedTransactions;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INCOMING_XML_ID_FK")
    public XmlDocument getIncomingXml() {
        return incomingXml;
    }

    public void setIncomingXml(XmlDocument incomingXml) {
        this.incomingXml = incomingXml;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OUTGOING_XML_ID_FK")
    public XmlDocument getOutgoingXml() {
        return outgoingXml;
    }

    public void setOutgoingXml(XmlDocument outgoingXml) {
        this.outgoingXml = outgoingXml;
    }

    @Transient
    public BatchReceiptStatus getStatus() {
        return status;
    }

    public void setStatus(BatchReceiptStatus status) {
        this.status = status;
        statusCode = status.getId();
    }

    protected void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        status = EnumUtils.findById(BatchReceiptStatus.class, statusCode);
    }

    @Column(name = "STATUS", length = 1)
    protected String getStatusCode() {
        return statusCode;
    }

}



