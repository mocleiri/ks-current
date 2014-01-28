package com.sigmasys.kuali.ksa.service.jta;

import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * Custom version of JtaTransactionManager that overrides a few Spring methods.
 *
 * @author Michael Ivanov
 */
public class JtaTransactionManager extends org.springframework.transaction.jta.JtaTransactionManager {

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        TransactionManagerHelper.doCommit(status);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        TransactionManagerHelper.doRollback(status);
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) {
        super.doSetRollbackOnly(status);
        doRollback(status);
    }

    /**
     * Overrides the original method initTransactionSynchronizationRegistry() from Spring's JtaTransactionManager class.
     * There is no need to support TransactionSynchronizationRegistry from JTA 1.1 in this application,
     * therefore, this method is empty.
     */
    @Override
    protected void initTransactionSynchronizationRegistry() {
        // Do nothing
    }

}

