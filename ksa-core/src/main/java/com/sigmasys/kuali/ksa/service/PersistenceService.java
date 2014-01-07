package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.aop.AopProxy;

import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.List;

/**
 * Persistence Service interface
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 */
public interface PersistenceService extends AopProxy {


    /**
     * Returns Identifiable entity by ID
     *
     * @param id Entity ID
     * @return Identifiable instance
     */
    <T extends Identifiable> T getEntity(Serializable id, Class<T> entityClass);


    /**
     * Removes Identifiable entity by ID
     *
     * @param id Entity ID
     * @return Identifiable instance
     */
    <T extends Identifiable> boolean deleteEntity(Serializable id, Class<T> entityClass);

    /**
     * Returns the list of all Identifiable entities for the given class with the default search criteria.
     *
     * @param entityClass Entity Class
     * @return List of Identifiable objects
     */
    <T extends Identifiable> List<T> getEntities(Class<T> entityClass);


    /**
     * Returns the list of all Identifiable entities for the given class with the default search criteria.
     *
     * @param entityClass Entity Class
     * @param orderBy     array of fields used in "order by" clause, can be null
     * @return List of Identifiable objects
     */
    <T extends Identifiable> List<T> getEntities(Class<T> entityClass, Pair<String, SortOrder>... orderBy);

    /**
     * Returns the list of all Identifiable entities for the given class.
     *
     * @param entityClass Entity Class
     * @param predicates  Predicates, can be null
     * @param orderBy     optional array of fields used in "order by" clause, can be null
     * @return List of Identifiable objects
     */
    <T extends Identifiable> List<T> getEntities(Class<T> entityClass,
                                                 List<Predicate> predicates,
                                                 Integer offset,
                                                 Integer limit,
                                                 Pair<String, SortOrder>... orderBy);


    /**
     * Persists the Identifiable entity in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param entity Identifiable instance
     * @return Entity ID
     */
    <T extends Serializable> T persistEntity(Identifiable entity);

}
