package com.sigmasys.kuali.ksa.krad.controller;


import com.sigmasys.kuali.ksa.krad.form.SearchForm;
import com.sigmasys.kuali.ksa.service.AbstractServiceTest;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.ServiceTestSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class SearchControllerTest extends AbstractServiceTest {


    @Autowired
    private SearchController searchController;

    @Autowired
    private AccountService accountService;


    @Test
    public void createInitialForm() {

        // set up test data within the transaction
        accountService.getOrCreateAccount(TEST_USER_ID);

        // Initializing request parameters
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", TEST_USER_ID);

        // Initializing the form
        SearchForm searchForm = searchController.createInitialForm(request);

        Assert.notNull(searchForm);
    }


}
