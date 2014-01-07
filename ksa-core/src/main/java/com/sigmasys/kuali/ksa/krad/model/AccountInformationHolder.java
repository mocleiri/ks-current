package com.sigmasys.kuali.ksa.krad.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.sigmasys.kuali.ksa.model.*;

/**
 * This class is a data holder for the New/Edit Account form object.
 * Data is stored within the Account and AccountProtectedInfo objects.
 * Additional data is stored in the extra attributes.
 *
 * @author Sergey Godunov
 */
@SuppressWarnings("serial")
public class AccountInformationHolder implements Serializable {

    /**
     * AccountProtectedInfo associated with the account.
     */
    private AccountProtectedInfo accountProtectedInfo;

    /**
     * Account type.
     */
    private String accountType;

    /**
     * Date of Birth. Only for DirectChargeAccount type of accounts.
     */
    private Date dateOfBirth;

    /**
     * Account preferences.
     */
    private List<UserPreference> userPreferences;


    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public AccountProtectedInfo getAccountProtectedInfo() {
        return accountProtectedInfo;
    }

    public void setAccountProtectedInfo(AccountProtectedInfo accountProtectedInfo) {
        this.accountProtectedInfo = accountProtectedInfo;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<UserPreference> getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(List<UserPreference> userPreferences) {
        this.userPreferences = userPreferences;
    }
}
