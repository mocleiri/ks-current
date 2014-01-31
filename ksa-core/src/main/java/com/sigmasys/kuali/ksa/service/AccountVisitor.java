package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.CollectionAgencyAccount;
import com.sigmasys.kuali.ksa.model.DelegateAccount;
import com.sigmasys.kuali.ksa.model.DirectChargeAccount;
import com.sigmasys.kuali.ksa.model.ThirdPartyAccount;

/**
 * Account visitor. Typically used to avoid class casting problems with Hibernate proxies.
 *
 * @author Michael Ivanov
 */
public interface AccountVisitor {

    void visit(DirectChargeAccount account);

    void visit(ThirdPartyAccount account);

    void visit(DelegateAccount account);

    void visit(CollectionAgencyAccount account);

}
