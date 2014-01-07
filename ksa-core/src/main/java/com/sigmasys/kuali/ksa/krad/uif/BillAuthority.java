package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Bill authority model
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_BILL_AUTHORITY")
public class BillAuthority extends AccountIdAware implements Identifiable {

    /**
     * Person name ID
     */
    private Long id;

    /**
     * Account
     */
    private Account account;


    /**
     * Person name
     */
    private PersonName personName;

    /**
     * Postal address
     */
    private PostalAddress postalAddress;

    /**
     * Electronic contact
     */
    private ElectronicContact electronicContact;

    /**
     * Preferred method
     */
    private String preferredMethod;

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
     * Account ID of the user who agreed to release this record to another person
     */
    private String releaseAgreedBy;

    /**
     * Timestamp of the time when the user agreed to release this record
     */
    private Date releaseAgreedDate;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_BILL_AUTHORITY",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "BILL_AUTHORITY_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_BILL_AUTHORITY")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACNT_ID_FK")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_NAME_ID_FK")
    public PersonName getPersonName() {
        return personName;
    }

    public void setPersonName(PersonName personName) {
        this.personName = personName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSTAL_ADDRESS_ID_FK")
    public PostalAddress getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAddress postalAddress) {
        this.postalAddress = postalAddress;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ELECTRONIC_CONTACT_ID_FK")
    public ElectronicContact getElectronicContact() {
        return electronicContact;
    }

    public void setElectronicContact(ElectronicContact electronicContact) {
        this.electronicContact = electronicContact;
    }

    @Column(name = "PREFERRED_METHOD", length = 1)
    public String getPreferredMethod() {
        return preferredMethod;
    }

    public void setPreferredMethod(String preferredMethod) {
        this.preferredMethod = preferredMethod;
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

    @Column(name = "RELEASE_AGREED_BY", length = 45)
    public String getReleaseAgreedBy() {
        return releaseAgreedBy;
    }

    public void setReleaseAgreedBy(String releaseAgreedBy) {
        this.releaseAgreedBy = releaseAgreedBy;
    }

    @Column(name = "RELEASE_AGREED_DATE")
    public Date getReleaseAgreedDate() {
        return releaseAgreedDate;
    }

    public void setReleaseAgreedDate(Date releaseAgreedDate) {
        this.releaseAgreedDate = releaseAgreedDate;
    }
}
