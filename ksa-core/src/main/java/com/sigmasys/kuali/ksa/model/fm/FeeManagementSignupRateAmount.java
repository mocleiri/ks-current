package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.Identifiable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Fee management signup rate amount model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_FM_SIGNUP_RATE_AMOUNT")
public class FeeManagementSignupRateAmount implements Identifiable {

    private Long id;

    private FeeManagementSignupRate signupRate;

    private String transactionTypeId;

    private Integer units;

    private BigDecimal amount;


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
    @JoinColumn(name = "FM_SIGNUP_RATE_ID_FK")
    public FeeManagementSignupRate getSignupRate() {
        return signupRate;
    }

    public void setSignupRate(FeeManagementSignupRate signupRate) {
        this.signupRate = signupRate;
    }

    @Column(name = "TRANSACTION_TYPE_ID", length = 20)
    public String getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(String transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    @Column(name = "UNITS")
    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    @Column(name = "AMOUNT")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
