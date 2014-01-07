package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.util.CommonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Created by: dmulderink on 6/29/12 at 2:56 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class TransactionImportServiceTest extends AbstractServiceTest {

    @Autowired
    private TransactionImportService transactionImportService;

    @Autowired
    private AccountService accountService;

    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);
    }

    /**
     * This is a single KsaTransaction import to be wrapped in a batch
     */
    @Test
    public void singleImport() {

        String begValue = "<batch-status>";
        String endValue = "</batch-status>";

        String content = CommonUtils.getResourceAsString("xmlImport/transaction.xml");

        Assert.notNull(content);
        String response = transactionImportService.importTransactions(content);
        Assert.notNull(response);

        logger.info("Response: \n" + response);

        int begIndex = response.indexOf(begValue) + begValue.length();
        int endIndex = response.indexOf(endValue);

        String batchStatus = response.substring(begIndex, endIndex);

        Assert.hasText(batchStatus);
        Assert.hasText(batchStatus, "complete");
    }

    /**
     * The transactions.xml file contains 3 transactions to import
     * All transaction should have valid information so you should except
     * a "complete" batch status. Likewise changing the account to a unknown
     * value would negate this test. Other values can be adjusted to suit needs.
     */
    @Test
    public void batchImport() {

        String begValue = "<batch-status>";
        String endValue = "</batch-status>";

        String content = CommonUtils.getResourceAsString("xmlImport/transactions.xml");

        Assert.notNull(content);
        String response = transactionImportService.importTransactions(content);
        Assert.notNull(response);

        logger.info("Response: \n" + response);

        int begIndex = response.indexOf(begValue) + begValue.length();
        int endIndex = response.indexOf(endValue);

        String batchStatus = response.substring(begIndex, endIndex);

        Assert.hasText(batchStatus);
        Assert.hasText(batchStatus, "complete");
    }

    /**
     * The transactions_fail.xml file contains 4 transactions to import
     * The fourth transaction has an invalid account so you should except
     * and "incomplete" batch status. Likewise changing the account to a known
     * value would negate this test. Other values can be adjusted to suit needs
     */
    //@Test
    public void batchImportFail() {

        String begValue = "<batch-status>";
        String endValue = "</batch-status>";

        String content = CommonUtils.getResourceAsString("xmlImport/transactions_fail.xml");

        Assert.notNull(content);
        String response = transactionImportService.importTransactions(content);

        int begIndex = response.indexOf(begValue) + begValue.length();
        int endIndex = response.indexOf(endValue);

        String batchStatus = response.substring(begIndex, endIndex);

        Assert.hasText(batchStatus);
        Assert.hasText(batchStatus, "incomplete");
    }
}
