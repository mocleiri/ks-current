package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.model.ConfigParameter;
import com.sigmasys.kuali.ksa.model.UserPreference;
import com.sigmasys.kuali.ksa.model.UserPreferenceId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class UserPreferenceServiceTest extends AbstractServiceTest {

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private AccountService accountService;


    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);
    }


    @Test
    public void createUserPreference() {

        UserPreference userPref = userPreferenceService.createUserPreference("admin", "test.1.name", "test.1.value");

        Assert.notNull(userPref);
        Assert.notNull(userPref.getId());

        userPref = userPreferenceService.createUserPreference("admin", "test.2.name", "test.2.value");

        Assert.notNull(userPref);
        Assert.notNull(userPref.getId());


    }

    @Test
    public void updateUserPreference() {

        createUserPreference();

        List<UserPreference> userPrefs = userPreferenceService.getUserPreferences("admin");

        Assert.notNull(userPrefs);
        Assert.notEmpty(userPrefs);
        Assert.isTrue(userPrefs.size() > 1);

        UserPreference userPref = userPrefs.get(0);
        userPref.setOriginalValue("test.1.value.updated");

        UserPreferenceId userPrefId = userPref.getId();

        userPref = userPreferenceService.persistUserPreference(userPref);

        Assert.notNull(userPref);
        Assert.notNull(userPref.getId());

        Assert.isTrue(userPref.getId().equals(userPrefId));

        Assert.isTrue("test.1.value.updated".equals(userPref.getValue()));


    }


    @Test
    public void getUserPreferences() {

        createUserPreference();

        List<UserPreference> userPrefs = userPreferenceService.getUserPreferences("admin");

        Assert.notNull(userPrefs);
        Assert.notEmpty(userPrefs);
        Assert.isTrue(userPrefs.size() > 1);

        Map<String, String> prefsMap = new HashMap<String, String>(userPrefs.size());
        for (UserPreference userPref : userPrefs) {
            Assert.notNull(userPref);
            Assert.notNull(userPref.getId());
            Assert.notNull(userPref.getAccountId());
            Assert.notNull(userPref.getName());
            Assert.notNull(userPref.getValue());
            prefsMap.put(userPref.getName(), userPref.getValue());
        }

        Assert.isTrue(prefsMap.containsKey("test.1.name"));
        Assert.isTrue(prefsMap.containsKey("test.2.name"));

        Assert.isTrue("test.1.value".equals(prefsMap.get("test.1.name")));
        Assert.isTrue("test.2.value".equals(prefsMap.get("test.2.name")));

    }

    @Test
    public void overrideUserPreference() {

        UserPreference userPref = userPreferenceService.createUserPreference("admin", "pref_7", "value_7");

        Assert.notNull(userPref);
        Assert.notNull(userPref.getId());

        userPref = userPreferenceService.overrideUserPreferenceValue("admin", "pref_7", "ovrd_value_7");

        String value = userPref.getValue();

        Assert.notNull(value);
        Assert.isTrue(value.equals("ovrd_value_7"));
        Assert.isTrue(value.equals(userPref.getOverriddenValue()));

    }

    @Test
    public void reloadLockedParameters() {

           UserPreference userPref = userPreferenceService.createUserPreference("admin", "pref_1", "value_1");

           Assert.notNull(userPref);
           Assert.notNull(userPref.getId());

           List<ConfigParameter> configParameters = configService.getParameters();

           Assert.notNull(configParameters);

           // Adding a new locked config parameter
           configParameters.add(new ConfigParameter("pref_1", "value_2", false, true));

           configService.updateParameters(configParameters);

           // Forcing LoadConfigEvent to be fired
           // and hence make "pref_1" locked parameter available in UserPreferenceService
           configService.reloadParameters();

           String prefValue = userPreferenceService.getUserPreferenceValue("admin", "pref_1");

           Assert.notNull(prefValue);
           Assert.isTrue(prefValue.equals("value_2"));

     }

}
