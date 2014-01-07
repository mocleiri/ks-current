package com.sigmasys.kuali.ksa.model.pb;

import com.sigmasys.kuali.ksa.model.Charge;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.DirectChargeAccount;
import com.sigmasys.kuali.ksa.model.Identifiable;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Payment Billing Transfer Detail.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_PB_TRANSFER_DETAIL")
public class PaymentBillingTransferDetail implements Identifiable {

    /**
     * Identifier
     */
    private Long id;

    /**
     * The account from which the charges will be transferred. (The beneficiary account).
     */
    private DirectChargeAccount directChargeAccount;

    /**
     * The plan under which the transfer was made.
     */
    private PaymentBillingPlan plan;

    /**
     * Flat-fee transaction
     */
    private Charge flatFeeCharge;

    /**
     * Variable-fee transaction
     */
    private Charge variableFeeCharge;

    /**
     * Initiation date
     */
    private Date initiationDate;

    /**
     * Transfer Group ID
     */
    private String transferGroupId;

    /**
     * Maximum requested amount
     */
    private BigDecimal maxAmount;

    /**
     * Total amount that was financed by this plan. Equal to or less than "maxAmount".
     */
    private BigDecimal planAmount;

    /**
     * Payment billing charge status
     */
    protected PaymentBillingChargeStatus chargeStatus;

    /**
     * Payment billing charge status code
     */
    protected String chargeStatusCode;


    @PostLoad
    protected void populateTransientFields() {
        chargeStatus = (chargeStatusCode != null) ? EnumUtils.findById(PaymentBillingChargeStatus.class, chargeStatusCode) : null;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACNT_ID_FK")
    public DirectChargeAccount getDirectChargeAccount() {
        return directChargeAccount;
    }

    public void setDirectChargeAccount(DirectChargeAccount directChargeAccount) {
        this.directChargeAccount = directChargeAccount;
    }

    @Column(name = "TRANSFER_GROUP_ID", length = 100)
    public String getTransferGroupId() {
        return transferGroupId;
    }

    public void setTransferGroupId(String transferGroupId) {
        this.transferGroupId = transferGroupId;
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
    @JoinColumn(name = "FLAT_FEE_CHARGE_ID_FK")
    public Charge getFlatFeeCharge() {
        return flatFeeCharge;
    }

    public void setFlatFeeCharge(Charge flatFeeCharge) {
        this.flatFeeCharge = flatFeeCharge;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VAR_FEE_CHARGE_ID_FK")
    public Charge getVariableFeeCharge() {
        return variableFeeCharge;
    }

    public void setVariableFeeCharge(Charge variableFeeCharge) {
        this.variableFeeCharge = variableFeeCharge;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "INIT_DATE")
    public Date getInitiationDate() {
        return initiationDate;
    }

    public void setInitiationDate(Date initiationDate) {
        this.initiationDate = initiationDate;
    }

    @Column(name = "MAX_AMOUNT", nullable = false)
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Column(name = "PLAN_AMOUNT", nullable = false)
    public BigDecimal getPlanAmount() {
        return planAmount;
    }

    public void setPlanAmount(BigDecimal planAmount) {
        this.planAmount = planAmount;
    }

    @Column(name = "STATUS", length = 1)
    protected String getChargeStatusCode() {
        return chargeStatusCode;
    }

    protected void setChargeStatusCode(String chargeStatusCode) {
        this.chargeStatusCode = chargeStatusCode;
        chargeStatus = EnumUtils.findById(PaymentBillingChargeStatus.class, chargeStatusCode);
    }

    @Transient
    public PaymentBillingChargeStatus getChargeStatus() {
        return chargeStatus;
    }

    public void setChargeStatus(PaymentBillingChargeStatus chargeStatus) {
        this.chargeStatus = chargeStatus;
        chargeStatusCode = chargeStatus.getId();
    }

    @Override
    public String toString() {
        return "PaymentBillingTransferDetail{" +
                "id=" + id +
                ", directChargeAccount=" + directChargeAccount +
                ", plan=" + plan +
                ", flatFeeCharge=" + flatFeeCharge +
                ", variableFeeCharge=" + variableFeeCharge +
                ", initiationDate=" + initiationDate +
                ", transferGroupId='" + transferGroupId + '\'' +
                ", maxAmount=" + maxAmount +
                ", planAmount=" + planAmount +
                ", chargeStatus=" + chargeStatus +
                ", chargeStatusCode='" + chargeStatusCode + '\'' +
                '}';
    }
}
