package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.service.AccountVisitor;

import javax.persistence.*;
import java.util.Set;

/**
 * Collection Agency Account model.
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(AccountTypeValue.COLLECTION_AGENCY_CODE)
public class CollectionAgencyAccount extends NonChargeableAccount {

    private Set<CollectionAccount> collectionAccounts;

    /**
     * Allows AccountVisitor to access this Account.
     *
     * @param visitor AccountVisitor instance
     */
    @Override
    public void accept(AccountVisitor visitor) {
        visitor.visit(this);
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "AGENCY_ACNT_ID_FK")
    public Set<CollectionAccount> getCollectionAccounts() {
        return collectionAccounts;
    }

    public void setCollectionAccounts(Set<CollectionAccount> collectionAccounts) {
        this.collectionAccounts = collectionAccounts;
    }

}

