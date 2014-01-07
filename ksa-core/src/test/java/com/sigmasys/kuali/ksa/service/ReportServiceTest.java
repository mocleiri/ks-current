package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.sigmasys.kuali.ksa.model.CashLimitEventStatus.QUEUED;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class ReportServiceTest extends AbstractServiceTest {

    private static final String GL_ACCOUNT_ID = "01-0-131121 1327";

    @Autowired
    protected AccountService accountService;

    @Autowired
    private ReportService reportService;

    @Autowired
    protected GeneralLedgerService glService;

    @Autowired
    private TransactionExportService transactionExportService;

    @Autowired
    private CashLimitService cashLimitService;

    @Autowired
    private AuditableEntityService entityService;

    @Autowired
    private BillRecordService billRecordService;

    @Autowired
    private TransactionService transactionService;

    @PersistenceContext(unitName = Constants.KSA_PERSISTENCE_UNIT)
    private EntityManager em;

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    private SimpleDateFormat dateFormat;


    @Before
    public void setUpWithinTransaction() throws Exception {

        // set up test data within the transaction

        String userId = "admin";

        accountService.getOrCreateAccount(userId);
        accountService.getOrCreateAccount("user1");
        accountService.getOrCreateAccount("user2");

        dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date transactionDate = dateFormat.parse("10/12/2012");


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
        return glService.createGlTransaction(transaction.getId(), GL_ACCOUNT_ID, new BigDecimal(370.51),
                GlOperationType.CREDIT, "GL transaction statement");
    }

    @Test
    public void generateGeneralLedgerReport() throws Exception {

        GlTransaction glTransaction1 = createGlTransaction(transaction1);
        Assert.notNull(glTransaction1);
        Assert.notNull(glTransaction1.getId());
        Assert.notNull(glTransaction1.getStatus());
        Assert.notNull(glTransaction1.getRecognitionPeriod());

        GlTransaction glTransaction2 = createGlTransaction(transaction2);
        Assert.notNull(glTransaction2);
        Assert.notNull(glTransaction2.getId());
        Assert.notNull(glTransaction2.getStatus());
        Assert.notNull(glTransaction2.getRecognitionPeriod());

        GlTransaction glTransaction3 = createGlTransaction(transaction2);
        Assert.notNull(glTransaction3);
        Assert.notNull(glTransaction3.getId());
        Assert.notNull(glTransaction3.getStatus());
        Assert.notNull(glTransaction3.getRecognitionPeriod());

        Date startDate = dateFormat.parse("01/01/1970");
        Date endDate = dateFormat.parse("01/01/2020");

        String xml = reportService.generateGeneralLedgerReport(GL_ACCOUNT_ID, startDate, endDate);

        logger.debug("GL Report: \n" + xml);

        Assert.notNull(xml);
        Assert.hasText(xml);

    }

    @Test
    public void generateGeneralLedgerReport2() throws Exception {

        GlTransaction glTransaction1 = createGlTransaction(transaction1);
        Assert.notNull(glTransaction1);
        Assert.notNull(glTransaction1.getId());
        Assert.notNull(glTransaction1.getStatus());
        Assert.notNull(glTransaction1.getRecognitionPeriod());

        GlTransaction glTransaction2 = createGlTransaction(transaction2);
        Assert.notNull(glTransaction2);
        Assert.notNull(glTransaction2.getId());
        Assert.notNull(glTransaction2.getStatus());
        Assert.notNull(glTransaction2.getRecognitionPeriod());

        GeneralLedgerType glType = glService.getDefaultGeneralLedgerType();
        Assert.notNull(glType);

        Transaction[] transactions = {transaction1, transaction2};

        for (Transaction transaction : transactions) {

            TransactionTypeId typeId = transaction.getTransactionType().getId();

            List<GlBreakdown> breakdowns = new LinkedList<GlBreakdown>();

            GlBreakdown breakdown = new GlBreakdown();
            breakdown.setGlAccount(GL_ACCOUNT_ID);
            breakdown.setGlOperation(GlOperationType.CREDIT);
            breakdown.setBreakdown(new BigDecimal(0.5));
            breakdowns.add(breakdown);

            breakdown = new GlBreakdown();
            breakdown.setGlAccount(GL_ACCOUNT_ID);
            breakdown.setGlOperation(GlOperationType.DEBIT);
            breakdown.setBreakdown(new BigDecimal(99.4));
            breakdowns.add(breakdown);

            breakdown = new GlBreakdown();
            breakdown.setGlAccount(GL_ACCOUNT_ID);
            breakdown.setGlOperation(GlOperationType.CREDIT);
            breakdown.setBreakdown(new BigDecimal(0));
            breakdowns.add(breakdown);

            List<Long> breakdownIds = glService.createGlBreakdowns(glType.getId(), typeId, breakdowns);

            Assert.notNull(breakdownIds);
            Assert.notEmpty(breakdownIds);
            Assert.isTrue(breakdownIds.size() == breakdowns.size());

            transactionService.makeEffective(transaction.getId(), true);
        }

        String xml = transactionExportService.exportTransactions();

        logger.debug("Transaction export: \n" + xml);

        Assert.notNull(xml);
        Assert.hasText(xml);

        Date startDate = dateFormat.parse("12/01/2010");
        Date endDate = dateFormat.parse("12/01/2015");

        xml = reportService.generateGeneralLedgerReport(GL_ACCOUNT_ID, startDate, endDate, true);

        logger.debug("GL Report: \n" + xml);

        Assert.notNull(xml);
        Assert.hasText(xml);

    }

    @Test
    public void generateAccountReport() throws Exception {

        Date startDate = dateFormat.parse("12/01/2010");
        Date endDate = dateFormat.parse("12/01/2015");

        String xml = reportService.generateAccountReport("admin", startDate, endDate, true, true, true, false);

        logger.debug("Account Report: \n" + xml);

        Assert.notNull(xml);
        Assert.hasText(xml);
    }

    @Test
    public void generateAgedBalanceReport() throws Exception {

        List<String> userIds = Arrays.asList("admin", "user1");

        String xml = reportService.generateAgedBalanceReport(userIds, false, true);

        logger.debug("Aged Balance Report: \n" + xml);

        Assert.notNull(xml);
        Assert.hasText(xml);
    }

    @Test
    public void generateRejectedTransactionsReport() throws Exception {

        Date startDate = dateFormat.parse("12/01/1987");
        Date endDate = dateFormat.parse("12/01/2020");

        String xml = reportService.generateRejectedTransactionsReport(startDate, endDate, false);

        logger.debug("Rejected Transactions Report: \n" + xml);

        Assert.notNull(xml);
        Assert.hasText(xml);

    }

    @Test
    public void generateTransactionReceipt() throws Exception {

        String xml = reportService.generateTransactionReceipt(transaction2.getId());

        logger.debug("Transaction Receipt: \n" + xml);

        Assert.notNull(xml);
        Assert.hasText(xml);

    }

    @Test
    public void generateIrs1098TReports() throws Exception {

        Date startDate = dateFormat.parse("01/01/2012");
        Date endDate = dateFormat.parse("12/31/2012");

        List<String> userIds = Arrays.asList("admin", "user1", "user2");

        for (boolean isTransient : Arrays.asList(true, false)) {

            for (String userId : userIds) {

                String createdXml = reportService.generate1098TReport(userId, startDate, endDate, 4, isTransient);

                logger.debug("IRS 1098T Report for user '" + userId + "' \n" + createdXml);

                Assert.notNull(createdXml);
                Assert.hasText(createdXml);

                if (!isTransient) {

                    String restoredXml = reportService.getIrs1098TReport(userId, startDate, endDate);

                    Assert.notNull(restoredXml);
                    Assert.hasText(restoredXml);

                    Assert.isTrue(createdXml.equals(restoredXml));
                }
            }
        }

    }

    @Test
    public void generateIrs1098TReportsByYear() throws Exception {

        final int year = 2011;

        List<String> userIds = Arrays.asList("admin", "user1", "user2");

        for (boolean isTransient : new boolean[]{true, false}) {

            for (String userId : userIds) {

                String createdXml = reportService.generate1098TReportByYear(userId, year, 4, isTransient);

                logger.debug("IRS 1098T Report for user '" + userId + "' \n" + createdXml);

                Assert.notNull(createdXml);
                Assert.hasText(createdXml);

                if (!isTransient) {

                    String restoredXml = reportService.getIrs1098TReportByYear(userId, year);

                    Assert.notNull(restoredXml);
                    Assert.hasText(restoredXml);

                    Assert.isTrue(createdXml.equals(restoredXml));
                }
            }
        }
    }

    @Test
    public void generateIrs8300Report() {

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
        for (Tag tag : transaction.getTags()) {
            if ("Cash".equals(tag.getCode())) {
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
        Assert.notNull(cashLimitEvents.get(0));

        String report = reportService.generateIrs8300Report(cashLimitEvents.get(0).getId());

        logger.debug("IRS 8300 report XML:\n" + report);

        Assert.notNull(report);
        Assert.hasLength(report);

    }

    @Test
    public void getBillRecordTransactions() {

        String accountId = "admin";

        Transaction transaction1 = transactionService.createTransaction("cash", accountId, new Date(), new BigDecimal(55));
        Transaction transaction2 = transactionService.createTransaction("1020", accountId, new Date(), new BigDecimal(-8));

        Assert.notNull(transaction1);
        Assert.notNull(transaction1.getId());

        Assert.notNull(transaction2);
        Assert.notNull(transaction2.getId());

        BillRecord billRecord = billRecordService.createBillRecord(accountId, "Bill test message", new Date(),
                new Date(), new Date(), false, true, true, true,
                new HashSet<Long>(Arrays.asList(transaction1.getId(), transaction2.getId())));

        Assert.notNull(billRecord);
        Assert.notNull(billRecord.getId());

        Query query = em.createQuery("select b.transactions from BillRecord b where b.account.id = :accountId");
        query.setParameter("accountId", accountId);

        List<Transaction> transactions = query.getResultList();

        Assert.notNull(transactions);
        Assert.notEmpty(transactions);
        Assert.isTrue(transactions.size() >= 2);

    }

    @Test
    public void generateBill() throws Exception {

        String accountId = "admin";

        Date billDate = dateFormat.parse("01/01/2012");
        Date startDate = dateFormat.parse("01/01/1970");
        Date endDate = dateFormat.parse("01/01/2020");

        Transaction transaction1 = transactionService.createTransaction("cash", accountId, new Date(), new BigDecimal(5500));
        Transaction transaction2 = transactionService.createTransaction("1020", accountId, new Date(), new BigDecimal(12000));
        Transaction transaction3 = transactionService.createTransaction("finaid", accountId, new Date(), new BigDecimal(-90));

        Assert.notNull(transaction1);
        Assert.notNull(transaction1.getId());

        Assert.notNull(transaction2);
        Assert.notNull(transaction2.getId());

        Assert.notNull(transaction3);
        Assert.notNull(transaction3.getId());

        Rollup rollup1 = entityService.createAuditableEntity("_1", "Rollup _1", "Description 111", Rollup.class);
        Rollup rollup2 = entityService.createAuditableEntity("_2", "Rollup _2", "Description 222", Rollup.class);
        Rollup rollup3 = entityService.createAuditableEntity("_3", "Rollup _3", "Description 333", Rollup.class);

        Assert.notNull(rollup1);
        Assert.notNull(rollup1.getId());

        Assert.notNull(rollup2);
        Assert.notNull(rollup2.getId());

        Assert.notNull(rollup3);
        Assert.notNull(rollup3.getId());

        Set<Long> rollupIds = new HashSet<Long>(Arrays.asList(rollup1.getId(), rollup2.getId(), rollup3.getId()));

        transaction1.setRollup(rollup1);
        transactionService.persistTransaction(transaction1);

        transaction2.setRollup(rollup1);
        transactionService.persistTransaction(transaction2);

        transaction3.setRollup(rollup1);
        transactionService.persistTransaction(transaction3);

        String bill = reportService.generateBill(
                accountId,
                "Bill message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                true, true, true, true, true);

        logger.debug("Bill XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);

        bill = reportService.generateBill(
                accountId,
                "Bill message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                false, true, true, true, true);

        logger.debug("Bill XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);

        bill = reportService.generateBill(
                accountId,
                "Bill message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                false, false, true, true, true);

        logger.debug("Bill 1 XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);

        bill = reportService.generateBill(
                accountId,
                "Bill 2 message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                false, false, false, true, true);

        logger.debug("Bill 3 XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);

        bill = reportService.generateBill(
                accountId,
                "Bill 4 message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                false, false, false, false, true);

        logger.debug("Bill 5 XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);

        bill = reportService.generateBill(
                accountId,
                "Bill 6 message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                false, false, false, false, false);

        logger.debug("Bill 7 XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);

        transaction1.setRollup(rollup1);
        transactionService.persistTransaction(transaction1);

        transaction2.setRollup(rollup1);
        transactionService.persistTransaction(transaction2);

        transaction3.setRollup(rollup2);
        transactionService.persistTransaction(transaction3);

        bill = reportService.generateBill(
                accountId,
                "Bill message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                false, true, true, true, true);

        logger.debug("Bill 8 XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);

        transaction1.setRollup(rollup1);
        transactionService.persistTransaction(transaction1);

        transaction2.setRollup(rollup1);
        transactionService.persistTransaction(transaction2);

        transaction3.setRollup(null);
        transactionService.persistTransaction(transaction3);

        bill = reportService.generateBill(
                accountId,
                "Bill message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                false, true, true, true, true);

        logger.debug("Bill 9 XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);

        transaction1.setRollup(null);
        transactionService.persistTransaction(transaction1);

        transaction2.setRollup(null);
        transactionService.persistTransaction(transaction2);

        transaction3.setRollup(null);
        transactionService.persistTransaction(transaction3);

        bill = reportService.generateBill(
                accountId,
                "Bill message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                false, true, true, true, true);

        logger.debug("Bill 10 XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);

        List<BillRecord> billRecords = billRecordService.getBillRecords(accountId);

        Assert.notNull(billRecords);
        Assert.notEmpty(billRecords);
        Assert.isTrue(billRecords.size() >= 10);

        BillRecord latestBillRecord = billRecordService.getLatestBillRecord(accountId);

        Assert.notNull(latestBillRecord);
        Assert.notNull(latestBillRecord.getId());

        boolean latestBillRecordIsPresent = false;

        for (BillRecord billRecord : billRecords) {

            Assert.notNull(billRecord);
            Assert.notNull(billRecord.getId());

            if (latestBillRecord.getId().equals(billRecord.getId())) {
                latestBillRecordIsPresent = true;
            }

        }

        Assert.isTrue(latestBillRecordIsPresent);

    }

    @Test
    public void generateBills() throws Exception {

        String accountId1 = "admin";
        String accountId2 = "user1";

        Date billDate = dateFormat.parse("01/01/2012");
        Date startDate = dateFormat.parse("01/01/1970");
        Date endDate = dateFormat.parse("01/01/2020");

        Transaction transaction1 = transactionService.createTransaction("cash", accountId1, new Date(), new BigDecimal(5500));
        Transaction transaction2 = transactionService.createTransaction("1020", accountId2, new Date(), new BigDecimal(12000));

        Assert.notNull(transaction1);
        Assert.notNull(transaction1.getId());

        Assert.notNull(transaction2);
        Assert.notNull(transaction2.getId());

        Rollup rollup = entityService.createAuditableEntity("_1", "Rollup _1", "Description 111", Rollup.class);

        Assert.notNull(rollup);
        Assert.notNull(rollup.getId());

        Set<Long> rollupIds = new HashSet<Long>(Arrays.asList(rollup.getId()));

        transaction1.setRollup(rollup);
        transactionService.persistTransaction(transaction1);

        transaction2.setRollup(rollup);
        transactionService.persistTransaction(transaction2);

        String bill = reportService.generateBills(
                Arrays.asList(accountId1, accountId2),
                "Bill message",
                billDate,
                startDate,
                endDate,
                rollupIds,
                rollupIds,
                true, true, true, true, true);

        logger.debug("Bill XML:\n" + bill);

        Assert.notNull(bill);
        Assert.hasLength(bill);
    }


}