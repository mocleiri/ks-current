package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.AccountVisitor;

/**
 * AccountVisitor's default implementation that stores accounts in class member variables.
 *
 * @author Michael Ivanov
 */
public class SimpleAccountVisitor implements AccountVisitor {

    private DirectChargeAccount directChargeAccount;
    private ThirdPartyAccount thirdPartyAccount;
    private DelegateAccount delegateAccount;
    private CollectionAgencyAccount collectionAgencyAccount;


    private SimpleAccountVisitor() {
    }

    public static SimpleAccountVisitor getInstance() {
        return new SimpleAccountVisitor();
    }

    @Override
    public void visit(DirectChargeAccount account) {
        directChargeAccount = account;
    }

    @Override
    public void visit(ThirdPartyAccount account) {
        thirdPartyAccount = account;
    }

    @Override
    public void visit(DelegateAccount account) {
        delegateAccount = account;
    }

    @Override
    public void visit(CollectionAgencyAccount account) {
        collectionAgencyAccount = account;
    }

    public DirectChargeAccount getDirectChargeAccount() {
        return directChargeAccount;
    }

    public ThirdPartyAccount getThirdPartyAccount() {
        return thirdPartyAccount;
    }

    public DelegateAccount getDelegateAccount() {
        return delegateAccount;
    }

    public CollectionAgencyAccount getCollectionAgencyAccount() {
        return collectionAgencyAccount;
    }
}
