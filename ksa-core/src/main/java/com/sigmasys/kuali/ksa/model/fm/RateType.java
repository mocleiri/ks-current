package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.AuditableEntity;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Rate type.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_RATE_TYPE")
@AttributeOverride(name = "code", column = @Column(name = "CODE", length = 100, nullable = false, unique = true))
public class RateType extends AuditableEntity<Long> {


    private Boolean isGrouping;

    private String rateAmountTypeCode;

    private RateAmountType rateAmountType;

    @PostLoad
    protected void populateTransientFields() {
        rateAmountType = (rateAmountTypeCode != null) ? EnumUtils.findById(RateAmountType.class, rateAmountTypeCode) : null;
    }

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_GROUPING")
    public Boolean isGrouping() {
        return isGrouping != null ? isGrouping : false;
    }

    public void setGrouping(Boolean grouping) {
        isGrouping = grouping;
    }

    @Column(name = "RATE_AMOUNT_TYPE", length = 10)
    protected String getRateAmountTypeCode() {
        return rateAmountTypeCode;
    }

    protected void setRateAmountTypeCode(String rateAmountTypeCode) {
        this.rateAmountTypeCode = rateAmountTypeCode;
        rateAmountType = EnumUtils.findById(RateAmountType.class, rateAmountTypeCode);
    }

    @Transient
    public RateAmountType getRateAmountType() {
        return rateAmountType;
    }

    public void setRateAmountType(RateAmountType rateAmountType) {
        this.rateAmountType = rateAmountType;
        rateAmountTypeCode = rateAmountType.getId();
    }
}
