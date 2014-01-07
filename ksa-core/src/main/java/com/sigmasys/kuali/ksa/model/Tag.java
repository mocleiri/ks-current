package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A very simple Tag.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_TAG", uniqueConstraints = {@UniqueConstraint(columnNames = {"CODE"})})
public class Tag extends AuditableEntity<Long> {

    /**
     * Indicates whether the Tag is administrative or not.
     */
    private Boolean isAdministrative;

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_ADMINISTRATIVE")
    public Boolean isAdministrative() {
        return isAdministrative != null ? isAdministrative : false;
    }

    public void setAdministrative(Boolean isAdministrative) {
        this.isAdministrative = isAdministrative;
    }

}
