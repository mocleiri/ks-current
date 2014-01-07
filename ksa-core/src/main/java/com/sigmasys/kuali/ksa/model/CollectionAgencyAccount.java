package com.sigmasys.kuali.ksa.model;

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


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "AGENCY_ACNT_ID_FK")
    public Set<CollectionAccount> getCollectionAccounts() {
        return collectionAccounts;
    }

    public void setCollectionAccounts(Set<CollectionAccount> collectionAccounts) {
        this.collectionAccounts = collectionAccounts;
    }

}

