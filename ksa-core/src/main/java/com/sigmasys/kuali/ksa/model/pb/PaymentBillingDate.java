package com.sigmasys.kuali.ksa.model.pb;

import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.Identifiable;
import com.sigmasys.kuali.ksa.model.Rollup;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Payment Billing Date model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_PB_DATE")
public class PaymentBillingDate implements Identifiable {

    private Long id;

    private PaymentBillingPlan plan;

    private Date effectiveDate;

    private BigDecimal percentage;

    private Rollup rollup;


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
    @JoinColumn(name = "PB_PLAN_ID_FK")
    public PaymentBillingPlan getPlan() {
        return plan;
    }

    public void setPlan(PaymentBillingPlan plan) {
        this.plan = plan;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_DATE")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Column(name = "PERCENTAGE")
    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLLUP_ID_FK")
    public Rollup getRollup() {
        return rollup;
    }

    public void setRollup(Rollup rollup) {
        this.rollup = rollup;
    }
}
