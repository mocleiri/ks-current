package com.sigmasys.kuali.ksa.krad.controller;


import com.sigmasys.kuali.ksa.krad.form.SearchForm;
import com.sigmasys.kuali.ksa.model.Transaction;
import com.sigmasys.kuali.ksa.service.AbstractServiceTest;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.ServiceTestSuite;
import com.sigmasys.kuali.ksa.service.TransactionService;
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

import java.util.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class SearchControllerTest extends AbstractServiceTest {


    @Autowired
    private SearchController searchController;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    private SearchForm searchForm;

    private String userId = "admin";


    @Before
    public void setUpWithinTransaction() {

        // set up test data within the transaction
        accountService.getOrCreateAccount(userId);

        // Initializing request parameters
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);

        // Initializing the form
        searchForm = searchController.createInitialForm(request);
    }

    @Test
    public void getTransactionList() throws Exception {
        // removed code.  Transactions are now in the TransactionController

    }


}
