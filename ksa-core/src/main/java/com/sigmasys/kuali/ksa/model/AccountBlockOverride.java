package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.model.rule.Rule;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Account block override model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_ACNT_BLOCK_OVERRIDE")
public class AccountBlockOverride implements Identifiable {

    private Long id;

    private Account account;

    private Rule rule;

    private String reason;

    private Boolean isActive;

    private String creatorId;

    private Date creationDate;

    private Date expirationDate;


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
    @JoinColumn(name = "ACNT_ID_FK")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_ID_FK")
    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    @Column(name = "REASON", length = 2000)
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_ACTIVE")
    public Boolean isActive() {
        return isActive != null ? isActive : false;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Column(name = "CREATOR_ID", length = 45)
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRATION_DATE")
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
