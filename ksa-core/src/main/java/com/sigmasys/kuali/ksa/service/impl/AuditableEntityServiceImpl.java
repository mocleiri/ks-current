package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

/**
 * AuditableEntityService implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("auditableEntityService")
@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class AuditableEntityServiceImpl extends GenericPersistenceService implements AuditableEntityService {


    private static final Log logger = LogFactory.getLog(AuditableEntityServiceImpl.class);

    /**
     * Returns AuditableEntity by ID
     *
     * @param id         AuditableEntity ID
     * @param entityType Class instance of AuditableEntity subclass
     * @return AuditableEntity instance
     */
    @Override
    public <T extends AuditableEntity> T getAuditableEntity(Long id, Class<T> entityType) {
        return getEntity(id, entityType);
    }

    /**
     * Returns AuditableEntity by code
     *
     * @param code       AuditableEntity code
     * @param entityType Class instance of AuditableEntity subclass
     * @return AuditableEntity instance
     */
    @Override
    public <T extends AuditableEntity> T getAuditableEntity(String code, Class<T> entityType) {
        Query query = em.createQuery("select ae from " + entityType.getName() + " ae where ae.code = :code");
        query.setParameter("code", code);
        List<T> entities = query.getResultList();
        return (entities != null && !entities.isEmpty()) ? entities.get(0) : null;
    }

    /**
     * Returns all AuditableEntity instances sorted by ID in the descendant order for the given entity type
     *
     * @param entityType Class instance of AuditableEntity subclass
     * @return List of Information instances
     */
    @Override
    public <T extends AuditableEntity> List<T> getAuditableEntities(Class<T> entityType) {
        return getEntities(entityType, new Pair<String, SortOrder>("id", SortOrder.DESC));
    }

    /**
     * Returns all AuditableEntity instances containing the name string sorted by ID
     * in the descendant order for the given entity type
     *
     * @param name       String containing characters within the name
     * @param entityType Class instance of AuditableEntity subclass
     * @return List of Information instances
     */
    @Override
    public <T extends AuditableEntity> List<T> getAuditableEntitiesByNamePattern(String name, Class<T> entityType) {
        Query query = em.createQuery("select ae from " + entityType.getName() +
                " ae where upper(ae.name) like upper(:name) order by ae.id desc");
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    /**
     * Persists the AuditableEntity entity in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param entity AuditableEntity instance
     * @return Entity ID
     */
    @Override
    @Transactional(readOnly = true)
    public Long persistAuditableEntity(AuditableEntity entity) {
        return persistEntity(entity);
    }

    /**
     * Removes AuditableEntity entity by ID
     *
     * @param id Entity ID
     * @return AuditableEntity instance
     */
    @Override
    @Transactional(readOnly = true)
    public <T extends AuditableEntity> boolean deleteAuditableEntity(Long id, Class<T> entityClass) {
        return deleteEntity(id, entityClass);
    }

    /**
     * Creates AuditableEntity based on the given parameters.
     *
     * @param code        Entity code
     * @param name        Entity name
     * @param description Entity description
     * @param entityType  Class instance of AuditableEntity subclass
     * @return AuditableEntity instance
     */
    @Override
    @Transactional(readOnly = true)
    public <T extends AuditableEntity> T createAuditableEntity(String code, String name, String description, Class<T> entityType) {
        try {
            T entity = entityType.newInstance();
            entity.setCode(code);
            entity.setName(name != null ? name : code);
            entity.setDescription(description);
            persistAuditableEntity(entity);
            return entity;
        } catch (Exception e) {
            String errMsg = "Cannot create an instance of " + entityType.getName() + " class. " + e.getMessage();
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }

    /**
     * Returns Currency by ISO symbol
     *
     * @param code ISO currency code
     * @return Currency instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_CURRENCY)
    public Currency getCurrency(String code) {
        Query query = em.createQuery("select c from Currency c where upper(c.code) = upper(:code)");
        query.setParameter("code", code);
        List<Currency> currencies = query.getResultList();
        if (currencies != null && !currencies.isEmpty()) {
            return currencies.get(0);
        }
        throw new IllegalArgumentException("Currency with ISO = '" + code + "' does not exist");
    }

    /**
     * Returns all currencies sorted by ISO in the ascending order
     *
     * @return List of currencies
     */
    @Override
    @PermissionsAllowed(Permission.READ_CURRENCY)
    public List<Currency> getCurrencies() {
        return getEntities(Currency.class, new Pair<String, SortOrder>("code", SortOrder.ASC));
    }

}
