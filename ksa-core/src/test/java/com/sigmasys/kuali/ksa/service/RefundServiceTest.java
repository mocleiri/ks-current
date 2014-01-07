package com.sigmasys.kuali.ksa.service;

import static junit.framework.Assert.assertTrue;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sigmasys.kuali.ksa.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class RefundServiceTest extends AbstractServiceTest {

    @PersistenceContext(unitName = Constants.KSA_PERSISTENCE_UNIT)
    protected EntityManager em;

    @Autowired
    private RefundService refundService;

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
    public void testIsRefundRuleValid() throws Exception {
        // Test null rule:
        String refundRule = null;
        boolean ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(ruleValid);

        // Test empty rule:
        refundRule = "";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(ruleValid);

        // Test blank rule:
        refundRule = "   ";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(ruleValid);

        // Test bad format rule:
        refundRule = "1234345---";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test bad format "A" rule:
        refundRule = "A(1234345---";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test bad format "S" rule:
        refundRule = "S123434)5---";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test "A" rule numeric parameter out of lower bound:
        refundRule = "A(-1)";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test "A" rule numeric parameter out of upper bound:
        refundRule = "A(65536)";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test "S" rule numeric parameter out of lower bound:
        refundRule = "S(-100)";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test "S" rule numeric parameter out of upper bound:
        refundRule = "A(65538)";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test "A" rule does not have an account:
        refundRule = "A(9)";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test "A" rule has an blank account:
        refundRule = "A(9)()";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test "A" rule has an non-existing account:
        refundRule = "A(9)(dum-de-dum)";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(!ruleValid);

        // Test valid "A" rule:
        refundRule = "A(9)(admin1)";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(ruleValid);

        // Test valid "S" rule:
        refundRule = "S(9)";
        ruleValid = refundService.isRefundRuleValid(refundRule);

        isTrue(ruleValid);
    }

    @Test
    public void testGetOrCreateRefundType() throws Exception {

        boolean exceptionCaught = false;

        // Test both parameters null:
        try {
            refundService.getRefundType(null);
            assertTrue(false); // we should not be here
        } catch (Exception e) {
            exceptionCaught = true;
        }

        isTrue(exceptionCaught);

        // Test one parameter null:
        exceptionCaught = false;

        isTrue(refundService.getRefundType("cash") != null);

        isTrue(exceptionCaught);

    }

    @Test
    public void createRefundForPayment() throws Exception {

        Transaction transaction = transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(10e5));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());

        isTrue(transaction instanceof Payment);

        Payment payment = (Payment) transaction;

        payment.setRefundable(true);
        payment.setRefundRule("A(9)(admin)");

        Refund refund = refundService.checkForRefund(payment.getId());

        notNull(refund);
        notNull(refund.getId());
        notNull(refund.getRequestedBy());
        notNull(refund.getRequestDate());
        notNull(refund.getAmount());

        notNull(refund.getTransaction());
        notNull(refund.getTransaction().getStatus());

        isTrue(payment.getStatus() == TransactionStatus.REFUND_REQUESTED);

    }

    @Test
    public void cancelRefund() throws Exception {

        Transaction transaction = transactionService.createTransaction("cash", "admin", new Date(), new BigDecimal(30.56));

        notNull(transaction);
        notNull(transaction.getId());
        notNull(transaction.getTransactionType());
        notNull(transaction.getAccount());
        notNull(transaction.getAccountId());
        notNull(transaction.getCurrency());

        isTrue(transaction instanceof Payment);

        Payment payment = (Payment) transaction;

        payment.setRefundable(true);
        payment.setRefundRule("A(9)(admin)");

        Refund refund = refundService.checkForRefund(payment.getId());

        notNull(refund);
        notNull(refund.getId());
        notNull(refund.getRequestedBy());
        notNull(refund.getRequestDate());
        notNull(refund.getAmount());
        notNull(refund.getTransaction());
        notNull(refund.getTransaction().getStatus());

        isTrue(payment.getStatus() == TransactionStatus.REFUND_REQUESTED);

        refund = refundService.validateRefund(refund.getId());

        notNull(refund);
        notNull(refund.getId());

        refund = refundService.performRefund(refund.getId());

        notNull(refund);
        notNull(refund.getId());
        notNull(refund.getTransaction());
        notNull(refund.getRefundTransaction());
        notNull(refund.getTransaction().getStatus());

        isTrue(refund.getTransaction().getStatus() == TransactionStatus.REFUNDING);

        refund = refundService.cancelRefund(refund.getId(), "Refund for payment 'cash' has been cancelled");

        notNull(refund);
        notNull(refund.getId());
        notNull(refund.getTransaction());
        notNull(refund.getTransaction().getStatus());

        isTrue(refund.getTransaction().getStatus() == TransactionStatus.ACTIVE);

    }


}
