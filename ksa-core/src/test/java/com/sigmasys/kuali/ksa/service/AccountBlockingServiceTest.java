package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.exception.AccountBlockedException;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class AccountBlockingServiceTest extends AbstractServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountBlockingService accountBlockingService;

    @Autowired
    private PersistenceService persistenceService;


    @Before
    public void setUpWithinTransaction() {
        // Set up test data within the transaction
        Account userAccount = accountService.getOrCreateAccount("user1");
        Assert.isTrue(userAccount.isBlockingEnabled());
    }

    @Test
    public void checkBlock1() {

        String userId = "user1";

        String transactionTypeId1 = "cash";
        String transactionTypeId2 = "chip";
        String transactionTypeId3 = "finaid";

        Map<String, Object> attributes = new HashMap<String, Object>();

        List<String> transactionTypeIds = Arrays.asList(transactionTypeId1, transactionTypeId2, transactionTypeId3);

        attributes.put(Constants.BRM_TRANSACTION_TYPE_IDS, transactionTypeIds);

        try {

            accountBlockingService.checkBlock(userId, attributes, Permission.CREATE_PAYMENT, Permission.CREATE_ALLOCATION);

        } catch (AccountBlockedException abe) {

            logger.info("Message = " + abe.getMessage() + ", Account ID = " + abe.getAccountId() +
                    ", Block names = " + abe.getBlockNames());

            Assert.notNull(abe.getAccountId());
            Assert.notNull(abe.getBlockNames());

            Assert.isTrue(abe.getAccountId().equals(userId));
            Assert.notEmpty(abe.getBlockNames());

            Assert.isTrue(abe.getBlockNames().contains("Block 1"));

        }

    }

    @Test
    public void checkBlock2() {

        String userId = "user1";

        String transactionTypeId1 = "cash";
        String transactionTypeId2 = "finaid2";

        String atpId1 = "0000000";
        String atpId2 = "20122";

        String holdIssueName1 = "Disciplinary Suspension";
        String holdIssueName2 = "Name 2";

        Map<String, Object> attributes = new HashMap<String, Object>();


        attributes.put(Constants.BRM_TRANSACTION_TYPE_IDS, Arrays.asList(transactionTypeId1, transactionTypeId2));
        attributes.put(Constants.BRM_ATP_IDS, Arrays.asList(atpId1, atpId2));
        attributes.put(Constants.BRM_HOLD_ISSUE_NAMES, Arrays.asList(holdIssueName1, holdIssueName2));

        try {

            accountBlockingService.checkBlock(userId, attributes, Permission.CREATE_PAYMENT, Permission.REQUEST_REFUND);

        } catch (AccountBlockedException abe) {

            logger.info("Message = " + abe.getMessage() + ", Account ID = " + abe.getAccountId() +
                    ", Block names = " + abe.getBlockNames());

            Assert.notNull(abe.getAccountId());
            Assert.notNull(abe.getBlockNames());

            Assert.isTrue(abe.getAccountId().equals(userId));

            Assert.notEmpty(abe.getBlockNames());
            Assert.isTrue(abe.getBlockNames().size() > 1);

            Assert.isTrue(abe.getBlockNames().contains("Block 1"));
            Assert.isTrue(abe.getBlockNames().contains("Block 3"));

        }

    }


    @Test
    public void checkBlock3() {

        String userId = "user1";

        String transactionTypeId1 = "finaid";


        Account account = accountService.getFullAccount(userId);

        Assert.notNull(account);
        Assert.notNull(account.getId());
        Assert.isTrue(account.getId().equals(userId));

        Assert.isTrue(account.isBlockingEnabled());

        account.setBlockingEnabled(false);

        persistenceService.persistEntity(account);

        Map<String, Object> attributes = new HashMap<String, Object>();

        attributes.put(Constants.BRM_TRANSACTION_TYPE_IDS, Arrays.asList(transactionTypeId1));

        // No AccountBlockingException should be thrown because "isBlockingEnabled" is false
        accountBlockingService.checkBlock(userId, attributes, Permission.CREATE_PAYMENT, Permission.CREATE_CHARGE);

    }


}