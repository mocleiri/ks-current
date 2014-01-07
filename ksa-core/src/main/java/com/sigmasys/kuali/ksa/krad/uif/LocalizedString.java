package com.sigmasys.kuali.ksa.model;


import com.sigmasys.kuali.ksa.annotation.Auditable;

import javax.persistence.*;

/**
 * LocalizedString entity
 * <p/>
 *
 * @author Michael Ivanov
 */
@Auditable
@Entity
@Table(name = "KSSA_UI_STRING")
public class LocalizedString implements Identifiable {

    private LocalizedStringId id;
    private String value;
    private Integer maxLength;
    private Boolean isOverridden;

    @Override
    @EmbeddedId
    public LocalizedStringId getId() {
        return id;
    }

    public void setId(LocalizedStringId id) {
        this.id = id;
    }

    @Column(name = "TEXT", length = 4000)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "MAX_LENGTH")
    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_OVERRIDDEN")
    public Boolean getOverridden() {
        return isOverridden;
    }

    public void setOverridden(Boolean overridden) {
        isOverridden = overridden;
    }

    @Override
    public String toString() {
        return "LocalizedString{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", maxLength=" + maxLength +
                ", isOverridden=" + isOverridden +
                '}';
    }
}
