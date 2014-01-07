package com.sigmasys.kuali.ksa.model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;


/**
 * Cash limit summary element.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_CASH_LIMIT_PARAMETER")
public class CashLimitParameter extends AuditableEntity<Long> {

    /**
     * XML element name
     */
    private String xmlElement;

    /**
     * Authority name
     */
    private String authorityName;

    /**
     * Lower bound of transactions that are tracked
     */
    private BigDecimal lowerLimit;

    /**
     * Upper bound of transactions that are tracked
     */
    private BigDecimal upperLimit;

    /**
     * Tag associated with this parameter
     */
    private Tag tag;

    /**
     * Is still active?
     */
    private Boolean isActive;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "XML_ELEMENT", length = 45)
    public String getXmlElement() {
        return xmlElement;
    }

    public void setXmlElement(String xmlElement) {
        this.xmlElement = xmlElement;
    }

    @Column(name = "AUTHORITY_NAME", length = 255)
    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    @Column(name = "LOWER_LIMIT")
    public BigDecimal getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(BigDecimal lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    @Column(name = "UPPER_LIMIT")
    public BigDecimal getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(BigDecimal upperLimit) {
        this.upperLimit = upperLimit;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID_FK")
    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_ACTIVE")
    public Boolean isActive() {
        return isActive != null ? isActive : false;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}



