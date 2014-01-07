package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.event.EventMulticaster;
import com.sigmasys.kuali.ksa.event.LoadConfigEvent;
import com.sigmasys.kuali.ksa.event.LoadConfigListener;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.UserPreferenceService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import java.util.*;

/**
 * User Preference Service implementation
 *
 * @author Michael Ivanov
 */
@Service("userPreferenceService")
@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class UserPreferenceServiceImpl extends GenericPersistenceService implements UserPreferenceService {

    private static final Log logger = LogFactory.getLog(UserPreferenceServiceImpl.class);


    @Autowired
    private ConfigService configService;

    @Autowired
    private AccountService accountService;

    private List<ConfigParameter> lockedParameters;


    @PostConstruct
    private void postConstruct() {
        initResources();
        // Registering LoadConfigListener to update the parameters every time LoadConfigEvent is fired
        EventMulticaster.getInstance().addListener(new LoadConfigListener() {
            @Override
            public void onLoad(LoadConfigEvent loadConfigEvent) {
                initResources();
            }
        });
    }

    private void initResources() {
        lockedParameters = configService.getLockedParameters();
    }

    /**
     * Returns the list of user preferences for the given user ID
     *
     * @param userId User ID
     * @return a list of preferences
     */
    @Override
    public List<UserPreference> getUserPreferences(String userId) {

        Query query = em.createQuery("select p from " + UserPreference.class.getName() + " p where " +
                " p.accountId = :userId");

        query.setParameter("userId", userId);

        List<UserPreference> originalUserPreferences = query.getResultList();

        List<UserPreference> finalUserPreferences = new ArrayList<UserPreference>(originalUserPreferences.size());

        // If the preference name matches the locked parameter name we have to use the config parameter value
        if (CollectionUtils.isNotEmpty(originalUserPreferences)) {
            for (UserPreference userPref : originalUserPreferences) {
                UserPreference finalUserPref = userPref;
                for (ConfigParameter parameter : lockedParameters) {
                    if (userPref.getName().equals(parameter.getName())) {
                        finalUserPref = new UserPreference(userId, userPref.getName(), userPref.getOriginalValue());
                        finalUserPref.setOverriddenValue(parameter.getValue());
                        break;
                    }
                }
                finalUserPreferences.add(finalUserPref);
            }
        }

        return finalUserPreferences;
    }

    /**
     * Returns the map of user preferences for the given user ID
     *
     * @param userId User ID
     * @return a map of key/value pairs
     */
    @Override
    public Map<String, String> getUserPreferenceMap(String userId) {
        List<UserPreference> userPreferences = getUserPreferences(userId);
        Map<String, String> preferenceMap = new HashMap<String, String>(userPreferences.size());
        for (UserPreference userPreference : userPreferences) {
            preferenceMap.put(userPreference.getName(), userPreference.getValue());
        }
        return preferenceMap;
    }

    /**
     * Creates or updates the user preference in the database
     *
     * @param userPref instance of UserPreference
     * @return updated user preference instance
     */
    @Override
    @Transactional(readOnly = false)
    public UserPreference persistUserPreference(UserPreference userPref) {
        persistEntity(userPref);
        return userPref;
    }

    /**
     * Creates a new user preference in the database
     *
     * @return user preference instance
     */
    @Override
    @Transactional(readOnly = false)
    public UserPreference createUserPreference(String userId, String prefName, String prefValue) {
        accountService.getOrCreateAccount(userId);
        UserPreference userPref = new UserPreference(userId, prefName, prefValue);
        persistEntity(userPref);
        return userPref;
    }

    /**
     * Returns a single user preference with the specified name for the given user ID.
     *
     * @param userId   User ID
     * @param prefName Preference name.
     * @return A single user preference with the matching name or <code>null</code> if such a preference does not exist.
     */
    @Override
    public UserPreference getUserPreference(String userId, String prefName) {

        // If the preference name matches the locked parameter name we have to return the parameter value
        for (ConfigParameter parameter : lockedParameters) {
            if (prefName.equals(parameter.getName())) {
                return new UserPreference(userId, parameter.getName(), parameter.getValue());
            }
        }

        Query query = em.createQuery("select p from " + UserPreference.class.getName() + " p where " +
                " p.accountId = :userId and p.name = :prefName");

        query.setParameter("userId", userId);
        query.setParameter("prefName", prefName);
        query.setMaxResults(1);

        List<UserPreference> results = query.getResultList();

        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * Returns the value of a user preference with the specified name for the given user ID.
     *
     * @param userId   User ID
     * @param prefName Preference name.
     * @return The value of a user preference with the matching name or <code>null</code> if such a preference does not exist.
     */
    @Override
    public String getUserPreferenceValue(String userId, String prefName) {
        UserPreference pref = getUserPreference(userId, prefName);
        return (pref != null) ? pref.getValue() : null;
    }

    /**
     * Overrides a user preference value,
     * throws <code>IllegalArgumentException</code> if the preference is not found.
     *
     * @param prefName        Preference name
     * @param overriddenValue Preference overridden value
     * @return user preference instance
     */
    @Override
    @Transactional(readOnly = false)
    public UserPreference overrideUserPreferenceValue(String userId, String prefName, String overriddenValue) {

        UserPreference preference = getUserPreference(userId, prefName);
        if (preference == null) {
            String errMsg = "Cannot find user preference with name '" + prefName + "', account ID '" + userId + "'";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        preference.setOverriddenValue(overriddenValue);

        return persistUserPreference(preference);
    }

}
