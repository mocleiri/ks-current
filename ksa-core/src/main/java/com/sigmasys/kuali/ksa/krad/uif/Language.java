package com.sigmasys.kuali.ksa.model;


import javax.persistence.*;

/**
 * Language model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_LANGUAGE")
public class Language extends AuditableEntity {

    private String locale;

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_LANGUAGE",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "LANGUAGE_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_LANGUAGE")
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
