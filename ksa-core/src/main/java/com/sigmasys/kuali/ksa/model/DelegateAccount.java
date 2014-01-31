package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.service.AccountVisitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A delegate (non-chargeable) account
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(AccountTypeValue.DELEGATE_CODE)
public class DelegateAccount extends NonChargeableAccount {

    /**
     * Allows AccountVisitor to access this Account.
     *
     * @param visitor AccountVisitor instance
     */
    @Override
    public void accept(AccountVisitor visitor) {
        visitor.visit(this);
    }

}

