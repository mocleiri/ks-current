package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.service.AccountVisitor;

import javax.persistence.*;

/**
 * A third party account model.
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(AccountTypeValue.THIRD_PARTY_CODE)
public class ThirdPartyAccount extends ChargeableAccount {

    /**
     * Allows AccountVisitor to access this Account.
     *
     * @param visitor AccountVisitor instance
     */
    @Override
    public void accept(AccountVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * This type of Account is never stored in KIM.
     *
     * @return false
     */
    @Override
    @Transient
    public Boolean isKimAccount() {
        return false;
    }
}

