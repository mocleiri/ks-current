package com.sigmasys.kuali.ksa.service.jta;


import org.hibernate.transaction.TransactionManagerLookup;

import javax.naming.InitialContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import java.util.Properties;


/**
 * AtomikosTransactionManagerLookup.
 *
 * @author Michael Ivanov
 */
public class AtomikosTransactionManagerLookup implements TransactionManagerLookup {

    private static final String USER_TRANSACTION_JNDI_NAME = "java:comp/UserTransaction";
    private static final String TRANSACTION_MANAGER_JNDI_NAME = "java:comp/env/TransactionManager";


    /**
     * @see org.hibernate.transaction.TransactionManagerLookup#getTransactionManager(Properties)
     */
    @Override
    public TransactionManager getTransactionManager(Properties props) {
        try {
            InitialContext initialContext = new InitialContext();
            return (TransactionManager) initialContext.lookup(TRANSACTION_MANAGER_JNDI_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Could not obtain Atomikos transaction manager instance", e);
        }
    }

    @Override
    public String getUserTransactionName() {
        return USER_TRANSACTION_JNDI_NAME;
    }

    /**
     * Determine an identifier for the given transaction appropriate for use in caching/lookup usages.
     * <p/>
     * Generally speaking the transaction itself will be returned here.  This method was added specifically
     * for use in WebSphere and other unfriendly JEE containers (although WebSphere is still the only known
     * such brain-dead, sales-driven impl).
     *
     * @param transaction The transaction to be identified.
     * @return An appropriate identifier
     */
    @Override
    public Object getTransactionIdentifier(Transaction transaction) {
        return transaction;
    }
}
