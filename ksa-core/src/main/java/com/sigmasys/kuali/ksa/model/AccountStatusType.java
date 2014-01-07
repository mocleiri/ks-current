package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Account status type.
 * <p/>
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_ACNT_STATUS_TYPE")
public class AccountStatusType extends AuditableEntity<Long> {


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }
}
