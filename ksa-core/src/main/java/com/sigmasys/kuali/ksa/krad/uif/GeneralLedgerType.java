package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.util.EnumUtils;

import javax.persistence.*;

/**
 * General ledger type.
 * <p/>
 * User: mike
 * Date: 1/22/12
 * Time: 3:47 PM
 */
@Entity
@Table(name = "KSSA_GL_TYPE")
public class GeneralLedgerType extends AuditableEntity {

    /**
     * General Ledger Account ID
     */
    private String glAccountId;

    /**
     * General Ledger Operation on Charge
     */
    private GlOperationType glOperationOnCharge;

    /**
     * GL operation code (C or D)
     */
    private String glOperationCode;


    @PrePersist
    void populateDBFields() {
        glOperationCode = (glOperationOnCharge != null) ? glOperationOnCharge.getId() : null;
    }

    @PostLoad
    void populateTransientFields() {
        glOperationOnCharge = (glOperationCode != null) ?
                EnumUtils.findById(GlOperationType.class, glOperationCode) : null;
    }


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_GL_TYPE",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "GL_TYPE_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_GL_TYPE")
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "GL_ASSET_ACCOUNT", length = 45)
    public String getGlAccountId() {
        return glAccountId;
    }

    public void setGlAccountId(String glAccountId) {
        this.glAccountId = glAccountId;
    }

    @Column(name = "GL_OPERATION_ON_CHARGE", length = 1)
    protected String getGlOperationCode() {
        return glOperationCode;
    }

    protected void setGlOperationCode(String glOperationCode) {
        this.glOperationCode = glOperationCode;
    }

    @Transient
    public GlOperationType getGlOperationOnCharge() {
        return glOperationOnCharge;
    }

    public void setGlOperationOnCharge(GlOperationType glOperationOnCharge) {
        this.glOperationOnCharge = glOperationOnCharge;
    }
}
