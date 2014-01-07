package com.sigmasys.kuali.ksa.model;

import java.util.Set;

/**
 * KeyPair-aware interface. Primarily used by entities that deal with key pairs in KSA and KeyPairService.
 *
 * @author Michael Ivanov
 */
public interface KeyPairAware {

    Set<KeyPair> getKeyPairs();

    void setKeyPairs(Set<KeyPair> keyPairs);

}
