package com.sigmasys.kuali.ksa.krad.controller;


import com.sigmasys.kuali.ksa.krad.form.TemplateForm;
import com.sigmasys.kuali.ksa.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Date;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class TemplateControllerTest extends AbstractServiceTest {


    @Autowired
    private TemplateController templateController;

    @Autowired
    private AccountService accountService;


    private TemplateForm templateForm;


    @Before
    public void setUpWithinTransaction() {

        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);

        // Initializing request parameters
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);
        request.setParameter("param1", "value1");
        request.setParameter("param2", "value2");

        // Initializing the form
        templateForm = templateController.createInitialForm(request);
    }

    @Test
    public void get() throws Exception {

        // Passing request parameters needed to perform get() method
        MockHttpServletRequest request = getRequest();
        request.setParameter("userId", "admin");
        request.setParameter("pageId", TemplateController.FIRST_PAGE_ID);

        templateForm.setDateValue(new Date());
        templateForm.setDoubleValue(10.34);
        templateForm.setStringValues(Arrays.asList("value1", "value2", "value3"));

        ModelAndView modelAndView = templateController.get(templateForm, request);

        // Checking assertions
        Assert.notNull(modelAndView);
        Assert.notNull(templateForm.getPageId());
        Assert.isTrue(templateForm.getPageId().equals(TemplateController.FIRST_PAGE_ID));

        // TODO: add more assertions
    }

    @Test
    public void submit() throws Exception {

        // Passing request parameters needed to perform get() method
        MockHttpServletRequest request = getRequest();
        request.setParameter("userId", "admin");

        templateForm.setDateValue(new Date());
        templateForm.setDoubleValue(10.34);
        templateForm.setStringValues(Arrays.asList("value1", "value2", "value3"));

        ModelAndView modelAndView = templateController.submit(templateForm);

        // Checking assertions
        Assert.notNull(modelAndView);
        Assert.notNull(templateForm.getPageId());
        Assert.isTrue(templateForm.getPageId().equals(TemplateController.SECOND_PAGE_ID));

        // TODO: add more assertions
    }


}
