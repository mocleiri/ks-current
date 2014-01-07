package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * This class keeps information about a failed GL transaction
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_FAILED_GL_TRANSACTION")
public class FailedGlTransaction implements Identifiable {

    /**
     * Failed GL Transaction ID
     */
    private Long id;

    /**
     * Original transaction for which a GL attempt was made
     */
    private Transaction transaction;

    /**
     * Failure reason
     */
    private String failureReason;

    /**
     * Indicates whether this failed transaction has been fixed or not
     */
    private Boolean isFixed;

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
    @JoinColumn(name = "TRANSACTION_ID_FK")
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Column(name = "REASON", length = 1000)
    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_FIXED")
    public Boolean isFixed() {
        return isFixed != null ? isFixed : false;
    }

    public void setFixed(Boolean fixed) {
        isFixed = fixed;
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
