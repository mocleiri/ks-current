package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

/**
 * Transaction rollup.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_ROLLUP")
public class Rollup extends AuditableEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_ROLLUP",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "ROLLUP_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_ROLLUP")
    @Override
    public Long getId() {
        return id;
    }

}
