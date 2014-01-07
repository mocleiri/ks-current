package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.util.EnumUtils;

import javax.persistence.*;


/**
 * General Ledger Breakdown model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_GL_BREAKDOWN")
public class GlBreakdown extends AbstractGlBreakdown {

    /**
     * GL operation
     */
    private GlOperationType glOperation;

    /**
     * GL operation code (C or D)
     */
    private String glOperationCode;

    /**
     * Reference to DEBIT type
     */
    private DebitType debitType;

    /**
     * Reference to general ledger type
     */
    private GeneralLedgerType generalLedgerType;

    @PrePersist
    void populateDBFields() {
        glOperationCode = (glOperation != null) ? glOperation.getId() : null;
    }

    @PostLoad
    void populateTransientFields() {
        glOperation = (glOperationCode != null) ? EnumUtils.findById(GlOperationType.class, glOperationCode) : null;
    }


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_GL_BR",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "GL_BREAKDOWN_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_GL_BR")
    @Override
    public Long getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "TRANSACTION_TYPE_ID_FK", referencedColumnName = "ID"),
            @JoinColumn(name = "TRANSACTION_TYPE_SUB_CODE_FK", referencedColumnName = "SUB_CODE")
    })
    public DebitType getDebitType() {
        return debitType;
    }

    public void setDebitType(DebitType debitType) {
        this.debitType = debitType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GL_TYPE_ID_FK")
    public GeneralLedgerType getGeneralLedgerType() {
        return generalLedgerType;
    }

    public void setGeneralLedgerType(GeneralLedgerType generalLedgerType) {
        this.generalLedgerType = generalLedgerType;
    }

    @Column(name = "GL_OPERATION", length = 1)
    protected String getGlOperationCode() {
        return glOperationCode;
    }

    protected void setGlOperationCode(String glOperationCode) {
        this.glOperationCode = glOperationCode;
    }

    @Transient
    public GlOperationType getGlOperation() {
        return glOperation;
    }

    public void setGlOperation(GlOperationType glOperation) {
        this.glOperation = glOperation;
    }
}
	


