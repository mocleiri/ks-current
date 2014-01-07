package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.AuditableEntity;
import com.sigmasys.kuali.ksa.model.KeyPair;
import com.sigmasys.kuali.ksa.model.KeyPairAware;
import com.sigmasys.kuali.ksa.util.EnumUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Set;

/**
 * Abstract Rate entity.
 * <p/>
 *
 * @author Michael Ivanov
 */
@MappedSuperclass
@AttributeOverride(name = "code", column = @Column(name = "CODE", length = 100, nullable = false))
public abstract class AbstractRateEntity extends AuditableEntity<Long> implements KeyPairAware {

    protected Boolean isLimitAmount;

    protected RateType rateType;

    protected Set<KeyPair> keyPairs;

    protected Integer minLimitUnits;

    protected Integer maxLimitUnits;

    protected TransactionDateType transactionDateType;

    protected String transactionDateTypeCode;


    @Transient
    @XmlTransient
    public abstract Set<KeyPair> getKeyPairs();


    public void setKeyPairs(Set<KeyPair> keyPairs) {
        this.keyPairs = keyPairs;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_LIMIT_AMOUNT")
    public Boolean isLimitAmount() {
        return isLimitAmount != null ? isLimitAmount : false;
    }

    public void setLimitAmount(Boolean limitAmount) {
        isLimitAmount = limitAmount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATE_TYPE_ID_FK")
    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    @Column(name = "MIN_LIMIT_UNITS")
    public Integer getMinLimitUnits() {
        return minLimitUnits;
    }

    public void setMinLimitUnits(Integer minLimitUnits) {
        this.minLimitUnits = minLimitUnits;
    }

    @Column(name = "MAX_LIMIT_UNITS")
    public Integer getMaxLimitUnits() {
        return maxLimitUnits;
    }

    public void setMaxLimitUnits(Integer maxLimitUnits) {
        this.maxLimitUnits = maxLimitUnits;
    }

    @Column(name = "TRANS_DATE_TYPE", length = 10)
    protected String getTransactionDateTypeCode() {
        return transactionDateTypeCode;
    }

    protected void setTransactionDateTypeCode(String transactionDateTypeCode) {
        this.transactionDateTypeCode = transactionDateTypeCode;
        transactionDateType = EnumUtils.findById(TransactionDateType.class, transactionDateTypeCode);
    }

    @Transient
    public TransactionDateType getTransactionDateType() {
        return transactionDateType;
    }

    public void setTransactionDateType(TransactionDateType transactionDateType) {
        this.transactionDateType = transactionDateType;
        transactionDateTypeCode = (transactionDateType != null) ? transactionDateType.getId() : null;
    }

}
