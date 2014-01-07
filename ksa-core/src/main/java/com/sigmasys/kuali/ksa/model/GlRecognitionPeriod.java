package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * General Ledger Recognition Period model.
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_GL_RECOGNITION_PERIOD")
public class GlRecognitionPeriod extends AuditableEntity<Long> {


    /**
     * Start date of the period.
     */
    private Date startDate;

    /**
     * End date of the period.
     */
    private Date endDate;

    /**
     * Fiscal year
     */
    private Integer fiscalYear;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "START_DATE", nullable = false)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "END_DATE", nullable = false)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "FISCAL_YEAR", nullable = false)
    public Integer getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }
}
