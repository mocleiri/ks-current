package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;

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
    @TableGenerator(name = "TABLE_GEN_XML",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "XML_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_XML")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Lob
    @Column(name = "XML_DOCUMENT")
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
