package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class GeneralLedgerServiceTest extends AbstractServiceTest {

    private static final String GL_ACCOUNT_ID = "01-0-131120 1326";

    @Autowired
    protected ConfigService configService;

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected TransactionService transactionService;

    @Autowired
    protected GeneralLedgerService glService;

    @Autowired
    private TransactionExportService transactionExportService;

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;


    @Before
    public void setUpWithinTransaction() throws Exception {

        // set up test data within the transaction
        String userId = "admin";

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date transactionDate = dateFormat.parse("10/12/2012");

        accountService.getOrCreateAccount(userId);

        // Creating transactions with the test user ID
        transaction1 = transactionService.createTransaction("1020", userId, transactionDate, new BigDecimal(10e7));
        transaction2 = transactionService.createTransaction("cash", userId, transactionDate, new BigDecimal(300.99));
        transaction3 = transactionService.createTransaction("chip", userId, transactionDate, new BigDecimal(77777.980));

        // Creating GL transactions with the status Q and the test user ID
        createGlTransaction(transaction1);
        createGlTransaction(transaction2);
        createGlTransaction(transaction3);
    }

    protected GlTransaction createGlTransaction(Transaction transaction) {
        return glService.createGlTransaction(transaction.getId(), GL_ACCOUNT_ID, new BigDecimal(10e4),
                GlOperationType.DEBIT, "GL transaction statement");
    }


    @Test
    public void getDefaultGLValues() {
        Assert.notNull(glService.getDefaultGeneralLedgerType());
        Assert.notNull(glService.getDefaultGeneralLedgerMode());
        Assert.notNull(configService.getParameter(Constants.DEFAULT_GL_PA_STATEMENT));
    }

    @Test
    public void createGlTransaction() throws Exception {

        GlTransaction glTransaction = createGlTransaction(transaction1);

        Assert.notNull(glTransaction);
        Assert.notNull(glTransaction.getId());
        Assert.notNull(glTransaction.getGlAccountId());
        Assert.hasLength(glTransaction.getStatement());

        Assert.isTrue(GlOperationType.DEBIT == glTransaction.getGlOperation());

    }

    @Test
    public void summarizeGlTransactions() throws Exception {

        GlTransaction glTransaction2 = createGlTransaction(transaction2);
        Assert.notNull(glTransaction2);
        Assert.notNull(glTransaction2.getId());
        Assert.notNull(glTransaction2.getStatus());
        Assert.notNull(glTransaction2.getRecognitionPeriod());

        GlTransaction glTransaction3 = createGlTransaction(transaction3);
        Assert.notNull(glTransaction3);
        Assert.notNull(glTransaction3.getId());
        Assert.notNull(glTransaction3.getStatus());
        Assert.notNull(glTransaction3.getRecognitionPeriod());

        List<GlTransaction> glTransactions =
                glService.summarizeGlTransactions(Arrays.asList(glTransaction2, glTransaction3));

        Assert.notNull(glTransactions);
        Assert.notEmpty(glTransactions);

    }

    @Test
    public void createGeneralLedgerType() throws Exception {

        GeneralLedgerType glType = glService.createGeneralLedgerType("GL_TYPE1", "Test GL type1",
                "Test GL Description 1", GL_ACCOUNT_ID, GlOperationType.CREDIT);

        Assert.notNull(glType);
        Assert.notNull(glType.getId());
        Assert.notNull(glType.getCode());
        Assert.notNull(glType.getName());
        Assert.notNull(glType.getDescription());
        Assert.notNull(glType.getGlAccountId());
        Assert.notNull(glType.getGlOperationOnCharge());
        Assert.notNull(glType.getCreationDate());
        Assert.notNull(glType.getCreatorId());

        Assert.isTrue(GlOperationType.CREDIT == glType.getGlOperationOnCharge());

    }

    @Test
    public void persistGeneralLedgerType() throws Exception {

        GeneralLedgerType glType = glService.createGeneralLedgerType("GL_TYPE1", "Test GL type1",
                "Test GL Description 1", GL_ACCOUNT_ID, GlOperationType.CREDIT);

        Assert.notNull(glType);
        Assert.notNull(glType.getId());
        Assert.notNull(glType.getCode());
        Assert.notNull(glType.getName());
        Assert.notNull(glType.getDescription());
        Assert.notNull(glType.getGlAccountId());
        Assert.notNull(glType.getGlOperationOnCharge());
        Assert.notNull(glType.getCreationDate());
        Assert.notNull(glType.getCreatorId());

        Assert.isTrue(GlOperationType.CREDIT == glType.getGlOperationOnCharge());

        glType.setCode("GL_TYPE2");
        glType.setGlOperationOnCharge(GlOperationType.DEBIT);

        Long glTypeId = glService.persistGeneralLedgerType(glType);

        Assert.notNull(glTypeId);
        Assert.notNull(glType.getId());

        Assert.isTrue(glTypeId.equals(glTypeId));
        Assert.isTrue("GL_TYPE2".equals(glType.getCode()));
        Assert.isTrue("Test GL type1".equals(glType.getName()));
        Assert.isTrue(GlOperationType.DEBIT == glType.getGlOperationOnCharge());

    }

    @Test
    public void getGeneralLedgerTypes() throws Exception {

        GeneralLedgerType glType = glService.createGeneralLedgerType("GL_TYPE10000000", "Test GL type2",
                "Test GL Description 2", GL_ACCOUNT_ID, GlOperationType.DEBIT);

        Assert.notNull(glType);
        Assert.notNull(glType.getId());
        Assert.notNull(glType.getCode());
        Assert.notNull(glType.getName());
        Assert.notNull(glType.getDescription());
        Assert.notNull(glType.getGlAccountId());
        Assert.notNull(glType.getGlOperationOnCharge());
        Assert.notNull(glType.getCreationDate());
        Assert.notNull(glType.getCreatorId());

        Assert.isTrue(GlOperationType.DEBIT == glType.getGlOperationOnCharge());

        List<GeneralLedgerType> glTypes = glService.getGeneralLedgerTypes();

        Assert.notNull(glTypes);
        Assert.notEmpty(glTypes);

        for (GeneralLedgerType type : glTypes) {

            logger.info("General Ledger Type = " + type);

            Assert.notNull(type);
            Assert.notNull(type.getId());
            Assert.notNull(type.getCode());
            Assert.notNull(type.getGlOperationOnCharge());
            Assert.notNull(type.getGlAccountId());
        }

    }

    @Test
    public void getGeneralLedgerTransactions() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date startDate = dateFormat.parse("01/01/1970");
        Date endDate = dateFormat.parse("01/01/2020");

        List<GlTransaction> glTransactions = glService.getGlTransactions(startDate, endDate);

        Assert.notNull(glTransactions);
        Assert.notEmpty(glTransactions);

        for (GlTransaction glTransaction : glTransactions) {
            Assert.notNull(glTransaction);
            Assert.notNull(glTransaction.getId());
            Assert.notNull(glTransaction.getDate());
            Assert.notNull(glTransaction.getAmount());
            Assert.isTrue(glTransaction.getDate().compareTo(startDate) >= 0);
            Assert.isTrue(glTransaction.getDate().compareTo(endDate) <= 0);
        }

    }

    @Test
    public void getGeneralLedgerTransactions2() throws Exception {

        GlTransaction glTransaction1 = createGlTransaction(transaction1);
        Assert.notNull(glTransaction1);
        Assert.notNull(glTransaction1.getId());
        Assert.notNull(glTransaction1.getStatus());
        Assert.notNull(glTransaction1.getRecognitionPeriod());

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date startDate = dateFormat.parse("01/01/1970");
        Date endDate = dateFormat.parse("01/01/2020");

        List<GlTransaction> glTransactions = glService.getGlTransactions(startDate, endDate, GL_ACCOUNT_ID);

        Assert.notNull(glTransactions);
        Assert.notEmpty(glTransactions);

        for (GlTransaction glTransaction : glTransactions) {
            Assert.notNull(glTransaction);
            Assert.notNull(glTransaction.getId());
            Assert.notNull(glTransaction.getDate());
            Assert.notNull(glTransaction.getAmount());
            Assert.isTrue(glTransaction.getDate().compareTo(startDate) >= 0);
            Assert.isTrue(glTransaction.getDate().compareTo(endDate) <= 0);
        }

    }


    @Test
    public void getQueuedGlTransactions() {

        List<GlTransaction> transactions = glService.getGlTransactionsByStatus(GlTransactionStatus.QUEUED);

        Assert.notNull(transactions);

        Assert.notEmpty(transactions);

        Assert.isTrue(transactions.size() >= 3);
    }

    private Set<String> _createGlBaselineAmounts() {

        GeneralLedgerType glType = glService.getDefaultGeneralLedgerType();

        notNull(glType);

        Transaction transaction1 = transactionService.createTransaction("cash", TEST_USER_ID, new Date(), new BigDecimal(120.01));
        Transaction transaction2 = transactionService.createTransaction("chip", TEST_USER_ID, new Date(), new BigDecimal(-10));
        Transaction transaction3 = transactionService.createTransaction("finaid", TEST_USER_ID, new Date(), new BigDecimal(10e5));

        TransactionTypeId[] typeIds = {
                transaction1.getTransactionType().getId(),
                transaction2.getTransactionType().getId(),
                transaction3.getTransactionType().getId()
        };

        for (TransactionTypeId typeId : typeIds) {

            List<GlBreakdown> breakdowns = new LinkedList<GlBreakdown>();

            GlBreakdown breakdown = new GlBreakdown();
            breakdown.setGlAccount(GL_ACCOUNT_ID);
            breakdown.setGlOperation(GlOperationType.DEBIT);
            breakdown.setBreakdown(new BigDecimal(10.5));
            breakdowns.add(breakdown);

            breakdown = new GlBreakdown();
            breakdown.setGlAccount(GL_ACCOUNT_ID);
            breakdown.setGlOperation(GlOperationType.DEBIT);
            breakdown.setBreakdown(new BigDecimal(51.99));
            breakdowns.add(breakdown);

            breakdown = new GlBreakdown();
            breakdown.setGlAccount(GL_ACCOUNT_ID);
            breakdown.setGlOperation(GlOperationType.CREDIT);
            breakdown.setBreakdown(new BigDecimal(0));
            breakdowns.add(breakdown);

            List<Long> breakdownIds = glService.createGlBreakdowns(glType.getId(), typeId, breakdowns);

            notNull(breakdownIds);
            notEmpty(breakdownIds);
            isTrue(breakdownIds.size() == breakdowns.size());
        }

        transactionService.makeEffective(transaction1.getId(), true);
        transactionService.makeEffective(transaction2.getId(), true);
        transactionService.makeEffective(transaction3.getId(), true);

        String xml = transactionExportService.exportTransactions();

        Assert.notNull(xml);
        Assert.hasText(xml);

        List<GlTransmission> transmissions = glService.getGlTransmissionsByStatuses(GlTransmissionStatus.TRANSMITTED);

        Assert.notNull(transmissions);
        Assert.notEmpty(transmissions);

        Set<String> batchIds = new HashSet<String>();

        for (GlTransmission transmission : transmissions) {

            Assert.notNull(transmission.getBatchId());
            Assert.hasText(transmission.getBatchId());

            batchIds.add(transmission.getBatchId());
        }

        for (String batchId : batchIds) {

            List<GlBatchBaseline> baselines = glService.createGlBaselineAmounts(batchId);

            Assert.notNull(baselines);

            for (GlBatchBaseline baseline : baselines) {

                logger.debug("GL Batch Baseline = " + baseline);

                Assert.notNull(baseline);
                Assert.notNull(baseline.getId());
                Assert.notNull(baseline.getAmount());
                Assert.notNull(baseline.getBatchId());
                Assert.notNull(baseline.getType());

                if (baseline.getType() == GlBatchBaselineType.GL_TYPE) {
                    Assert.notNull(baseline.getGeneralLedgerType());
                } else {
                    Assert.isNull(baseline.getGeneralLedgerType());
                }

            }
        }

        return batchIds;
    }

    @Test
    public void createGlBaselineAmounts() {
        _createGlBaselineAmounts();
    }

    @Test
    public void getGlBaselineAmounts() {

        Set<String> batchIds = _createGlBaselineAmounts();

        Assert.notNull(batchIds);
        Assert.notEmpty(batchIds);

        for (String batchId : batchIds) {

            List<GlBatchBaseline> baselines = glService.getGlBaselineAmounts(batchId);

            Assert.notNull(baselines);

            for (GlBatchBaseline baseline : baselines) {
                logger.debug("GL Batch Baseline = " + baseline);

                Assert.notNull(baseline);
                Assert.notNull(baseline.getId());
                Assert.notNull(baseline.getAmount());
                Assert.notNull(baseline.getBatchId());
                Assert.notNull(baseline.getType());

                if (baseline.getType() == GlBatchBaselineType.GL_TYPE) {
                    Assert.notNull(baseline.getGeneralLedgerType());
                } else {
                    Assert.isNull(baseline.getGeneralLedgerType());
                }

            }

        }

    }

    @Test
    public void createGlBreakdowns() throws Exception {

        TransactionType chargeType = transactionService.getTransactionType("1299", new Date());

        notNull(chargeType);

        GeneralLedgerType glType = glService.getDefaultGeneralLedgerType();

        notNull(glType);

        List<GlBreakdown> breakdowns = new LinkedList<GlBreakdown>();

        GlBreakdown breakdown = new GlBreakdown();
        breakdown.setGlAccount(GL_ACCOUNT_ID);
        breakdown.setGlOperation(GlOperationType.CREDIT);
        breakdown.setBreakdown(new BigDecimal(20.5));
        breakdowns.add(breakdown);

        breakdown = new GlBreakdown();
        breakdown.setGlAccount(GL_ACCOUNT_ID);
        breakdown.setGlOperation(GlOperationType.DEBIT);
        breakdown.setBreakdown(new BigDecimal(50));
        breakdowns.add(breakdown);

        breakdown = new GlBreakdown();
        breakdown.setGlAccount(GL_ACCOUNT_ID);
        breakdown.setGlOperation(GlOperationType.CREDIT);
        breakdown.setBreakdown(new BigDecimal(0));
        breakdowns.add(breakdown);

        List<Long> breakdownIds = glService.createGlBreakdowns(glType.getId(), chargeType.getId(), breakdowns);

        notNull(breakdownIds);
        notEmpty(breakdownIds);
        isTrue(breakdownIds.size() == breakdowns.size());

        for (GlBreakdown b : breakdowns) {
            notNull(b.getId());
            isTrue(GL_ACCOUNT_ID.equals(b.getGlAccount()));
        }

    }

    @Test
    public void getGlBreakdowns() throws Exception {

        String id = "1001";
        Integer subCode = 1;

        TransactionTypeId debitTypeId = new TransactionTypeId(id, subCode);

        TransactionType transactionType = transactionService.getTransactionType(debitTypeId);

        Assert.isTrue(transactionType instanceof DebitType, "Transaction Type must be a DebitType");

        List<GlBreakdown> breakdowns = glService.getGlBreakdowns(debitTypeId);

        Assert.notNull(breakdowns);
        Assert.notEmpty(breakdowns);
    }


}
