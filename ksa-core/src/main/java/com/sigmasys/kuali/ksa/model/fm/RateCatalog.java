package com.sigmasys.kuali.ksa.model.fm;


import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.KeyPair;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Rate catalog model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_RATE_CATALOG")
public class RateCatalog extends AbstractRateEntity {


    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal minLimitAmount;

    private BigDecimal maxLimitAmount;

    private String transactionTypeId;

    private Boolean isTransactionTypeFinal;

    private Boolean isLimitAmountFinal;

    private Boolean isKeyPairFinal;

    private Boolean isRecognitionDateDefinable;

    private Boolean isTransactionDateTypeFinal;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "MIN_AMOUNT")
    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    @Column(name = "MAX_AMOUNT")
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Column(name = "MIN_LIMIT_AMOUNT")
    public BigDecimal getMinLimitAmount() {
        return minLimitAmount;
    }

    public void setMinLimitAmount(BigDecimal minLimitAmount) {
        this.minLimitAmount = minLimitAmount;
    }

    @Column(name = "MAX_LIMIT_AMOUNT")
    public BigDecimal getMaxLimitAmount() {
        return maxLimitAmount;
    }

    public void setMaxLimitAmount(BigDecimal maxLimitAmount) {
        this.maxLimitAmount = maxLimitAmount;
    }

    @Column(name = "TRANSACTION_TYPE_ID", length = 20)
    public String getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(String transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_TRANS_TYPE_FINAL")
    public Boolean isTransactionTypeFinal() {
        return isTransactionTypeFinal != null ? isTransactionTypeFinal : false;
    }

    public void setTransactionTypeFinal(Boolean transactionTypeFinal) {
        isTransactionTypeFinal = transactionTypeFinal;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_LIMIT_AMOUNT_FINAL")
    public Boolean isLimitAmountFinal() {
        return isLimitAmountFinal != null ? isLimitAmountFinal : false;
    }

    public void setLimitAmountFinal(Boolean limitAmountFinal) {
        isLimitAmountFinal = limitAmountFinal;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_KEYPAIR_FINAL")
    public Boolean isKeyPairFinal() {
        return isKeyPairFinal != null ? isKeyPairFinal : false;
    }

    public void setKeyPairFinal(Boolean keyPairFinal) {
        isKeyPairFinal = keyPairFinal;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_RECOGNITION_DATE_DEFINABLE")
    public Boolean isRecognitionDateDefinable() {
        return isRecognitionDateDefinable != null ? isRecognitionDateDefinable : false;
    }

    public void setRecognitionDateDefinable(Boolean recognitionDateDefinable) {
        isRecognitionDateDefinable = recognitionDateDefinable;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_TRANS_DATE_TYPE_FINAL")
    public Boolean isTransactionDateTypeFinal() {
        return isTransactionDateTypeFinal != null ? isTransactionDateTypeFinal : false;
    }

    public void setTransactionDateTypeFinal(Boolean transactionDateTypeFinal) {
        isTransactionDateTypeFinal = transactionDateTypeFinal;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_RATE_CATALOG_KEY_PAIR",
            joinColumns = {
                    @JoinColumn(name = "RATE_CATALOG_ID_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "KEY_PAIR_ID_FK")
            }
    )
    public Set<KeyPair> getKeyPairs() {
        return keyPairs;
    }

}
