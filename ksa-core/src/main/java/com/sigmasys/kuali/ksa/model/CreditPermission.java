package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


/**
 * Credit permission model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_CREDIT_PERMISSION")
public class CreditPermission implements Identifiable {

    /**
     * The unique identifier
     */
    private Long id;

    /**
     * Allowable debit type ID
     */
    private String allowableDebitType;


    /**
     * Priority
     */
    private Integer priority;


    /**
     * Reference to CREDIT type
     */
    private CreditType creditType;


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

    @Column(name = "PRIORITY")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(name = "ALLOWABLE_DEBIT_TYPE_MASK", length = 500)
    public String getAllowableDebitType() {
        return allowableDebitType;
    }

    public void setAllowableDebitType(String allowableDebitType) {
        this.allowableDebitType = allowableDebitType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "TRANSACTION_TYPE_ID_FK", referencedColumnName = "ID"),
            @JoinColumn(name = "TRANSACTION_TYPE_SUB_CODE_FK", referencedColumnName = "SUB_CODE")
    })
    public CreditType getCreditType() {
        return creditType;
    }

    public void setCreditType(CreditType creditType) {
        this.creditType = creditType;
    }

    @Override
    public String toString() {
        return "CreditPermission{" +
                "id=" + id +
                ", allowableDebitType='" + allowableDebitType + '\'' +
                ", priority=" + priority +
                ", creditType=" + creditType +
                '}';
    }
}



