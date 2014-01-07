package com.sigmasys.kuali.ksa.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;


/**
 * TransactionType defines different transaction types existing in KSA.
 * <p/>
 * User: mivanov
 * Date: 1/22/12
 * Time: 3:46 PM
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_TRANSACTION_TYPE")
@DiscriminatorColumn(name = "TYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class TransactionType implements Identifiable {

    // Discriminator type value constants
    public static final String DEBIT_TYPE = "D";
    public static final String CREDIT_TYPE = "C";


    protected TransactionTypeId id;

    protected Date startDate;

    protected Date endDate;

    protected String description;

    protected List<Tag> tags;

    /**
     * The default rollup
     */
    protected Rollup rollup;

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


    @Override
    @EmbeddedId
    public TransactionTypeId getId() {
        return id;
    }

    public void setId(TransactionTypeId id) {
        this.id = id;
    }

    @Column(name = "START_DATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "END_DATE")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "DEF_TRN_TXT", length = 100)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of associated tags
     *
     * @return list of tags
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "KSSA_TRANSACTION_TYPE_TAG",
            joinColumns = {
                    @JoinColumn(name = "TRANSACTION_TYPE_ID_FK"),
                    @JoinColumn(name = "TRANSACTION_TYPE_SUB_CODE_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "TAG_ID_FK")
            }
    )
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEF_ROLLUP_ID_FK")
    public Rollup getRollup() {
        return rollup;
    }

    public void setRollup(Rollup rollup) {
        this.rollup = rollup;
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
