package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.config.ConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class ConfigServiceTest extends AbstractServiceTest {

    @Autowired
    private ConfigService configService;

    @Autowired
    private AccountService accountService;


    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);
    }


    @Test
    public void getConfigParameters() throws Exception {

        Map<String, String> params = configService.getParameterMap();

        Assert.notNull(params);
        Assert.notEmpty(params);

        logger.info("Config parameters: " + params);

        for (Map.Entry<String, String> entry : params.entrySet()) {

            String name = entry.getKey();
            String value = entry.getValue();

            logger.info("Config parameter: name = " + name + ", value = " + value);

            Assert.notNull(name);

        }

    }

}
