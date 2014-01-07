package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

/**
 * Activity type.
 * <p/>
 * User: mike
 * Date: 1/22/12
 * Time: 3:47 PM
 */
@Entity
@Table(name = "KSSA_ACTIVITY_TYPE")
public class ActivityType extends AuditableEntity {


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_ACT_TYPE",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "ACTIVITY_TYPE_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_ACT_TYPE")
    @Override
    public Long getId() {
        return id;
    }
}
