package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * XML Document model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Auditable
@Entity
@Table(name = "KSSA_XML")
public class XmlDocument implements Identifiable {

    /**
     * Document ID
     */
    private Long id;

    /**
     * XML content
     */
    private String xml;

    /**
     * Creation date
     */
    private Date creationDate;

    /**
     * Creator user ID
     */
    private String creatorId;


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

    @Lob
    @Type(type = "text")
    @Column(name = "XML_DOCUMENT", length = Integer.MAX_VALUE, nullable = false)
    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Column(name = "CREATOR_ID", length = 45)
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

}
