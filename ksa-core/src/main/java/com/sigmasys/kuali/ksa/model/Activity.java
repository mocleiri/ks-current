package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Activity model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_ACTIVITY")
public class Activity implements Identifiable {

    /**
     * Activity ID
     */
    private Long id;

    /**
     * IP address
     */
    private String ipAddress;

    /**
     * Entity ID
     */
    private String entityId;

    /**
     * Entity Type
     */
    private String entityType;

    /**
     * Entity property name
     */
    private String entityProperty;

    /**
     * Account ID associated with Entity ID and Type
     */
    private String accountId;

    /**
     * Timestamp
     */
    private Date timestamp;

    /**
     * Log details
     */
    private String logDetail;

    /**
     * Creator ID
     */
    private String creatorId;

    /**
     * Old attribute value
     */
    private String oldAttribute;

    /**
     * New attribute value
     */
    private String newAttribute;

    /**
     * Activity Type ID
     */
    private Long typeId;


    @Id
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "IP", length = 32)
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Column(name = "ALTERED_ENTITY_ID", length = 255)
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Column(name = "CREATION_DATE")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "LOG_DETAIL", length = 1024)
    public String getLogDetail() {
        return logDetail;
    }

    public void setLogDetail(String logDetail) {
        this.logDetail = logDetail;
    }

    @Column(name = "CREATOR_ID", length = 45)
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Column(name = "ALTERED_ACNT_ID", length = 45)
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Column(name = "ALTERED_ENTITY_TYPE", length = 255)
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Column(name = "OLD_ATTRIBUTE", length = 4000)
    public String getOldAttribute() {
        return oldAttribute;
    }

    public void setOldAttribute(String oldAttribute) {
        this.oldAttribute = oldAttribute;
    }

    @Column(name = "NEW_ATTRIBUTE", length = 4000)
    public String getNewAttribute() {
        return newAttribute;
    }

    public void setNewAttribute(String newAttribute) {
        this.newAttribute = newAttribute;
    }

    @Column(name = "ACTIVITY_TYPE_ID", nullable = false)
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Column(name = "ALTERED_ENTITY_PROPERTY", length = 100)
    public String getEntityProperty() {
        return entityProperty;
    }

    public void setEntityProperty(String entityProperty) {
        this.entityProperty = entityProperty;
    }
}
