package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

/**
 * TransactionMaskRole model.
 * It represents a mapping of transaction masks used by KSA to KIM roles
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_TRANSACTION_MASK_ROLE")
public class TransactionMaskRole extends AuditableEntity {

    private String typeMask;

    private String roleName;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_TRAN_MASK_ROLE",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "TRANSACTION_MASK_ROLE_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_TRAN_MASK_ROLE")
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "TYPE_MASK", length = 512)
    public String getTypeMask() {
        return typeMask;
    }

    public void setTypeMask(String typeMask) {
        this.typeMask = typeMask;
    }

    @Column(name = "ROLE_NAME", length = 256)
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}