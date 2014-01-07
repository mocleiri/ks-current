package com.sigmasys.kuali.ksa.model.pb;

import com.sigmasys.kuali.ksa.model.AuditableEntity;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.TransferType;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Payment Billing Plan model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_PB_PLAN")
@AttributeOverride(name = "code", column = @Column(name = "CODE", length = 20, nullable = false, unique = true))
public class PaymentBillingPlan extends AuditableEntity<Long> {


    private TransferType transferType;

    private String flatFeeDebitTypeId;

    private String variableFeeDebitTypeId;

    private Date openPeriodStartDate;

    private Date openPeriodEndDate;

    private Date chargePeriodStartDate;

    private Date chargePeriodEndDate;

    private BigDecimal maxAmount;

    private BigDecimal flatFeeAmount;

    private BigDecimal variableFeeAmount;

    private BigDecimal minFeeAmount;

    private BigDecimal maxFeeAmount;

    private Integer roundingFactor;

    private Boolean isGlCreationImmediate;

    private String statementPrefix;

    private PaymentRoundingType paymentRoundingType;

    private String paymentRoundingTypeCode;

    private ScheduleType scheduleType;

    private String scheduleTypeCode;


    @PostLoad
    protected void populateTransientFields() {
        paymentRoundingType = (paymentRoundingTypeCode != null) ? EnumUtils.findById(PaymentRoundingType.class, paymentRoundingTypeCode) : null;
        scheduleType = (scheduleTypeCode != null) ? EnumUtils.findById(ScheduleType.class, scheduleTypeCode) : null;
    }


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSFER_TYPE_ID_FK")
    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    @Column(name = "FLAT_FEE_DEBIT_TYPE_ID", length = 20, nullable = false)
    public String getFlatFeeDebitTypeId() {
        return flatFeeDebitTypeId;
    }

    public void setFlatFeeDebitTypeId(String flatFeeDebitTypeId) {
        this.flatFeeDebitTypeId = flatFeeDebitTypeId;
    }

    @Column(name = "VAR_FEE_DEBIT_TYPE_ID", length = 20, nullable = false)
    public String getVariableFeeDebitTypeId() {
        return variableFeeDebitTypeId;
    }

    public void setVariableFeeDebitTypeId(String variableFeeDebitTypeId) {
        this.variableFeeDebitTypeId = variableFeeDebitTypeId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "START_OPEN_PERIOD", nullable = false)
    public Date getOpenPeriodStartDate() {
        return openPeriodStartDate;
    }

    public void setOpenPeriodStartDate(Date openPeriodStartDate) {
        this.openPeriodStartDate = openPeriodStartDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "END_OPEN_PERIOD", nullable = false)
    public Date getOpenPeriodEndDate() {
        return openPeriodEndDate;
    }

    public void setOpenPeriodEndDate(Date openPeriodEndDate) {
        this.openPeriodEndDate = openPeriodEndDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "START_CHARGE_PERIOD", nullable = false)
    public Date getChargePeriodStartDate() {
        return chargePeriodStartDate;
    }

    public void setChargePeriodStartDate(Date chargePeriodStartDate) {
        this.chargePeriodStartDate = chargePeriodStartDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "END_CHARGE_PERIOD", nullable = false)
    public Date getChargePeriodEndDate() {
        return chargePeriodEndDate;
    }

    public void setChargePeriodEndDate(Date chargePeriodEndDate) {
        this.chargePeriodEndDate = chargePeriodEndDate;
    }

    @Column(name = "MAX_AMOUNT", nullable = false)
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Column(name = "FLAT_FEE_AMOUNT")
    public BigDecimal getFlatFeeAmount() {
        return flatFeeAmount;
    }

    public void setFlatFeeAmount(BigDecimal flatFeeAmount) {
        this.flatFeeAmount = flatFeeAmount;
    }

    @Column(name = "VAR_FEE_AMOUNT")
    public BigDecimal getVariableFeeAmount() {
        return variableFeeAmount;
    }

    public void setVariableFeeAmount(BigDecimal variableFeeAmount) {
        this.variableFeeAmount = variableFeeAmount;
    }

    @Column(name = "MIN_FEE_AMOUNT")
    public BigDecimal getMinFeeAmount() {
        return minFeeAmount;
    }

    public void setMinFeeAmount(BigDecimal minFeeAmount) {
        this.minFeeAmount = minFeeAmount;
    }

    @Column(name = "MAX_FEE_AMOUNT")
    public BigDecimal getMaxFeeAmount() {
        return maxFeeAmount;
    }

    public void setMaxFeeAmount(BigDecimal maxFeeAmount) {
        this.maxFeeAmount = maxFeeAmount;
    }

    @Column(name = "ROUND_FACTOR")
    public Integer getRoundingFactor() {
        return roundingFactor;
    }

    public void setRoundingFactor(Integer roundingFactor) {
        this.roundingFactor = roundingFactor;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_GL_IMMEDIATE")
    public Boolean isGlCreationImmediate() {
        return isGlCreationImmediate != null ? isGlCreationImmediate : false;
    }

    public void setGlCreationImmediate(Boolean glCreationImmediate) {
        isGlCreationImmediate = glCreationImmediate;
    }

    @Column(name = "STMT_PREFIX", length = 100)
    public String getStatementPrefix() {
        return statementPrefix;
    }

    public void setStatementPrefix(String statementPrefix) {
        this.statementPrefix = statementPrefix;
    }

    @Column(name = "PAYMENT_ROUND_TYPE", length = 1)
    protected String getPaymentRoundingTypeCode() {
        return paymentRoundingTypeCode;
    }

    protected void setPaymentRoundingTypeCode(String paymentRoundingTypeCode) {
        this.paymentRoundingTypeCode = paymentRoundingTypeCode;
        paymentRoundingType = EnumUtils.findById(PaymentRoundingType.class, paymentRoundingTypeCode);
    }

    @Column(name = "SCHEDULE_TYPE", length = 1)
    protected String getScheduleTypeCode() {
        return scheduleTypeCode;
    }

    protected void setScheduleTypeCode(String scheduleTypeCode) {
        this.scheduleTypeCode = scheduleTypeCode;
        scheduleType = EnumUtils.findById(ScheduleType.class, scheduleTypeCode);
    }

    @Transient
    public PaymentRoundingType getPaymentRoundingType() {
        return paymentRoundingType;
    }

    public void setPaymentRoundingType(PaymentRoundingType paymentRoundingType) {
        this.paymentRoundingType = paymentRoundingType;
        paymentRoundingTypeCode = paymentRoundingType.getId();
    }

    @Transient
    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
        scheduleTypeCode = scheduleType.getId();
    }
}
