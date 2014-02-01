package com.sigmasys.kuali.ksa.krad.controller;


import com.sigmasys.kuali.ksa.krad.form.TransactionForm;
import com.sigmasys.kuali.ksa.krad.model.InformationModel;
import com.sigmasys.kuali.ksa.krad.model.TransactionModel;
import com.sigmasys.kuali.ksa.model.Alert;
import com.sigmasys.kuali.ksa.model.Flag;
import com.sigmasys.kuali.ksa.model.Transaction;
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

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class TransactionControllerTest extends AbstractServiceTest {


    @Autowired
    private TransactionController transactionController;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private InformationService informationService;

    private TransactionForm form;

    private String userId = "user1";


    @Before
    public void setUpWithinTransaction() {

        // set up test data within the transaction
        accountService.getOrCreateAccount(userId);

        // Initializing request parameters
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);

        // Initializing the form
        form = transactionController.createInitialForm(request);
    }

    @Test
    public void getList() throws Exception {

        // Passing request parameters needed to perform get() method
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);
        request.setParameter("pageId", "ViewTransactions");

        List<Transaction> transactions = transactionService.getTransactions(userId);

        ModelAndView modelAndView = transactionController.get(form, request);

        // Checking assertions
        Assert.notNull(modelAndView);
        Assert.notNull(form);

        Assert.notNull(form.getAllTransactions());
        Assert.isTrue(form.getAllTransactions().size() == transactions.size());
        Assert.isTrue(form.getAllTransactions().size() > 0);
    }

    @Test
    public void getRollups() throws Exception {

        // Passing request parameters needed to perform get() method
        // Passing request parameters needed to perform get() method
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);
        request.setParameter("pageId", "ViewTransactions");

        transactionService.getTransactions(userId);

        ModelAndView modelAndView = transactionController.get(form, request);

        // Checking assertions
        Assert.notNull(modelAndView);
        Assert.notNull(form);

        List<TransactionModel> rollups = form.getRollupTransactions();

        // Every rollup should have at least one transaction in it's sub list (otherwise, how did it get there
        for (TransactionModel rollup : rollups) {
            List<TransactionModel> subs = rollup.getSubTransactions();
            Assert.notNull(subs);
            Assert.isTrue(subs.size() > 0);
        }

    }

    @Test
    public void getInformations() throws Exception {

        // Passing request parameters needed to perform get() method
        // Passing request parameters needed to perform get() method
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);
        request.setParameter("pageId", "ViewTransactions");

        List<Alert> alerts = informationService.getAlerts(userId);
        List<Flag> flags = informationService.getFlags(userId);
        //List<Memo> memos = informationService.getMemos(userId);
        ModelAndView modelAndView = transactionController.get(form, request);

        // Checking assertions
        Assert.notNull(modelAndView);
        Assert.notNull(form);

        List<InformationModel> formAlerts = form.getAlerts();
        Assert.notNull(alerts);
        Assert.notNull(formAlerts);
        for (Alert a : alerts) {
            boolean found = false;
            for (InformationModel formA : formAlerts) {
                if (formA.getText().equals(a.getText())) {
                    found = true;
                    break;
                }

            }
            Assert.isTrue(found, a.getText() + " not found in form Alerts");
        }

        List<InformationModel> formFlags = form.getFlags();
        Assert.notNull(flags);
        Assert.notNull(formFlags);
        for (Flag f : flags) {
            boolean found = false;
            for (InformationModel formF : formFlags) {
                if (formF.getText().equals(f.getText())) {
                    found = true;
                    break;
                }

            }
            Assert.isTrue(found, f.getText() + " not found in form Alerts");
        }

    }
}
