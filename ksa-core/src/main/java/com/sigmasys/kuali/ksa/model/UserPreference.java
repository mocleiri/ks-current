package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;

import javax.persistence.*;

/**
 * User Preference
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 */
@Auditable
@Entity
@IdClass(UserPreferenceId.class)
@Table(name = "KSSA_USER_PREF")
public class UserPreference extends AccountIdAware implements Identifiable {

    /**
     * The preference name
     */
    private String name;

    /**
     * Original value
     */
    private String originalValue;

    /**
     * Overridden value
     */
    private String overriddenValue;


    public UserPreference() {
    }

    public UserPreference(String userId, String name, String originalValue) {
        setAccountId(userId);
        setName(name);
        setOriginalValue(originalValue);
    }

    public UserPreference(String userId, String name, String originalValue, String overriddenValue) {
        setAccountId(userId);
        setName(name);
        setOriginalValue(originalValue);
        setOverriddenValue(overriddenValue);
    }

    @Transient
    public UserPreferenceId getId() {
        return (getAccountId() != null && getName() != null) ? new UserPreferenceId(getAccountId(), getName()) : null;
    }

    @Id
    @Column(name = "ACNT_ID_FK", length = 45)
    public String getAccountId() {
        return accountId;
    }

    @Id
    @Column(name = "NAME", length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "VALUE", length = 1024)
    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    @Column(name = "OVRD_VALUE", length = 1024)
    public String getOverriddenValue() {
        return overriddenValue;
    }

    public void setOverriddenValue(String overriddenValue) {
        this.overriddenValue = overriddenValue;
    }

    @Transient
    public String getValue() {
        // Overridden value should be used if not null
        return overriddenValue != null ? overriddenValue : originalValue;
    }

    @Override
    public boolean equals(Object object) {

        if (object == null || !(object instanceof UserPreference)) {
            return false;
        }

        UserPreference userPref = (UserPreference) object;

        return (getAccountId().equals(userPref.getAccountId()) && getName().equals(userPref.getName()));
    }

    @Override
    public int hashCode() {
        return 31 * getAccountId().hashCode() + getName().hashCode();
    }

    @Override
    public String toString() {
        return "UserPreference{" +
                "accountId='" + accountId + '\'' +
                ",name='" + name + '\'' +
                ",originalValue='" + originalValue + '\'' +
                ",overriddenValue='" + overriddenValue + '\'' +
                '}';
    }
}
