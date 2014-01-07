package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

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
     * Reference to Transaction type
     */
    private TransactionType transactionType;

    /**
     * Reference to general ledger type
     */
    private GeneralLedgerType generalLedgerType;


    @PostLoad
    protected void populateTransientFields() {
        glOperation = (glOperationCode != null) ? EnumUtils.findById(GlOperationType.class, glOperationCode) : null;
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
    @JoinColumns({
            @JoinColumn(name = "TRANSACTION_TYPE_ID_FK", referencedColumnName = "ID"),
            @JoinColumn(name = "TRANSACTION_TYPE_SUB_CODE_FK", referencedColumnName = "SUB_CODE")
    })
    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
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
        glOperation = EnumUtils.findById(GlOperationType.class, glOperationCode);
    }

    @Transient
    public GlOperationType getGlOperation() {
        return glOperation;
    }

    public void setGlOperation(GlOperationType glOperation) {
        this.glOperation = glOperation;
        glOperationCode = glOperation.getId();
    }
}



