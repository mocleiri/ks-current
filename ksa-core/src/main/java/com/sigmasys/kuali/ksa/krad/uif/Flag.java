package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

/**
 * Flag
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(InformationTypeValue.FLAG_CODE)
public class Flag extends Information {

    /**
     * Flag type
     */
    private FlagType type;

    /**
     * Severity
     */
    private Integer severity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FLAG_TYPE_ID_FK")
    public FlagType getType() {
        return type;
    }

    public void setType(FlagType type) {
        this.type = type;
    }

    @Column(name = "SEVERITY")
    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }
}

