package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;
import java.util.Date;

/**
 * A direct charge account
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(AccountTypeValue.DIRECT_CHARGE_CODE)
public class DirectChargeAccount extends ChargeableAccount {

    /**
     * Date of birth
     */
    private Date dateOfBirth;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_BIRTH")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}

