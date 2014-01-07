package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

/**
 * Tag.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_TAG")
public class Tag extends AuditableEntity {

    /**
     * Access level
     */
    private Integer accessLevel;

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_TAG",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "TAG_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_TAG")
    @Override
    public Long getId() {
        return id;
    }


    @Column(name = "ACCESS_LEVEL")
    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

}
