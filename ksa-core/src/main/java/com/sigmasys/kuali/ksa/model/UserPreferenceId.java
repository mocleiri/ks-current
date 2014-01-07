package com.sigmasys.kuali.ksa.model;

import java.io.Serializable;

/**
 * The composite primary key for UserPreference entity
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("serial")
public class UserPreferenceId implements Serializable {

    private String accountId;
    private String name;


    public UserPreferenceId() {
    }

    public UserPreferenceId(String accountId, String name) {
        this.accountId = accountId;
        this.name = name;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (object == null || !getClass().equals(object.getClass())) {
            return false;
        }

        UserPreferenceId that = (UserPreferenceId) object;

        return !((name != null) ? !name.equals(that.name) : that.name != null) &&
                !((accountId != null) ? !accountId.equals(that.accountId) : that.accountId != null);

    }

    @Override
    public int hashCode() {
        int result = accountId != null ? accountId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
