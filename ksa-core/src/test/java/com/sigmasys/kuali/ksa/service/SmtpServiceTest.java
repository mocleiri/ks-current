package com.sigmasys.kuali.ksa.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class SmtpServiceTest extends AbstractServiceTest {

    @Autowired
    private SmtpService smtpService;

    @Autowired
    private AccountService accountService;


    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        accountService.getOrCreateAccount("admin");
    }

    @Test
    public void sendEmail1() throws Exception {

        String[] recipients = {"mivanov@sigmasys.com"};

        smtpService.sendEmail(recipients, "Test", "Test message");

    }

}