package com.sigmasys.kuali.ksa.krad.controller;


import com.sigmasys.kuali.ksa.krad.form.TransactionTypeForm;
import com.sigmasys.kuali.ksa.krad.model.TransactionTypeGroupModel;
import com.sigmasys.kuali.ksa.model.*;
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
public class TransactionTypeControllerTest extends AbstractServiceTest {


    @Autowired
    private TransactionTypeController transactionTypeController;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuditableEntityService auditableEntityService;

    private TransactionTypeForm form;

    private String userId = "admin";


    @Before
    public void setUpWithinTransaction() {

        // set up test data within the transaction
        accountService.getOrCreateAccount(userId);

        // Initializing request parameters
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);

        // Initializing the form
        form = transactionTypeController.createInitialForm(request);
    }

    @Test
    public void getList() throws Exception {

        // Passing request parameters needed to perform get() method
        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);
        request.setParameter("pageId", "TransactionTypePage");

        List<TransactionType> types = persistenceService.getEntities(TransactionType.class);

        ModelAndView modelAndView = transactionTypeController.get(form, request);

        // Checking assertions
        Assert.notNull(modelAndView);
        Assert.notNull(form);

        Assert.notNull(form.getTransactionTypes());

        // All of the transaction types are grouped by ID.
        // Sum them up to compare to the whole list.
        int count = 0;
        for (TransactionTypeGroupModel group : form.getTransactionTypes()) {
            count += group.getTransactionTypes().size();
        }
        Assert.isTrue(count == types.size());
        Assert.isTrue(form.getTransactionTypes().size() > 0);
    }

    @Test
    public void getDetail() throws Exception {

        MockHttpServletRequest request = getRequest();

        String id = "1001";
        String subCode = "1";

        request.setParameter("userId", userId);
        request.setParameter("pageId", "TransactionTypeDetailsPage");
        request.setParameter("entityId", id);
        request.setParameter("subCode", subCode);

        TransactionTypeId typeId = new TransactionTypeId(id, Integer.parseInt(subCode));

        TransactionType type = transactionService.getTransactionType(typeId);

        ModelAndView modelAndView = transactionTypeController.get(form, request);

        // Checking assertions
        Assert.notNull(modelAndView);
        Assert.notNull(form);

        Assert.notNull(form.getTransactionType());
        Assert.isTrue(form.getTransactionType().getTransactionType().getDescription().equals(type.getDescription()));
        Assert.notNull(form.getTransactionType().getTransactionType().getStartDate());
        Assert.isTrue(form.getTransactionType().getTransactionType().getStartDate().equals(type.getStartDate()));
    }

    @Test
    public void setupCreate() throws Exception {

        MockHttpServletRequest request = getRequest();

        request.setParameter("userId", userId);
        request.setParameter("pageId", "TransactionTypeCreatePage");

        List<GeneralLedgerType> glTypes = auditableEntityService.getAuditableEntities(GeneralLedgerType.class);

        Assert.notNull(glTypes);

        ModelAndView modelAndView = transactionTypeController.create(form, request);

        // Checking assertions
        Assert.notNull(modelAndView);
        Assert.notNull(form);
    }

}
