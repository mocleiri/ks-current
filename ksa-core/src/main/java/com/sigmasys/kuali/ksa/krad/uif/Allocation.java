package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;
import java.math.BigDecimal;


/**
 * Transaction allocation model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_ALLOCATION")
public class Allocation extends AccountIdAware implements Identifiable {

    /**
     * The unique identifier
     */
    private Long id;

    /**
     * First transaction
     */
    private Transaction firstTransaction;

    /**
     * Second transaction
     */
    private Transaction secondTransaction;


    /**
     * Account reference
     */
    private Account account;


    /**
     * Allocation amount
     */
    private BigDecimal amount;

    /**
     * Is locked
     */
    private Boolean isLocked;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_ALLOC",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "ALLOCATION_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_ALLOC")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRN_ID_1_FK")
    public Transaction getFirstTransaction() {
        return firstTransaction;
    }

    public void setFirstTransaction(Transaction firstTransaction) {
        this.firstTransaction = firstTransaction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRN_ID_2_FK")
    public Transaction getSecondTransaction() {
        return secondTransaction;
    }

    public void setSecondTransaction(Transaction secondTransaction) {
        this.secondTransaction = secondTransaction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACNT_ID_FK")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Column(name = "AMNT")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_LOCKED")
    public Boolean isLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }
}
	


