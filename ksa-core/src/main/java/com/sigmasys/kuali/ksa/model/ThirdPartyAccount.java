package com.sigmasys.kuali.ksa.model;

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

