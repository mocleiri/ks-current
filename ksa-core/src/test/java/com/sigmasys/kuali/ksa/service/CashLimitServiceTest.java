package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.CashLimitEvent;
import com.sigmasys.kuali.ksa.model.CashLimitParameter;
import com.sigmasys.kuali.ksa.model.Tag;
import com.sigmasys.kuali.ksa.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.sigmasys.kuali.ksa.model.CashLimitEventStatus.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class CashLimitServiceTest extends AbstractServiceTest {

    @Autowired
    private CashLimitService cashLimitService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuditableEntityService entityService;

    @Autowired
    private TransactionService transactionService;


    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        accountService.getOrCreateAccount(TEST_USER_ID);
    }

    @Test
    public void checkCashLimit() throws Exception {

        Tag cashTag = entityService.getAuditableEntity("Cash", Tag.class);

        Assert.notNull(cashTag);
        Assert.notNull(cashTag.getId());
        Assert.notNull(cashTag.getCode());

        CashLimitParameter parameter = cashLimitService.createCashLimitParameter("param1", "Param 1", "Param 1 Desc",
                cashTag, BigDecimal.ZERO, new BigDecimal(100000), true, "Authority name", "xmlParam1");

        Assert.notNull(parameter);
        Assert.notNull(parameter.getId());
        Assert.isTrue(parameter.getLowerLimit().compareTo(BigDecimal.ZERO) == 0);
        Assert.isTrue(parameter.getUpperLimit().compareTo(new BigDecimal(99999.999)) > 0);
        Assert.isTrue(parameter.getUpperLimit().compareTo(new BigDecimal(100000.001)) < 0);

        Transaction transaction = transactionService.createTransaction("cash", TEST_USER_ID, new Date(),
                new BigDecimal(25000.587));

        Assert.notNull(transaction);
        Assert.notNull(transaction.getId());
        Assert.notNull(transaction.getTransactionType());
        Assert.notNull(transaction.getTags());
        Assert.notEmpty(transaction.getTags());

        boolean cashTagIsPresent = false;
        for ( Tag tag : transaction.getTags() ) {
             if ( "Cash".equals(tag.getCode())) {
                 cashTagIsPresent = true;
                 break;
             }
        }

        Assert.isTrue(cashTagIsPresent);

        boolean cashLimitEventIsCreated = cashLimitService.checkCashLimit(TEST_USER_ID);

        Assert.isTrue(cashLimitEventIsCreated);

        List<CashLimitEvent> cashLimitEvents = cashLimitService.getCashLimitEvents(Arrays.asList(TEST_USER_ID), QUEUED);

        Assert.notNull(cashLimitEvents);
        Assert.notEmpty(cashLimitEvents);

        for (CashLimitEvent cashLimitEvent : cashLimitEvents) {

            Assert.notNull(cashLimitEvent);
            Assert.notNull(cashLimitEvent.getId());
            Assert.notNull(cashLimitEvent.getAccountId());
            Assert.notNull(cashLimitEvent.getCreatorId());
            Assert.notNull(cashLimitEvent.getEventDate());
            Assert.notNull(cashLimitEvent.getNotificationDate());
            Assert.notNull(cashLimitEvent.getRecipient());
            Assert.notNull(cashLimitEvent.getStatus());

            Assert.isTrue(cashLimitEvent.getStatus() == QUEUED);
            Assert.isTrue(cashLimitEvent.getAccountId().equals(TEST_USER_ID));

        }

    }

}