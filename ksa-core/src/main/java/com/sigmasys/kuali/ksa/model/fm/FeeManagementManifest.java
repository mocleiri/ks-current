package com.sigmasys.kuali.ksa.model.fm;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.*;

import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

/**
 * Fee management manifest model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_FM_MANIFEST")
public class FeeManagementManifest implements Identifiable, KeyPairAware {

    private Long id;

    private FeeManagementSession session;

    private FeeManagementManifest linkedManifest;

    private Transaction transaction;

    private Rate rate;

    private Rollup rollup;

    private BigDecimal amount;

    private String registrationId;

    private String offeringId;

    private String internalChargeId;

    private String transactionTypeId;

    private Date effectiveDate;

    private Date recognitionDate;

    private Boolean isSessionCurrent;

    private Set<KeyPair> keyPairs;

    private Set<Tag> tags;

    private Set<ManifestGlBreakdownOverride> glBreakdownOverrides;

    private FeeManagementManifestStatus status;

    private String statusCode;

    private FeeManagementManifestType type;

    private String typeCode;


    @PostLoad
    protected void populateTransientFields() {
        status = (statusCode != null) ? EnumUtils.findById(FeeManagementManifestStatus.class, statusCode) : null;
        type = (typeCode != null) ? EnumUtils.findById(FeeManagementManifestType.class, typeCode) : null;
    }

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FM_SESSION_ID_FK")
    public FeeManagementSession getSession() {
        return session;
    }

    public void setSession(FeeManagementSession session) {
        this.session = session;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LINKED_MANIFEST_ID_FK")
    public FeeManagementManifest getLinkedManifest() {
        return linkedManifest;
    }

    public void setLinkedManifest(FeeManagementManifest linkedManifest) {
        this.linkedManifest = linkedManifest;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID_FK")
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATE_ID_FK")
    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLLUP_ID_FK")
    public Rollup getRollup() {
        return rollup;
    }

    public void setRollup(Rollup rollup) {
        this.rollup = rollup;
    }

    @Column(name = "AMOUNT")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "REG_ID", length = 45)
    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    @Column(name = "OFFERING_ID", length = 45)
    public String getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    @Column(name = "INTERNAL_CHARGE_ID", length = 45)
    public String getInternalChargeId() {
        return internalChargeId;
    }

    public void setInternalChargeId(String internalChargeId) {
        this.internalChargeId = internalChargeId;
    }

    @Column(name = "TRANSACTION_TYPE_ID", length = 20)
    public String getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(String transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_SESSION_CURRENT")
    public Boolean isSessionCurrent() {
        return isSessionCurrent != null ? isSessionCurrent : false;
    }

    public void setSessionCurrent(Boolean isSessionCurrent) {
        this.isSessionCurrent = isSessionCurrent;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_DATE")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "RECOGNITION_DATE")
    public Date getRecognitionDate() {
        return recognitionDate;
    }

    public void setRecognitionDate(Date recognitionDate) {
        this.recognitionDate = recognitionDate;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_FM_MANIFEST_KEY_PAIR",
            joinColumns = {
                    @JoinColumn(name = "FM_MANIFEST_ID_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "KEY_PAIR_ID_FK")
            }
    )
    public Set<KeyPair> getKeyPairs() {
        return keyPairs;
    }

    public void setKeyPairs(Set<KeyPair> keyPairs) {
        this.keyPairs = keyPairs;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_FM_MANIFEST_TAG",
            joinColumns = {
                    @JoinColumn(name = "FM_MANIFEST_ID_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "TAG_ID_FK")
            }
    )
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FM_MANIFEST_ID_FK")
    public Set<ManifestGlBreakdownOverride> getGlBreakdownOverrides() {
        return glBreakdownOverrides;
    }

    public void setGlBreakdownOverrides(Set<ManifestGlBreakdownOverride> glBreakdownOverrides) {
        this.glBreakdownOverrides = glBreakdownOverrides;
    }

    @Column(name = "STATUS", length = 1)
    protected String getStatusCode() {
        return statusCode;
    }

    protected void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        status = EnumUtils.findById(FeeManagementManifestStatus.class, statusCode);
    }

    @Column(name = "TYPE", length = 2)
    protected String getTypeCode() {
        return typeCode;
    }

    protected void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        type = EnumUtils.findById(FeeManagementManifestType.class, typeCode);
    }

    @Transient
    public FeeManagementManifestStatus getStatus() {
        return status;
    }

    public void setStatus(FeeManagementManifestStatus status) {
        this.status = status;
        statusCode = status.getId();
    }

    @Transient
    public FeeManagementManifestType getType() {
        return type;
    }

    public void setType(FeeManagementManifestType type) {
        this.type = type;
        typeCode = type.getId();
    }

}
