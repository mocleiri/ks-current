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
     * The preference value
     */
    private String value;

    public UserPreference() {
    }

    public UserPreference(String userId, String name, String value) {
        setAccountId(userId);
        setName(name);
        setValue(value);
    }

    @Transient
    public UserPreferenceId getId() {
        return (getAccountId() != null && getName() != null) ?
                new UserPreferenceId(getAccountId(), getName()) : null;
    }

    @Id
    @Column(name = "ACNT_ID_FK", length = 45)
    public String getAccountId() {
        return accountId;
    }

    @Id
    @Column(name = "NAME", length = 256)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "VALUE", length = 1024)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
