package com.sigmasys.kuali.ksa.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


/**
 * Abstract GL Breakdown Override model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_GL_BREAKDOWN_OVERRIDE")
@DiscriminatorColumn(name = "TYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractGlBreakdownOverride extends AbstractGlBreakdown {


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

}



