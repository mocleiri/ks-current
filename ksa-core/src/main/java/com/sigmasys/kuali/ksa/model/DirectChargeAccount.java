package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.service.AccountVisitor;

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

    /**
     * Allows AccountVisitor to access this Account.
     *
     * @param visitor AccountVisitor instance
     */
    @Override
    public void accept(AccountVisitor visitor) {
        visitor.visit(this);
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_BIRTH")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}

