package com.sigmasys.kuali.ksa.krad.model;

import java.io.Serializable;
import java.util.Date;

import com.sigmasys.kuali.ksa.model.*;

/**
 * This class represents a single row in the Account Search table. 
 * 
 * @author Sergey
 *
 */
@SuppressWarnings("serial")
public class AccountSearchResultModel implements Serializable {
	
	/**
	 * Account instance.
	 */
	private Account account;
	
	/**
	 * Account protected information.
	 */
	private AccountProtectedInfo accountProtectedInfo;

	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AccountProtectedInfo getAccountProtectedInfo() {
		return accountProtectedInfo;
	}

	public void setAccountProtectedInfo(AccountProtectedInfo accountProtectedInfo) {
		this.accountProtectedInfo = accountProtectedInfo;
	}
	
	public Date getDateOfBirth() {
		return (account instanceof DirectChargeAccount) ? ((DirectChargeAccount)account).getDateOfBirth() : null;
	}
	
	public void setDateOfBirth(Date dob) {
		if (account instanceof DirectChargeAccount) {
			((DirectChargeAccount)account).setDateOfBirth(dob);
		}
	}
}
