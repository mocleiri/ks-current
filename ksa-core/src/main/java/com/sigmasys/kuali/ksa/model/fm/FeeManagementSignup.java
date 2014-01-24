package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.Identifiable;
import com.sigmasys.kuali.ksa.model.KeyPair;
import com.sigmasys.kuali.ksa.model.KeyPairAware;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Fee management signup model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_FM_SIGNUP")
public class FeeManagementSignup implements Identifiable, KeyPairAware {

    private Long id;

    private FeeManagementSession session;

    private String registrationId;

    private String offeringId;

    private String atpId;

    private UnitNumber units;

    private Boolean isComplete;

    private Boolean isTaken;

    private Date creationDate;

    private Date effectiveDate;

    private Set<KeyPair> keyPairs;

    private Set<FeeManagementSignupRate> signupRates;

    private FeeManagementSignupOperation operation;

    private String operationCode;

    private OfferingType offeringType;

    private String offeringTypeCode;


    @PostLoad
    protected void populateTransientFields() {
        operation = (operationCode != null) ? EnumUtils.findById(FeeManagementSignupOperation.class, operationCode) : null;
        offeringType = (offeringTypeCode != null) ? EnumUtils.findById(OfferingType.class, offeringTypeCode) : null;
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

    @Column(name = "ATP_ID", length = 45)
    public String getAtpId() {
        return atpId;
    }

    public void setAtpId(String atpId) {
        this.atpId = atpId;
    }

    @Column(name = "UNITS")
    public UnitNumber getUnits() {
        return units;
    }

    public void setUnits(UnitNumber units) {
        this.units = units;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_REVIEW_COMPLETE")
    public Boolean isComplete() {
        return isComplete != null ? isComplete : false;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_TAKEN")
    public Boolean isTaken() {
        return isTaken != null ? isTaken : false;
    }

    public void setTaken(Boolean taken) {
        isTaken = taken;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_DATE")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_FM_SIGNUP_KEY_PAIR",
            joinColumns = {
                    @JoinColumn(name = "FM_SIGNUP_ID_FK")
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FM_SIGNUP_ID_FK")
    public Set<FeeManagementSignupRate> getSignupRates() {
        return signupRates;
    }

    public void setSignupRates(Set<FeeManagementSignupRate> signupRates) {
        this.signupRates = signupRates;
    }

    @Column(name = "OPERATION", length = 2)
    protected String getOperationCode() {
        return operationCode;
    }

    protected void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
        operation = EnumUtils.findById(FeeManagementSignupOperation.class, operationCode);
    }

    @Column(name = "OFFERING_TYPE", length = 3)
    protected String getOfferingTypeCode() {
        return offeringTypeCode;
    }

    protected void setOfferingTypeCode(String offeringTypeCode) {
        this.offeringTypeCode = offeringTypeCode;
        offeringType = EnumUtils.findById(OfferingType.class, offeringTypeCode);
    }

    @Transient
    public FeeManagementSignupOperation getOperation() {
        return operation;
    }

    public void setOperation(FeeManagementSignupOperation operation) {
        this.operation = operation;
        operationCode = operation.getId();
    }

    @Transient
    public OfferingType getOfferingType() {
        return offeringType;
    }

    public void setOfferingType(OfferingType offeringType) {
        this.offeringType = offeringType;
        offeringTypeCode = offeringType.getId();
    }

    @Transient
    public Set<FeeManagementSignupRate> getIncompleteSignupRates() {
        Set<FeeManagementSignupRate> incompleteSignupRates = new HashSet<FeeManagementSignupRate>();
        if (signupRates != null) {
            for (FeeManagementSignupRate signupRate : signupRates) {
                if (!signupRate.isComplete()) {
                    incompleteSignupRates.add(signupRate);
                }
            }
        }
        return incompleteSignupRates;
    }
}
