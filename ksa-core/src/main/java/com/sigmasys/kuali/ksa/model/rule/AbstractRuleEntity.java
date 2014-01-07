package com.sigmasys.kuali.ksa.model.rule;

import com.sigmasys.kuali.ksa.model.Identifiable;

import javax.persistence.*;

/**
 * Generic BRM JPA model.
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 */
@MappedSuperclass
public abstract class AbstractRuleEntity implements Identifiable {

    /**
     * Entity ID
     */
    protected Long id;

    /**
     * Entity Name
     */
    protected String name;

    /**
        * Entity Name
        */
    protected String description;

    /**
     * Rule Type
     */
    protected RuleType type;

    /**
     * Entity header
     */
    protected String header;


    @Transient
    public abstract Long getId();

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "NAME", length = 128, nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION", length = 512)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "HEADER", length = 4000)
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_TYPE_ID_FK", nullable = false)
    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }
}
