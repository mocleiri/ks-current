package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

import static com.sigmasys.kuali.ksa.util.CommonUtils.nvl;

/**
 * Person name model.
 * One KSA account can be associated with one or more person names.
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(NameType.PERSON_CODE)
public class PersonName extends Name {

    /**
     * First name
     */
    private String firstName;

    /**
     * Middle name
     */
    private String middleName;

    /**
     * Last name
     */
    private String lastName;

    /**
     * Suffix
     */
    private String suffix;

    /**
     * Title
     */
    private String title;

    /**
     * Indicates whether this person name is default
     */
    private Boolean isDefault;


    @Column(name = "FIRST_NAME", length = 100)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "MIDDLE_NAME", length = 100)
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Column(name = "LAST_NAME", length = 100)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "SUFFIX", length = 10)
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Column(name = "TITLE", length = 10)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_DEFAULT")
    public Boolean isDefault() {
        return isDefault != null ? isDefault : false;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Transient
    public Boolean getDefault() {
        return isDefault();
    }

    /**
     * Get lastName, firstName of the default PersonName Set record
     */
    @Transient
    public String getDisplayValue() {
        StringBuilder personNameBuilder = new StringBuilder(nvl(getLastName()));
        personNameBuilder.append(", ");
        personNameBuilder.append(nvl(getFirstName()));
        return personNameBuilder.toString();
    }

    @Override
    public String toString() {
        return "PersonName{" +
                "id=" + id +
                ", kimNameType='" + kimNameType + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", title='" + title + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", editorId='" + editorId + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", isDefault=" + isDefault +
                '}';
    }
}
