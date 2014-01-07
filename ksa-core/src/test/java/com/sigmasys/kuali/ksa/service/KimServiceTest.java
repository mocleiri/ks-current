package com.sigmasys.kuali.ksa.service;


import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.kuali.rice.kim.api.identity.AuthenticationService;
import org.kuali.rice.kim.api.identity.IdentityService;

import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.identity.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.util.Assert;

import com.sigmasys.kuali.ksa.util.RequestUtils;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class KimServiceTest extends AbstractServiceTest {

    @Autowired
    private UserSessionManager userSessionManager;

    @Test
    public void getIdentityService() throws Exception {

        HttpServletRequest request = RequestUtils.getThreadRequest();

        Assert.notNull(request);

        //IdentityService service = ContextUtils.getBean("kimIdentityService", IdentityService.class);

        //Assert.notNull(service);

        IdentityService service = KimApiServiceLocator.getIdentityService();

        Assert.notNull(service);

    }

    @Test
    public void getUserId() throws Exception {

        HttpServletRequest request = RequestUtils.getThreadRequest();

        Assert.notNull(request);

        String userId = userSessionManager.getUserId(request);

        Assert.notNull(userId);

        AuthenticationService service = new AuthenticationServiceImpl();
        // ContextUtils.getBean("kimAuthenticationService", AuthenticationService.class);

        userId = service.getPrincipalName(request);

        Assert.notNull(userId);

        Assert.isTrue(userId.equals(TEST_USER_ID));

    }

}
