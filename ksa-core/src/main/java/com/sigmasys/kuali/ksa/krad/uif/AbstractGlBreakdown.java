package com.sigmasys.kuali.ksa.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigDecimal;


/**
 * Generic GL breakdown model that has common properties.
 *
 * @author Michael Ivanov
 */
@MappedSuperclass
public abstract class AbstractGlBreakdown implements Identifiable {

    /**
     * The unique identifier
     */
    protected Long id;

    /**
     * GL account
     */
    protected String glAccount;


    /**
     * Percentage breakdown
     */
    protected BigDecimal breakdown;


    @Transient
    public abstract Long getId();

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "GL_ACCOUNT", length = 45)
    public String getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }

    @Column(name = "BREAKDOWN")
    public BigDecimal getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(BigDecimal breakdown) {
        this.breakdown = breakdown;
    }
}
	


