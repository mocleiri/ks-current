package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Postal address
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 *         Date: 3/13/12
 *         Time: 3:56 PM
 */
@Entity
@Table(name = "KSSA_POSTAL_ADDRESS")
public class PostalAddress implements Identifiable {

    /**
     * Address ID
     */
    private Long id;

    /**
     * KIM address type
     */
    private String kimAddressType;

    /**
     * Street address 1
     */
    private String streetAddress1;

    /**
     * Street address 1
     */
    private String streetAddress2;

    /**
     * Street address 1
     */
    private String streetAddress3;

    /**
     * Postal (zip) code
     */
    private String postalCode;

    /**
     * Country
     */
    private String country;

    /**
     * State
     */
    private String state;

    /**
     * City
     */
    private String city;

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
     * Indicates whether this address is default
     */
    private Boolean isDefault;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_ADDRESS",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "ADDRESS_NAME_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_ADDRESS")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "KIM_ADDRESS_TYPE", length = 45)
    public String getKimAddressType() {
        return kimAddressType;
    }

    public void setKimAddressType(String kimAddressType) {
        this.kimAddressType = kimAddressType;
    }

    @Column(name = "LINE1", length = 100)
    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    @Column(name = "LINE2", length = 100)
    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }

    @Column(name = "LINE3", length = 100)
    public String getStreetAddress3() {
        return streetAddress3;
    }

    public void setStreetAddress3(String streetAddress3) {
        this.streetAddress3 = streetAddress3;
    }

    @Column(name = "POSTAL_CODE", length = 12)
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Column(name = "COUNTRY_CODE", length = 10)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "STATE_CODE", length = 5)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "CITY", length = 100)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
