package com.sigmasys.kuali.ksa.model;


import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;


/**
 * Cash limit event model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_CASH_LIMIT_EVENT")
public class CashLimitEvent implements Identifiable {

    /**
     * The unique identifier
     */
    private Long id;

    /**
     * Account ID
     */
    private String accountId;

    /**
     * Creator ID
     */
    private String creatorId;

    /**
     * Event date
     */
    private Date eventDate;

    /**
     * Notification date
     */
    private Date notificationDate;

    /**
     * Report date
     */
    private Date reportDate;

    /**
     * A person name to whom the notification has been sent
     */
    private String recipient;

    /**
     * Total payment amount
     */
    private BigDecimal paymentAmount;

    /**
     * Total charge amount
     */
    private BigDecimal chargeAmount;

    /**
     * Serial numbers
     */
    private String serials;

    /**
     * If there is more than one transaction referenced in this event, this will be set to TRUE.
     * Otherwise, it will be FALSE.
     */
    private Boolean isMultiple;

    /**
     * Reference to XML document
     */
    private XmlDocument xmlDocument;

    /**
     * Payments associated with this cash limit event
     */
    private Set<Payment> payments;

    /**
     * Status
     */
    private CashLimitEventStatus status;

    /**
     * Status code
     */
    private String statusCode;


    @PostLoad
    protected void populateTransientFields() {
        status = (statusCode != null) ? EnumUtils.findById(CashLimitEventStatus.class, statusCode) : null;
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

    @Column(name = "ACCOUNT_ID", length = 45)
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Column(name = "CREATOR_ID", length = 45)
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Column(name = "EVENT_DATE")
    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    @Column(name = "NOTIF_DATE")
    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

    @Column(name = "REPORT_DATE")
    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    @Column(name = "NOTIF_RECIPIENT", length = 255)
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Column(name = "TOTAL_PAYMENT_AMOUNT")
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    @Column(name = "TOTAL_CHARGE_AMOUNT")
    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    @Column(name = "SERIALS", length = 4000)
    public String getSerials() {
        return serials;
    }

    public void setSerials(String serials) {
        this.serials = serials;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_MULTIPLE")
    public Boolean isMultiple() {
        return isMultiple != null ? isMultiple : false;
    }

    @Transient
    public Boolean getMultiple() {
        return isMultiple != null ? isMultiple : false;
    }

    public void setMultiple(Boolean multiple) {
        isMultiple = multiple;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "XML_ID_FK")
    public XmlDocument getXmlDocument() {
        return xmlDocument;
    }

    public void setXmlDocument(XmlDocument xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_CASH_LIMIT_EVENT_TRANS",
            joinColumns = {
                    @JoinColumn(name = "CASH_LIMIT_EVENT_ID_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "TRANSACTION_ID_FK")
            }
    )
    public Set<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    @Column(name = "STATUS", length = 1)
    protected String getStatusCode() {
        return statusCode;
    }

    protected void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        status = EnumUtils.findById(CashLimitEventStatus.class, statusCode);
    }

    @Transient
    public CashLimitEventStatus getStatus() {
        return status;
    }

    public void setStatus(CashLimitEventStatus status) {
        this.status = status;
        statusCode = status.getId();
    }

}



