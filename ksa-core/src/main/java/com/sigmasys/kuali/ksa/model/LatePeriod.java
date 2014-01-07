package com.sigmasys.kuali.ksa.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * LatePeriod model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_LATE_PERIOD")
public class LatePeriod extends AuditableEntity<Long> {

    private Boolean isDefault;

    private Integer daysLate1;
    private Integer daysLate2;
    private Integer daysLate3;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_DEFAULT")
    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    @Column(name = "DAYS_LATE1")
    public Integer getDaysLate1() {
        return daysLate1;
    }

    public void setDaysLate1(Integer daysLate1) {
        this.daysLate1 = daysLate1;
    }

    @Column(name = "DAYS_LATE2")
    public Integer getDaysLate2() {
        return daysLate2;
    }

    public void setDaysLate2(Integer daysLate2) {
        this.daysLate2 = daysLate2;
    }

    @Column(name = "DAYS_LATE3")
    public Integer getDaysLate3() {
        return daysLate3;
    }

    public void setDaysLate3(Integer daysLate3) {
        this.daysLate3 = daysLate3;
    }
}
