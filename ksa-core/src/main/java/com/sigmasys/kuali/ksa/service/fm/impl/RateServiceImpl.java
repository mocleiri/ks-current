package com.sigmasys.kuali.ksa.service.fm.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.exception.InvalidRateCatalogException;
import com.sigmasys.kuali.ksa.exception.InvalidRateException;
import com.sigmasys.kuali.ksa.exception.InvalidRateTypeException;
import com.sigmasys.kuali.ksa.exception.InvalidTransactionTypeException;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.KeyPair;
import com.sigmasys.kuali.ksa.model.fm.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.TransactionService;
import com.sigmasys.kuali.ksa.service.atp.AtpService;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import com.sigmasys.kuali.ksa.service.impl.GenericPersistenceService;
import com.sigmasys.kuali.ksa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

import static com.sigmasys.kuali.ksa.util.CalendarUtils.*;

/**
 * Fee Rate Service implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("rateService")
@Transactional(readOnly = true)
@WebService(serviceName = RateService.SERVICE_NAME, portName = RateService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
@SuppressWarnings("unchecked")
public class RateServiceImpl extends GenericPersistenceService implements RateService {

    private static final Log logger = LogFactory.getLog(RateServiceImpl.class);

    private static final String GET_RATE_SELECT = "select r from Rate r " +
            " inner join fetch r.rateType rt " +
            " inner join fetch r.rateCatalogAtp rca " +
            " left outer join fetch r.defaultRateAmount dra " +
            " left outer join fetch r.keyPairs kp " +
            " left outer join fetch r.rateAmounts ra ";


    @Autowired
    private AtpService atpService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuditableEntityService auditableEntityService;


    // Rate Type methods

    /**
     * Creates a new RateType instance and persists it in the database.
     *
     * @param code           RateType code
     * @param name           RateType name
     * @param description    RateType description
     * @param isGrouping     Indicates whether the rates of this type should be grouped or not
     * @param rateAmountType Rate amount type enum value
     * @return a new RateType instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_RATE_TYPE)
    public RateType createRateType(String code, String name, String description, boolean isGrouping, RateAmountType rateAmountType) {

        RateType rateType = new RateType();
        rateType.setCode(code);
        rateType.setName(name);
        rateType.setDescription(description);

        rateType.setGrouping(isGrouping);
        rateType.setRateAmountType(rateAmountType);

        validateRateType(rateType);

        auditableEntityService.persistAuditableEntity(rateType);

        return rateType;
    }

    /**
     * Persists RateType in the database.
     *
     * @param rateType RateType instance
     * @return RateType ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_RATE_TYPE)
    public Long persistRateType(RateType rateType) {
        validateRateType(rateType);
        return auditableEntityService.persistAuditableEntity(rateType);
    }

    /**
     * Removes the RateType instance specified by ID from the persistent store.
     *
     * @param rateTypeId RateType ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_RATE_TYPE)
    public void deleteRateType(Long rateTypeId) {
        auditableEntityService.deleteAuditableEntity(rateTypeId, RateType.class);
    }

    /**
     * Removes the RateType instance specified by code from the persistent store
     *
     * @param rateTypeCode RateType code
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_RATE_TYPE)
    public void deleteRateTypeByCode(String rateTypeCode) {

        RateType rateType = getRateTypeByCode(rateTypeCode);
        if (rateType == null) {
            String errMsg = "RateType with code '" + rateTypeCode + "' does not exist";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        auditableEntityService.deleteAuditableEntity(rateType.getId(), RateType.class);
    }

    /**
     * Retrieves the RateType instance from the persistent store by ID.
     *
     * @param rateTypeId RateType ID
     * @return RateType instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_TYPE)
    public RateType getRateType(Long rateTypeId) {
        return auditableEntityService.getAuditableEntity(rateTypeId, RateType.class);
    }

    /**
     * Retrieves the RateType instance from the persistent store by code.
     *
     * @param rateTypeCode RateType code
     * @return RateType instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_TYPE)
    public RateType getRateTypeByCode(String rateTypeCode) {
        return auditableEntityService.getAuditableEntity(rateTypeCode, RateType.class);
    }

    /**
     * Checks if the rate type specified by code exists.
     *
     * @param rateTypeCode RateType code
     * @return true if the rate type exists, false - otherwise
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_TYPE)
    public boolean rateTypeExists(String rateTypeCode) {
        Query query = em.createQuery("select 1 from RateType where code = :code");
        query.setParameter("code", rateTypeCode);
        query.setMaxResults(1);
        return CollectionUtils.isNotEmpty(query.getResultList());
    }

    /**
     * Returns the list of rate types by the given name pattern.
     *
     * @param namePattern Name pattern
     * @return a list of RateType instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_TYPE)
    public List<RateType> getRateTypesByNamePattern(String namePattern) {
        return auditableEntityService.getAuditableEntitiesByNamePattern(namePattern, RateType.class);
    }

    /**
     * Returns the list of all rate types.
     *
     * @return a list of RateType instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_TYPE)
    public List<RateType> getAllRateTypes() {
        return auditableEntityService.getAuditableEntities(RateType.class);
    }

    // Rate Catalog methods

    /**
     * Creates a new RateCatalog persistent instance from the set of given parameters.
     *
     * @param rateCatalogCode            RateCatalog code
     * @param rateTypeCode               RateType code
     * @param transactionTypeId          TransactionType ID
     * @param transactionDateType        TransactionDateType enum value
     * @param minAmount                  Minimum transaction amount
     * @param maxAmount                  Maximum transaction amount
     * @param minLimitUnits              Minimum number of Limit Units
     * @param maxLimitUnits              Maximum number of Limit Units
     * @param atpIds                     list of ATP IDs
     * @param keyPairs                   list of KeyPair instances
     * @param isTransactionTypeFinal     Indicates whether the transaction type is final
     * @param isTransactionDateTypeFinal Indicates whether the transaction date type is final
     * @param isRecognitionDateDefinable Indicates whether the recognition date can be set
     * @param isKeyPairFinal             Indicates whether the set of KeyPairs is immutable
     * @param isLimitAmount              Indicates whether the limit amount exists
     * @param isLimitAmountFinal         Indicates whether the limit amount is final
     * @return a new RateCatalog instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_RATE_CATALOG)
    public RateCatalog createRateCatalog(String rateCatalogCode,
                                         String rateTypeCode,
                                         String transactionTypeId,
                                         TransactionDateType transactionDateType,
                                         BigDecimal minAmount,
                                         BigDecimal maxAmount,
                                         BigDecimal minLimitAmount,
                                         BigDecimal maxLimitAmount,
                                         int minLimitUnits,
                                         int maxLimitUnits,
                                         List<String> atpIds,
                                         List<KeyPair> keyPairs,
                                         boolean isTransactionTypeFinal,
                                         boolean isTransactionDateTypeFinal,
                                         boolean isRecognitionDateDefinable,
                                         boolean isKeyPairFinal,
                                         boolean isLimitAmount,
                                         boolean isLimitAmountFinal) {

        if (StringUtils.isBlank(rateCatalogCode)) {
            String errMsg = "RateCatalog code cannot be empty";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        RateType rateType = getRateTypeByCode(rateTypeCode);
        if (rateType == null) {
            String errMsg = "RateType with code = " + rateTypeCode;
            logger.error(errMsg);
            throw new InvalidRateTypeException(errMsg);
        }

        RateCatalog rateCatalog = new RateCatalog();
        rateCatalog.setCode(rateCatalogCode);
        rateCatalog.setRateType(rateType);
        rateCatalog.setTransactionTypeId(transactionTypeId);
        rateCatalog.setTransactionDateType(transactionDateType);
        rateCatalog.setMinAmount(minAmount);
        rateCatalog.setMaxAmount(maxAmount);
        rateCatalog.setMinLimitAmount(minLimitAmount);
        rateCatalog.setMaxLimitAmount(maxLimitAmount);
        rateCatalog.setMinLimitUnits(minLimitUnits);
        rateCatalog.setMaxLimitUnits(maxLimitUnits);

        rateCatalog.setTransactionTypeFinal(isTransactionTypeFinal);
        rateCatalog.setTransactionDateTypeFinal(isTransactionDateTypeFinal);
        rateCatalog.setRecognitionDateDefinable(isRecognitionDateDefinable);
        rateCatalog.setKeyPairFinal(isKeyPairFinal);
        rateCatalog.setLimitAmount(isLimitAmount);
        rateCatalog.setLimitAmountFinal(isLimitAmountFinal);

        // Persisting RateCatalog's key pairs
        Set<KeyPair> keyPairSet = new HashSet<KeyPair>(keyPairs);
        if (CollectionUtils.isNotEmpty(keyPairSet)) {
            for (KeyPair keyPair : keyPairSet) {
                persistEntity(keyPair);
            }
            rateCatalog.setKeyPairs(keyPairSet);
        }

        // Persisting the new RateCatalog instance
        persistRateCatalog(rateCatalog);

        // Persisting ATP IDs
        Set<String> atpIdSet = new HashSet<String>(atpIds);

        if (CollectionUtils.isNotEmpty(atpIdSet)) {

            // Checking if there are other rate catalogs with the same code and any ATP IDs from the list
            Query query = em.createQuery("select 1 from RateCatalogAtp where id.code = :code and id.atpId in (:atpIds)");
            query.setParameter("code", rateCatalogCode);
            query.setParameter("atpIds", atpIdSet);
            query.setMaxResults(1);

            if (CollectionUtils.isNotEmpty(query.getResultList())) {
                String errMsg = "The same ATP IDs cannot be associated with 2 different catalogs with the same code";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            for (String atpId : atpIds) {

                // Checking if the ATP ID exists using AtpService
                if (!atpService.atpExists(atpId)) {
                    String errMsg = "ATP with ID = " + atpId + " does not exist";
                    logger.error(errMsg);
                    throw new IllegalArgumentException(errMsg);
                }

                // Creating a new instance of RateCatalogAtp and persisting the ATP ID
                RateCatalogAtp rateCatalogAtp = new RateCatalogAtp();
                rateCatalogAtp.setId(new RateCatalogAtpId(rateCatalogCode, atpId));
                rateCatalogAtp.setRateCatalog(rateCatalog);

                persistEntity(rateCatalogAtp);
            }

            // Validating the catalog transaction type with ATP IDs
            validateTransactionTypeWithAtps(InvalidRateCatalogException.class, transactionTypeId, atpIdSet);
        }

        return rateCatalog;
    }


    /**
     * Persists RateCatalog in the database.
     *
     * @param rateCatalog RateCatalog instance
     * @return RateCatalog ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_RATE_CATALOG)
    public Long persistRateCatalog(RateCatalog rateCatalog) {

        if (!isRateCatalogValid(rateCatalog)) {
            String errMsg = "RateCatalog (code=" + rateCatalog.getCode() + ") is invalid";
            logger.error(errMsg);
            throw new InvalidRateCatalogException(errMsg);
        }

        return auditableEntityService.persistAuditableEntity(rateCatalog);
    }

    /**
     * Removes RateCatalog from the persistent store.
     *
     * @param rateCatalogId RateCatalog ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_RATE_CATALOG)
    public void deleteRateCatalog(Long rateCatalogId) {

        RateCatalog rateCatalog = getRateCatalog(rateCatalogId);

        if (rateCatalog == null) {
            String errMsg = "RateCatalog with ID = " + rateCatalogId + " does not exist";
            logger.error(errMsg);
            throw new InvalidRateCatalogException(errMsg);
        }

        rateCatalog.setKeyPairs(null);

        persistEntity(rateCatalog);

        Query query = em.createQuery("delete from RateCatalogAtp where rateCatalog.id = :rateCatalogId");
        query.setParameter("rateCatalogId", rateCatalogId);
        query.executeUpdate();

        auditableEntityService.deleteAuditableEntity(rateCatalogId, RateCatalog.class);
    }

    /**
     * Retrieves the RateCatalog instance specified by ID from the persistent store.
     *
     * @param rateCatalogId RateCatalog ID
     * @return RateCatalog instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_CATALOG)
    public RateCatalog getRateCatalog(Long rateCatalogId) {
        Query query = em.createQuery("select rc from RateCatalog rc " +
                " left outer join fetch rc.rateType rt " +
                " left outer join fetch rc.keyPairs kp " +
                " where rc.id = :id");
        query.setParameter("id", rateCatalogId);
        List<RateCatalog> results = query.getResultList();
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * Retrieves the RateCatalog instance specified by Rate ID from the persistent store.
     *
     * @param rateId Rate ID
     * @return RateCatalog instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_CATALOG)
    public RateCatalog getRateCatalogByRateId(Long rateId) {
        Query query = em.createQuery("select rc from RateCatalog rc, RateCatalogAtp rca, Rate r " +
                " left outer join fetch rc.rateType rt " +
                " left outer join fetch rc.keyPairs kp " +
                " where rc.id = rca.rateCatalog.id and rca.id = r.rateCatalogAtp.id and r.id = :rateId");
        query.setParameter("rateId", rateId);
        List<RateCatalog> results = query.getResultList();
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * Retrieves the RateCatalog instance specified by code and ATP ID from the persistent store.
     *
     * @param rateCatalogCode RateCatalog code
     * @param atpId           ATP ID
     * @return RateCatalog instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_CATALOG)
    public RateCatalog getRateCatalogByCodeAndAtpId(String rateCatalogCode, String atpId) {
        Query query = em.createQuery("select rc from RateCatalog rc, RateCatalogAtp rca " +
                " left outer join fetch rc.rateType rt " +
                " left outer join fetch rc.keyPairs kp " +
                " where rca.id = :id and rc.id = rca.rateCatalog.id");
        query.setParameter("id", new RateCatalogAtpId(rateCatalogCode, atpId));
        List<RateCatalog> results = query.getResultList();
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * Returns the list of rate catalogs by code.
     *
     * @param rateCatalogCode RateCatalog code
     * @return a list of RateCatalog instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_CATALOG)
    public List<RateCatalog> getRateCatalogsByCode(String rateCatalogCode) {
        Query query = em.createQuery("select rc from RateCatalog rc " +
                " left outer join fetch rc.rateType rt " +
                " left outer join fetch rc.keyPairs kp " +
                " where rc.code = :code");
        query.setParameter("code", rateCatalogCode);
        return query.getResultList();
    }

    /**
     * Returns the list of rate catalogs by RateType ID.
     *
     * @param rateTypeId RateType ID
     * @return a list of RateCatalog instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_CATALOG)
    public List<RateCatalog> getRateCatalogsByRateTypeId(Long rateTypeId) {
        Query query = em.createQuery("select distinct rc from RateCatalog rc " +
                " left outer join fetch rc.rateType rt " +
                " left outer join fetch rc.keyPairs kp " +
                " where rt.id = :rateTypeId");
        query.setParameter("rateTypeId", rateTypeId);
        return query.getResultList();
    }

    /**
     * Returns the list of rate catalogs by ATP ID.
     *
     * @param atpId ATP ID
     * @return a list of RateCatalog instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_CATALOG)
    public List<RateCatalog> getRateCatalogsByAtpId(String atpId) {
        Query query = em.createQuery("select rc from RateCatalog rc, RateCatalogAtp rca " +
                " left outer join fetch rc.rateType rt " +
                " left outer join fetch rc.keyPairs kp " +
                " where rca.id.atpId = :atpId and rc.id = rca.rateCatalog.id");
        query.setParameter("atpId", atpId);
        return query.getResultList();
    }

    protected RateCatalogAtp getRateCatalogAtp(String rateCatalogCode, String atpId) {
        Query query = em.createQuery("select rca from RateCatalogAtp rca " +
                " inner join fetch rca.rateCatalog rc " +
                " left outer join fetch rc.rateType rt " +
                " left outer join fetch rc.keyPairs kp " +
                " where rca.id = :id");
        query.setParameter("id", new RateCatalogAtpId(rateCatalogCode, atpId));
        List<RateCatalogAtp> results = query.getResultList();
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * Returns the list of rate catalogs by the given name pattern.
     *
     * @param namePattern Name pattern
     * @return a list of RateCatalog instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_CATALOG)
    public List<RateCatalog> getRateCatalogsByNamePattern(String namePattern) {
        Query query = em.createQuery("select rc from RateCatalog rc " +
                " left outer join fetch rc.rateType rt " +
                " left outer join fetch rc.keyPairs kp " +
                " where upper(rc.name) like upper(:namePattern)");
        query.setParameter("namePattern", "%" + namePattern + "%");
        return query.getResultList();
    }

    /**
     * Returns the list of all rate catalogs.
     *
     * @return a list of RateCatalog instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_CATALOG)
    public List<RateCatalog> getAllRateCatalogs() {
        Query query = em.createQuery("select rc from RateCatalog rc " +
                " left outer join fetch rc.rateType rt " +
                " left outer join fetch rc.keyPairs kp " +
                " order by rc.id desc");
        return query.getResultList();
    }

    // Rate methods

    /**
     * Creates a new persistent Rate instance based on the given parameters.
     *
     * @param rateCode            Rate code
     * @param subCode             Rate sub-code
     * @param rateName            Rate name
     * @param rateCatalogCode     RateCatalog code
     * @param transactionTypeId   Transaction Type ID
     * @param transactionDateType TransactionDateType enum value
     * @param defaultRateAmount   Default Rate amount
     * @param limitAmount         Limit amount
     * @param minLimitUnits       Minimum number of units
     * @param maxLimitUnits       Maximum number of units
     * @param transactionDate     Transaction date
     * @param recognitionDate     Recognition date
     * @param atpId               ATP ID
     * @param isLimitAmount       Indicates whether the limit amount should exist
     * @return a new Rate instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_RATE)
    public Rate createRate(String rateCode,
                           String subCode,
                           String rateName,
                           String rateCatalogCode,
                           String transactionTypeId,
                           TransactionDateType transactionDateType,
                           BigDecimal defaultRateAmount,
                           BigDecimal limitAmount,
                           Integer minLimitUnits,
                           Integer maxLimitUnits,
                           Date transactionDate,
                           Date recognitionDate,
                           String atpId,
                           boolean isLimitAmount) {

        if (StringUtils.isBlank(rateCatalogCode)) {
            String errMsg = "RateCatalog code cannot be empty";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (StringUtils.isBlank(atpId)) {
            String errMsg = "ATP ID cannot be empty";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (!atpService.atpExists(atpId)) {
            String errMsg = "ATP ID = " + atpId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        RateCatalogAtp rateCatalogAtp = getRateCatalogAtp(rateCatalogCode, atpId);
        if (rateCatalogAtp == null || rateCatalogAtp.getRateCatalog() == null) {
            String errMsg = "RateCatalog does not exist with code = " + rateCatalogCode + " and ATP ID = " + atpId;
            logger.error(errMsg);
            throw new InvalidRateCatalogException(errMsg);
        }

        RateCatalog rateCatalog = rateCatalogAtp.getRateCatalog();

        if (StringUtils.isBlank(transactionTypeId)) {
            transactionTypeId = rateCatalog.getTransactionTypeId();
        }

        // Creating a new Rate instance
        Rate rate = new Rate();

        rate.setCode(rateCode);
        rate.setSubCode(subCode);
        rate.setName(rateName);
        rate.setRateType(rateCatalog.getRateType());
        rate.setRateCatalogAtp(rateCatalogAtp);
        rate.setAtpId(atpId);
        rate.setTransactionDate(transactionDate);
        rate.setTransactionDateType(transactionDateType);
        rate.setRecognitionDate(recognitionDate);

        rate.setMinLimitUnits(minLimitUnits);
        rate.setMaxLimitUnits(maxLimitUnits);

        rate.setLimitAmountValue(limitAmount);
        rate.setLimitAmount(isLimitAmount);

        // Creating a new default RateAmount instance
        RateAmount rateAmount = new RateAmount();
        rateAmount.setAmount(defaultRateAmount);
        rateAmount.setTransactionTypeId(transactionTypeId);
        rateAmount.setUnits(0);

        // Persisting RateAmount
        persistEntity(rateAmount);

        rate.setDefaultRateAmount(rateAmount);
        rate.setRateAmounts(new HashSet<RateAmount>(Arrays.asList(rateAmount)));

        // Copying the key pairs from RateCatalog
        Set<KeyPair> catalogKeyPairs = rateCatalog.getKeyPairs();
        if (CollectionUtils.isNotEmpty(catalogKeyPairs)) {
            Set<KeyPair> rateKeyPairs = new HashSet<KeyPair>(catalogKeyPairs.size());
            for (KeyPair keyPair : catalogKeyPairs) {
                // Making a KeyPair deep copy and make sure it is new (setting its ID to null)
                KeyPair keyPairCopy = BeanUtils.getDeepCopy(keyPair);
                keyPairCopy.setId(null);
                rateKeyPairs.add(keyPairCopy);
            }
            rate.setKeyPairs(rateKeyPairs);
        }

        // Validating the new rate with the rate catalog
        validateRateWithCatalog(rate, rateCatalog);

        // Persisting the new Rate instance
        auditableEntityService.persistAuditableEntity(rate);

        // Setting the persistent Rate reference in RateAmount
        rateAmount.setRate(rate);

        return rate;
    }

    /**
     * Persists the Rate instance in the database.
     *
     * @param rate Rate instance
     * @return Rate ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_RATE)
    public Long persistRate(Rate rate) {
        validateRate(rate);
        return persistEntity(rate);
    }

    /**
     * Removes Rate specified by ID from the persistent store
     *
     * @param rateId Rate ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_RATE)
    public void deleteRate(Long rateId) {

        Rate rate = getRate(rateId);
        if (rate == null) {
            String errMsg = "Rate with ID = " + rateId + " does not exist";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        // Removing Rate key pairs
        Set<KeyPair> keyPairs = rate.getKeyPairs();
        if (CollectionUtils.isNotEmpty(keyPairs)) {
            for (KeyPair keyPair : new HashSet<KeyPair>(keyPairs)) {
                keyPairs.remove(keyPair);
                em.flush();
                deleteEntity(keyPair.getId(), KeyPair.class);
            }
        }

        Set<RateAmount> rateAmounts = rate.getRateAmounts();
        if (CollectionUtils.isNotEmpty(rateAmounts)) {
            for (RateAmount rateAmount : new HashSet<RateAmount>(rateAmounts)) {
                rateAmounts.remove(rateAmount);
            }
        }

        Query query = em.createQuery("delete from RateAmount where rate.id = :rateId");
        query.setParameter("rateId", rateId);
        query.executeUpdate();

        deleteEntity(rateId, Rate.class);
    }

    /**
     * Retrieves the Rate instance from the database by ID.
     *
     * @param rateId Rate ID
     * @return Rate instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public Rate getRate(Long rateId) {
        Query query = em.createQuery(GET_RATE_SELECT + " where r.id = :id");
        query.setParameter("id", rateId);
        List<Rate> rates = query.getResultList();
        return CollectionUtils.isNotEmpty(rates) ? rates.get(0) : null;
    }

    /**
     * Retrieves the Rate instance from the database by code and ATP ID.
     *
     * @param rateCode Rate code
     * @param subCode  Rate sub-code
     * @param atpId    ATP ID
     * @return Rate instance
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_RATE)
    public Rate getRate(String rateCode, String subCode, String atpId) {

        if (!atpService.atpExists(atpId)) {
            String errMsg = "ATP ID = " + atpId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Query query = em.createQuery(GET_RATE_SELECT + " where r.code = :rateCode and r.subCode = :subCode and rca.id.atpId = :atpId");

        query.setParameter("rateCode", rateCode);
        query.setParameter("subCode", subCode);
        query.setParameter("atpId", atpId);

        List<Rate> rates = query.getResultList();

        return CollectionUtils.isNotEmpty(rates) ? rates.get(0) : null;
    }

    /**
     * Returns the list of rates by code.
     *
     * @param rateCode Rate code
     * @return a list of Rate instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public List<Rate> getRatesByCode(String rateCode) {

        Query query = em.createQuery(GET_RATE_SELECT + " where r.code = :code order by r.id desc");

        query.setParameter("code", rateCode);

        return query.getResultList();
    }

    /**
     * Returns the list of rates by code and sub-code.
     *
     * @param rateCode Rate code
     * @param subCode  Rate sub-code
     * @return a list of Rate instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public List<Rate> getRatesByCodeAndSubCode(String rateCode, String subCode) {

        Query query = em.createQuery(GET_RATE_SELECT + " where r.code = :code and r.subCode = :subCode order by r.id desc");

        query.setParameter("code", rateCode);
        query.setParameter("subCode", subCode);

        return query.getResultList();
    }

    /**
     * Returns the list of rates by ATP ID.
     *
     * @param atpId ATP ID
     * @return a list of Rate instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public List<Rate> getRatesByAtpId(String atpId) {

        if (!atpService.atpExists(atpId)) {
            String errMsg = "ATP ID = " + atpId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Query query = em.createQuery(GET_RATE_SELECT + " where rca.id.atpId = :atpId");
        query.setParameter("atpId", atpId);

        return query.getResultList();
    }

    /**
     * Returns the list of rates by RateCatalog ID.
     *
     * @param rateCatalogId Rate catalog ID
     * @return a list of Rate instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public List<Rate> getRatesByCatalogId(Long rateCatalogId) {
        Query query = em.createQuery(GET_RATE_SELECT + " where rca.rateCatalog.id = :rateCatalogId");
        query.setParameter("rateCatalogId", rateCatalogId);
        return query.getResultList();
    }

    /**
     * Returns the list of rates by name pattern.
     *
     * @param namePattern Name pattern
     * @return a list of Rate instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public List<Rate> getRatesByNamePattern(String namePattern) {
        Query query = em.createQuery(GET_RATE_SELECT + " where upper(r.name) like upper(:namePattern)");
        query.setParameter("namePattern", "%" + namePattern + "%");
        return query.getResultList();
    }

    /**
     * Returns the list of all rates.
     *
     * @return a list of Rate instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public List<Rate> getAllRates() {
        Query query = em.createQuery(GET_RATE_SELECT + " order by r.id desc");
        return query.getResultList();
    }

    /**
     * Retrieves a set of ATP IDs for the rate catalog specified by ID.
     *
     * @param rateCatalogId RateCatalog ID
     * @return a set of ATP IDs
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE_CATALOG)
    public Set<String> getAtpsForRateCatalog(Long rateCatalogId) {
        Query query = em.createQuery("select rca.id.atpId from RateCatalogAtp rca where rca.rateCatalog.id = :id");
        query.setParameter("id", rateCatalogId);
        return new HashSet<String>(query.getResultList());
    }


    // Additional methods

    /**
     * Validates the given RateType instance and throws <code>InvalidRateTypeException</code> if the validation fails.
     *
     * @param rateType RateType instance
     * @throws InvalidRateTypeException
     */
    @Override
    @PermissionsAllowed(Permission.VALIDATE_RATE_TYPE)
    public void validateRateType(RateType rateType) throws InvalidRateTypeException {

        if (rateType == null) {
            String errMsg = "RateType cannot be null";
            logger.error(errMsg);
            throw new InvalidRateTypeException(errMsg);
        }

        if (StringUtils.isBlank(rateType.getCode())) {
            String errMsg = "RateType code is required";
            logger.error(errMsg);
            throw new InvalidRateTypeException(errMsg);
        }

        if (StringUtils.isBlank(rateType.getName())) {
            String errMsg = "RateType name is required";
            logger.error(errMsg);
            throw new InvalidRateTypeException(errMsg);
        }

    }

    /**
     * Validates the given Rate instance and throws <code>InvalidRateException</code> if the validation fails.
     *
     * @param rate Rate instance
     * @throws com.sigmasys.kuali.ksa.exception.InvalidRateException
     *
     */
    @Override
    @PermissionsAllowed(Permission.VALIDATE_RATE)
    public void validateRate(Rate rate) throws InvalidRateException {
        RateCatalogAtp rateCatalogAtp = rate.getRateCatalogAtp();
        validateRateWithCatalog(rate, (rateCatalogAtp != null) ? rateCatalogAtp.getRateCatalog() : null);
    }

    private boolean isTransactionTypeValid(String transactionTypeId, Date date) {
        boolean isTransactionTypeValid;
        try {
            isTransactionTypeValid =
                    transactionService.getTransactionType(transactionTypeId, date) != null;
        } catch (InvalidTransactionTypeException e) {
            isTransactionTypeValid = false;
        }
        return isTransactionTypeValid;
    }

    protected void validateTransactionTypeWithAtps(Class<? extends Exception> exceptionClass,
                                                   String transactionTypeId,
                                                   Collection<String> atpIds) {

        final List<AtpInfo> atps;

        try {
            atps = atpService.getAtpsByIds(new ArrayList<String>(atpIds), atpService.getAtpContextInfo());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        if (atps != null) {
            for (AtpInfo atp : atps) {
                Date startDate = atp.getStartDate();
                Date endDate = atp.getEndDate();
                if (!isTransactionTypeValid(transactionTypeId, startDate) || !isTransactionTypeValid(transactionTypeId, endDate)) {
                    String errMsg = "Transaction type '" + transactionTypeId + "' is invalid for ATP ID = " + atp.getId();
                    logger.error(errMsg);
                    try {
                        throw exceptionClass.getConstructor(String.class).newInstance(errMsg);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }
    }


    /**
     * Validates the given RateCatalog instance and throws <code>InvalidRateCatalogException</code> if the validation fails.
     *
     * @param rateCatalog RateCatalog instance
     * @throws com.sigmasys.kuali.ksa.exception.InvalidRateException
     *
     * @throws com.sigmasys.kuali.ksa.exception.InvalidRateCatalogException
     *
     */
    @Override
    @PermissionsAllowed(Permission.VALIDATE_RATE_CATALOG)
    public void validateRateCatalog(RateCatalog rateCatalog) throws InvalidRateException, InvalidRateCatalogException {

        if (rateCatalog == null) {
            String errMsg = "RateCatalog cannot be null";
            logger.error(errMsg);
            throw new InvalidRateCatalogException(errMsg);
        }

        RateType rateType = rateCatalog.getRateType();

        validateRateType(rateType);

        if (!rateTypeExists(rateType.getCode())) {
            String errMsg = "RateType does not exist with code = " + rateType.getCode();
            logger.error(errMsg);
            throw new InvalidRateTypeException(errMsg);
        }

        BigDecimal minAmount = rateCatalog.getMinAmount();
        BigDecimal maxAmount = rateCatalog.getMaxAmount();

        if (minAmount != null && maxAmount != null) {
            if (minAmount.compareTo(maxAmount) > 0) {
                String errMsg = "Minimum amount cannot be greater than Maximum amount";
                logger.error(errMsg);
                throw new InvalidRateCatalogException(errMsg);
            }
        }

        Integer minLimitUnits = rateCatalog.getMinLimitUnits();
        Integer maxLimitUnits = rateCatalog.getMaxLimitUnits();

        if (rateCatalog.isLimitAmount() && minLimitUnits == null) {
            String errMsg = "RateCatalog's minLimitUnits must not be null";
            logger.error(errMsg);
            throw new InvalidRateCatalogException(errMsg);
        }

        if (minLimitUnits != null && maxLimitUnits != null) {
            if (minLimitUnits.compareTo(maxLimitUnits) > 0) {
                String errMsg = "RateCatalog's minLimitUnits cannot be greater than maxLimitUnits";
                logger.error(errMsg);
                throw new InvalidRateCatalogException(errMsg);
            }
        }

        BigDecimal minLimitAmount = rateCatalog.getMinLimitAmount();
        BigDecimal maxLimitAmount = rateCatalog.getMaxLimitAmount();

        if (minLimitAmount != null && maxLimitAmount != null) {
            if (minLimitAmount.compareTo(maxLimitAmount) > 0) {
                String errMsg = "RateCatalog's minLimitAmount cannot be greater than maxLimitAmount";
                logger.error(errMsg);
                throw new InvalidRateCatalogException(errMsg);
            }
        }

        if (rateCatalog.getId() != null) {
            Set<String> atpIds = getAtpsForRateCatalog(rateCatalog.getId());
            if (CollectionUtils.isNotEmpty(atpIds)) {
                String transactionTypeId = rateCatalog.getTransactionTypeId();
                validateTransactionTypeWithAtps(InvalidRateCatalogException.class, transactionTypeId, atpIds);
            }
        }

        // If the rate catalog exists we have to check all the rates associated with it
        if (rateCatalog.getId() != null) {
            Query query = em.createQuery("select rate from Rate rate " +
                    "inner join rate.rateCatalogAtp rca " +
                    "inner join rca.rateCatalog rc " +
                    "where rc.id = :rateCatalogId");
            query.setParameter("rateCatalogId", rateCatalog.getId());
            List<Rate> rates = query.getResultList();
            if (CollectionUtils.isNotEmpty(rates)) {
                for (Rate rate : rates) {
                    validateRate(rate);
                }
            }
        }

    }

    /**
     * Validates the Rate against the RateCatalog instance
     * and throws <code>InvalidRateException</code> or <code>InvalidRateCatalogException</code> if the validation fails.
     *
     * @param rate        Rate instance
     * @param rateCatalog RateCatalog instance
     * @throws com.sigmasys.kuali.ksa.exception.InvalidRateException
     *
     */
    @Override
    @PermissionsAllowed({Permission.VALIDATE_RATE, Permission.VALIDATE_RATE_CATALOG})
    public void validateRateWithCatalog(Rate rate, RateCatalog rateCatalog) throws InvalidRateException {

        if (rate == null) {
            String errMsg = "Rate cannot be null";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        if (StringUtils.isBlank(rate.getCode())) {
            String errMsg = "Rate code is required";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        if (StringUtils.isBlank(rate.getSubCode())) {
            String errMsg = "Rate sub-code is required";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        if (StringUtils.isBlank(rate.getName())) {
            String errMsg = "Rate name is required";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        Set<RateAmount> rateAmounts = rate.getRateAmounts();

        if (CollectionUtils.isEmpty(rateAmounts)) {
            String errMsg = "Rate must have at least one RateAmount";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        RateAmount defaultRateAmount = rate.getDefaultRateAmount();
        if (defaultRateAmount == null) {
            String errMsg = "Rate must have the default RateAmount";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        boolean defaultAmountIsPresent = false;

        Set<Integer> uniqueUnits = new HashSet<Integer>();

        for (RateAmount rateAmount : rateAmounts) {

            if (rateAmount.getId() == null) {
                String errMsg = "RateAmount ID cannot be null";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }

            if (rateAmount.getUnits() == null) {
                String errMsg = "RateAmount unit cannot be null";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }

            if (rateAmount.getAmount() == null) {
                String errMsg = "RateAmount amount cannot be null";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }

            String transactionTypeId = rateAmount.getTransactionTypeId();
            if (transactionTypeId == null) {
                String errMsg = "RateAmount's transaction type cannot be null";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }

            if (rateCatalog != null) {
                if (rateCatalog.isTransactionTypeFinal() && !transactionTypeId.equals(rateCatalog.getTransactionTypeId())) {
                    String errMsg = "RateAmount must have the same transaction type as RateCatalog";
                    logger.error(errMsg);
                    throw new InvalidRateException(errMsg);
                }
            }

            if (rate.getTransactionDate() != null) {

                if (!isTransactionTypeValid(transactionTypeId, rate.getTransactionDate())) {
                    String errMsg = "RateAmount's transaction type is invalid";
                    logger.error(errMsg);
                    throw new InvalidRateException(errMsg);
                }

            } else {

                // Validating the RateAmount's transaction type with the Rate's ATP ID
                validateTransactionTypeWithAtps(InvalidRateException.class, transactionTypeId, Arrays.asList(rate.getAtpId()));
            }

            if (rateAmount.getId().equals(defaultRateAmount.getId())) {
                defaultAmountIsPresent = true;
            } else {
                uniqueUnits.add(rateAmount.getUnits());
            }
        }

        if (!defaultAmountIsPresent) {
            String errMsg = "Default RateAmount must be one of existing Rate's amounts";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        // Checking that the unit numbers of the current rate are unique
        if (uniqueUnits.size() != rateAmounts.size() - 1) {
            String errMsg = "Rate must have unique RateAmount units";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        if (rate.getLimitAmountValue() != null && rate.getLimitAmountValue().compareTo(defaultRateAmount.getAmount()) < 0) {
            String errMsg = "Rate limit amount cannot be less than the default Rate amount";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        if (rate.getTransactionDateType() != null && rate.getTransactionDate() == null) {
            String errMsg = "Rate transaction date must not be null when transaction date type is set";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        if (rate.isLimitAmount() && rate.getLimitAmountValue() == null) {
            String errMsg = "Rate limit amount cannot be null";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        Integer minLimitUnits = rate.getMinLimitUnits();
        Integer maxLimitUnits = rate.getMaxLimitUnits();

        if (rate.isLimitAmount() && minLimitUnits == null) {
            String errMsg = "Rate's minLimitUnits cannot be null";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        if (minLimitUnits != null && maxLimitUnits != null) {
            if (minLimitUnits.compareTo(maxLimitUnits) > 0) {
                String errMsg = "Rate's minLimitUnits cannot be greater than maxLimitUnits";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }
        }

        if (rateCatalog != null) {

            if (rate.isLimitAmount() && rateCatalog.isLimitAmount()) {

                BigDecimal minLimitAmount = rateCatalog.getMinLimitAmount();
                BigDecimal maxLimitAmount = rateCatalog.getMaxLimitAmount();

                if (minLimitAmount != null && rate.getLimitAmountValue().compareTo(minLimitAmount) < 0) {
                    String errMsg = "Rate limit amount must not be less than minLimitAmount of RateCatalog";
                    logger.error(errMsg);
                    throw new InvalidRateException(errMsg);
                }

                if (maxLimitAmount != null && rate.getLimitAmountValue().compareTo(maxLimitAmount) > 0) {
                    String errMsg = "Rate limit amount must not be greater than maxLimitAmount of RateCatalog";
                    logger.error(errMsg);
                    throw new InvalidRateException(errMsg);
                }
            }

            if (rateCatalog.isLimitAmountFinal()) {

                if (minLimitUnits != null && maxLimitUnits != null &&
                        rateCatalog.getMinLimitUnits() != null && rateCatalog.getMaxLimitAmount() != null) {

                    if (minLimitUnits.compareTo(rateCatalog.getMinLimitUnits()) != 0) {
                        String errMsg = "minLimitUnits values of Rate and RateCatalog must match";
                        logger.error(errMsg);
                        throw new InvalidRateException(errMsg);
                    }

                    if (maxLimitUnits.compareTo(rateCatalog.getMaxLimitUnits()) != 0) {
                        String errMsg = "maxLimitUnits values of Rate and RateCatalog must match";
                        logger.error(errMsg);
                        throw new InvalidRateException(errMsg);
                    }
                }

                if (rate.isLimitAmount() != rateCatalog.isLimitAmount()) {
                    String errMsg = "Rate's isLimitAmount must be " + rateCatalog.isLimitAmount();
                    logger.error(errMsg);
                    throw new InvalidRateException(errMsg);
                }
            }

            BigDecimal minAmount = rateCatalog.getMinAmount();
            BigDecimal maxAmount = rateCatalog.getMaxAmount();

            if (minAmount != null && minAmount.compareTo(defaultRateAmount.getAmount()) > 0) {
                String errMsg = "Default Rate amount cannot be less than the lower bound amount";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }

            if (maxAmount != null && maxAmount.compareTo(defaultRateAmount.getAmount()) < 0) {
                String errMsg = "Default Rate amount cannot be greater than the upper bound amount";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }

            if (!rateCatalog.isRecognitionDateDefinable() && rate.getRecognitionDate() != null) {
                String errMsg = "Rate's recognition date is not definable and must not be set";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }

            Set<KeyPair> catalogKeyPairs = rateCatalog.getKeyPairs();

            if (CollectionUtils.isNotEmpty(catalogKeyPairs)) {

                Set<KeyPair> rateKeyPairs = rate.getKeyPairs();

                if (CollectionUtils.isEmpty(rateKeyPairs)) {
                    String errMsg = "Rate's set of key pairs cannot be empty";
                    logger.error(errMsg);
                    throw new InvalidRateException(errMsg);
                }

                if (!rateKeyPairs.containsAll(catalogKeyPairs)) {
                    String errMsg = "Rate's set of key pairs must be a sub-collection of RateCatalog's set of key pairs";
                    logger.error(errMsg);
                    throw new InvalidRateException(errMsg);
                }

                if (rateCatalog.isKeyPairFinal() && catalogKeyPairs.size() != rateKeyPairs.size()) {
                    String errMsg = "Rate's and RateCatalog's sets of key pairs must be the same";
                    logger.error(errMsg);
                    throw new InvalidRateException(errMsg);
                }

            }

        }
    }


    /**
     * Checks if the rate type is valid.
     *
     * @param rateType RateType instance
     * @return true if the rate is valid, otherwise false
     */
    @Override
    @PermissionsAllowed(Permission.VALIDATE_RATE_TYPE)
    public boolean isRateTypeValid(RateType rateType) {
        try {
            validateRateType(rateType);
            return true;
        } catch (InvalidRateTypeException e) {
            return false;
        }
    }


    /**
     * Checks if the rate is valid.
     *
     * @param rate Rate instance
     * @return true if the rate is valid, otherwise false
     */
    @Override
    @PermissionsAllowed(Permission.VALIDATE_RATE)
    public boolean isRateValid(Rate rate) {
        try {
            validateRate(rate);
            return true;
        } catch (InvalidRateException e) {
            return false;
        }
    }

    /**
     * Checks if the rate catalog is valid.
     *
     * @param rateCatalog RateCatalog instance
     * @return true if the rate catalog is valid, otherwise false
     */
    @Override
    @PermissionsAllowed(Permission.VALIDATE_RATE_CATALOG)
    public boolean isRateCatalogValid(RateCatalog rateCatalog) {
        try {
            validateRateCatalog(rateCatalog);
            return true;
        } catch (InvalidRateException e) {
            return false;
        } catch (InvalidRateCatalogException ce) {
            return false;
        }
    }

    /**
     * Checks if the rate is valid for the given rate catalog.
     *
     * @param rate        Rate instance
     * @param rateCatalog RateCatalog instance
     * @return true if the rate is valid, otherwise false
     */
    @Override
    @PermissionsAllowed({Permission.VALIDATE_RATE, Permission.VALIDATE_RATE_CATALOG})
    public boolean isRateValidWithCatalog(Rate rate, RateCatalog rateCatalog) {
        try {
            validateRateWithCatalog(rate, rateCatalog);
            return true;
        } catch (InvalidRateException e) {
            return false;
        } catch (InvalidRateCatalogException ce) {
            return false;
        }
    }

    /**
     * Assigns the array of ATP IDs to the rate catalog specified by ID.
     *
     * @param rateCatalogId RateCatalog ID
     * @param atpIds        Array of ATP IDs
     * @return RateCatalog instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_RATE_CATALOG)
    public RateCatalog assignAtpsToRateCatalog(Long rateCatalogId, String... atpIds) {

        // Trying to remove ATPs from the rate catalog first
        RateCatalog rateCatalog = removeAtpsFromRateCatalog(rateCatalogId, atpIds);

        // Retrieving rates by RateCatalog code and ATP IDs to see if there are other rates
        // with the same rate catalog code
        Query query = em.createQuery("select r from Rate r where r.rateCatalogAtp.id.code = :code and " +
                " r.rateCatalogAtp.id.atpId in (:atpIds) and r.rateCatalogAtp.rateCatalog.id <> :id");
        query.setParameter("code", rateCatalog.getCode());
        query.setParameter("atpIds", Arrays.asList(atpIds));
        query.setParameter("id", rateCatalog.getId());

        List<Rate> rates = query.getResultList();

        // If there are other dependent rates we have to throw an exception
        if (CollectionUtils.isNotEmpty(rates)) {
            String errMsg = "There are other rates with the same RateCatalog code = " + rateCatalog.getCode();
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        // Creating new RateCatalogAtp entities
        for (String atpId : atpIds) {

            if (!atpService.atpExists(atpId)) {
                String errMsg = "ATP with ID = " + atpId + " does not exist";
                logger.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }

            RateCatalogAtp rateCatalogAtp = new RateCatalogAtp();
            rateCatalogAtp.setId(new RateCatalogAtpId(rateCatalog.getCode(), atpId));
            rateCatalogAtp.setRateCatalog(rateCatalog);

            persistEntity(rateCatalogAtp);

        }

        return rateCatalog;
    }


    /**
     * Adds new and/or transfer the existing array of ATP IDs
     * from another rate catalog (if other rate catalogs use them) the to the rate catalog specified by ID.
     *
     * @param rateCatalogId RateCatalog ID
     * @param atpIds        Array of ATP IDs
     * @return RateCatalog instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed({Permission.UPDATE_RATE_CATALOG, Permission.UPDATE_RATE})
    public RateCatalog transferAtpsToRateCatalog(Long rateCatalogId, String... atpIds) {

        RateCatalog rateCatalog = getRateCatalog(rateCatalogId);

        if (rateCatalog == null) {
            String errMsg = "RateCatalog with ID = " + rateCatalogId + " does not exist";
            logger.error(errMsg);
            throw new InvalidRateCatalogException(errMsg);
        }

        Set<String> newAtpIds = new HashSet<String>(Arrays.asList(atpIds));

        // Retrieving rates by RateCatalog code and ATP IDs
        Query query = em.createQuery("select r from Rate r where r.rateCatalogAtp.id.code = :code and " +
                " r.rateCatalogAtp.id.atpId in (:atpIds) and r.rateCatalogAtp.rateCatalog.id <> :id");
        query.setParameter("code", rateCatalog.getCode());
        query.setParameter("atpIds", newAtpIds);
        query.setParameter("id", rateCatalog.getId());

        List<Rate> rates = query.getResultList();

        // Validating each rate with the rate catalog
        if (CollectionUtils.isNotEmpty(rates)) {
            for (Rate rate : rates) {
                validateRateWithCatalog(rate, rateCatalog);
            }
        }

        // Retrieving the existing ATP IDs to preserve them while adding the new ones
        Set<String> oldAtpIds = getAtpsForRateCatalog(rateCatalog.getId());

        // Creating and persisting the new RateCatalogAtp instances the ATP IDs and rate catalog ID
        for (String atpId : newAtpIds) {

            if (!oldAtpIds.contains(atpId)) {

                if (!atpService.atpExists(atpId)) {
                    String errMsg = "ATP with ID = " + atpId + " does not exist";
                    logger.error(errMsg);
                    throw new IllegalArgumentException(errMsg);
                }

                RateCatalogAtp rateCatalogAtp = new RateCatalogAtp();
                rateCatalogAtp.setId(new RateCatalogAtpId(rateCatalog.getCode(), atpId));
                rateCatalogAtp.setRateCatalog(rateCatalog);

                // Updating the existing or creating a new association between RateCatalog and ATPs
                persistEntity(rateCatalogAtp);
            }
        }

        return rateCatalog;
    }

    /**
     * Remove the ATP IDs from the rate catalog specified by ID.
     *
     * @param rateCatalogId RateCatalog ID
     * @param atpIds        Array of ATP IDs to be removed
     * @return RateCatalog instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_RATE_CATALOG)
    public RateCatalog removeAtpsFromRateCatalog(Long rateCatalogId, String... atpIds) {

        RateCatalog rateCatalog = getRateCatalog(rateCatalogId);

        if (rateCatalog == null) {
            String errMsg = "RateCatalog with ID = " + rateCatalogId + " does not exist";
            logger.error(errMsg);
            throw new InvalidRateCatalogException(errMsg);
        }

        // Checking if there are any rates that use these ATPs
        Query query = em.createQuery("select 1 from Rate where rateCatalogAtp.id.code = :code and " +
                " rateCatalogAtp.id.atpId in (:atpIds)");
        query.setParameter("code", rateCatalog.getCode());
        query.setParameter("atpIds", Arrays.asList(atpIds));
        query.setMaxResults(1);

        if (CollectionUtils.isNotEmpty(query.getResultList())) {
            String errMsg = "Cannot assign ATPs to a rate catalog with code '" + rateCatalog.getCode() +
                    "' because they are being used by other rates";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        // Removing ATPs from rate catalogs with the same code
        query = em.createQuery("delete from RateCatalogAtp where id.code = :code and id.atpId in (:atpIds)");
        query.setParameter("code", rateCatalog.getCode());
        query.setParameter("atpIds", Arrays.asList(atpIds));
        query.executeUpdate();

        return rateCatalog;
    }


    /**
     * Adds a new key pair to the rate catalog specified by ID.
     *
     * @param key           KeyPair key
     * @param value         KeyPair value
     * @param rateCatalogId RateCatalog ID
     * @return RateCatalog instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed({Permission.UPDATE_RATE_CATALOG, Permission.UPDATE_RATE})
    public RateCatalog addKeyPairToRateCatalog(String key, String value, Long rateCatalogId) {

        RateCatalog rateCatalog = getRateCatalog(rateCatalogId);

        if (rateCatalog == null) {
            String errMsg = "RateCatalog with ID = " + rateCatalogId + " does not exist";
            logger.error(errMsg);
            throw new InvalidRateCatalogException(errMsg);
        }

        KeyPair newKeyPair = new KeyPair(key, value);

        Set<KeyPair> catalogKeyPairs = rateCatalog.getKeyPairs();

        persistEntity(newKeyPair);

        if (catalogKeyPairs == null) {
            catalogKeyPairs = new HashSet<KeyPair>();
        }

        catalogKeyPairs.add(newKeyPair);

        rateCatalog.setKeyPairs(catalogKeyPairs);

        // Adding the key pair to all dependent rates
        List<Rate> rates = getRatesByCatalogId(rateCatalogId);
        if (CollectionUtils.isNotEmpty(rates)) {
            for (Rate rate : rates) {
                addKeyPairToRate(key, value, rate.getId(), false);
            }
        }

        return rateCatalog;
    }

    /**
     * Removes the key pair from the rate catalog specified by ID.
     *
     * @param key           KeyPair key
     * @param rateCatalogId RateCatalog ID
     * @return RateCatalog instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed({Permission.UPDATE_RATE_CATALOG, Permission.UPDATE_RATE})
    public RateCatalog removeKeyPairFromRateCatalog(String key, Long rateCatalogId) {

        RateCatalog rateCatalog = getRateCatalog(rateCatalogId);

        if (rateCatalog == null) {
            String errMsg = "RateCatalog with ID = " + rateCatalogId + " does not exist";
            logger.error(errMsg);
            throw new InvalidRateCatalogException(errMsg);
        }

        Set<KeyPair> catalogKeyPairs = rateCatalog.getKeyPairs();

        if (CollectionUtils.isNotEmpty(catalogKeyPairs)) {
            for (KeyPair keyPair : new HashSet<KeyPair>(catalogKeyPairs)) {
                if (keyPair.getKey().equals(key)) {
                    catalogKeyPairs.remove(keyPair);
                    em.flush();
                    deleteEntity(keyPair.getId(), KeyPair.class);
                }
            }
        }

        // Removing the key pair from all dependent rates
        List<Rate> rates = getRatesByCatalogId(rateCatalogId);
        if (CollectionUtils.isNotEmpty(rates)) {
            for (Rate rate : rates) {
                removeKeyPairFromRate(key, rate.getId(), false);
            }
        }

        return rateCatalog;
    }

    protected Rate addKeyPairToRate(String key, String value, Long rateId, boolean checkRestrictions) {

        if (checkRestrictions) {

            RateCatalog rateCatalog = getRateCatalogByRateId(rateId);
            if (rateCatalog == null) {
                String errMsg = "Rate (ID=" + rateId + ") is not associated with any RateCatalog";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }

            if (rateCatalog.isKeyPairFinal()) {
                String errMsg = "Rate's set of key pairs cannot be modified";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }
        }

        Rate rate = getRate(rateId);

        if (rate == null) {
            String errMsg = "Rate with ID = " + rateId + " does not exist";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        KeyPair newKeyPair = new KeyPair(key, value);

        Set<KeyPair> rateKeyPairs = rate.getKeyPairs();

        persistEntity(newKeyPair);

        if (rateKeyPairs == null) {
            rateKeyPairs = new HashSet<KeyPair>();
        }

        rateKeyPairs.add(newKeyPair);

        rate.setKeyPairs(rateKeyPairs);

        return rate;

    }

    protected Rate removeKeyPairFromRate(String key, Long rateId, boolean checkRestrictions) {

        Rate rate = getRate(rateId);

        if (rate == null) {
            String errMsg = "Rate with ID = " + rateId + " does not exist";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        Set<KeyPair> rateKeyPairs = rate.getKeyPairs();

        boolean keyPairExists = false;
        if (CollectionUtils.isNotEmpty(rateKeyPairs)) {
            for (KeyPair keyPair : new HashSet<KeyPair>(rateKeyPairs)) {
                if (keyPair.getKey().equals(key)) {
                    rateKeyPairs.remove(keyPair);
                    em.flush();
                    deleteEntity(keyPair.getId(), KeyPair.class);
                    if (!keyPairExists) {
                        keyPairExists = true;
                    }
                }
            }
        }

        if (checkRestrictions) {

            if (!keyPairExists) {
                String errMsg = "KeyPair (key=" + key + ") does not exist in Rate (ID=" + rateId + ")";
                logger.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }

            RateCatalog rateCatalog = getRateCatalogByRateId(rateId);
            if (rateCatalog == null) {
                String errMsg = "Rate (ID=" + rateId + ") is not associated with any RateCatalog";
                logger.error(errMsg);
                throw new InvalidRateException(errMsg);
            }

            if (rateCatalog.isKeyPairFinal()) {
                String errMsg = "Rate's set of key pairs cannot be modified";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }
        }

        return rate;
    }

    /**
     * Adds a new key pair to the rate specified by ID.
     *
     * @param key    KeyPair key
     * @param value  KeyPair value
     * @param rateId Rate ID
     * @return Rate instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_RATE)
    public Rate addKeyPairToRate(String key, String value, Long rateId) {
        return addKeyPairToRate(key, value, rateId, true);
    }

    /**
     * Removes the key pair from the rate specified by ID.
     *
     * @param key    KeyPair key
     * @param rateId Rate ID
     * @return Rate instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_RATE)
    public Rate removeKeyPairFromRate(String key, Long rateId) {
        return removeKeyPairFromRate(key, rateId, true);
    }

    /**
     * Adds new amounts to the rate specified by ID
     *
     * @param rateId      Rate ID
     * @param rateAmounts list of RateAmount instances
     * @return Rate instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_RATE)
    public Rate addAmountsToRate(Long rateId, List<RateAmount> rateAmounts) {

        Rate rate = getRate(rateId);

        if (rate == null) {
            String errMsg = "Rate with ID = " + rateId + " does not exist";
            logger.error(errMsg);
            throw new InvalidRateException(errMsg);
        }

        Set<RateAmount> newRateAmounts = new HashSet<RateAmount>(rateAmounts);

        // Persisting new rate amounts
        for (RateAmount rateAmount : newRateAmounts) {
            persistEntity(rateAmount);
            rateAmount.setRate(rate);
        }

        // Merging old and new rate amounts
        Set<RateAmount> oldRateAmounts = rate.getRateAmounts();
        if (CollectionUtils.isNotEmpty(oldRateAmounts)) {
            newRateAmounts.addAll(oldRateAmounts);
        }

        rate.setRateAmounts(newRateAmounts);

        validateRate(rate);

        return rate;
    }

    /**
     * Removes the rate amount from the persistent store by ID.
     *
     * @param rateAmountId RateAmount ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_RATE_AMOUNT)
    public void deleteRateAmount(Long rateAmountId) {

        RateAmount rateAmount = getEntity(rateAmountId, RateAmount.class);
        if (rateAmount == null) {
            String errMsg = "RateAmount with ID = " + rateAmountId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Rate rate = rateAmount.getRate();
        if (rate == null) {
            String errMsg = "RateAmount (ID=" + rateAmountId + ") does not have any rates associated with it";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        RateAmount defaultRateAmount = rate.getDefaultRateAmount();
        if (defaultRateAmount != null && defaultRateAmount.getId().equals(rateAmountId)) {
            String errMsg = "Default RateAmount (ID=" + rateAmountId + ") cannot be deleted";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        Set<RateAmount> rateAmounts = rate.getRateAmounts();
        if (CollectionUtils.isNotEmpty(rateAmounts)) {
            for (RateAmount amount : new HashSet<RateAmount>(rateAmounts)) {
                if (amount.getId().equals(rateAmountId)) {
                    rateAmounts.remove(amount);
                }
            }
        } else {
            deleteEntity(rateAmountId, RateAmount.class);
        }

    }

    /**
     * Returns a list of Rate sub-codes by Rate code and ATP ID
     *
     * @param rateCode Rate code
     * @param atpId    ATP ID
     * @return list of Rate sub-codes
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public List<String> getRateSubCodes(String rateCode, String atpId) {

        Query query = em.createQuery("select subCode from Rate where code = :rateCode and atpId = :atpId");

        query.setParameter("rateCode", rateCode);
        query.setParameter("atpId", atpId);

        List<String> subCodes = query.getResultList();

        if (CollectionUtils.isNotEmpty(subCodes)) {
            Collections.sort(subCodes);
        }

        return subCodes;
    }


    /**************************************************************
     *
     * Rate Interpretation methods.
     *
     **************************************************************/

    /**
     * Calculates the effective date of a Rate with the given ID.
     * Uses the provided based date to compare to the transaction date.
     *
     * @param rateId   ID of a Rate, which effective date is to be found.
     * @param baseDate Date used to compare to the transaction date.
     * @return Effective date from the Rate with the given ID.
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public Date getEffectiveDateFromRate(Long rateId, Date baseDate) {

        // Get the Rate:
        Rate rate = getRate(rateId);

        if (rate == null) {
            String errorMsg = String.format("Invalid Rate ID %d in getEffectiveDateFromRate. Rate cannot be found.", rateId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Check the value of "overrideTransactionDateType"
        TransactionDateType transactionDateType =
                (rate.getRateCatalogAtp() != null) && (rate.getRateCatalogAtp().getRateCatalog() != null)
                        ? rate.getRateCatalogAtp().getRateCatalog().getTransactionDateType() : null;

        if (transactionDateType != null) {
            switch (transactionDateType) {
                case ALWAYS:
                    return rate.getTransactionDate();
                case UNTIL:
                    return isAfter(baseDate, rate.getTransactionDate()) ? baseDate : rate.getTransactionDate();
                case AFTER:
                    return isAfter(baseDate, rate.getTransactionDate()) ? rate.getTransactionDate() : baseDate;
            }
        }

        return baseDate;
    }

    /**
     * Calculates the recognition date of a Rate with the given ID.
     * Uses the provided base date to supply as a default value in case all else fails.
     *
     * @param rateId   ID of a Rate which recognition date is to be found.
     * @param baseDate Date used as a default value if all else fail.
     * @return Recognition date from the Rate with the given ID.
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public Date getRecognitionDateFromRate(Long rateId, Date baseDate) {

        // Get the Rate:
        Rate rate = getRate(rateId);

        if (rate == null) {
            String errorMsg = String.format("Invalid Rate ID %d in getRecognitionDateFromRate. Rate cannot be found.", rateId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        Date recognitionDate = null;

        // Check the value of the "isRecognitionDateDefinable":
        if ((rate.getRateCatalogAtp() != null) && (rate.getRateCatalogAtp().getRateCatalog() != null)
                && BooleanUtils.isTrue(rate.getRateCatalogAtp().getRateCatalog().isRecognitionDateDefinable())) {

            // Get the Recognition Date from Rate:
            recognitionDate = rate.getRecognitionDate();
        }

        // If Recognition Date wasn't found, get the Effective Date:
        if (recognitionDate == null) {
            recognitionDate = getEffectiveDateFromRate(rateId, baseDate);
        }

        return (recognitionDate != null) ? recognitionDate : baseDate;
    }

    /**
     * Calculates the total amount of the Rate with the given ID.
     * Uses the number of units to either locate a flexible rate or calculate a fixed rate.
     *
     * @param rateId   ID of a Rate which total amount is to be calculated.
     * @param numUnits Number of units.
     * @return The total amount from the Rate with the given ID.
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public BigDecimal getAmountFromRate(Long rateId, int numUnits) {

        // Get the Rate:
        Rate rate = getRate(rateId);

        if (rate == null) {
            String errorMsg = String.format("Invalid Rate ID %d in getAmountFromRate. Rate cannot be found.", rateId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Check the rate type and determine how to get the amount:
        RateType rateType = rate.getRateType();

        if (rateType != null && numUnits > 0) {

            switch (rateType.getRateAmountType()) {

                case FLEXIBLE:
                    RateAmount rateAmount = getRateAmount(rate, numUnits);
                    return (rateAmount != null) ? rateAmount.getAmount() : BigDecimal.ZERO;

                case FIXED:
                    return calculateFixedRateAmount(rate, numUnits);
            }
        }

        return (rate.getDefaultRateAmount() != null) ? rate.getDefaultRateAmount().getAmount() : BigDecimal.ZERO;
    }

    /**
     * Finds out the transaction type ID from RateAmount associated with a Rate
     * with the given ID and the number of units.
     *
     * @param rateId   ID of a Rate which associated amount's transaction type is to be found.
     * @param numUnits Number of units.
     * @return Transaction Type from the RateAmount.
     */
    @Override
    @PermissionsAllowed(Permission.READ_RATE)
    public String getTransactionTypeIdFromRate(Long rateId, int numUnits) {

        // Get the Rate:
        Rate rate = getRate(rateId);

        if (rate == null) {
            String errorMsg = String.format("Invalid Rate ID %d in getTransactionTypeFromRate. Rate cannot be found.", rateId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Get the matching RateAmount:
        RateAmount rateAmount = (numUnits > 0) ? getRateAmount(rate, numUnits) : rate.getDefaultRateAmount();

        return (rateAmount != null) ? rateAmount.getTransactionTypeId() : null;
    }

    /**
     * Returns a RateAmount from the given Rate that matches the given number of units.
     * Returns the Rate's default amount if none can be matched by the number of units.
     * Returns <code>null</code> if no such Rate is found.
     *
     * @param rate     Rate which Rate Amount is to be found if any.
     * @param numUnits Number of units to match.
     * @return RateAmount instance
     */
    private RateAmount getRateAmount(Rate rate, int numUnits) {

        // Loop through the RateAmounts:
        if (CollectionUtils.isNotEmpty(rate.getRateAmounts())) {

            for (RateAmount rateAmount : rate.getRateAmounts()) {

                // Compare the number of units to locate a match:
                if ((rateAmount.getUnits() != null) && (rateAmount.getUnits() == numUnits)) {

                    // Match found:
                    return rateAmount;
                }
            }
        }

        // Default to the Rate's default RateAmount:
        return rate.getDefaultRateAmount();
    }

    /**
     * Calculates a Fixed rate amount using the provided Rate and number of units.
     *
     * @param rate     Rate for which to calculate a Fixed rate amount.
     * @param numUnits Number of units used for calculations.
     * @return Fixed rate amount for the given Rate.
     */
    private BigDecimal calculateFixedRateAmount(Rate rate, int numUnits) {

        // Get the default amount:
        RateAmount rateAmount = rate.getDefaultRateAmount();

        BigDecimal fixedRateAmount = null;

        if ((rateAmount != null) && (rateAmount.getAmount() != null)) {

            // Check if the Rate amount must be limited:
            if (rate.isLimitAmount()) {

                // Get the limit range or assign default values:
                int minUnits = (rate.getMinLimitUnits() != null) ? rate.getMinLimitUnits() : 0;
                int maxUnits = (rate.getMaxLimitUnits() != null) ? rate.getMaxLimitUnits() : Integer.MAX_VALUE;

                if (numUnits > maxUnits) {

                    // Calculate the amount:
                    fixedRateAmount = rateAmount.getAmount().multiply(new BigDecimal(numUnits - maxUnits));
                    fixedRateAmount = (rate.getLimitAmountValue() != null) ?
                            fixedRateAmount.add(rate.getLimitAmountValue()) : fixedRateAmount;
                } else if (numUnits >= minUnits) {

                    // Use the limit amount:
                    fixedRateAmount = rate.getLimitAmountValue();
                }
            }

            // Calculate the default fixed rate amount if it hasn't been assigned
            // by multiplying the default amount by the number of units:
            if (fixedRateAmount == null) {
                fixedRateAmount = rateAmount.getAmount().multiply(new BigDecimal(numUnits));
            }
        }

        return (fixedRateAmount != null) ? fixedRateAmount : BigDecimal.ZERO;
    }
}
