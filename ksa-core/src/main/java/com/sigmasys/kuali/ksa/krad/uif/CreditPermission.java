package com.sigmasys.kuali.ksa.model;

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
    @TableGenerator(name = "TABLE_GEN_CREDIT_PERM",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "CREDIT_PERMISSION_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_CREDIT_PERM")
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

    @Column(name = "ALLOWABLE_DEBIT_TYPE_MASK", length = 20)
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
}
	


