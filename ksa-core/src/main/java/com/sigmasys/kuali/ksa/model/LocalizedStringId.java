package com.sigmasys.kuali.ksa.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The composite primary key for LocalizedString entity
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("serial")
@Embeddable
public class LocalizedStringId implements Serializable {

    private String id;
    private String locale;


    public LocalizedStringId() {
    }

    public LocalizedStringId(String id, String locale) {
        setId(id);
        setLocale(locale);
    }

    @Column(name = "ID", length = 128)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "LOCALE", length = 20)
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        LocalizedStringId other = (LocalizedStringId) obj;

        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }

        if (getLocale() == null) {
            if (other.getLocale() != null) {
                return false;
            }
        } else if (!getLocale().equals(other.getLocale())) {
            return false;
        }

        return true;
    }

}
