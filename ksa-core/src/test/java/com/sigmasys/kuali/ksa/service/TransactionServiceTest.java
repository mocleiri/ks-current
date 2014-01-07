package com.sigmasys.kuali.ksa.service;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sigmasys.kuali.ksa.exception.GlTransactionFailedException;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.hold.HoldService;
import com.sigmasys.kuali.ksa.util.CalendarUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.student.r2.core.hold.dto.AppliedHoldInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import static org.springframework.util.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class TransactionServiceTest extends AbstractServiceTest {

    private static final String GL_ACCOUNT_ID = "03-2-998870 7723";

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private GeneralLedgerService glService;

    @Autowired
    private InformationService informationService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private HoldService holdService;

    private Transaction transaction1;
    private Transaction transaction2;

    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);
    }

    @Test
    public void createTransaction() throws Exception {

        String id = "cash";

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e5));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());

        isTrue("USD".equals(transaction.getCurrency().getCode()));
        isTrue("admin".equals(transaction.getAccount().getId()));
        isTrue(new Date().compareTo(transaction.getEffectiveDate()) >= 0);
        isTrue(new BigDecimal(10e5).equals(transaction.getNativeAmount()));

        logger.info("Transaction ID = " + transaction.getId());
        logger.info("Date = " + System.currentTimeMillis());
    }

    private Allocation createAllocation(boolean locked, boolean internallyLocked) {

        String id = "1020";

        transaction1 = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(100));
        transaction2 = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(-100));

        notNull(transaction1);
        notNull(transaction2);
        notNull(transaction1.getId());
        notNull(transaction2.getId());
        notNull(transaction1.getAmount());
        notNull(transaction2.getAmount());

        CompositeAllocation compositeAllocation;
        if (locked) {
            compositeAllocation = internallyLocked ?
                    transactionService.createLockedAllocation(transaction1.getId(), transaction2.getId(), new BigDecimal(90), true, true) :
                    transactionService.createLockedAllocation(transaction1.getId(), transaction2.getId(), new BigDecimal(90));
        } else {
            compositeAllocation = transactionService.createAllocation(transaction1.getId(), transaction2.getId(), new BigDecimal(90));
        }

        notNull(compositeAllocation);

        Allocation allocation = compositeAllocation.getAllocation();

        notNull(allocation);
        notNull(allocation.getId());
        notNull(allocation.getFirstTransaction());
        notNull(allocation.getSecondTransaction());
        notNull(allocation.getFirstTransaction().getId());
        notNull(allocation.getSecondTransaction().getId());
        isTrue(allocation.getFirstTransaction().getId().equals(transaction1.getId()));
        isTrue(allocation.getSecondTransaction().getId().equals(transaction2.getId()));

        transaction1 = transactionService.getTransaction(transaction1.getId());
        transaction2 = transactionService.getTransaction(transaction2.getId());

        notNull(transaction1);
        notNull(transaction2);
        notNull(transaction1.getId());
        notNull(transaction2.getId());

        isTrue(new BigDecimal(90).equals(allocation.getAmount()));

        BigDecimal allocatedAmount1 = locked ?
                transaction1.getLockedAllocatedAmount() :
                transaction1.getAllocatedAmount();

        BigDecimal allocatedAmount2 = locked ?
                transaction2.getLockedAllocatedAmount() :
                transaction2.getAllocatedAmount();

        logger.info("allocatedAmount1 = " + allocatedAmount1);
        logger.info("allocatedAmount2 = " + allocatedAmount2);

        isTrue(new BigDecimal(90).equals(allocatedAmount1));
        isTrue(new BigDecimal(90).equals(allocatedAmount2));

        return allocation;
    }

    @Test
    public void createAllocation() throws Exception {

        createAllocation(false, false);

    }

    @Test
    public void createLockedAllocation() throws Exception {

        createAllocation(true, false);

    }

    @Test
    public void createInternalLockedAllocation() throws Exception {

        Allocation allocation = createAllocation(true, true);

        isTrue(allocation.isInternallyLocked());

    }

    @Test
    public void getAllocations() throws Exception {

        createAllocation(true, false);

        Transaction transaction = transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(-10));

        Assert.notNull(transaction);
        Assert.notNull(transaction.getId());

        CompositeAllocation compositeAllocation =
                transactionService.createAllocation(transaction, transaction1, new BigDecimal(3.5), true, true, false);

        Assert.notNull(compositeAllocation);
        Assert.notNull(compositeAllocation.getAllocation());
        Assert.notNull(compositeAllocation.getAllocation().getFirstTransaction());
        Assert.notNull(compositeAllocation.getAllocation().getSecondTransaction());

        transaction = transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(-5));

        Assert.notNull(transaction);
        Assert.notNull(transaction.getId());

        compositeAllocation =
                transactionService.createAllocation(transaction1, transaction, new BigDecimal(1), true, true, false);

        Assert.notNull(compositeAllocation);
        Assert.notNull(compositeAllocation.getAllocation());
        Assert.notNull(compositeAllocation.getAllocation().getFirstTransaction());
        Assert.notNull(compositeAllocation.getAllocation().getSecondTransaction());

        List<Allocation> allocations = transactionService.getAllocations(transaction1.getId());

        Assert.notNull(allocations);
        Assert.notEmpty(allocations);

        logger.debug("Number of allocations = " + allocations.size());

        for (Allocation allocation : allocations) {

            Assert.notNull(allocation.getId());
            Assert.notNull(allocation.getFirstTransaction());
            Assert.notNull(allocation.getSecondTransaction());

            Long transactionId1 = allocation.getFirstTransaction().getId();
            Long transactionId2 = allocation.getSecondTransaction().getId();

            Assert.notNull(transactionId1);
            Assert.notNull(transactionId2);

            Assert.isTrue(allocation.isLocked());

        }

    }


    @Test
    public void removeAllocation() throws Exception {

        createAllocation();

        transactionService.removeAllocation(transaction1.getId(), transaction2.getId());

    }

    @Test
    public void removeLockedAllocation() throws Exception {

        createLockedAllocation();

        transactionService.removeLockedAllocation(transaction1.getId(), transaction2.getId());

    }

    @Test
    public void removeAllocations() throws Exception {

        createAllocation();

        transactionService.removeAllAllocations(transaction2.getId());

    }

    @Test
    public void getTransactions() throws Exception {

        List<Transaction> transactions = transactionService.getTransactions();

        notNull(transactions);
        notEmpty(transactions);

        // Add more assertions when we have some test data
    }


    @Test
    public void getTransactionsByDateRange() {

        Date startDate = null;
        Date endDate = null;
        String userId = "user1";

        List<Transaction> transactions = transactionService.getTransactions(userId, startDate, endDate);

        notNull(transactions);

        Set<Long> transactionIds = new HashSet<Long>();

        for (Transaction transaction : transactions) {

            notNull(transaction);
            notNull(transaction.getId());

            isTrue(!transactionIds.contains(transaction.getId()));

            transactionIds.add(transaction.getId());

            logger.info("Transaction = " + transaction);

            Date effective = transaction.getEffectiveDate();

            logger.info("Effective date = " + effective);

            if (startDate == null) {
                startDate = effective;
            }

            if (endDate == null) {
                endDate = effective;
            }

            if (effective.before(startDate)) {
                startDate = effective;
            }

            if (effective.after(endDate)) {
                endDate = effective;
            }


        }

        logger.info("Start date = " + startDate);
        logger.info("End date = " + endDate);

        List<Transaction> filtered = transactionService.getTransactions(userId, startDate, endDate);

        Assert.notNull(filtered);
        Assert.isTrue(transactions.size() == filtered.size(), "All transactions size: " + transactions.size() + " does not match filtered size: " + filtered.size());

    }


    @Test
    public void getCharges() throws Exception {

        List<Charge> charges = transactionService.getCharges();

        notNull(charges);
        notEmpty(charges);

        // Add more assertions when we have some test data
    }

    @Test
    public void getTransaction() throws Exception {

        Transaction transaction = transactionService.getTransaction(7777777L);

        // Check that the entity does not exist
        isNull(transaction);

        // Add more assertions when we have some test data
    }

    @Test
    public void getTransactionByUserId() throws Exception {

        List<Transaction> transactions = transactionService.getTransactions("dukakis");

        notNull(transactions);

        isTrue(transactions.isEmpty());

        // Add more assertions when we have some test data
    }

    @Test
    public void getChargesWithFormattedAmounts() throws Exception {

        List<Charge> charges = transactionService.getCharges();

        notNull(charges);
        notEmpty(charges);

        for (Charge charge : charges) {
            notNull(charge.getFormattedAmount());
            logger.info("Formatted amount = " + charge.getFormattedAmount());
        }

    }

    @Test
    public void getTransactionType() throws Exception {

        String id = "1020";

        TransactionType transactionType = transactionService.getTransactionType(id, new Date());

        notNull(transactionType);
        notNull(transactionType.getId());
        isTrue("1020".equals(transactionType.getId().getId()));

    }

    @Test
    public void getTransactionTypeClass() throws Exception {

        String id = "1020";

        Class<TransactionType> debitTypeClass = transactionService.getTransactionTypeClass(id);

        notNull(debitTypeClass);
        notNull(debitTypeClass.equals(DebitType.class));

    }

    @Test
    public void reverseTransaction() throws Exception {

        String id = "cash";

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e5));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        TransactionTypeId transactionTypeId = transaction.getTransactionType().getId();

        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        transaction = transactionService.reverseTransaction(transaction.getId(), "Memo text", new BigDecimal(150.05),
                "Reversed");

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        isTrue(transaction.getStatementText().contains("Reversed"));
        isTrue(transactionTypeId.equals(transaction.getTransactionType().getId()));

    }

    @Test
    public void contestCharge() throws Exception {

        String id = "1020";

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e5));

        notNull(transaction);
        isTrue(transaction instanceof Charge);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.CHARGE);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        transaction = transactionService.contestCharge(transaction.getId(), new Date(), "Memo text");

        notNull(transaction);
        isTrue(transaction instanceof Charge);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.CHARGE);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        List<Allocation> allocations = transactionService.getAllocations(transaction.getId());

        notNull(allocations);
        notEmpty(allocations);

        boolean chargeExists = false;
        boolean defermentExists = false;

        for (Allocation allocation : allocations) {

            notNull(allocation);
            notNull(allocation.getId());
            notNull(allocation.getTransactions());
            notEmpty(allocation.getTransactions());
            isTrue(allocation.getTransactions().size() == 2);

            for (Transaction allocatedTransaction : allocation.getTransactions()) {

                notNull(allocatedTransaction);

                if (transaction.getId().equals(allocatedTransaction.getId())) {
                    chargeExists = true;
                } else if (allocatedTransaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT) {
                    defermentExists = true;
                }

            }

        }

        isTrue(chargeExists);
        isTrue(defermentExists);

    }

    @Test
    public void cancelCharge() throws Exception {

        String id = "1020";

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e5));

        notNull(transaction);
        isTrue(transaction instanceof Charge);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.CHARGE);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        transaction = transactionService.cancelCharge(transaction.getId(), "Memo text");

        notNull(transaction);
        isTrue(transaction instanceof Charge);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.CHARGE);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        // TODO: provide charges with cancellation rules so that a cancellation will not be 0 and check the result of
        // TODO: reverseTransaction() method call

        //isTrue(transaction.isOffset());
        //isTrue(transaction.getStatus() == TransactionStatus.ACTIVE);

    }

    @Test
    public void discountCharge() throws Exception {

        String discountTransactionTypeId = "1020";

        Transaction transaction = transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(10e5));

        notNull(transaction);
        isTrue(transaction instanceof Charge);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.CHARGE);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        transaction = transactionService.discountCharge(transaction.getId(), discountTransactionTypeId,
                new BigDecimal(240.99), "Memo text", "Statement Prefix");

        notNull(transaction);
        isTrue(transaction instanceof Charge);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.CHARGE);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        isTrue(transaction.isOffset());
        isTrue(transaction.getStatus() == TransactionStatus.ACTIVE);

        List<Allocation> allocations = transactionService.getAllocations(transaction.getId());

        notNull(allocations);
        notEmpty(allocations);

        boolean discountingTransactionExists = false;

        for (Allocation allocation : allocations) {

            notNull(allocation);
            notNull(allocation.getId());
            notNull(allocation.getTransactions());
            notEmpty(allocation.getTransactions());
            isTrue(allocation.getTransactions().size() == 2);

            for (Transaction allocatedTransaction : allocation.getTransactions()) {

                notNull(allocatedTransaction);

                if (allocatedTransaction.getStatus() == TransactionStatus.DISCOUNTING) {

                    discountingTransactionExists = true;

                    isTrue(allocatedTransaction instanceof Charge);
                    notNull(allocatedTransaction.getAmount());
                    isTrue(allocatedTransaction.getAmount().compareTo(BigDecimal.ZERO) < 0);

                }

            }

        }

        isTrue(discountingTransactionExists);
    }

    @Test
    public void bouncePayment1() throws Exception {

        String id = "cash";

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e5));

        notNull(transaction);
        isTrue(transaction instanceof Payment);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        transaction = transactionService.bouncePayment(transaction.getId(), "Memo text");

        notNull(transaction);
        isTrue(transaction instanceof Payment);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        isTrue(transaction.isOffset());
        isTrue(transaction.getStatus() == TransactionStatus.ACTIVE);

        List<Allocation> allocations = transactionService.getAllocations(transaction.getId());

        notNull(allocations);
        notEmpty(allocations);

        boolean bouncingTransactionExists = false;

        for (Allocation allocation : allocations) {

            notNull(allocation);
            notNull(allocation.getId());
            notNull(allocation.getTransactions());
            notEmpty(allocation.getTransactions());
            isTrue(allocation.getTransactions().size() == 2);

            for (Transaction allocatedTransaction : allocation.getTransactions()) {

                notNull(allocatedTransaction);

                if (allocatedTransaction.getStatus() == TransactionStatus.BOUNCING) {

                    bouncingTransactionExists = true;

                    logger.info("Original payment amount = " + transaction.getAmount());
                    logger.info("Bouncing payment amount = " + allocatedTransaction.getAmount());

                    isTrue(allocatedTransaction instanceof Payment);
                    notNull(allocatedTransaction.getAmount());
                    isTrue(allocatedTransaction.getAmount().compareTo(BigDecimal.ZERO) < 0);
                    isTrue(allocatedTransaction.getAmount().negate().compareTo(transaction.getAmount()) == 0);

                }

            }

        }

        isTrue(bouncingTransactionExists);
    }

    @Test
    public void bouncePayment2() throws Exception {

        String id = "finaid";

        Transaction transaction = transactionService.createPayment(id, TEST_USER_ID, new Date(), new BigDecimal(2500.00));

        notNull(transaction);
        isTrue(transaction instanceof Payment);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        holdService.createAppliedHold(TEST_USER_ID, "kuali.hold.issue.type.library",
                "Unpaid Library Fine", "Hold_1 Name", "Hold_1 Description", new Date(), null);

        transaction = transactionService.bouncePayment(transaction.getId(), "Memo text");

        notNull(transaction);
        isTrue(transaction instanceof Payment);
        isTrue(transaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        isTrue(transaction.isOffset());
        isTrue(transaction.getStatus() == TransactionStatus.ACTIVE);

        List<Allocation> allocations = transactionService.getAllocations(transaction.getId());

        notNull(allocations);
        notEmpty(allocations);

        List<Flag> flags = informationService.getFlags(TEST_USER_ID);

        notNull(flags);
        notEmpty(flags);

        boolean overLimitFlagExists = false;

        for (Flag flag : flags) {

            notNull(flag);
            notNull(flag.getType());
            notNull(flag.getType().getCode());

            if (flag.getType().getCode().equals("OverLimit") && new Integer(10).equals(flag.getSeverity())) {
                overLimitFlagExists = true;
            }
        }

        isTrue(overLimitFlagExists);

        List<Alert> alerts = informationService.getAlerts(TEST_USER_ID);

        notNull(alerts);
        notEmpty(alerts);

        boolean alertExists = false;

        for (Alert alert : alerts) {

            notNull(alert);
            notNull(alert.getText());

            if (alert.getText().equals("Library Fine Alert")) {
                alertExists = true;
            }
        }

        isTrue(alertExists);

        List<AppliedHoldInfo> holds = holdService.getAppliedHoldsByPerson(TEST_USER_ID, holdService.getHoldContextInfo());

        notNull(holds);
        notEmpty(holds);

        boolean holdExists = false;

        for (AppliedHoldInfo hold : holds) {

            notNull(hold);
            notNull(hold.getName());

            if (hold.getName().equals("Library Fine")) {
                holdExists = true;
            }
        }

        isTrue(holdExists);

        List<Charge> charges = transactionService.getCharges(TEST_USER_ID, new Date(), new Date());

        notNull(charges);
        notEmpty(charges);

        boolean chargeExists = false;

        for (Charge charge : charges) {

            notNull(charge);

            TransactionType transactionType = charge.getTransactionType();

            notNull(transactionType);

            String transactionTypeId = transactionType.getId().getId();

            notNull(transactionTypeId);

            if (transactionTypeId.equals("1020") && new BigDecimal(850.45).compareTo(charge.getAmount()) == 0) {
                chargeExists = true;
            }
        }

        isTrue(chargeExists);

    }

    @Test
    public void createDeferment() throws Exception {

        // Must be a credit type 'C'
        String id = "ach";
        String userId = "admin";

        Date effectiveDate = new Date();
        Date expirationDate = new Date(effectiveDate.getTime() * 2);

        Transaction deferment =
                transactionService.createTransaction(id, null, userId, effectiveDate, effectiveDate, expirationDate,
                        new BigDecimal(10e5), false);


        notNull(deferment);

        isTrue(deferment instanceof Deferment);

        notNull(deferment.getId());
        notNull(deferment.getTransactionType());
        notNull(deferment.getTransactionType().getId());

        notNull(deferment.getAccount());
        notNull(deferment.getAccountId());
        notNull(deferment.getCurrency());
        notNull(deferment.getAmount());

        notNull(deferment.getEffectiveDate());
        notNull(deferment.getRecognitionDate());

    }

    @Test
    public void expireDeferment() throws Exception {

        // Must be a credit type 'C'
        String id = "ach";
        String userId = "admin";

        Date effectiveDate = new Date();
        Date expirationDate = new Date(effectiveDate.getTime() * 2);

        Transaction deferment =
                transactionService.createTransaction(id, null, userId, effectiveDate, effectiveDate, expirationDate,
                        new BigDecimal(10e5), false);


        notNull(deferment);

        isTrue(deferment instanceof Deferment);

        notNull(deferment.getId());
        notNull(deferment.getTransactionType());
        notNull(deferment.getTransactionType().getId());

        notNull(deferment.getAccount());
        notNull(deferment.getAccountId());
        notNull(deferment.getCurrency());
        notNull(deferment.getAmount());

        notNull(deferment.getEffectiveDate());
        notNull(deferment.getRecognitionDate());

        transactionService.expireDeferment(deferment.getId());

    }

    @Test
    public void makeEffective() throws Exception {

        String transactionTypeId = "1299";

        TransactionType transactionType = transactionService.getTransactionType(transactionTypeId, new Date());

        notNull(transactionType);

        GeneralLedgerType glType = glService.getDefaultGeneralLedgerType();

        notNull(glType);

        Transaction transaction = transactionService.createTransaction(transactionTypeId, TEST_USER_ID, new Date(),
                new BigDecimal(12000.89));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        notNull(transaction.getEffectiveDate());
        notNull(transaction.getRecognitionDate());

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

        List<Long> breakdownIds = glService.createGlBreakdowns(glType.getId(), transactionType.getId(), breakdowns);

        notNull(breakdownIds);
        notEmpty(breakdownIds);
        isTrue(breakdownIds.size() == breakdowns.size());

        boolean result = transactionService.makeEffective(transaction.getId(), false);

        Assert.isTrue(result);

    }

    @Test
    public void makeEffectiveFailed() throws Exception {

        TransactionType debitType = transactionService.createDebitType("Debit_#", "Debit_# Name", new Date(), 1, "");

        Assert.notNull(debitType);
        Assert.notNull(debitType.getId());

        String debitTypeId = debitType.getId().getId();

        Assert.notNull(debitTypeId);

        Transaction transaction = transactionService.createTransaction(debitTypeId, TEST_USER_ID, new Date(), new BigDecimal(12000.89));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        notNull(transaction.getEffectiveDate());
        notNull(transaction.getRecognitionDate());

        boolean success = false;

        try {
            success = transactionService.makeEffective(transaction.getId(), false);
        } catch (GlTransactionFailedException e) {
            logger.error("makeEffective() failed for Transaction ID = " + e.getSourceTransactionId());
        }

        Assert.isTrue(!success);

    }

    @Test
    public void makeEffectiveForced() throws Exception {

        String transactionTypeId = "cash";

        TransactionType transactionType = transactionService.getTransactionType(transactionTypeId, new Date());

        notNull(transactionType);

        GeneralLedgerType glType = glService.getDefaultGeneralLedgerType();

        notNull(glType);

        Transaction transaction = transactionService.createTransaction(transactionTypeId, TEST_USER_ID, new Date(),
                new BigDecimal(-10e6));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        notNull(transaction.getEffectiveDate());
        notNull(transaction.getRecognitionDate());

        List<GlBreakdown> breakdowns = new LinkedList<GlBreakdown>();

        GlBreakdown breakdown = new GlBreakdown();
        breakdown.setGlAccount(GL_ACCOUNT_ID);
        breakdown.setGlOperation(GlOperationType.CREDIT);
        breakdown.setBreakdown(new BigDecimal(10.13));

        breakdown = new GlBreakdown();
        breakdown.setGlAccount(GL_ACCOUNT_ID);
        breakdown.setGlOperation(GlOperationType.DEBIT);
        breakdown.setBreakdown(new BigDecimal(0));
        breakdowns.add(breakdown);

        List<Long> breakdownIds = glService.createGlBreakdowns(glType.getId(), transactionType.getId(), breakdowns);

        notNull(breakdownIds);
        notEmpty(breakdownIds);
        isTrue(breakdownIds.size() == breakdowns.size());

        boolean result = transactionService.makeEffective(transaction.getId(), true);

        Assert.isTrue(result);
    }

    @Test
    public void makeAllTransactionsEffective() throws Exception {

        String transactionTypeId = "chip";

        TransactionType transactionType = transactionService.getTransactionType(transactionTypeId, new Date());

        notNull(transactionType);

        GeneralLedgerType glType = glService.getDefaultGeneralLedgerType();

        notNull(glType);

        List<GlBreakdown> breakdowns = new LinkedList<GlBreakdown>();

        GlBreakdown breakdown = new GlBreakdown();
        breakdown.setGlAccount(GL_ACCOUNT_ID);
        breakdown.setGlOperation(GlOperationType.DEBIT);
        breakdown.setBreakdown(new BigDecimal(88.09));

        breakdown = new GlBreakdown();
        breakdown.setGlAccount(GL_ACCOUNT_ID);
        breakdown.setGlOperation(GlOperationType.CREDIT);
        breakdown.setBreakdown(new BigDecimal(0));
        breakdowns.add(breakdown);

        List<Long> breakdownIds = glService.createGlBreakdowns(glType.getId(), transactionType.getId(), breakdowns);

        notNull(breakdownIds);
        notEmpty(breakdownIds);
        isTrue(breakdownIds.size() == breakdowns.size());

        transactionService.createTransaction("cash", TEST_USER_ID, new Date(), new BigDecimal(350));
        transactionService.createTransaction("1020", TEST_USER_ID, new Date(), new BigDecimal(-900));

        Transaction transaction = transactionService.createTransaction(transactionTypeId, TEST_USER_ID, new Date(),
                new BigDecimal(10e5));

        List<AbstractGlBreakdown> abstractBreakdowns = glService.getGlBreakdowns(transaction);

        Assert.notNull(abstractBreakdowns);
        Assert.notEmpty(abstractBreakdowns);
        Assert.isTrue(abstractBreakdowns.size() == breakdowns.size());

        for (AbstractGlBreakdown abstractBreakdown : abstractBreakdowns) {

            Assert.notNull(abstractBreakdown);
            Assert.notNull(abstractBreakdown.getId());

            Assert.isTrue(abstractBreakdown instanceof GlBreakdown);
        }

        Assert.isTrue(transactionService.makeAllTransactionsEffective(false) > 0);

        Assert.isTrue(transactionService.makeFailedTransactionsEffective() == 0);
    }

    @Test
    public void makeFailedTransactionsEffective() throws Exception {

        TransactionType creditType =
                transactionService.createCreditType("TT_77", "TT_77 Name", new Date(), 1, "Credit Type 77 Description");

        Assert.notNull(creditType);
        Assert.notNull(creditType.getId());

        String creditTypeId = creditType.getId().getId();

        Assert.notNull(creditTypeId);

        Transaction transaction1 =
                transactionService.createTransaction(creditTypeId, TEST_USER_ID, new Date(), new BigDecimal(0.01));

        Assert.notNull(transaction1);
        Assert.notNull(transaction1.getId());

        Transaction transaction2 =
                transactionService.createTransaction(creditTypeId, TEST_USER_ID, new Date(), new BigDecimal(-600));

        Assert.notNull(transaction2);
        Assert.notNull(transaction2.getId());

        Transaction transaction3 =
                transactionService.createTransaction(creditTypeId, TEST_USER_ID, new Date(), new BigDecimal(10e3));

        Assert.notNull(transaction3);
        Assert.notNull(transaction3.getId());

        transactionService.makeAllTransactionsEffective(true);

        List<FailedGlTransaction> failedGlTransactions = glService.getFailedGlTransactions();

        Assert.notNull(failedGlTransactions);
        Assert.notEmpty(failedGlTransactions);
        Assert.isTrue(failedGlTransactions.size() >= 3);

        failedGlTransactions = glService.getFailedGlTransactionsForAccount(TEST_USER_ID);

        Assert.notNull(failedGlTransactions);
        Assert.notEmpty(failedGlTransactions);
        Assert.isTrue(failedGlTransactions.size() >= 3);

        failedGlTransactions = glService.getFailedGlTransactionsForDates(new Date(), new Date());

        Assert.notNull(failedGlTransactions);
        Assert.notEmpty(failedGlTransactions);
        Assert.isTrue(failedGlTransactions.size() >= 3);

        boolean transaction1Exists = false;
        boolean transaction2Exists = false;
        boolean transaction3Exists = false;

        for (FailedGlTransaction failedGlTransaction : failedGlTransactions) {

            Assert.notNull(failedGlTransaction);
            Assert.notNull(failedGlTransaction.getId());
            Assert.notNull(failedGlTransaction.getTransaction());
            Assert.notNull(failedGlTransaction.getTransaction().getId());

            if (transaction1.getId().equals(failedGlTransaction.getTransaction().getId())) {
                Assert.isTrue(!failedGlTransaction.isFixed());
                transaction1Exists = true;
            } else if (transaction2.getId().equals(failedGlTransaction.getTransaction().getId())) {
                Assert.isTrue(!failedGlTransaction.isFixed());
                transaction2Exists = true;
            } else if (transaction3.getId().equals(failedGlTransaction.getTransaction().getId())) {
                Assert.isTrue(!failedGlTransaction.isFixed());
                transaction3Exists = true;
            }
        }

        Assert.isTrue(transaction1Exists);
        Assert.isTrue(transaction2Exists);
        Assert.isTrue(transaction3Exists);

        Assert.isTrue(transactionService.makeFailedTransactionsEffective() == 0);
    }

    @Test
    public void makeAllTransactionsEffectiveForced() throws Exception {

        String transactionTypeId = "finaid";

        TransactionType transactionType = transactionService.getTransactionType(transactionTypeId, new Date());

        notNull(transactionType);

        GeneralLedgerType glType = glService.getDefaultGeneralLedgerType();

        notNull(glType);

        List<GlBreakdown> breakdowns = new LinkedList<GlBreakdown>();

        GlBreakdown breakdown = new GlBreakdown();
        breakdown.setGlAccount(GL_ACCOUNT_ID);
        breakdown.setGlOperation(GlOperationType.DEBIT);
        breakdown.setBreakdown(new BigDecimal(0));
        breakdowns.add(breakdown);

        List<Long> breakdownIds = glService.createGlBreakdowns(glType.getId(), transactionType.getId(), breakdowns);

        notNull(breakdownIds);
        notEmpty(breakdownIds);
        isTrue(breakdownIds.size() == breakdowns.size());

        transactionService.createTransaction(transactionTypeId, TEST_USER_ID, new Date(), new BigDecimal(350));
        transactionService.createTransaction("1020", TEST_USER_ID, new Date(), new BigDecimal(3900));

        Assert.isTrue(transactionService.makeAllTransactionsEffective(true) > 0);
    }

    @Test
    public void writeOffTransaction() throws Exception {

        // Must be a Charge
        String id = "1020";

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e3));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        notNull(transaction.getEffectiveDate());
        notNull(transaction.getRecognitionDate());

        transaction = transactionService.writeOffTransaction(transaction.getId(), "Memo text", "Write-off");

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        isTrue(transaction.getStatementText().contains("Write-off"));
        isTrue(transaction.getAmount().compareTo(new BigDecimal(10e3).negate()) == 0);

    }

    @Test
    public void setGeneralLedgerType1() throws Exception {

        String id = "1020";

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e3));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        isTrue(!transaction.isGlEntryGenerated());

        GeneralLedgerType glType = glService.getDefaultGeneralLedgerType();

        notNull(glType);
        notNull(glType.getId());

        transactionService.setGeneralLedgerType(transaction.getId(), glType.getId());

        transaction = transactionService.getTransaction(transaction.getId());

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getGeneralLedgerType());
        isTrue(glType.getId().equals(transaction.getGeneralLedgerType().getId()));
        isTrue(!transaction.isGlEntryGenerated());

    }

    @Test
    public void setGeneralLedgerType2() throws Exception {

        String id = "1020";

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e3));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        isTrue(!transaction.isGlEntryGenerated());

        transactionService.makeEffective(transaction.getId(), false);

        GeneralLedgerType glType = glService.getDefaultGeneralLedgerType();

        notNull(glType);
        notNull(glType.getId());

        boolean isException = false;
        try {
            transactionService.setGeneralLedgerType(transaction.getId(), glType.getId());
        } catch (IllegalStateException e) {
            notNull(e.getMessage());
            isException = true;
        }

        isTrue(isException);
    }


    @Test
    public void allocateReversals() throws Exception {

        String userId = "admin";

        Transaction transaction = transactionService.createTransaction("1020", userId, new Date(), new BigDecimal(10e5));

        notNull(transaction);
        notNull(transaction.getId());

        transaction = transactionService.createTransaction("1020", userId, new Date(), new BigDecimal(-10e5));

        notNull(transaction);
        notNull(transaction.getId());


        List<Transaction> transactions = transactionService.getTransactions(userId);

        notNull(transactions);
        notEmpty(transactions);

        for (Transaction t : transactions) {
            notNull(t);
            notNull(t.getId());
        }

        List<GlTransaction> glTransactions = transactionService.allocateReversals(userId, true);

        notNull(glTransactions);
        notEmpty(glTransactions);

        isTrue(glTransactions.size() == 2);

        glTransactions = transactionService.allocateReversals(userId, false);

        notNull(glTransactions);

        isTrue(glTransactions.isEmpty());

    }


    @Test
    public void testTransactionExistsByTransactionType() throws Exception {

        // Create a new Transaction:
        String transactionTypeId = "1020";
        String accountId = "admin";
        BigDecimal amount = new BigDecimal(10e3);
        Date effectiveDate = new Date();

        transactionService.createTransaction(transactionTypeId, accountId, effectiveDate, amount);

        // Call the service:
        boolean exists = transactionService.transactionExists(accountId, transactionTypeId);

        isTrue(exists);

        // Try to find a Transaction by a fake Account:
        String fakeAccount = "fake";

        exists = transactionService.transactionExists(fakeAccount, transactionTypeId);
        isTrue(!exists);

        // Try to find a Transaction by a fake Transaction ID:
        String fakeTransactionTypeId = "somethingelse";

        exists = transactionService.transactionExists(accountId, fakeTransactionTypeId);
        isTrue(!exists);

        // Try to find a Transaction by all fake parameters:
        exists = transactionService.transactionExists(fakeAccount, fakeTransactionTypeId);
        isTrue(!exists);

        // Pass invalid parameters:
        try {
            transactionService.transactionExists(null, fakeTransactionTypeId);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(fakeAccount, null);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }
    }

    @Test
    public void testTransactionExistsByTransactionTypeAndAmount() throws Exception {

        // Create a new Transaction:
        String transactionTypeId = "1020";
        String accountId = "admin";
        BigDecimal amount = new BigDecimal(10e3);
        Date effectiveDate = new Date();

        transactionService.createTransaction(transactionTypeId, accountId, effectiveDate, amount);

        // Call the service:
        BigDecimal amountFrom = new BigDecimal(10e2);
        BigDecimal amountTo = new BigDecimal(10e4);
        boolean exists = transactionService.transactionExists(accountId, transactionTypeId, amountFrom, amountTo);

        isTrue(exists);

        // Try to find a Transaction by a fake Account:
        String fakeAccount = "fake";

        exists = transactionService.transactionExists(fakeAccount, transactionTypeId, amountFrom, amountTo);
        isTrue(!exists);

        // Try to find a Transaction by a fake Transaction ID:
        String fakeTransactionTypeId = "somethingelse";

        exists = transactionService.transactionExists(accountId, fakeTransactionTypeId, amountFrom, amountTo);
        isTrue(!exists);

        // Try to find a Transaction by a fake amount:
        BigDecimal fakeAmount = new BigDecimal(1.0);

        exists = transactionService.transactionExists(accountId, transactionTypeId, fakeAmount, fakeAmount);
        isTrue(!exists);

        // Try to find a Transaction by all fake parameters:
        exists = transactionService.transactionExists(fakeAccount, fakeTransactionTypeId, fakeAmount, fakeAmount);
        isTrue(!exists);

        // Pass invalid parameters:
        try {
            transactionService.transactionExists(null, fakeTransactionTypeId, fakeAmount, fakeAmount);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(fakeAccount, null, fakeAmount, fakeAmount);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(fakeAccount, fakeTransactionTypeId, (BigDecimal) null, null);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, (BigDecimal) null, null);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }
    }

    @Test
    public void testTransactionExistsByTransactionTypeAndEffectiveDate() throws Exception {

        // Create a new Transaction:
        String transactionTypeId = "1020";
        String accountId = "admin";
        BigDecimal amount = new BigDecimal(10e3);
        Date effectiveDate = new Date();

        transactionService.createTransaction(transactionTypeId, accountId, effectiveDate, amount);

        // Prepare input Date parameters:
        Calendar newDateFrom = Calendar.getInstance();
        int newDateFromYear = newDateFrom.get(Calendar.YEAR) - 1;
        int newDateFromMonth = Calendar.JULY;
        int newDateFromDay = 18;
        Calendar newDateTo = Calendar.getInstance();
        int newDateToYear = newDateTo.get(Calendar.YEAR) + 1;
        int newDateToMonth = Calendar.JANUARY;
        int newDateToDay = 25;

        newDateFrom.set(Calendar.YEAR, newDateFromYear);
        newDateFrom.set(Calendar.MONTH, newDateFromMonth);
        newDateFrom.set(Calendar.DAY_OF_MONTH, newDateFromDay);

        newDateTo.set(Calendar.YEAR, newDateToYear);
        newDateTo.set(Calendar.MONTH, newDateToMonth);
        newDateTo.set(Calendar.DAY_OF_MONTH, newDateToDay);

        // Call the service:
        boolean exists = transactionService.transactionExists(accountId, transactionTypeId, newDateFrom.getTime(), newDateTo.getTime());

        isTrue(exists);

        // Try to find a Transaction by a fake Account:
        String fakeAccount = "fake";

        exists = transactionService.transactionExists(fakeAccount, transactionTypeId, newDateFrom.getTime(), newDateTo.getTime());
        isTrue(!exists);

        // Try to find a Transaction by a fake Transaction ID:
        String fakeTransactionTypeId = "somethingelse";

        exists = transactionService.transactionExists(accountId, fakeTransactionTypeId, newDateFrom.getTime(), newDateTo.getTime());
        isTrue(!exists);

        // Try to find a Transaction by a fake Effective Date:
        Date fakeEffectiveDate = new Date(0);

        exists = transactionService.transactionExists(accountId, transactionTypeId, fakeEffectiveDate, fakeEffectiveDate);
        isTrue(!exists);

        // Try to find a Transaction by all fake parameters:
        exists = transactionService.transactionExists(fakeAccount, fakeTransactionTypeId, fakeEffectiveDate, fakeEffectiveDate);
        isTrue(!exists);

        // Pass invalid parameters:
        try {
            transactionService.transactionExists(null, fakeTransactionTypeId, fakeEffectiveDate, fakeEffectiveDate);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(fakeAccount, null, fakeEffectiveDate, fakeEffectiveDate);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, fakeEffectiveDate, fakeEffectiveDate);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, fakeEffectiveDate, null);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, null, fakeEffectiveDate);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, (Date) null, null);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }
    }

    @Test
    public void testTransactionExistsByTransactionTypeAmountAndEffectiveDate() throws Exception {

        // Create a new Transaction:
        String transactionTypeId = "1020";
        String accountId = "admin";
        BigDecimal amount = new BigDecimal(10e3);
        Date effectiveDate = new Date();

        transactionService.createTransaction(transactionTypeId, accountId, effectiveDate, amount);

        // Prepare input Date parameters:
        Calendar newDateFrom = Calendar.getInstance();
        int newDateFromYear = newDateFrom.get(Calendar.YEAR) - 1;
        int newDateFromMonth = Calendar.JULY;
        int newDateFromDay = 18;
        Calendar newDateTo = Calendar.getInstance();
        int newDateToYear = newDateTo.get(Calendar.YEAR) + 1;
        int newDateToMonth = Calendar.JANUARY;
        int newDateToDay = 25;

        newDateFrom.set(Calendar.YEAR, newDateFromYear);
        newDateFrom.set(Calendar.MONTH, newDateFromMonth);
        newDateFrom.set(Calendar.DAY_OF_MONTH, newDateFromDay);

        newDateTo.set(Calendar.YEAR, newDateToYear);
        newDateTo.set(Calendar.MONTH, newDateToMonth);
        newDateTo.set(Calendar.DAY_OF_MONTH, newDateToDay);

        // Call the service:
        BigDecimal amountFrom = new BigDecimal(10e2);
        BigDecimal amountTo = new BigDecimal(10e4);
        boolean exists = transactionService.transactionExists(accountId, transactionTypeId, amountFrom, amountTo, newDateFrom.getTime(), newDateTo.getTime());

        isTrue(exists);

        // Try to find a Transaction by a fake Account:
        String fakeAccount = "fake";

        exists = transactionService.transactionExists(fakeAccount, transactionTypeId, amountFrom, amountTo, newDateFrom.getTime(), newDateTo.getTime());
        isTrue(!exists);

        // Try to find a Transaction by a fake Transaction ID:
        String fakeTransactionTypeId = "somethingelse";

        exists = transactionService.transactionExists(accountId, fakeTransactionTypeId, amountFrom, amountTo, newDateFrom.getTime(), newDateTo.getTime());
        isTrue(!exists);

        // Try to find a Transaction by a fake amount:
        BigDecimal fakeAmount = new BigDecimal(1.0);

        exists = transactionService.transactionExists(accountId, transactionTypeId, fakeAmount, fakeAmount, newDateFrom.getTime(), newDateTo.getTime());
        isTrue(!exists);

        // Try to find a Transaction by a fake Effective Date:
        Date fakeEffectiveDate = new Date(0);

        exists = transactionService.transactionExists(accountId, transactionTypeId, amountFrom, amountTo, fakeEffectiveDate, fakeEffectiveDate);
        isTrue(!exists);

        // Try to find a Transaction by all fake parameters:
        exists = transactionService.transactionExists(fakeAccount, fakeTransactionTypeId, fakeAmount, fakeAmount, fakeEffectiveDate, fakeEffectiveDate);
        isTrue(!exists);

        // Pass invalid parameters:
        try {
            transactionService.transactionExists(null, fakeTransactionTypeId, fakeAmount, fakeAmount, fakeEffectiveDate, fakeEffectiveDate);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(fakeAccount, null, fakeAmount, fakeAmount, fakeEffectiveDate, fakeEffectiveDate);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, fakeAmount, fakeAmount, fakeEffectiveDate, fakeEffectiveDate);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, null, null, fakeEffectiveDate, fakeEffectiveDate);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, null, null, fakeEffectiveDate, null);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, null, null, null, fakeEffectiveDate);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }

        try {
            transactionService.transactionExists(null, null, null, null, null, null);
            isTrue(false); // should not even get here
        } catch (Exception e) {
        }
    }

    @Test
    public void getCreditPermissions() throws Exception {

        Transaction transaction = transactionService.createTransaction("chip", "admin", new Date(), new BigDecimal(100.55));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getTransactionType().getId());

        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());
        notNull(transaction.getAmount());

        // Getting credit permissions
        List<CreditPermission> creditPermissions =
                transactionService.getCreditPermissions(transaction.getTransactionType().getId());

        notNull(creditPermissions);
        notEmpty(creditPermissions);

        for (CreditPermission creditPermission : creditPermissions) {
            logger.info("Credit Permission = " + creditPermission);
            notNull(creditPermission);
            notNull(creditPermission.getId());
            notNull(creditPermission.getAllowableDebitType());
            notNull(creditPermission.getCreditType());
        }

        creditPermissions =
                transactionService.getCreditPermissions(transaction.getTransactionType().getId(), 0, Integer.MAX_VALUE);

        notNull(creditPermissions);
        notEmpty(creditPermissions);

        for (CreditPermission creditPermission : creditPermissions) {
            logger.info("Credit Permission = " + creditPermission);
            notNull(creditPermission);
            notNull(creditPermission.getId());
            notNull(creditPermission.getAllowableDebitType());
            notNull(creditPermission.getCreditType());
        }

    }

    @Test
    public void isCancellationRuleValid() throws Exception {

        // Valid cases

        String rule = "DAYS(10)PERCENTAGE(50);DAYS(20)PERCENTAGE(20);DAYS(30)PERCENTAGE(1)";

        isTrue(transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(10)AMOUNT(500.99)";

        isTrue(transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/31/2001)PERCENTAGE(100)";

        isTrue(transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(0)PERCENTAGE(99.99);DAYS(365)AMOUNT(1160000.456)";

        isTrue(transactionService.isCancellationRuleValid(rule));

        rule = "DATE(10/11/1789)PERCENTAGE(50);DATE(12/12/2012)AMOUNT(4333.45);DATE(12/13/2012)PERCENTAGE(2.9)";

        isTrue(transactionService.isCancellationRuleValid(rule));

        rule = "DATE(10/10/2001)PERCENTAGE(50);DATE(01/01/2007)PERCENTAGE(20);DATE(11/23/2009)AMOUNT(1800.99)";

        isTrue(transactionService.isCancellationRuleValid(rule));

        rule = "  DAYS(0)PERCENTAGE(99.99);DAYS(365)AMOUNT(1160000.456)     ";

        isTrue(transactionService.isCancellationRuleValid(rule));


        // Invalid cases

        rule = null;

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "        ";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "BLAH;BLAH;?{}!@~*&^%$";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(10)PERCENTAGE(50);DAYS(20)PERCENTAGE(20);DAYS(30)PERCENTAGE(10";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "!!!DAYS(10)PERCENTAGE(50);DAYS(20)PERCENTAGE(20);DAYS(30)PERCENTAGE(1.1)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(10)PERCENTAGE(50);DAYS(20)PERCENTAGE(20);DAYS(30)PERCENTAGE(1.9);";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(10)PERCENTAGE(50);DAYS(20)PERCENTAGE(20);DAYS(30)PERCENTAGE(0);";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(10)PERCENTAGE(500);DAYS(20)PERCENTAGE(20);DAYS(30)PERCENTAGE(13)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(10)PERCENTAGE(50);DAYS(20)PERCENTAGE(20);DAYS(30)PERCENTAGE(-30)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(10)PERCENTAGE(50);DATE(12/10/1999)PERCENTAGE(20);DAYS(30)PERCENTAGE(80)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(10)PERCENTAGE(50);DAYS(5)PERCENTAGE(20);DAYS(30)PERCENTAGE(80)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DAYS(10)PERCENTAGE(50);DAYS(25)PERCENTAGE(20);DAYS(30)PERSENTAGE(80)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(10/10/2001)PERCENTAGE(50);DATE(01/01/2007)PERCENTAGE(20);DATE(30)AMOUNT(800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(10/10/2001)PERCENTAGE(50);DATE(01/01/2007)PERCENTAGE(20);DATE(11/23/2003)AMOUNT(800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(10/10/2001)PERCENTAGE(50);DATE(01/01/2007)PERCENTAGE(20);DATE(11/23/2009)AMOUNT(-1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(10/10/2001)PERCENTAGE(50);DATE(01/01/2007)PERCENTAGE(20);DATE(11/23/2009))AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(10/10/2001)PERCENTAGE(50);DATE(01/01/2007)PERCENTAGE(20);DATE(11/23/2009);AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(13/10/2001)PERCENTAGE(50);DATE(01/01/2007)PERCENTAGE(20);DATE(11/23/2009)AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001)PERCENTAGE(50);DATE(01-01-2007)PERCENTAGE(20);DATE(11/23/2009)AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001PERCENTAGE(50);DATE(01/01/2007)PERCENTAGE(20);DATE(11/23/2009)AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001)PERCENTAGE(50)PERCENTAGE(20);DATE(11/23/2009)AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001)PERCENTAGE(50);;DATE(11/23/2009)AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = ";DATE(12/10/2001)PERCENTAGE(50);DATE(11/23/2009)AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001);PERCENTAGE(50);DATE(11/23/2009)AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001)PERCENTAGE(50)DATE(11/23/2009)AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001)PERCENTAGE(50);DATE(11/23/2009);AMOUNT(1800.99)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001)PERCENTAGE(50);DATE(11/23/2009)AMOUNT(1800.99);DATE";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001)PERCENTAGE(50);DATE(11/23/2009)AMOUNT(1800.99);DATE(02/12/2012)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

        rule = "DATE(12/10/2001)PERCENTAGE(50);DATE(11/23/2009)AMOUNT(1800.99);DATE(null)AMOUNT(30)";

        isTrue(!transactionService.isCancellationRuleValid(rule));

    }

    @Test
    public void calculateCancellationRule1() throws Exception {

        String rule = "DAYS(10)PERCENTAGE(50);DAYS(20)PERCENTAGE(20);DAYS(30)PERCENTAGE(5.8)";

        rule = transactionService.calculateCancellationRule(rule, new Date());

        notNull(rule);
        isTrue(!rule.isEmpty());
        isTrue(rule.split(";").length == 3);
        isTrue(!rule.contains("DAYS"));
        isTrue(rule.contains("DATE"));

        logger.info("Modified cancellation rule = " + rule);
    }

    @Test
    public void calculateCancellationRule2() throws Exception {

        String rule = "DATE(10/11/1789)PERCENTAGE(50);DATE(12/12/2012)AMOUNT(4333.45);DATE(12/12/2013)PERCENTAGE(10)";

        rule = transactionService.calculateCancellationRule(rule, new Date());

        notNull(rule);
        isTrue(!rule.isEmpty());
        isTrue(rule.split(";").length == 3);
        isTrue(!rule.contains("DAYS"));
        isTrue(rule.contains("DATE"));

        logger.info("Modified cancellation rule = " + rule);
    }

    @Test
    public void getCancellationAmount() throws Exception {

        String userId = "admin";

        String rule = "DATE(10/11/1789)PERCENTAGE(50);DATE(12/12/2012)AMOUNT(4333.45);DATE(12/12/2013)PERCENTAGE(10)";

        TransactionType chargeType = transactionService.getTransactionType("1299", new Date());

        notNull(chargeType);
        notNull(chargeType.getId());
        notNull(chargeType.getId().getId());
        isTrue(chargeType instanceof DebitType);

        ((DebitType) chargeType).setCancellationRule(rule);

        transactionService.persistTransactionType(chargeType);

        String typeId = chargeType.getId().getId();

        Transaction charge = transactionService.createTransaction(typeId, userId, new Date(), new BigDecimal(1500.99));

        notNull(charge);
        notNull(charge.getId());

        BigDecimal cancellationAmount = transactionService.getCancellationAmount(charge.getId());

        notNull(cancellationAmount);
        isTrue(cancellationAmount.compareTo(BigDecimal.ZERO) > 0);

    }

    @Test
    public void setGeneralLedgerType() throws Exception {

        GeneralLedgerType glType = glService.createGeneralLedgerType("GL_TYPE10000000", "Test GL type2",
                "Test GL Description 2", GL_ACCOUNT_ID, GlOperationType.DEBIT);

        notNull(glType);
        notNull(glType.getId());
        notNull(glType.getCode());
        notNull(glType.getName());
        notNull(glType.getDescription());
        notNull(glType.getGlAccountId());
        notNull(glType.getGlOperationOnCharge());
        notNull(glType.getCreationDate());
        notNull(glType.getCreatorId());

        isTrue(GlOperationType.DEBIT == glType.getGlOperationOnCharge());

        Transaction transaction = transactionService.createTransaction("chip", "admin", new Date(), new BigDecimal(1.1));

        notNull(transaction);
        notNull(transaction.getId());

        transactionService.setGeneralLedgerType(transaction.getId(), glType.getId());

        transaction = transactionService.getTransaction(transaction.getId());

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getGeneralLedgerType());
        notNull(transaction.getGeneralLedgerType().getId());
        isTrue(glType.getId().equals(transaction.getGeneralLedgerType().getId()));

    }

    @Test
    public void addTagsToTransaction() throws Exception {

        int numberOfTags = 100;

        Set<Tag> tags = new HashSet<Tag>(numberOfTags);

        for (int i = 0; i < numberOfTags; i++) {
            int j = i + 1;
            Tag tag = auditableEntityService.createAuditableEntity("tag_" + j, "Tag name" + j, "Tag desc " + j, Tag.class);
            tags.add(tag);
        }

        Transaction transaction = transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(10e3));

        Assert.notNull(transaction);
        Assert.notNull(transaction.getId());

        transaction = transactionService.addTagsToTransaction(transaction.getId(), new ArrayList<Tag>(tags));

        tags = transaction.getTags();

        Assert.notNull(tags);
        Assert.notEmpty(tags);
        Assert.isTrue(tags.size() >= numberOfTags);

        for (Tag tag : tags) {
            Assert.notNull(tag);
            Assert.notNull(tag.getId());
            Assert.notNull(tag.getCode());
            Assert.notNull(tag.getCreatorId());
            Assert.notNull(tag.getCreationDate());
            Assert.isTrue(tag.getCreationDate().before(new Date()));
        }

        Tag tag = auditableEntityService.createAuditableEntity("t##", "t##", "Tag desc ##", Tag.class);
        tags.add(tag);

        transaction = transactionService.addTagsToTransaction(transaction.getId(), new ArrayList<Tag>(tags));

        tags = transaction.getTags();

        Assert.notNull(tags);
        Assert.notEmpty(tags);
        Assert.isTrue(tags.size() >= numberOfTags + 1);

    }

    @Test
    public void addTagsToTransactionType() throws Exception {

        int numberOfTags = 66;

        Set<Tag> tags = new HashSet<Tag>(numberOfTags);

        for (int i = 0; i < numberOfTags; i++) {
            int j = i + 1;
            Tag tag = auditableEntityService.createAuditableEntity("tag_" + j, "Tag name" + j, "Tag desc " + j, Tag.class);
            tags.add(tag);
        }

        TransactionType transactionType = transactionService.getTransactionType("cash", new Date());

        Assert.notNull(transactionType);
        Assert.notNull(transactionType.getId());

        transactionType = transactionService.addTagsToTransactionType(transactionType.getId(), new ArrayList<Tag>(tags));

        tags = transactionType.getTags();

        Assert.notNull(tags);
        Assert.notEmpty(tags);
        Assert.isTrue(tags.size() >= numberOfTags);

        for (Tag tag : tags) {
            Assert.notNull(tag);
            Assert.notNull(tag.getId());
            Assert.notNull(tag.getCode());
            Assert.notNull(tag.getCreatorId());
            Assert.notNull(tag.getCreationDate());
            Assert.isTrue(tag.getCreationDate().before(new Date()));
        }

        Tag tag = auditableEntityService.createAuditableEntity("t##", "t##", "Tag desc ##", Tag.class);
        tags.add(tag);

        transactionType = transactionService.addTagsToTransactionType(transactionType.getId(), new ArrayList<Tag>(tags));

        tags = transactionType.getTags();

        Assert.notNull(tags);
        Assert.notEmpty(tags);
        Assert.isTrue(tags.size() >= numberOfTags + 1);

    }

    @Test
    public void getTransactionTypesByNamePattern1() throws Exception {

        // Search on the id
        String search = "cc";
        Class type = CreditType.class;

        List<CreditType> cTypes = transactionService.getTransactionTypesByNamePattern(search, type);

        Assert.notNull(cTypes);
        Assert.notEmpty(cTypes);

        // Search on the description
        search = "Credit Card payment";
        cTypes = transactionService.getTransactionTypesByNamePattern(search, type);

        Assert.notNull(cTypes);
        Assert.notEmpty(cTypes);

        // Search on the same string but as Debit Type should give 0 results
        type = DebitType.class;
        List<DebitType> dTypes = transactionService.getTransactionTypesByNamePattern(search, type);

        Assert.notNull(dTypes);
        Assert.isTrue(dTypes.isEmpty());

        search = "LAB FEE";
        dTypes = transactionService.getTransactionTypesByNamePattern(search, type);

        Assert.notNull(dTypes);
        Assert.notEmpty(cTypes);
    }

    @Test
    public void getTransactionTypesByNamePattern2() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        final Date effectiveDate = dateFormat.parse("05/01/2012");

        // Search on the id
        String search = "cc";
        Class type = CreditType.class;

        List<CreditType> cTypes = transactionService.getTransactionTypesByNamePattern(search, type, effectiveDate);

        Assert.notNull(cTypes);
        Assert.notEmpty(cTypes);

        // Search on the description
        search = "Credit Card payment";
        cTypes = transactionService.getTransactionTypesByNamePattern(search, type, effectiveDate);

        Assert.notNull(cTypes);
        Assert.notEmpty(cTypes);

        // Search on the same string but as Debit Type should give 0 results
        type = DebitType.class;
        List<DebitType> dTypes = transactionService.getTransactionTypesByNamePattern(search, type, effectiveDate);

        Assert.notNull(dTypes);
        Assert.isTrue(dTypes.isEmpty());

        search = "LAB FEE";
        dTypes = transactionService.getTransactionTypesByNamePattern(search, type, effectiveDate);

        Assert.notNull(dTypes);
        Assert.notEmpty(cTypes);
    }

    @Test
    public void createDebitSubType() throws Exception {

        String transactionTypeCode = "1020";

        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        TransactionType transactionType1 =
                transactionService.createDebitSubType(transactionTypeCode, dateFormat.parse("03/27/2015"));

        Assert.notNull(transactionType1);
        Assert.notNull(transactionType1.getCode());
        Assert.notNull(transactionType1.getId());
        Assert.notNull(transactionType1.getId().getSubCode());
        Assert.isTrue(transactionType1.getId().getSubCode() > 0);
        Assert.notNull(transactionType1.getStartDate());
        Assert.isNull(transactionType1.getEndDate());

        TransactionType transactionType2 =
                transactionService.createDebitSubType(transactionTypeCode, dateFormat.parse("07/02/2015"));

        transactionType1 = transactionService.getTransactionType(transactionType1.getId());

        Assert.notNull(transactionType1);
        Assert.notNull(transactionType1.getCode());
        Assert.notNull(transactionType1.getEndDate());


        Assert.notNull(transactionType2);
        Assert.notNull(transactionType2.getCode());
        Assert.notNull(transactionType2.getId());
        Assert.notNull(transactionType2.getId().getSubCode());
        Assert.isTrue(transactionType2.getId().getSubCode() > 0);
        Assert.isTrue(transactionType2.getId().getSubCode() > transactionType1.getId().getSubCode());
        Assert.isNull(transactionType2.getEndDate());
        Assert.notNull(transactionType2.getStartDate());

        Date startDate = transactionType2.getStartDate();
        Date endDate = transactionType1.getEndDate();

        Assert.isTrue(endDate.equals(CalendarUtils.addCalendarDays(startDate, -1)));
    }

    @Test
    public void expireDeferments() throws Exception {

        Date effectiveDate = new SimpleDateFormat(Constants.DATE_FORMAT_US).parse("01/27/2011");

        Date expirationDate = new SimpleDateFormat(Constants.DATE_FORMAT_US).parse("01/27/2012");

        String externalId = null;

        String userId = "admin";

        String type = "chip";

        Transaction transaction = transactionService.createTransaction(type, externalId, userId, effectiveDate,
                expirationDate, new BigDecimal(100.11111));

        notNull(transaction);
        notNull(transaction.getId());

        isTrue(transaction instanceof Deferment);

        isTrue(!TransactionStatus.EXPIRED.equals(transaction.getStatus()));

        transactionService.expireDeferments(userId);

        Deferment deferment = transactionService.getDeferment(transaction.getId());

        notNull(deferment);
        notNull(deferment.getId());

        isTrue(TransactionStatus.EXPIRED.equals(transaction.getStatus()));

    }

    @Test
    public void getNumberOfTransactions() throws Exception {

        TransactionTypeId transactionTypeId = new TransactionTypeId("1001", 1);

        Date effectiveDate = new SimpleDateFormat(Constants.DATE_FORMAT_US).parse("01/27/2012");

        Transaction transaction = transactionService.createTransaction("1001", TEST_USER_ID, effectiveDate, new BigDecimal(99));

        notNull(transaction);
        notNull(transaction.getId());

        long numberOfTransactions = transactionService.getNumberOfTransactions(transactionTypeId);

        logger.info("Number of transactions = " + numberOfTransactions);

        isTrue(numberOfTransactions > 0);
    }


    @Test
    public void getTransactionsByGlTransactionId() throws Exception {

        String typeId = "cash";

        Transaction transaction1 = transactionService.createTransaction(typeId, TEST_USER_ID, new Date(), new BigDecimal(10e5));

        notNull(transaction1);
        notNull(transaction1.getId());

        GlTransaction glTransaction = glService.createGlTransaction(transaction1.getId(), GL_ACCOUNT_ID, new BigDecimal(10e4),
                GlOperationType.DEBIT, "GL transaction statement");

        notNull(glTransaction);
        notNull(glTransaction.getId());
        notNull(glTransaction.getTransactions());
        notEmpty(glTransaction.getTransactions());

        Transaction transaction2 = transactionService.createTransaction(typeId, TEST_USER_ID, new Date(), new BigDecimal(-90));

        notNull(transaction2);
        notNull(transaction2.getId());

        glTransaction.getTransactions().add(transaction2);

        Long glTransactionId = glService.persistGlTransaction(glTransaction);

        notNull(glTransactionId);

        List<Transaction> transactions = transactionService.getTransactionsByGlTransactionId(glTransaction.getId());

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() >= 2);

    }

    @Test
    public void getPotentialRefunds1() {

        String typeId = "cash";

        Transaction transaction1 = transactionService.createTransaction(typeId, TEST_USER_ID, new Date(), new BigDecimal(10e5));

        notNull(transaction1);
        notNull(transaction1.getId());

        isTrue(transaction1 instanceof Payment);

        if (transaction1 instanceof Payment) {
            ((Payment) transaction1).setRefundable(true);
            ((Payment) transaction1).setClearDate(CalendarUtils.addCalendarDays(new Date(), -1));
        }

        transactionService.persistTransaction(transaction1);

        Transaction transaction2 = transactionService.createTransaction(typeId, TEST_USER_ID, new Date(), new BigDecimal(-100.3));

        notNull(transaction2);
        notNull(transaction2.getId());

        isTrue(transaction2 instanceof Payment);

        if (transaction2 instanceof Payment) {
            ((Payment) transaction2).setRefundable(true);
            ((Payment) transaction2).setClearDate(CalendarUtils.addCalendarDays(new Date(), -2));
        }

        transactionService.persistTransaction(transaction2);

        Set<String> userIds = new HashSet<String>(1);
        userIds.add(TEST_USER_ID);

        List<Payment> payments = transactionService.getPotentialRefunds(userIds);

        notNull(payments);
        notEmpty(payments);

        isTrue(payments.size() >= 2);

        boolean payment1Exists = false;
        boolean payment2Exists = false;

        for (Payment payment : payments) {

            notNull(payment);
            notNull(payment.getId());

            isTrue(payment.isRefundable());

            isTrue(payment.getUnallocatedAmount().compareTo(BigDecimal.ZERO) > 0);

            isTrue(payment.getClearDate().before(new Date()));

            if (payment.getId().equals(transaction1.getId())) {
                payment1Exists = true;
            } else if (payment.getId().equals(transaction2.getId())) {
                payment2Exists = true;
            }
        }

        isTrue(payment1Exists);
        isTrue(payment2Exists);

    }

    @Test
    public void getPotentialRefunds2() {

        String typeId = "finaid";

        Transaction transaction1 = transactionService.createTransaction(typeId, TEST_USER_ID, new Date(), new BigDecimal(10e5));

        notNull(transaction1);
        notNull(transaction1.getId());

        isTrue(transaction1 instanceof Payment);

        if (transaction1 instanceof Payment) {
            ((Payment) transaction1).setRefundable(true);
            ((Payment) transaction1).setClearDate(CalendarUtils.addCalendarDays(new Date(), -1));
        }

        transactionService.persistTransaction(transaction1);

        Transaction transaction2 = transactionService.createTransaction(typeId, TEST_USER_ID, new Date(), new BigDecimal(33));

        notNull(transaction2);
        notNull(transaction2.getId());

        isTrue(transaction2 instanceof Payment);

        if (transaction2 instanceof Payment) {
            ((Payment) transaction2).setRefundable(true);
            ((Payment) transaction2).setClearDate(CalendarUtils.addCalendarDays(new Date(), -2));
        }

        transactionService.persistTransaction(transaction2);

        Set<String> userIds = new HashSet<String>(1);
        userIds.add(TEST_USER_ID);

        List<Payment> payments = transactionService.getPotentialRefunds(userIds, new Date(), new Date());

        notNull(payments);
        notEmpty(payments);

        isTrue(payments.size() >= 2);

        boolean payment1Exists = false;
        boolean payment2Exists = false;

        for (Payment payment : payments) {

            notNull(payment);
            notNull(payment.getId());

            isTrue(payment.isRefundable());

            isTrue(payment.getUnallocatedAmount().compareTo(BigDecimal.ZERO) > 0);

            isTrue(payment.getClearDate().before(new Date()));

            if (payment.getId().equals(transaction1.getId())) {
                payment1Exists = true;
            } else if (payment.getId().equals(transaction2.getId())) {
                payment2Exists = true;
            }
        }

        isTrue(payment1Exists);
        isTrue(payment2Exists);

    }

    @Test
    public void getPotentialRefunds3() {

        String typeId = "chip";

        Tag tag1 = auditableEntityService.createAuditableEntity("tag_1", "Tag 1", "Tag 1 Desc", Tag.class);
        Tag tag2 = auditableEntityService.createAuditableEntity("tag_2", "Tag 2", "Tag 2 Desc", Tag.class);

        notNull(tag1);
        notNull(tag1.getId());

        notNull(tag2);
        notNull(tag2.getId());

        Transaction transaction1 = transactionService.createTransaction(typeId, TEST_USER_ID, new Date(), new BigDecimal(10e7));

        notNull(transaction1);
        notNull(transaction1.getId());

        isTrue(transaction1 instanceof Payment);

        if (transaction1 instanceof Payment) {
            ((Payment) transaction1).setRefundable(true);
            ((Payment) transaction1).setClearDate(CalendarUtils.addCalendarDays(new Date(), -1));
        }

        transactionService.persistTransaction(transaction1);

        transaction1 = transactionService.addTagsToTransaction(transaction1.getId(), Arrays.asList(tag1));

        notNull(transaction1);

        Transaction transaction2 = transactionService.createTransaction(typeId, TEST_USER_ID, new Date(), new BigDecimal(2000.45));

        notNull(transaction2);
        notNull(transaction2.getId());

        isTrue(transaction2 instanceof Payment);

        if (transaction2 instanceof Payment) {
            ((Payment) transaction2).setRefundable(true);
            ((Payment) transaction2).setClearDate(CalendarUtils.addCalendarDays(new Date(), -2));
        }

        transactionService.persistTransaction(transaction2);

        transaction2 = transactionService.addTagsToTransaction(transaction2.getId(), Arrays.asList(tag2));

        notNull(transaction2);

        Set<String> userIds = new HashSet<String>(1);
        userIds.add(TEST_USER_ID);
        userIds.add("user1");

        Set<Long> tagIds = new HashSet<Long>(2);
        tagIds.add(tag1.getId());
        tagIds.add(tag2.getId());

        List<Payment> payments = transactionService.getPotentialRefunds(userIds, new Date(0), new Date(), tagIds);

        notNull(payments);
        notEmpty(payments);

        logger.debug("Number of payments = " + payments.size());

        isTrue(payments.size() >= 2);

        boolean payment1Exists = false;
        boolean payment2Exists = false;

        Set<Long> paymentIds = new HashSet<Long>();

        for (Payment payment : payments) {

            notNull(payment);
            notNull(payment.getId());

            Assert.isTrue(!paymentIds.contains(payment.getId()));

            paymentIds.add(payment.getId());

            isTrue(payment.isRefundable());

            isTrue(payment.getUnallocatedAmount().compareTo(BigDecimal.ZERO) > 0);

            isTrue(payment.getClearDate().before(new Date()));

            if (payment.getId().equals(transaction1.getId())) {
                payment1Exists = true;
            } else if (payment.getId().equals(transaction2.getId())) {
                payment2Exists = true;
            }
        }

        isTrue(payment1Exists);
        isTrue(payment2Exists);

    }

    @Test
    public void createCharge() throws Exception {

        String typeId = "1020";

        Charge charge1 = transactionService.createCharge(typeId, TEST_USER_ID, new Date(), new BigDecimal(999.879));

        notNull(charge1);
        notNull(charge1.getId());
        notNull(charge1.getAmount());
        notNull(charge1.getEffectiveDate());
        notNull(charge1.getAccountId());

        isTrue(charge1.getAccountId().equals(TEST_USER_ID));
        isTrue(charge1.getAmount().compareTo(new BigDecimal(999.879)) == 0);
        isTrue(charge1.getAmount().compareTo(charge1.getUnallocatedAmount()) == 0);
        isTrue(!new Date().before(charge1.getEffectiveDate()));

        Charge charge2 = transactionService.getCharge(charge1.getId());

        notNull(charge2);
        notNull(charge2.getId());

        isTrue(charge1.getId().equals(charge2.getId()));

    }

    @Test
    public void createPayment() throws Exception {

        String typeId = "cash";

        Payment payment1 = transactionService.createPayment(typeId, TEST_USER_ID, new Date(), new BigDecimal(200.11));

        notNull(payment1);
        notNull(payment1.getId());
        notNull(payment1.getAmount());
        notNull(payment1.getEffectiveDate());
        notNull(payment1.getAccountId());

        isTrue(payment1.getAccountId().equals(TEST_USER_ID));
        isTrue(payment1.getAmount().compareTo(new BigDecimal(200.11)) == 0);
        isTrue(payment1.getAmount().compareTo(payment1.getUnallocatedAmount()) == 0);
        isTrue(!new Date().before(payment1.getEffectiveDate()));

        Payment payment2 = transactionService.getPayment(payment1.getId());

        notNull(payment2);
        notNull(payment2.getId());

        isTrue(payment1.getId().equals(payment2.getId()));

    }

    @Test
    public void createDeferment1() throws Exception {

        String typeId = "finaid";

        String expDateValue = "01/01/2015";

        Date expirationDate = new SimpleDateFormat(Constants.DATE_FORMAT_US).parse(expDateValue);

        Deferment deferment1 = transactionService.createDeferment(typeId, TEST_USER_ID, new Date(), expirationDate, new BigDecimal(200.11));

        notNull(deferment1);
        notNull(deferment1.getId());
        notNull(deferment1.getAmount());
        notNull(deferment1.getEffectiveDate());
        notNull(deferment1.getExpirationDate());
        notNull(deferment1.getAccountId());
        notNull(deferment1.getTransactionType());

        isTrue(deferment1.getTransactionType().getTypeValue().equals(TransactionType.CREDIT_TYPE));
        isTrue(deferment1.getAccountId().equals(TEST_USER_ID));
        isTrue(deferment1.getAmount().compareTo(new BigDecimal(200.11)) == 0);
        isTrue(deferment1.getAmount().compareTo(deferment1.getUnallocatedAmount()) == 0);
        isTrue(!new Date().before(deferment1.getEffectiveDate()));

        Deferment deferment2 = transactionService.getDeferment(deferment1.getId());

        notNull(deferment2);
        notNull(deferment2.getId());
        notNull(deferment2.getExpirationDate());

        isTrue(deferment1.getId().equals(deferment2.getId()));
        isTrue(deferment1.getExpirationDate().equals(deferment2.getExpirationDate()));

    }

}
