package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

/**
 * Refund type.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_REFUND_TYPE")
public class RefundType extends AuditableEntity {

    private String creditTypeId;

    private String debitTypeId;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_REFUND_TYPE",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "REFUND_TYPE_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_REFUND_TYPE")
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "CREDIT_TYPE_ID", length = 20)
    public String getCreditTypeId() {
        return creditTypeId;
    }

    public void setCreditTypeId(String creditTypeId) {
        this.creditTypeId = creditTypeId;
    }

    @Column(name = "DEBIT_TYPE_ID", length = 20)
    public String getDebitTypeId() {
        return debitTypeId;
    }

    public void setDebitTypeId(String debitTypeId) {
        this.debitTypeId = debitTypeId;
    }
}
