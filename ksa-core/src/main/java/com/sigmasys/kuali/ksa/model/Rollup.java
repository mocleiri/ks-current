package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Transaction rollup.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_ROLLUP", uniqueConstraints = {@UniqueConstraint(columnNames = {"CODE"})})
public class Rollup extends AuditableEntity<Long> implements Cloneable {

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

}
