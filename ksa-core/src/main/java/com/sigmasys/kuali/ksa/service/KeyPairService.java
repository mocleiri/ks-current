package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.KeyPair;
import com.sigmasys.kuali.ksa.model.KeyPairAware;

import java.util.List;

/**
 * This interface defines basic operations on KeyPair objects.
 * KeyPairAware has to be persistent if there is a need to propagate changes to the persistent store.
 * <p/>
 *
 * @author Michael Ivanov
 */
public interface KeyPairService {

    /**
     * Adds new or merges existing key pairs of the given KeyPairAware instance.
     *
     * @param entity   KeyPairAware instance
     * @param keyPairs list of key pairs
     * @return KeyPairAware object
     */
    <T extends KeyPairAware> T addKeyPairs(T entity, List<KeyPair> keyPairs);

    /**
     * Adds new or merges existing key pairs of the given KeyPairAware instance.
     *
     * @param entity   KeyPairAware instance
     * @param keyPairs list of key pairs
     * @return KeyPairAware object
     */
    <T extends KeyPairAware> T addKeyPairs(T entity, KeyPair... keyPairs);

    /**
     * Removes key pairs specified by IDs from the given KeyPairAware instance.
     *
     * @param entity     KeyPairAware instance
     * @param keyPairIds list of key pair IDs
     * @return KeyPairAware object
     */
    <T extends KeyPairAware> T removeKeyPairs(T entity, List<Long> keyPairIds);

    /**
     * Removes key pairs specified by IDs from the given KeyPairAware instance.
     *
     * @param entity     KeyPairAware instance
     * @param keyPairIds list of key pair IDs
     * @return KeyPairAware object
     */
    <T extends KeyPairAware> T removeKeyPairs(T entity, Long... keyPairIds);

    /**
     * Removes key pairs specified by keys from the given KeyPairAware instance.
     *
     * @param entity KeyPairAware instance
     * @param keys   list of KeyPair keys
     * @return KeyPairAware object
     */
    <T extends KeyPairAware> T removeKeyPairsByKeys(T entity, List<String> keys);

    /**
     * Removes key pairs specified by keys from the given KeyPairAware instance.
     *
     * @param entity KeyPairAware instance
     * @param keys   list of KeyPair keys
     * @return KeyPairAware object
     */
    <T extends KeyPairAware> T removeKeyPairsByKeys(T entity, String... keys);

    /**
     * Updates a key pair specified by ID of the given KeyPairAware instance.
     * Throws IllegalArgumentException if no KeyPair is found.
     *
     * @param entity    KeyPairAware instance
     * @param keyPairId KeyPair ID
     * @param key       New KeyPair key
     * @param value     New KeyPair value
     * @return KeyPairAware object
     */
    <T extends KeyPairAware> T updateKeyPair(T entity, Long keyPairId, String key, String value);

    /**
     * Retrieves the list of key pairs by key.
     *
     * @param entity KeyPairAware instance
     * @param key    KeyPair key
     * @return list of KeyPairAware objects
     */
    <T extends KeyPairAware> List<KeyPair> getKeyPairsByKey(T entity, String key);

    /**
     * Returns true if the key pair exists and false if it does not.
     *
     * @param entity KeyPairAware instance
     * @param key    KeyPair key
     * @param value  KeyPair value
     * @return true if the kay pair exists, otherwise false.
     */
    <T extends KeyPairAware> boolean keyPairExists(T entity, String key, String value);


}
