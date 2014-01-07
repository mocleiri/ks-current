package com.sigmasys.kuali.ksa.model;


import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;


/**
 * General ledger batch baseline model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_GL_BATCH_BASELINE")
public class GlBatchBaseline implements Identifiable {

    /**
     * The unique identifier
     */
    private Long id;

    /**
     * Batch ID
     */
    private String batchId;

    /**
     * Baseline amount
     */
    private BigDecimal amount;

    /**
     * General ledger type
     */
    private GeneralLedgerType generalLedgerType;

    /**
     * Baseline type
     */
    private GlBatchBaselineType type;

    /**
     * Baseline type code
     */
    private String typeCode;


    @PostLoad
    protected void populateTransientFields() {
        type = (typeCode != null) ? EnumUtils.findById(GlBatchBaselineType.class, typeCode) : null;
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

    @Column(name = "BATCH_ID", length = 45, nullable = false)
    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Column(name = "AMOUNT", nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GL_TYPE_ID_FK")
    public GeneralLedgerType getGeneralLedgerType() {
        return generalLedgerType;
    }

    public void setGeneralLedgerType(GeneralLedgerType generalLedgerType) {
        this.generalLedgerType = generalLedgerType;
    }

    @Column(name = "TYPE", length = 1, nullable = false)
    protected String getTypeCode() {
        return typeCode;
    }

    protected void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        type = EnumUtils.findById(GlBatchBaselineType.class, typeCode);
    }

    @Transient
    public GlBatchBaselineType getType() {
        return type;
    }

    public void setType(GlBatchBaselineType type) {
        this.type = type;
        typeCode = type.getId();
    }

    @Override
    public String toString() {
        return "GlBatchBaseline{" +
                "id=" + id +
                ", batchId='" + batchId + '\'' +
                ", amount=" + amount +
                ", generalLedgerType=" + generalLedgerType +
                ", type=" + type +
                ", typeCode='" + typeCode + '\'' +
                '}';
    }
}



