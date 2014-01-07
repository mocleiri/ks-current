package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;
import com.sigmasys.kuali.ksa.service.TransactionTypeVisitor;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;


/**
 * TransactionType defines different transaction types existing in KSA.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Auditable
@Entity
@Table(name = "KSSA_TRANSACTION_TYPE")
@DiscriminatorColumn(name = "TYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class TransactionType extends AuditableEntity<TransactionTypeId> {

    // Discriminator type value constants
    public static final String DEBIT_TYPE = "D";
    public static final String CREDIT_TYPE = "C";


    protected Date startDate;

    protected Date endDate;

    protected String defaultStatement;

    protected Set<Tag> tags;

    /**
     * Transaction priority
     */
    private Integer priority;


    /**
     * The default rollup
     */
    protected Rollup rollup;


    /**
     * Allows TransactionTypeVisitor to access this TransactionType
     *
     * @param visitor TransactionTypeVisitor instance
     */
    public abstract void accept(TransactionTypeVisitor visitor);

    /**
     * Returns the actual type value "C" or "D"
     *
     * @return Transaction type value
     */
    @Transient
    @XmlTransient
    public abstract String getTypeValue();


    @Override
    @EmbeddedId
    public TransactionTypeId getId() {
        return id;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "PRIORITY")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(name = "DEFAULT_STMT", length = 200)
    public String getDefaultStatement() {
        return defaultStatement;
    }

    public void setDefaultStatement(String defaultStatement) {
        this.defaultStatement = defaultStatement;
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
            },
            uniqueConstraints = {
                    @UniqueConstraint(columnNames =
                            {"TRANSACTION_TYPE_ID_FK", "TRANSACTION_TYPE_SUB_CODE_FK", "TAG_ID_FK"})
            }
    )
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
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

}
