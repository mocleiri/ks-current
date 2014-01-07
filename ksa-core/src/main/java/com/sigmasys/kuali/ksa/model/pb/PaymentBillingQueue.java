package com.sigmasys.kuali.ksa.model.pb;

import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.DirectChargeAccount;
import com.sigmasys.kuali.ksa.model.Identifiable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Payment Billing Queue model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_PB_QUEUE", uniqueConstraints = {@UniqueConstraint(columnNames = {"ACNT_ID_FK", "PB_PLAN_ID_FK"})})
public class PaymentBillingQueue implements Identifiable {

    /**
     * The entity ID
     */
    private Long id;

    /**
     * The reference to the account signed up to the corresponding billing plan.
     */
    private DirectChargeAccount directChargeAccount;

    /**
     * The specific payment billing plan being signed up to.
     */
    private PaymentBillingPlan plan;

    /**
     * The reference to the transfer detail object, it is not null if the plan has been executed
     */
    private PaymentBillingTransferDetail transferDetail;

    /**
     * The user ID of the person created this queue
     */
    private String creatorId;

    /**
     * The queue creation date
     */
    private Date creationDate;

    /**
     * The date on which the billing plan was added to the account.
     * This will value be validated against the open period in order to test validity.
     */
    private Date initiationDate;

    /**
     * If the plan has already been executed, it will be ignored, unless this is set to true.
     */
    private Boolean forceReversal;


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
    public DirectChargeAccount getDirectChargeAccount() {
        return directChargeAccount;
    }

    public void setDirectChargeAccount(DirectChargeAccount directChargeAccount) {
        this.directChargeAccount = directChargeAccount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PB_PLAN_ID_FK")
    public PaymentBillingPlan getPlan() {
        return plan;
    }

    public void setPlan(PaymentBillingPlan plan) {
        this.plan = plan;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PB_TRANSFER_DETAIL_ID_FK")
    public PaymentBillingTransferDetail getTransferDetail() {
        return transferDetail;
    }

    public void setTransferDetail(PaymentBillingTransferDetail transferDetail) {
        this.transferDetail = transferDetail;
    }

    @Column(name = "CREATOR_ID", length = 45)
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "INIT_DATE")
    public Date getInitiationDate() {
        return initiationDate;
    }

    public void setInitiationDate(Date initiationDate) {
        this.initiationDate = initiationDate;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "FORCE_REVERSAL")
    public Boolean isForceReversal() {
        return forceReversal != null ? forceReversal : false;
    }

    public void setForceReversal(Boolean forceReversal) {
        this.forceReversal = forceReversal;
    }
}
