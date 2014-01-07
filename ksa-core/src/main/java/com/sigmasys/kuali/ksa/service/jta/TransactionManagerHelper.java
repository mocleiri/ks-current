package com.sigmasys.kuali.ksa.service.jta;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.jta.JtaTransactionObject;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * TransactionManagerHelper.
 * Contains helper methods to manage JTA transactions.
 *
 * @author Michael Ivanov
 */
public class TransactionManagerHelper {

    private static final Log logger = LogFactory.getLog(TransactionManagerHelper.class);

    private TransactionManagerHelper() {
    }

    public static boolean isCommit(DefaultTransactionStatus status) {
        return status.hasTransaction() &&
                !status.isCompleted() &&
                !status.isRollbackOnly() &&
                !status.isLocalRollbackOnly() &&
                !status.isGlobalRollbackOnly();
    }

    public static boolean isRollback(DefaultTransactionStatus status) {
        return status.hasTransaction() && !status.isCompleted();
    }

    public static void doCommit(DefaultTransactionStatus status) {
        if (isCommit(status)) {
            try {
                JtaTransactionObject txObject = (JtaTransactionObject) status.getTransaction();
                UserTransaction userTransaction = txObject.getUserTransaction();
                if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                    userTransaction.commit();
                    logger.debug("JTA transaction [" + userTransaction + "] is committed");
                }
            } catch (Throwable t) {
                String errMsg = "Cannot commit JTA transaction: " + t.getMessage();
                logger.error(errMsg, t);
                throw new RuntimeException(errMsg, t);
            }
        }
    }

    public static void doRollback(DefaultTransactionStatus status) {
        if (isRollback(status)) {
            try {
                JtaTransactionObject txObject = (JtaTransactionObject) status.getTransaction();
                UserTransaction userTransaction = txObject.getUserTransaction();
                if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                    userTransaction.rollback();
                    logger.debug("JTA transaction [" + userTransaction + "] is rolled back");
                }
            } catch (Throwable t) {
                String errMsg = "Cannot rollback JTA transaction: " + t.getMessage();
                logger.error(errMsg, t);
                throw new RuntimeException(errMsg, t);
            }
        }
    }

}

