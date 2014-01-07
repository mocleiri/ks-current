package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Generic KSA Name model.
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_NAME")
@DiscriminatorColumn(name = "TYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Name implements Identifiable {

    /**
     * Name ID
     */
    protected Long id;

    /**
     * KIM Entity name type
     */
    protected String kimNameType;

    /**
     * Creator user ID
     */
    protected String creatorId;

    /**
     * Creation timestamp
     */
    protected Date creationDate;

    /**
     * Editor user ID
     */
    protected String editorId;

    /**
     * Last update timestamp
     */
    protected Date lastUpdate;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
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

}
