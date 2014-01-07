package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.GlOperationType;
import com.sigmasys.kuali.ksa.model.GlTransaction;
import com.sigmasys.kuali.ksa.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * TransactionExportServiceTest
 *
 * @author Michael Ivanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class TransactionExportServiceTest extends AbstractServiceTest {

    // GL Account ID must be the size of 12 according to UMD rules
    private static final String GL_ACCOUNT_ID = "01-0-131121 1326";

    @Autowired
    private TransactionExportService transactionExportService;

    @Autowired
    private GeneralLedgerService glService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;


    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);
        // Payment
        transaction1 = transactionService.createTransaction("cash", userId, new Date(), new BigDecimal(2000.00));
        // Charge
        transaction2 = transactionService.createTransaction("1020", userId, new Date(), new BigDecimal(501.999));
        // Deferment
        transaction3 = transactionService.createTransaction("chip", null, userId, new Date(), new Date(), new BigDecimal(300.00));
    }


    @Test
    public void exportTransactions() {

        GlTransaction glTransaction1 = glService.createGlTransaction(transaction1.getId(), GL_ACCOUNT_ID,
                new BigDecimal(10e4), GlOperationType.DEBIT, "Statement 1");

        Assert.notNull(glTransaction1);
        Assert.notNull(glTransaction1.getId());
        Assert.notNull(glTransaction1.getGlAccountId());

        GlTransaction glTransaction2 = glService.createGlTransaction(transaction2.getId(), GL_ACCOUNT_ID,
                new BigDecimal(399.3333), GlOperationType.CREDIT, "Statement 2");

        Assert.notNull(glTransaction2);
        Assert.notNull(glTransaction2.getId());
        Assert.notNull(glTransaction2.getGlAccountId());

        GlTransaction glTransaction3 = glService.createGlTransaction(transaction3.getId(), GL_ACCOUNT_ID,
                new BigDecimal(50.01), GlOperationType.DEBIT, "Statement 3");

        Assert.notNull(glTransaction3);
        Assert.notNull(glTransaction3.getId());
        Assert.notNull(glTransaction3.getGlAccountId());

        String xml = transactionExportService.exportTransactions();

        Assert.notNull(xml);
        Assert.hasText(xml);

        logger.info("XML = \n" + xml);

    }

    @Test
    public void exportTransactionsForDates() throws Exception {

        Date startDate = new SimpleDateFormat(Constants.DATE_FORMAT_US).parse("01/01/1990");
        Date endDate = new SimpleDateFormat(Constants.DATE_FORMAT_US).parse("01/01/2015");

        GlTransaction glTransaction1 = glService.createGlTransaction(transaction1.getId(), GL_ACCOUNT_ID,
                new BigDecimal(10e4), GlOperationType.DEBIT, "Statement 1");

        Assert.notNull(glTransaction1);
        Assert.notNull(glTransaction1.getId());
        Assert.notNull(glTransaction1.getGlAccountId());

        GlTransaction glTransaction2 = glService.createGlTransaction(transaction2.getId(), GL_ACCOUNT_ID,
                new BigDecimal(399.3333), GlOperationType.CREDIT, "Statement 2");

        Assert.notNull(glTransaction2);
        Assert.notNull(glTransaction2.getId());
        Assert.notNull(glTransaction2.getGlAccountId());

        GlTransaction glTransaction3 = glService.createGlTransaction(transaction3.getId(), GL_ACCOUNT_ID,
                new BigDecimal(50.01), GlOperationType.DEBIT, "Statement 3");

        Assert.notNull(glTransaction3);
        Assert.notNull(glTransaction3.getId());
        Assert.notNull(glTransaction3.getGlAccountId());

        // Exporting transactions for effective dates
        String xml = transactionExportService.exportTransactionsForDates(startDate, endDate, true);

        Assert.notNull(xml);
        Assert.hasText(xml);

        logger.info("XML = \n" + xml);

        // Exporting transactions for recognition dates
        xml = transactionExportService.exportTransactionsForDates(startDate, endDate, false);

        Assert.notNull(xml);
        Assert.hasText(xml);

        logger.info("XML = \n" + xml);

    }


    @Test
    public void exportTransactionsForRecognitionPeriods() throws Exception {

        final String[] recognitionPeriods = { "11", "12", "13"};

        GlTransaction glTransaction1 = glService.createGlTransaction(transaction1.getId(), GL_ACCOUNT_ID,
                new BigDecimal(10e4), GlOperationType.DEBIT, "Statement 1");

        Assert.notNull(glTransaction1);
        Assert.notNull(glTransaction1.getId());
        Assert.notNull(glTransaction1.getGlAccountId());

        GlTransaction glTransaction2 = glService.createGlTransaction(transaction2.getId(), GL_ACCOUNT_ID,
                new BigDecimal(399.3333), GlOperationType.CREDIT, "Statement 2");

        Assert.notNull(glTransaction2);
        Assert.notNull(glTransaction2.getId());
        Assert.notNull(glTransaction2.getGlAccountId());

        GlTransaction glTransaction3 = glService.createGlTransaction(transaction3.getId(), GL_ACCOUNT_ID,
                new BigDecimal(50.01), GlOperationType.DEBIT, "Statement 3");

        Assert.notNull(glTransaction3);
        Assert.notNull(glTransaction3.getId());
        Assert.notNull(glTransaction3.getGlAccountId());

        // Exporting transactions for recognition periods
        String xml = transactionExportService.exportTransactionsForPeriods(recognitionPeriods);

        Assert.notNull(xml);
        Assert.hasText(xml);

        logger.info("XML = \n" + xml);

    }


    @Test
    public void exportExistingTransactions() {

        String xml = transactionExportService.exportTransactions();

        Assert.notNull(xml);
        Assert.hasText(xml);

        logger.info("XML = \n" + xml);

    }


}
