package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.*;

import java.util.List;

/**
 * AuditableEntityService
 * <p/>
 *
 * @author Michael Ivanov
 */
public interface AuditableEntityService {

    /**
     * Returns AuditableEntity by ID
     *
     * @param id         AuditableEntity ID
     * @param entityType Class instance of AuditableEntity subclass
     * @return AuditableEntity instance
     */
    <T extends AuditableEntity> T getAuditableEntity(Long id, Class<T> entityType);

    /**
     * Returns AuditableEntity by code
     *
     * @param code       AuditableEntity code
     * @param entityType Class instance of AuditableEntity subclass
     * @return AuditableEntity instance
     */
    <T extends AuditableEntity> T getAuditableEntity(String code, Class<T> entityType);

    /**
     * Returns all AuditableEntity instances sorted by ID in the descendant order for the given entity type
     *
     * @param entityType Class instance of AuditableEntity subclass
     * @return List of Information instances
     */
    <T extends AuditableEntity> List<T> getAuditableEntities(Class<T> entityType);

    /**
     * Returns all AuditableEntity instances containing the name string sorted by ID
     * in the descendant order for the given entity type.
     *
     * @param name String containing characters within the name
     * @param entityType Class instance of AuditableEntity subclass
     * @return List of Information instances
     */
    <T extends AuditableEntity> List<T> getAuditableEntitiesByNamePattern(String name, Class<T> entityType);

    /**
     * Persists the AuditableEntity entity in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param entity AuditableEntity instance
     * @return Entity ID
     */
    Long persistAuditableEntity(AuditableEntity entity);

    /**
     * Removes AuditableEntity entity by ID
     *
     * @param id Entity ID
     * @return AuditableEntity instance
     */
    <T extends AuditableEntity> boolean deleteAuditableEntity(Long id, Class<T> entityClass);

    /**
     * Creates AuditableEntity based on the given parameters.
     *
     * @param code        Entity code
     * @param name        Entity name
     * @param description Entity description
     * @param entityType  Class instance of AuditableEntity subclass
     * @return AuditableEntity instance
     */
    <T extends AuditableEntity> T createAuditableEntity(String code, String name, String description, Class<T> entityType);

    /**
     * Returns Currency by ISO symbol
     *
     * @param code ISO currency code
     * @return Currency instance
     */
    Currency getCurrency(String code);

    /**
     * Returns all currencies sorted by ISO in the ascending order
     *
     * @return List of currencies
     */
    List<Currency> getCurrencies();

}
