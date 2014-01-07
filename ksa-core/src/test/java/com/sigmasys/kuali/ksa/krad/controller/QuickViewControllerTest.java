package com.sigmasys.kuali.ksa.krad.controller;


import com.sigmasys.kuali.ksa.krad.form.QuickViewForm;
import com.sigmasys.kuali.ksa.krad.model.TransactionTypeGroupModel;
import com.sigmasys.kuali.ksa.model.GeneralLedgerType;
import com.sigmasys.kuali.ksa.model.TransactionType;
import com.sigmasys.kuali.ksa.model.TransactionTypeId;
import com.sigmasys.kuali.ksa.service.AbstractServiceTest;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.ServiceTestSuite;
import com.sigmasys.kuali.ksa.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class QuickViewControllerTest extends AbstractServiceTest {

    @Autowired
    QuickViewController quickViewController;

    @Autowired
    private AccountService accountService;

    private QuickViewForm form;

    private String userId = "user1";


    @Before
    public void setUpWithinTransaction() {

        // set up test data within the transaction
        accountService.getOrCreateAccount(userId);

        // Initializing request parameters
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);

        GlobalVariables.setUserSession(new UserSession("admin"));

        // Initializing the form
        form = quickViewController.createInitialForm(request);
    }

    @Test
    public void getQuickView() throws Exception {

        // Passing request parameters needed to perform get() method
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);
        request.setParameter("viewId", "QuickView");


        ModelAndView modelAndView = quickViewController.get(form, request);

        // Checking assertions
        Assert.notNull(modelAndView);
        Assert.notNull(form);

        Assert.notNull(form.getAccount());
        Assert.notNull(form.getFlags());
        Assert.notNull(form.getAlerts());
        Assert.notNull(form.getHolds());
        Assert.notNull(form.getMemoModels());
        Assert.notNull(form.getAlertsFlags());
        Assert.isTrue(form.getAlertsFlags().size() > 0);
        Assert.notNull(form.getPastDueAmount());
        Assert.notNull(form.getBalanceAmount());
        Assert.notNull(form.getFutureAmount());
        Assert.notNull(form.getDaysLate1());
        Assert.notNull(form.getDaysLate2());
        Assert.notNull(form.getDaysLate3());

    }

}
