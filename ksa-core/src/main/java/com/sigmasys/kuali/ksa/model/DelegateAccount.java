package com.sigmasys.kuali.ksa.model;

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


}

