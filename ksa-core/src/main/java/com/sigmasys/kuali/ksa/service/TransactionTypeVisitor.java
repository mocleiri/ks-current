package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.CreditType;
import com.sigmasys.kuali.ksa.model.DebitType;

/**
 * TransactionType visitor. Typically used to avoid class casting problems with Hibernate proxies.
 *
 * @author Michael Ivanov
 */
public interface TransactionTypeVisitor {

    void visit(CreditType creditType);

    void visit(DebitType debitType);

}
