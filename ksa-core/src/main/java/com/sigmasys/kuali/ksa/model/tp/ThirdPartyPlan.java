package com.sigmasys.kuali.ksa.model.tp;

import com.sigmasys.kuali.ksa.model.AuditableEntity;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.ThirdPartyAccount;
import com.sigmasys.kuali.ksa.model.TransferType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Third-party Plan model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_TP_PLAN")
@AttributeOverride(name = "code", column = @Column(name = "CODE", length = 20, nullable = false, unique = true))
public class ThirdPartyPlan extends AuditableEntity<Long> {

    private ThirdPartyAccount thirdPartyAccount;

    private TransferType transferType;

    private BigDecimal maxAmount;

    private Date effectiveDate;

    private Date recognitionDate;

    private Date openPeriodStartDate;

    private Date openPeriodEndDate;

    private Date chargePeriodStartDate;

    private Date chargePeriodEndDate;

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACNT_ID_FK", nullable = false)
    public ThirdPartyAccount getThirdPartyAccount() {
        return thirdPartyAccount;
    }

    public void setThirdPartyAccount(ThirdPartyAccount thirdPartyAccount) {
        this.thirdPartyAccount = thirdPartyAccount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSFER_TYPE_ID_FK")
    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    @Column(name = "MAX_AMOUNT")
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_DATE")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "RECOGNITION_DATE")
    public Date getRecognitionDate() {
        return recognitionDate;
    }

    public void setRecognitionDate(Date recognitionDate) {
        this.recognitionDate = recognitionDate;
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
}
