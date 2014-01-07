package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.KeyPair;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * Rate model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_RATE", uniqueConstraints = {@UniqueConstraint(columnNames = {"CODE", "SUB_CODE", "RATE_CATALOG_ATP_ID_FK"})})
public class Rate extends AbstractRateEntity {

    private String subCode;

    private String atpId;

    private Date transactionDate;

    private Date recognitionDate;

    private RateCatalogAtp rateCatalogAtp;

    private Set<RateAmount> rateAmounts;

    private RateAmount defaultRateAmount;

    private BigDecimal limitAmountValue;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "SUB_CODE", nullable = false, length = 30)
    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    @Column(name = "RATE_CATALOG_ATP_ID_FK", insertable = false, updatable = false, nullable = false, length = 45)
    public String getAtpId() {
        return atpId;
    }

    public void setAtpId(String atpId) {
        this.atpId = atpId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "TRANSACTION_DATE")
    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "RECOGNITION_DATE")
    public Date getRecognitionDate() {
        return recognitionDate;
    }

    public void setRecognitionDate(Date recognitionDate) {
        this.recognitionDate = recognitionDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "RATE_CATALOG_CODE_FK", referencedColumnName = "RATE_CATALOG_CODE"),
            @JoinColumn(name = "RATE_CATALOG_ATP_ID_FK", referencedColumnName = "ATP_ID")
    })
    public RateCatalogAtp getRateCatalogAtp() {
        return rateCatalogAtp;
    }

    public void setRateCatalogAtp(RateCatalogAtp rateCatalogAtp) {
        this.rateCatalogAtp = rateCatalogAtp;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "RATE_ID_FK")
    public Set<RateAmount> getRateAmounts() {
        return rateAmounts;
    }

    public void setRateAmounts(Set<RateAmount> rateAmounts) {
        this.rateAmounts = rateAmounts;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFAULT_RATE_AMOUNT_ID_FK", nullable = false)
    public RateAmount getDefaultRateAmount() {
        return defaultRateAmount;
    }

    public void setDefaultRateAmount(RateAmount defaultRateAmount) {
        this.defaultRateAmount = defaultRateAmount;
    }

    @Column(name = "LIMIT_AMOUNT")
    public BigDecimal getLimitAmountValue() {
        return limitAmountValue;
    }

    public void setLimitAmountValue(BigDecimal limitAmountValue) {
        this.limitAmountValue = limitAmountValue;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_RATE_KEY_PAIR",
            joinColumns = {
                    @JoinColumn(name = "RATE_ID_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "RATE_KEY_PAIR_ID_FK")
            }
    )
    public Set<KeyPair> getKeyPairs() {
        return keyPairs;
    }

}
