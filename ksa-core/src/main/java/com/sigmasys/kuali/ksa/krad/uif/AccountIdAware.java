package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;
import java.math.BigDecimal;


/**
 * This abstract class can be used by subclasses to refer to Account ID without having to
 * retrieve the entire Account instance when using @Many-to-one annotation.
 *
 * @author Michael Ivanov
 */
@MappedSuperclass
public abstract class AccountIdAware {

    protected String accountId;

    @Column(name = "ACNT_ID_FK", insertable = false, updatable = false, length = 45)
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
	


