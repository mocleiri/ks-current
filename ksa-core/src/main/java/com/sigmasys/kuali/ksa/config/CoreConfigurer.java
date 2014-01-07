package com.sigmasys.kuali.ksa.config;

import org.springframework.transaction.jta.JtaTransactionManager;

import javax.annotation.PostConstruct;

/**
 * An extended version of Rice's CoreConfigurer that uses UserTransaction and TransactionManager set by
 * Spring JTA transaction manager.
 *
 * @author Michael Ivanov
 */
public class CoreConfigurer extends org.kuali.rice.core.impl.config.module.CoreConfigurer {

    private JtaTransactionManager jtaTransactionManager;


    @PostConstruct
    public void postConstruct() {
        setTransactionManager(jtaTransactionManager.getTransactionManager());
        setUserTransaction(jtaTransactionManager.getUserTransaction());
    }

    public JtaTransactionManager getJtaTransactionManager() {
        return jtaTransactionManager;
    }

    public void setJtaTransactionManager(JtaTransactionManager jtaTransactionManager) {
        this.jtaTransactionManager = jtaTransactionManager;
    }
}
