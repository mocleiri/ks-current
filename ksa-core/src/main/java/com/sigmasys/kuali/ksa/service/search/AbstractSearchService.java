package com.sigmasys.kuali.ksa.service.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * AbstractSearchService
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSearchService {

    protected static final int UNLIMITED_ITEMS_NUMBER = -1;

    protected static final Log logger = LogFactory.getLog(AbstractSearchService.class);

    /**
     * It is supposed to be overriden by subclasses to support search.
     *
     * @return EntityManager instance
     */
    protected abstract EntityManager getEntityManager();


    protected <T extends Serializable> List<T> populateListFromResult(List<T> result) {
        return result;
    }

    protected int countEntities(SearchCriteria searchCriteria, SearchQueryBuilder queryBuilder) {

        SearchQueryBuilder builder = queryBuilder.copy();

        String hql = builder.buildCountQuery(searchCriteria);

        logger.debug("count query = " + hql);

        Query query = getQuery(builder, hql);

        Object countResult = query.getSingleResult();
        return countResult instanceof BigDecimal ? ((BigDecimal) countResult).intValue() :
                ((Long) countResult).intValue();

    }


    protected <T extends Serializable> List<T> findEntities(SearchQueryBuilder queryBuilder, SearchCriteria sc, String sortDir,
                                                            String sortField, int offset, int limit) {


        String hql = queryBuilder.buildQuery(sc, sortDir, sortField);
        logger.debug("findEntities HQL:" + hql);
        Query query = getQuery(queryBuilder, hql);

        if (limit != UNLIMITED_ITEMS_NUMBER || !sc.isUnlimitedRows()) {

            if (!sc.isUnlimitedRows()) {
                if (limit == UNLIMITED_ITEMS_NUMBER) {
                    limit = sc.getMaxRows();
                } else {
                    // will not move further then searchCriteria.getMaxRows number of records
                    offset = Math.max(0, Math.min(offset, sc.getMaxRows() - limit));
                }
            }

            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        long statsBegin = System.currentTimeMillis();

        List<T> result = populateListFromResult(query.getResultList());

        logger.info("findEntities done in: " + (System.currentTimeMillis() - statsBegin) + " ms");

        return result;
    }

    protected Query getQuery(SearchQueryBuilder queryBuilder, String hql) {
        EntityManager em = getEntityManager();
        return queryBuilder.isUseNativeSql() ? em.createNativeQuery(hql) : em.createQuery(hql);
    }

    protected <T extends Serializable> List<T> findEntities(SearchQueryBuilder queryBuilder, SearchCriteria searchCriteria) {
        return findEntities(queryBuilder, searchCriteria, null, null, 0, UNLIMITED_ITEMS_NUMBER);
    }

    protected <T extends Serializable> PagingLoadResult buildPagingLoadResult(SearchQueryBuilder queryBuilder,
                                                                              SearchCriteria sc, String sortDir,
                                                                              String sortField, int offset, int limit) {
        int numberOfItems = 0;
        if (sc.isRunCount()) {
            numberOfItems = countEntities(sc, queryBuilder);
            logger.info("numberOfItems = " + numberOfItems);
            int numberOfPages = numberOfItems / limit;
            logger.info("numberOfPages = " + numberOfPages);
            logger.info("offset before adjustment = " + offset);
            while (numberOfItems <= offset && --numberOfPages >= 0) {
                offset = numberOfPages * limit;
            }
        }
        logger.info("offset after adjustment = " + offset);
        List<T> items = findEntities(queryBuilder, sc, sortDir, sortField, offset, limit);
        return (sc.isRunCount()) ?
                new PagingLoadResult(items, offset, numberOfItems) : new PagingLoadResult(items);
    }
}
