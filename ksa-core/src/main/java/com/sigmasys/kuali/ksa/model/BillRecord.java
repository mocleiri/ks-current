package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


/**
 * Bill record model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_BILL_RECORD")
public class BillRecord implements Identifiable {

    /**
     * The unique identifier
     */
    private Long id;

    /**
     * Account reference
     */
    private Account account;

    /**
     * Transactions associated with the current BillRecord
     */
    private Set<Transaction> transactions;

    /**
     * BillRecord's message
     */
    private String message;

    /**
     * Start date
     */
    private Date startDate;

    /**
     * End date
     */
    private Date endDate;

    /**
     * Bill date
     */
    private Date billDate;

    /**
     * Only not previously billed transactions will be shown in the bill
     */
    private Boolean showOnlyUnbilledTransactions;

    /**
     * Internal transactions will be shown in the bill
     */
    private Boolean showInternalTransactions;

    /**
     * Deferments will be shown in the bill
     */
    private Boolean showDeferments;

    /**
     * Dependents will be shown in the bill
     */
    private Boolean showDependents;

    /**
     * Creator ID
     */
    private String creatorId;

    /**
     * Creation date
     */
    private Date creationDate;


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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACNT_ID_FK")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_BILL_RECORD_TRANSACTION",
            joinColumns = {
                    @JoinColumn(name = "BILL_RECORD_ID_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "TRANSACTION_ID_FK")
            }
    )
    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Column(name = "MESSAGE", length = 2000)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "BILL_DATE")
    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "SHOW_ONLY_UNBILLED_TRANS")
    public Boolean isShowOnlyUnbilledTransactions() {
        return showOnlyUnbilledTransactions != null ? showOnlyUnbilledTransactions : false;
    }

    public void setShowOnlyUnbilledTransactions(Boolean showOnlyUnbilledTransactions) {
        this.showOnlyUnbilledTransactions = showOnlyUnbilledTransactions;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "SHOW_INTERNAL_TRANS")
    public Boolean isShowInternalTransactions() {
        return showInternalTransactions != null ? showInternalTransactions : false;
    }

    public void setShowInternalTransactions(Boolean showInternalTransactions) {
        this.showInternalTransactions = showInternalTransactions;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "SHOW_DEFERMENTS")
    public Boolean isShowDeferments() {
        return showDeferments != null ? showDeferments : false;
    }

    public void setShowDeferments(Boolean showDeferments) {
        this.showDeferments = showDeferments;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "SHOW_DEPENDENTS")
    public Boolean isShowDependents() {
        return showDependents != null ? showDependents : false;
    }

    public void setShowDependents(Boolean showDependents) {
        this.showDependents = showDependents;
    }

    @Column(name = "CREATOR_ID", length = 45)
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}



