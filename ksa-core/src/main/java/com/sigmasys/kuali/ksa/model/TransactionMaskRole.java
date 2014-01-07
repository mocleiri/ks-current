package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

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
public class TransactionMaskRole extends AuditableEntity<Long> {

    private String typeMask;

    private String roleName;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "TYPE_MASK", length = 255)
    public String getTypeMask() {
        return typeMask;
    }

    public void setTypeMask(String typeMask) {
        this.typeMask = typeMask;
    }

    @Column(name = "ROLE_NAME", length = 255)
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}