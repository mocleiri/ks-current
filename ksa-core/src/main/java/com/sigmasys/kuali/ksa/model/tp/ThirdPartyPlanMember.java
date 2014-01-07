package com.sigmasys.kuali.ksa.model.tp;

import com.sigmasys.kuali.ksa.model.AccountIdAware;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.DirectChargeAccount;
import com.sigmasys.kuali.ksa.model.Identifiable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Third-party plan membership model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_TP_PLAN_MEMBER", uniqueConstraints = {@UniqueConstraint(columnNames = {"ACNT_ID_FK", "TP_PLAN_ID_FK"})})
public class ThirdPartyPlanMember extends AccountIdAware implements Identifiable {

    /**
     * Identifier
     */
    private Long id;

    /**
     * The eligible account (plan member)
     */
    private DirectChargeAccount directChargeAccount;

    /**
     * The plan assigned to an eligible account
     */
    private ThirdPartyPlan plan;

    /**
     * Indicates whether a student has already been processed for this plan
     */
    private Boolean isExecuted;

    /**
     * Defines the order in which a student can have plans processed against their account
     */
    private Integer priority;


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
    public DirectChargeAccount getDirectChargeAccount() {
        return directChargeAccount;
    }

    public void setDirectChargeAccount(DirectChargeAccount directChargeAccount) {
        this.directChargeAccount = directChargeAccount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TP_PLAN_ID_FK")
    public ThirdPartyPlan getPlan() {
        return plan;
    }

    public void setPlan(ThirdPartyPlan plan) {
        this.plan = plan;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_EXECUTED")
    public Boolean isExecuted() {
        return isExecuted != null ? isExecuted : false;
    }

    @Transient
    public Boolean getExecuted() {
        return isExecuted();
    }

    public void setExecuted(Boolean executed) {
        isExecuted = executed;
    }

    @Column(name = "PRIORITY")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
