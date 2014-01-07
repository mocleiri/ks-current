package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.model.KeyPair;
import com.sigmasys.kuali.ksa.model.KeyPairAware;
import com.sigmasys.kuali.ksa.service.KeyPairService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * KeyPairService implementation.
 *
 * @author Michael Ivanov
 */
@Service("keyPairService")
@Transactional
public class KeyPairServiceImpl extends GenericPersistenceService implements KeyPairService {

    private static final Log logger = LogFactory.getLog(KeyPairServiceImpl.class);


    private Set<KeyPair> mergeNewAndPersistentKeyPairs(List<KeyPair> keyPairsToAdd, Set<KeyPair> persistentKeyPairs) {

        // Persisting new or merging existing key pairs in the persistent store
        Set<Long> keyPairIds = new HashSet<Long>();
        for (KeyPair keyPair : keyPairsToAdd) {
            if (keyPair.getId() != null) {
                em.merge(keyPair);
                keyPairIds.add(keyPair.getId());
            } else {
                em.persist(keyPair);
            }
        }

        em.flush();

        if (CollectionUtils.isNotEmpty(persistentKeyPairs)) {
            for (KeyPair keyPair : new ArrayList<KeyPair>(persistentKeyPairs)) {
                if (keyPairIds.contains(keyPair.getId())) {
                    persistentKeyPairs.remove(keyPair);
                }
            }
        } else {
            persistentKeyPairs = new HashSet<KeyPair>(keyPairsToAdd.size());
        }

        persistentKeyPairs.addAll(keyPairsToAdd);

        return persistentKeyPairs;
    }

    private Set<KeyPair> removeKeyPairs(List<Long> keyPairIdsToRemove, Set<KeyPair> keyPairs) {

        Set<Long> keyPairIds = new HashSet<Long>(keyPairIdsToRemove);

        if (CollectionUtils.isNotEmpty(keyPairs)) {
            for (KeyPair keyPair : new ArrayList<KeyPair>(keyPairs)) {
                if (keyPairIds.contains(keyPair.getId())) {
                    keyPairs.remove(keyPair);
                    em.remove(keyPair);
                }
            }
            em.flush();
        }

        return keyPairs;
    }

    private Set<KeyPair> removeKeyPairsByKeys(List<String> keyPairKeysToRemove, Set<KeyPair> keyPairs) {

        Set<String> keyPairKeys = new HashSet<String>(keyPairKeysToRemove);

        if (CollectionUtils.isNotEmpty(keyPairs)) {
            for (KeyPair keyPair : new ArrayList<KeyPair>(keyPairs)) {
                if (keyPairKeys.contains(keyPair.getKey())) {
                    keyPairs.remove(keyPair);
                    em.remove(keyPair);
                }
            }
            em.flush();
        }

        return keyPairs;
    }


    /**
     * Adds new or merges existing key pairs of the given KeyPairAware instance.
     *
     * @param entity   KeyPairAware instance
     * @param keyPairs list of key pairs
     * @return KeyPairAware object
     */
    @Override
    public <T extends KeyPairAware> T addKeyPairs(T entity, List<KeyPair> keyPairs) {
        Set<KeyPair> updatedKeyPairs = mergeNewAndPersistentKeyPairs(keyPairs, entity.getKeyPairs());
        entity.setKeyPairs(updatedKeyPairs);
        return entity;
    }

    /**
     * Adds new or merges existing key pairs of the given KeyPairAware instance.
     *
     * @param entity   KeyPairAware instance
     * @param keyPairs list of key pairs
     * @return KeyPairAware object
     */
    @Override
    public <T extends KeyPairAware> T addKeyPairs(T entity, KeyPair... keyPairs) {
        return addKeyPairs(entity, Arrays.asList(keyPairs));
    }

    /**
     * Removes key pairs specified by IDs from the given KeyPairAware instance.
     *
     * @param entity     KeyPairAware instance
     * @param keyPairIds list of key pair IDs
     * @return KeyPairAware object
     */
    @Override
    public <T extends KeyPairAware> T removeKeyPairs(T entity, List<Long> keyPairIds) {
        Set<KeyPair> updatedKeyPairs = removeKeyPairs(keyPairIds, entity.getKeyPairs());
        entity.setKeyPairs(updatedKeyPairs);
        return entity;
    }

    /**
     * Removes key pairs specified by IDs from the given KeyPairAware instance.
     *
     * @param entity     KeyPairAware instance
     * @param keyPairIds list of key pair IDs
     * @return KeyPairAware object
     */
    @Override
    public <T extends KeyPairAware> T removeKeyPairs(T entity, Long... keyPairIds) {
        return removeKeyPairs(entity, Arrays.asList(keyPairIds));
    }

    /**
     * Removes key pairs specified by keys from the given KeyPairAware instance.
     *
     * @param entity KeyPairAware instance
     * @param keys   list of KeyPair keys
     * @return KeyPairAware object
     */
    @Override
    public <T extends KeyPairAware> T removeKeyPairsByKeys(T entity, List<String> keys) {
        Set<KeyPair> updatedKeyPairs = removeKeyPairsByKeys(keys, entity.getKeyPairs());
        entity.setKeyPairs(updatedKeyPairs);
        return entity;
    }

    /**
     * Removes key pairs specified by keys from the given KeyPairAware instance.
     *
     * @param entity KeyPairAware instance
     * @param keys   list of KeyPair keys
     * @return KeyPairAware object
     */
    @Override
    public <T extends KeyPairAware> T removeKeyPairsByKeys(T entity, String... keys) {
        return removeKeyPairsByKeys(entity, Arrays.asList(keys));
    }

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
    @Override
    public <T extends KeyPairAware> T updateKeyPair(T entity, Long keyPairId, String key, String value) {

        Set<KeyPair> keyPairs = entity.getKeyPairs();

        boolean keyPairIsFound = false;

        if (CollectionUtils.isNotEmpty(keyPairs)) {
            for (KeyPair keyPair : new ArrayList<KeyPair>(keyPairs)) {
                if (keyPairId.equals(keyPair.getId())) {
                    keyPair.setKey(key);
                    keyPair.setValue(value);
                    em.merge(keyPair);
                    em.flush();
                    keyPairIsFound = true;
                    break;
                }
            }
        }

        if (keyPairIsFound) {
            return entity;
        }

        String errMsg = "Cannot find KeyPair on " + entity.getClass().getName() + " entity, KeyPair ID = " + keyPairId;
        logger.error(errMsg);
        throw new IllegalStateException(errMsg);
    }

    /**
     * Retrieves the list of key pairs by key.
     *
     * @param entity KeyPairAware instance
     * @param key    KeyPair key
     * @return list of KeyPairAware objects
     */
    @Override
    public <T extends KeyPairAware> List<KeyPair> getKeyPairsByKey(T entity, String key) {

        List<KeyPair> foundKeyPairs = new LinkedList<KeyPair>();

        Set<KeyPair> keyPairs = entity.getKeyPairs();

        if (CollectionUtils.isNotEmpty(keyPairs)) {
            for (KeyPair keyPair : keyPairs) {
                if (keyPair.getKey().equals(key)) {
                    foundKeyPairs.add(keyPair);
                }
            }

        }

        return foundKeyPairs;
    }

    /**
     * Returns true if the key pair exists and false if it does not.
     *
     * @param entity KeyPairAware instance
     * @param key    KeyPair key
     * @param value  KeyPair value
     * @return true if the kay pair exists, otherwise false.
     */
    @Override
    public <T extends KeyPairAware> boolean keyPairExists(T entity, String key, String value) {

        Set<KeyPair> keyPairs = entity.getKeyPairs();

        if (CollectionUtils.isNotEmpty(keyPairs)) {
            for (KeyPair keyPair : keyPairs) {
                if (keyPair.getKey().equals(key) && keyPair.getValue().equals(value)) {
                    return true;
                }
            }

        }

        return false;
    }


}
