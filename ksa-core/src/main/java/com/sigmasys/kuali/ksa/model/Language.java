package com.sigmasys.kuali.ksa.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Language model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_LANGUAGE")
public class Language extends AuditableEntity<Long> {

    private String locale;

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "LOCALE", unique = true, nullable = false, length = 20)
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
