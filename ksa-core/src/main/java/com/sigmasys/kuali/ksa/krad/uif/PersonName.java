package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Person name model.
 * One KSA account can be associated with one or more person names.
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 *         Date: 3/13/12
 *         Time: 3:56 PM
 */
@Entity
@Table(name = "KSSA_PERSON_NAME")
public class PersonName implements Identifiable {

    /**
     * Person name ID
     */
    private Long id;

    /**
     * KIM Entity name type
     */
    private String kimNameType;

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
     * Creator user ID
     */
    private String creatorId;

    /**
     * Editor user ID
     */
    private String editorId;

    /**
     * Timestamp
     */
    private Date lastUpdate;

    /**
     * Indicates whether this person name is default
     */
    private Boolean isDefault;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_PERSON",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "PERSON_NAME_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_PERSON")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "KIM_NAME_TYPE", length = 45)
    public String getKimNameType() {
        return kimNameType;
    }

    public void setKimNameType(String kimNameType) {
        this.kimNameType = kimNameType;
    }

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

    @Column(name = "CREATOR_ID", length = 45)
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Column(name = "EDITOR_ID", length = 45)
    public String getEditorId() {
        return editorId;
    }

    public void setEditorId(String editorId) {
        this.editorId = editorId;
    }

    @Column(name = "LAST_UPDATE")
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_DEFAULT")
    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

}
