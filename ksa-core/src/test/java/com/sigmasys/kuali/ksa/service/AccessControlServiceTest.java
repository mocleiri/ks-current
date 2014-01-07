package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.event.EventMulticaster;
import com.sigmasys.kuali.ksa.event.LoadAccessControlEvent;
import com.sigmasys.kuali.ksa.event.LoadAccessControlListener;
import com.sigmasys.kuali.ksa.service.security.AccessControlService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class AccessControlServiceTest extends AbstractServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private UserSessionManager userSessionManager;

    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        accountService.getOrCreateAccount(TEST_USER_ID);
    }

    @Test
    public void getRoles() throws Exception {

        Set<String> roles = accessControlService.getRoles(TEST_USER_ID);

        Assert.notNull(roles);
        Assert.notEmpty(roles);

        for (String role : roles) {
            Assert.notNull(role);
            Assert.isTrue(!role.isEmpty());
        }

        logger.info("Role names: " + roles);
    }

    @Test
    public void getPermissions() throws Exception {

        Set<String> permissions = accessControlService.getUserPermissions(TEST_USER_ID);

        Assert.notNull(permissions);
        Assert.notEmpty(permissions);

        for (String permission : permissions) {
            Assert.notNull(permission);
            Assert.isTrue(!permission.isEmpty());
        }

        logger.info("Permission names: " + permissions);
    }

    @Test
    public void hasPermissions1() throws Exception {

        final String[] permissions = {"Use Configuration Viewer Screen",
                "Inquire Into Pessimistic",
                "Use Java Security Management Screen",
                "Grant Permission KUALI Namespace",
                "Modify Batch Job",
                "Look Up Rule Attribute",
                "Maintain System Parameter"};

        boolean result = accessControlService.hasPermissions(TEST_USER_ID, permissions);

        Assert.isTrue(result);

        result = accessControlService.hasPermission(TEST_USER_ID, permissions[0]);

        Assert.isTrue(result);

    }

    @Test
    public void hasPermissions2() throws Exception {

        final String[] permissions = {"Use Configuration Viewer Screen",
                "Inquire Into Pessimistic",
                "Use Java Security Management Screen",
                "Grant Permission KUALI Namespace",
                "Modify Batch Job",
                "Look Up Rule Attribute",
                "Maintain System Parameter"};

        boolean result = accessControlService.hasPermissions(permissions);

        Assert.isTrue(result);

        result = accessControlService.hasPermission(permissions[0]);

        Assert.isTrue(result);

        Set<String> permissionSet1 = accessControlService.getUserPermissions(TEST_USER_ID);

        Assert.notEmpty(permissionSet1);

        Set<String> permissionSet2 = userSessionManager.getUserPermissions();

        Assert.notEmpty(permissionSet2);

        Assert.isTrue(permissionSet1.size() == permissionSet2.size());

        Assert.isTrue(CollectionUtils.subtract(permissionSet1, permissionSet2).isEmpty());

    }

    @Test
    public void refresh() throws Exception {

        final Set<Boolean> isEventHandled = new HashSet<Boolean>();

        EventMulticaster.getInstance().addListener(new LoadAccessControlListener() {
            @Override
            public void onLoad(LoadAccessControlEvent event) {
                logger.info("LoadAccessControlEvent is handled");
                isEventHandled.add(true);
            }
        });

        accessControlService.refresh();

        Assert.notEmpty(isEventHandled);
        Assert.isTrue(isEventHandled.iterator().next());

    }


}
