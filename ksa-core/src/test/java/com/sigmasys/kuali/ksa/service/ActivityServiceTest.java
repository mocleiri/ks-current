package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.Activity;
import com.sigmasys.kuali.ksa.model.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class ActivityServiceTest extends AbstractServiceTest {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private AccountService accountService;


    private Long activityId = 0L;

    @Before
    public void setUpWithinTransaction() {

        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);

        // Creating a new activity
        Activity activity = new Activity();
        activity.setAccountId(userId);
        activity.setEntityId(userId);
        activity.setEntityType(Account.class.getSimpleName());
        activity.setCreatorId(TEST_USER_ID);
        activity.setIpAddress("127.0.0.1");
        activity.setOldAttribute("Old_1");
        activity.setOldAttribute("New_1");

        activityId = activityService.persistActivity(activity);

        Assert.notNull(activity.getId());
        Assert.isTrue(activity.getId().equals(activityId));
        Assert.isTrue(activity.getId() > 0);
        Assert.notNull(activity.getTypeId());
        Assert.isTrue(activity.getTypeId().equals(Constants.ACTIVITY_TYPE_DATA_CHANGE_ID));

    }


    @Test
    public void getActivity() throws Exception {

        Activity activity = activityService.getActivity(activityId);

        Assert.notNull(activity);
        Assert.notNull(activity.getId());

        Assert.isTrue(activity.getId().equals(activityId));

    }

    @Test
    public void getActivities() throws Exception {

        List<Activity> activities = activityService.getActivities();

        Assert.notNull(activities);
        Assert.isTrue(!activities.isEmpty());

        for (Activity activity : activities) {
            Assert.notNull(activity);
            Assert.notNull(activity.getId());
        }

    }

    @Test
    public void getActivitiesForUser() throws Exception {

        List<Activity> activities = activityService.getActivities("admin");

        Assert.notNull(activities);
        Assert.isTrue(!activities.isEmpty());

    }


    @Test
    public void updateActivity1() throws Exception {

        Activity activity = activityService.getActivity(activityId);

        Assert.notNull(activity);
        Assert.notNull(activity.getId());

        Assert.isTrue(activity.getId().equals(activityId));

        activity.setOldAttribute("Attribute_1");


        activityService.persistActivity(activity);

        activity = activityService.getActivity(activityId);

        Assert.notNull(activity);
        Assert.notNull(activity.getOldAttribute());
        Assert.isTrue(activity.getOldAttribute().equals("Attribute_1"));

        Assert.notNull(activity.getTypeId());

    }

    @Test
    public void updateActivity2() throws Exception {

        Activity activity = activityService.getActivity(activityId);

        Assert.notNull(activity);
        Assert.notNull(activity.getId());

        Assert.isTrue(activity.getId().equals(activityId));

        activity.setOldAttribute("Attribute_Old");

        activity.setTypeId(2L);

        activityService.persistActivity(activity);

        activity = activityService.getActivity(activityId);

        Assert.notNull(activity);
        Assert.notNull(activity.getOldAttribute());
        Assert.isTrue(activity.getOldAttribute().equals("Attribute_Old"));

        Assert.notNull(activity.getTypeId());
        Assert.notNull(activity.getTypeId().equals(2L));

    }


}
