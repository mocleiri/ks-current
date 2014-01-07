package com.sigmasys.kuali.ksa.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Currency model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_CURRENCY",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"CODE"}), @UniqueConstraint(columnNames = {"NAME"})})
@AttributeOverrides({
        @AttributeOverride(name = "code", column = @Column(name = "CODE", length = 5, nullable = false, unique = true)),
        @AttributeOverride(name = "name", column = @Column(name = "NAME", length = 80, nullable = false, unique = true))
})
public class Currency extends AuditableEntity<Long> {


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

}
