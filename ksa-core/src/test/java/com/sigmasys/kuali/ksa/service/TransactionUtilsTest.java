package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.util.TransactionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.util.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class TransactionUtilsTest extends AbstractServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;


    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);
    }

    @Test
    public void calculateMatrixScores() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(10e3));

        notNull(transaction1);
        notNull(transaction1.getId());

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        TransactionUtils.calculateMatrixScores(Arrays.asList(transaction1, transaction2));

        logger.info("Matrix score 1 = " + transaction1.getMatrixScore());
        logger.info("Matrix score 2 = " + transaction2.getMatrixScore());

        notNull(transaction1.getMatrixScore());
        notNull(transaction2.getMatrixScore());
        isTrue(transaction1.getMatrixScore() == 1);
        isTrue(transaction2.getMatrixScore() == 1);

    }

    @Test
    public void getUnallocatedAmount() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(200.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        transactionService.createAllocation(transaction1, transaction2, new BigDecimal(100.00), true, true, false);

        BigDecimal amount = TransactionUtils.getUnallocatedAmount(transaction1);

        notNull(amount);
        isTrue(amount.compareTo(transaction1.getAmount().subtract(transaction1.getLockedAllocatedAmount())) == 0);
        isTrue(amount.compareTo(new BigDecimal(100.00)) == 0);

        amount = TransactionUtils.getUnallocatedAmount(transaction2);

        notNull(amount);
        isTrue(amount.compareTo(transaction2.getAmount().subtract(transaction2.getLockedAllocatedAmount())) == 0);
        isTrue(amount.compareTo(new BigDecimal(401.999)) == 0);

    }

    @Test
    public void removeFullyAllocatedTransactions() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(200.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        transactionService.createAllocation(transaction1, transaction2, new BigDecimal(200.00), true, true, false);

        isTrue(TransactionUtils.getUnallocatedAmount(transaction1).compareTo(BigDecimal.ZERO) == 0);
        isTrue(TransactionUtils.getUnallocatedAmount(transaction2).compareTo(BigDecimal.ZERO) > 0);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        transactions = TransactionUtils.removeFullyAllocatedTransactions(transactions);

        logger.info("Transaction list = " + transactions);

        notNull(transactions);
        notEmpty(transactions);
        isTrue(transactions.size() == 1);

        isTrue(transaction2.equals(transactions.get(0)));
        isTrue(new BigDecimal(301.999).compareTo(TransactionUtils.getUnallocatedAmount(transactions.get(0))) == 0);
    }

    @Test
    public void filterByUnallocatedAmount() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(200.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        transactionService.createAllocation(transaction1, transaction2, new BigDecimal(100.00), true, true, false);

        isTrue(TransactionUtils.getUnallocatedAmount(transaction1).compareTo(BigDecimal.ZERO) > 0);
        isTrue(TransactionUtils.getUnallocatedAmount(transaction2).compareTo(BigDecimal.ZERO) > 0);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        transactions = TransactionUtils.filterByUnallocatedAmount(transactions, BigDecimal.ZERO, new BigDecimal(200));

        logger.info("Transaction list = " + transactions);

        notNull(transactions);
        notEmpty(transactions);
        isTrue(transactions.size() == 1);

        isTrue(transaction1.equals(transactions.get(0)));
        isTrue(new BigDecimal(100).compareTo(TransactionUtils.getUnallocatedAmount(transactions.get(0))) == 0);
    }

    @Test
    public void filterByAmount() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(200.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(201.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        transactions = TransactionUtils.filterByAmount(transactions, new BigDecimal(200.001), new BigDecimal(300));

        logger.info("Transaction list = " + transactions);

        notNull(transactions);
        notEmpty(transactions);
        isTrue(transactions.size() == 1);

        isTrue(transaction2.equals(transactions.get(0)));
        isTrue(new BigDecimal(201.999).compareTo(transactions.get(0).getAmount()) == 0);
    }

    @Test
    public void filterByPriority() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(200.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        transaction1.getTransactionType().setPriority(35);

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(201.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        transaction2.getTransactionType().setPriority(20);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        transactions = TransactionUtils.filterByPriority(transactions, 5, 20);

        logger.info("Transaction list = " + transactions);

        notNull(transactions);
        notEmpty(transactions);
        isTrue(transactions.size() == 1);

        isTrue(transaction2.equals(transactions.get(0)));
        notNull(transactions.get(0).getTransactionType());
        notNull(transactions.get(0).getTransactionType().getPriority());
        isTrue(20 == transactions.get(0).getTransactionType().getPriority());

    }

    @Test
    public void filterByMatrixScore() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(200.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        transaction1.setMatrixScore(567);

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(201.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        transaction2.setMatrixScore(6789);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        transactions = TransactionUtils.filterByMatrixScore(transactions, 0, 1000);

        logger.info("Transaction list = " + transactions);

        notNull(transactions);
        notEmpty(transactions);
        isTrue(transactions.size() == 1);

        isTrue(transaction1.equals(transactions.get(0)));
        notNull(transactions.get(0).getMatrixScore());
        isTrue(567 == transactions.get(0).getMatrixScore());

    }

    @Test
    public void orderByEffectiveDate() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(200.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        transactions = TransactionUtils.orderByEffectiveDate(transactions, true);

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() == 2);
        isTrue(transaction1.equals(transactions.get(0)));
        isTrue(transaction2.equals(transactions.get(1)));

        transactions = TransactionUtils.orderByEffectiveDate(transactions, false);

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() == 2);
        isTrue(transaction2.equals(transactions.get(0)));
        isTrue(transaction1.equals(transactions.get(1)));
    }

    @Test
    public void orderByAmount() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(2000.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        transactions = TransactionUtils.orderByAmount(transactions, true);

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() == 2);
        isTrue(transaction2.equals(transactions.get(0)));
        isTrue(transaction1.equals(transactions.get(1)));

        transactions = TransactionUtils.orderByAmount(transactions, false);

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() == 2);
        isTrue(transaction1.equals(transactions.get(0)));
        isTrue(transaction2.equals(transactions.get(1)));
    }

    @Test
    public void orderByUnallocatedAmount() throws Exception {

        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(2000.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        transactionService.createAllocation(transaction1, transaction2, new BigDecimal(300.00), true, true, false);

        isTrue(TransactionUtils.getUnallocatedAmount(transaction1).compareTo(BigDecimal.ZERO) > 0);
        isTrue(TransactionUtils.getUnallocatedAmount(transaction2).compareTo(BigDecimal.ZERO) > 0);

        transactions = TransactionUtils.orderByUnallocatedAmount(transactions, true);

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() == 2);
        isTrue(transaction2.equals(transactions.get(0)));
        isTrue(transaction1.equals(transactions.get(1)));

        transactions = TransactionUtils.orderByAmount(transactions, false);

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() == 2);
        isTrue(transaction1.equals(transactions.get(0)));
        isTrue(transaction2.equals(transactions.get(1)));
    }

    @Test
    public void removeCharges() throws Exception {

        // Payment
        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(2000.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        // Charge
        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        // Deferment
        Transaction transaction3 =
                transactionService.createTransaction("chip", null, "admin", new Date(), new Date(), new BigDecimal(300.00));

        notNull(transaction3);
        notNull(transaction3.getId());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2, transaction3);


        transactions = TransactionUtils.removeCharges(transactions);

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() == 2);
        isTrue(!(transactions.get(0) instanceof Charge));
        isTrue(!(transactions.get(1) instanceof Charge));
    }

    @Test
    public void removePayments() throws Exception {

        // Payment
        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(2000.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        // Charge
        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        // Deferment
        Transaction transaction3 =
                transactionService.createTransaction("chip", null, "admin", new Date(), new Date(), new BigDecimal(300.00));

        notNull(transaction3);
        notNull(transaction3.getId());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2, transaction3);


        transactions = TransactionUtils.removePayments(transactions);

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() == 2);
        isTrue(!(transactions.get(0) instanceof Payment));
        isTrue(!(transactions.get(1) instanceof Payment));

    }

    @Test
    public void removeDeferments() throws Exception {

        // Payment
        Transaction transaction1 =
                transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(2000.00));

        notNull(transaction1);
        notNull(transaction1.getId());

        // Charge
        Transaction transaction2 =
                transactionService.createTransaction("1020", "admin", new Date(), new BigDecimal(501.999));

        notNull(transaction2);
        notNull(transaction2.getId());

        // Deferment
        Transaction transaction3 =
                transactionService.createTransaction("chip", null, "admin", new Date(), new Date(), new BigDecimal(300.00));

        notNull(transaction3);
        notNull(transaction3.getId());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2, transaction3);


        transactions = TransactionUtils.removeDeferments(transactions);

        notNull(transactions);
        notEmpty(transactions);

        isTrue(transactions.size() == 2);
        isTrue(!(transactions.get(0) instanceof Deferment));
        isTrue(!(transactions.get(1) instanceof Deferment));

    }

}
