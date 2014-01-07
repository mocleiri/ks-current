package com.sigmasys.kuali.ksa.model.tp;

import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.Identifiable;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Third-party allowable charge model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_TP_ALLOWABLE_CHARGE")
public class ThirdPartyAllowableCharge implements Identifiable {

    /**
     * Identifier
     */
    private Long id;

    /**
     * The plan assigned to an eligible account
     */
    private ThirdPartyPlan plan;

    /**
     * Transaction Type mask.
     * A regular expression used against the transaction type of transactions to see if they are eligible for transfer.
     */
    private String transactionTypeMask;

    /**
     * If only a percentage of a certain type of fee is allowed to be transferred
     * (i.e. “50% of bookstore charges”) then this percentage will be stored here.
     */
    private BigDecimal maxPercentage;

    /**
     * Total amount that can be transferred (i.e no more than $2000 will be paid for tuition).
     */
    private BigDecimal maxAmount;

    /**
     * Order in which the allowable charges will be sought.
     * This field is only important if the amount of the transfer is limited.
     */
    private Integer priority;

    /**
     * ChargeDistributionPlan
     */
    private ChargeDistributionPlan distributionPlan;


    /**
     * ChargeDistributionPlan code
     */
    private String distributionPlanCode;


    @PostLoad
    protected void populateTransientFields() {
        distributionPlan = (distributionPlanCode != null) ? EnumUtils.findById(ChargeDistributionPlan.class, distributionPlanCode) : null;
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
    @JoinColumn(name = "TP_PLAN_ID_FK")
    public ThirdPartyPlan getPlan() {
        return plan;
    }

    public void setPlan(ThirdPartyPlan plan) {
        this.plan = plan;
    }

    @Column(name = "TRANSACTION_TYPE_MASK", length = 500, nullable = false)
    public String getTransactionTypeMask() {
        return transactionTypeMask;
    }

    public void setTransactionTypeMask(String transactionTypeMask) {
        this.transactionTypeMask = transactionTypeMask;
    }

    @Column(name = "MAX_PERCENTAGE", nullable = false)
    public BigDecimal getMaxPercentage() {
        return maxPercentage;
    }

    public void setMaxPercentage(BigDecimal maxPercentage) {
        this.maxPercentage = maxPercentage;
    }

    @Column(name = "MAX_AMOUNT")
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Column(name = "PRIORITY")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(name = "DIST_PLAN", length = 1)
    public String getDistributionPlanCode() {
        return distributionPlanCode;
    }

    public void setDistributionPlanCode(String distributionPlanCode) {
        this.distributionPlanCode = distributionPlanCode;
        distributionPlan = EnumUtils.findById(ChargeDistributionPlan.class, distributionPlanCode);
    }

    @Transient
    public ChargeDistributionPlan getDistributionPlan() {
        return distributionPlan;
    }

    public void setDistributionPlan(ChargeDistributionPlan distributionPlan) {
        this.distributionPlan = distributionPlan;
        distributionPlanCode = distributionPlan.getId();
    }


}
