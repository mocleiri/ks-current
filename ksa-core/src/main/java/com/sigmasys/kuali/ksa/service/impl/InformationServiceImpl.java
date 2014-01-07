package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.exception.ConfigurationException;
import com.sigmasys.kuali.ksa.exception.InformationNotFoundException;
import com.sigmasys.kuali.ksa.exception.TransactionNotFoundException;
import com.sigmasys.kuali.ksa.exception.UserNotFoundException;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.InformationService;
import com.sigmasys.kuali.ksa.service.TransactionService;
import com.sigmasys.kuali.ksa.service.security.PermissionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Information service JPA implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("informationService")
@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class InformationServiceImpl extends GenericPersistenceService implements InformationService {


    private static final Log logger = LogFactory.getLog(InformationServiceImpl.class);


    private static enum InformationPermission {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        EXPIRE
    }

    private static final String GET_INFORMATION_JOIN =
            " left outer join fetch i.account a " +
                    " left outer join fetch i.accessLevel al " +
                    " left outer join fetch i.transaction t ";

    private static final String GET_FLAG_TYPE_SELECT =
            "select ft from FlagType ft " +
                    " left outer join fetch ft.accessLevel ";


    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuditableEntityService auditableEntityService;


    private <T extends Information> T getInformation(Long id, Class<T> entityType) {

        Query query = em.createQuery("select i from " + entityType.getName() + " i " + GET_INFORMATION_JOIN +
                " where i.id = :id ");

        query.setParameter("id", id);

        List<T> entities = query.getResultList();

        T entity = null;
        if (CollectionUtils.isNotEmpty(entities)) {
            entity = entities.get(0);
            checkReadPermission(entity.getAccessLevel());
        }

        return entity;
    }

    private <T extends Information> List<T> getInformationEntities(String userId, Class<T> entityType) {

        Query query = em.createQuery("select i from " + entityType.getName() + " i " + GET_INFORMATION_JOIN +
                (userId != null ? " where a.id = :userId " : "") +
                " order by i.creationDate desc, i.effectiveDate");

        if (userId != null) {
            query.setParameter("userId", userId);
        }

        List<T> entities = query.getResultList();
        if (CollectionUtils.isNotEmpty(entities)) {
            for (T entity : entities) {
                checkReadPermission(entity.getAccessLevel());
            }
        }

        return entities;
    }


    /**
     * Returns Information by ID
     *
     * @param id Information ID
     * @return Information instance
     */
    @Override
    public Information getInformation(Long id) {
        return getInformation(id, Information.class);
    }

    /**
     * Returns Memo by ID
     *
     * @param id Memo ID
     * @return Memo instance
     */
    @Override
    public Memo getMemo(Long id) {
        return getInformation(id, Memo.class);
    }

    /**
     * Returns Alert by ID
     *
     * @param id Alert ID
     * @return Alert instance
     */
    @Override
    public Alert getAlert(Long id) {
        return getInformation(id, Alert.class);
    }

    /**
     * Returns Flag by ID
     *
     * @param id Flag ID
     * @return Flag instance
     */
    @Override
    public Flag getFlag(Long id) {
        return getInformation(id, Flag.class);
    }


    /**
     * Returns all Information instances sorted by ID in the descendant order
     *
     * @return List of Information instances
     */
    @Override
    public List<Information> getInformationEntities() {
        return getInformationEntities(null, Information.class);
    }

    /**
     * Returns all Memo entities sorted by ID in the descendant order
     *
     * @return List of memos
     */
    @Override
    public List<Memo> getMemos() {
        return getInformationEntities(null, Memo.class);
    }

    /**
     * Returns all Flag entities sorted by ID in the descendant order
     *
     * @return List of flags
     */
    public List<Flag> getFlags() {
        return getInformationEntities(null, Flag.class);
    }

    /**
     * Returns all Alert entities sorted by ID in the descendant order
     *
     * @return List of alerts
     */
    public List<Alert> getAlerts() {
        return getInformationEntities(null, Alert.class);
    }

    /**
     * Returns all Alert entities by Account ID
     *
     * @param userId Account ID
     * @return List of memos
     */
    @Override
    public List<Alert> getAlerts(String userId) {
        return getInformationEntities(userId, Alert.class);
    }

    /**
     * Returns all Flag entities by Account ID
     *
     * @param userId Account ID
     * @return List of flags
     */
    @Override
    public List<Flag> getFlags(String userId) {
        return getInformationEntities(userId, Flag.class);
    }

    /**
     * Returns all Memo entities by Account ID
     *
     * @param userId Account ID
     * @return List of memos
     */
    @Override
    public List<Memo> getMemos(String userId) {
        return getInformationEntities(userId, Memo.class);
    }

    /**
     * Returns all Memo entities by Transaction ID
     *
     * @param transactionId Transaction ID
     * @return List of memos for the given transaction
     */
    @Override
    public List<Memo> getMemos(Long transactionId) {

        Query query = em.createQuery("select i from Memo i " + GET_INFORMATION_JOIN +
                " left outer join fetch i.nextMemo nm " +
                " left outer join fetch i.previousMemo pm " +
                " where i.transaction.id = :transactionId order by " +
                " i.creationDate desc, i.effectiveDate desc");

        query.setParameter("transactionId", transactionId);

        List<Memo> memos = query.getResultList();
        if (CollectionUtils.isNotEmpty(memos)) {
            for (Memo memo : memos) {
                checkReadPermission(memo.getAccessLevel());
            }
        }

        return memos;
    }

    /**
     * Persists Information in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param information Information instance
     * @return Information ID
     */
    @Override
    @Transactional(readOnly = false)
    public Long persistInformation(Information information) {

        checkUpdatePermission(information.getAccessLevel());

        String userId = userSessionManager.getUserId();
        Date currentDate = new Date();

        if (information.getId() != null) {
            information.setEditorId(userId);
            information.setLastUpdate(currentDate);
        } else {
            information.setCreatorId(userId);
            information.setCreationDate(currentDate);
        }

        return persistEntity(information);
    }

    /**
     * Removes Information from the database.
     *
     * @param id Information ID
     * @return true if Information entity has been deleted
     */
    @Override
    @Transactional(readOnly = false)
    public boolean deleteInformation(Long id) {

        Information entity = getInformation(id);
        if (entity == null) {
            String errMsg = "Information with ID = " + id + " does not exist";
            logger.error(errMsg);
            throw new InformationNotFoundException(errMsg);
        }

        checkDeletePermission(entity.getAccessLevel());

        return deleteEntity(id, Information.class);
    }

    /**
     * Creates a new memo based on the given parameters
     *
     * @param transactionId   Transaction ID
     * @param memoText        Memo text
     * @param accessLevelCode InformationAccessLevel code
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @param prevMemoId      Previous Memo ID    @return new Memo instance
     */
    @Override
    @Transactional(readOnly = false)
    public Memo createMemo(Long transactionId, String memoText, String accessLevelCode,
                           Date effectiveDate, Date expirationDate, Long prevMemoId) {

        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Memo memo = createMemo(transaction.getAccount().getId(), memoText, accessLevelCode,
                effectiveDate, expirationDate, prevMemoId);

        memo.setTransaction(transaction);

        return memo;

    }

    /**
     * Creates a memo with the default access level code.
     *
     * @param transactionId  Transaction ID
     * @param memoText       Memo text
     * @param effectiveDate  Effective date
     * @param expirationDate Expiration date
     * @param prevMemoId     Previous Memo ID
     * @return new Memo instance
     */
    @Override
    @Transactional(readOnly = false)
    public Memo createMemo(Long transactionId, String memoText, Date effectiveDate, Date expirationDate, Long prevMemoId) {

        String accessLevelCode = getDefaultMemoAccessLevelCode();
        if (StringUtils.isBlank(accessLevelCode)) {
            String errMsg = "Memo's default access level code is not set";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }

        return createMemo(transactionId, memoText, accessLevelCode, effectiveDate, expirationDate, prevMemoId);
    }

    /**
     * Creates a memo with the default access level code for the given account.
     *
     * @param accountId      Account ID
     * @param memoText       Memo text
     * @param effectiveDate  Effective date
     * @param expirationDate Expiration date
     * @param prevMemoId     Previous Memo ID
     * @return new Memo instance
     */
    @Override
    @Transactional(readOnly = false)
    public Memo createMemo(String accountId, String memoText, Date effectiveDate, Date expirationDate, Long prevMemoId) {

        String accessLevelCode = getDefaultMemoAccessLevelCode();
        if (StringUtils.isBlank(accessLevelCode)) {
            String errMsg = "Memo's default access level code is not set";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }

        return createMemo(accountId, memoText, accessLevelCode, effectiveDate, expirationDate, prevMemoId);
    }

    /**
     * Creates a new memo based on the given parameters
     *
     * @param accountId       Account ID
     * @param memoText        Memo text
     * @param accessLevelCode InformationAccessLevel code
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @param prevMemoId      Previous Memo ID    @return new Memo instance
     */
    @Override
    @Transactional(readOnly = false)
    public Memo createMemo(String accountId, String memoText, String accessLevelCode,
                           Date effectiveDate, Date expirationDate, Long prevMemoId) {

        if (accountId == null) {
            String errMsg = "Account ID cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Account account = accountService.getFullAccount(accountId);
        if (account == null) {
            String errMsg = "Account with ID = " + accountId + " does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        InformationAccessLevel accessLevel = getInformationAccessLevel(accessLevelCode);
        if (accessLevel == null) {
            String errMsg = "InformationAccessLevel with code = " + accessLevelCode + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        checkCreatePermission(accessLevel);

        Memo newMemo = new Memo();
        newMemo.setText(memoText);
        newMemo.setAccount(account);
        newMemo.setAccessLevel(accessLevel);

        Memo prevMemo = null;
        if (prevMemoId != null) {
            prevMemo = getMemo(prevMemoId);
            if (prevMemo == null) {
                String errMsg = "Memo with ID = " + prevMemoId + " does not exist";
                logger.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }
            newMemo.setPreviousMemo(prevMemo);
            prevMemo.setNextMemo(newMemo);
        }

        newMemo.setCreatorId(userSessionManager.getUserId());

        Date curDate = new Date();
        newMemo.setCreationDate(curDate);
        newMemo.setEffectiveDate(effectiveDate != null ? effectiveDate : curDate);
        newMemo.setExpirationDate(expirationDate);

        persistEntity(newMemo);

        if (prevMemo != null) {
            prevMemo.setNextMemo(newMemo);
        }

        return newMemo;
    }

    /**
     * Creates a new flag based on the given parameters
     *
     * @param transactionId   Transaction ID
     * @param flagTypeId      Flag Type ID
     * @param accessLevelCode InformationAccessLevel code
     * @param severity        Severity
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date    @return new Flag instance
     */
    @Override
    @Transactional(readOnly = false)
    public Flag createFlag(Long transactionId, Long flagTypeId, String accessLevelCode,
                           Integer severity, Date effectiveDate, Date expirationDate) {

        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Flag flag = createFlag(transaction.getAccount().getId(), flagTypeId, accessLevelCode, severity,
                effectiveDate, expirationDate);

        flag.setTransaction(transaction);

        return flag;

    }

    /**
     * Creates a new flag based on the given parameters
     *
     * @param accountId       Account ID
     * @param flagTypeId      Flag Type ID
     * @param accessLevelCode InformationAccessLevel code
     * @param severity        Severity
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date    @return new Flag instance
     */
    @Override
    @Transactional(readOnly = false)
    public Flag createFlag(String accountId, Long flagTypeId, String accessLevelCode,
                           Integer severity, Date effectiveDate, Date expirationDate) {
        return createFlag(accountId, getFlagType(flagTypeId), accessLevelCode, severity, effectiveDate, expirationDate);
    }

    /**
     * Creates a new flag based on the given parameters
     *
     * @param accountId       Account ID
     * @param flagTypeCode    Flag Type code
     * @param accessLevelCode InformationAccessLevel code
     * @param severity        Severity
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @return new Flag instance
     */
    @Override
    @Transactional(readOnly = false)
    public Flag createFlag(String accountId, String flagTypeCode, String accessLevelCode,
                           Integer severity, Date effectiveDate, Date expirationDate) {
        return createFlag(accountId, getFlagType(flagTypeCode), accessLevelCode, severity, effectiveDate, expirationDate);
    }

    protected Flag createFlag(String accountId, FlagType flagType, String accessLevelCode,
                              Integer severity, Date effectiveDate, Date expirationDate) {

        if (accountId == null) {
            String errMsg = "Account ID cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Account account = accountService.getFullAccount(accountId);
        if (account == null) {
            String errMsg = "Account with ID = " + accountId + " does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        InformationAccessLevel accessLevel = getInformationAccessLevel(accessLevelCode);
        if (accessLevel == null) {
            String errMsg = "InformationAccessLevel with code = " + accessLevelCode + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        checkCreatePermission(accessLevel);

        Flag flag = new Flag();
        flag.setType(flagType);
        flag.setAccount(account);
        flag.setAccessLevel(accessLevel);
        flag.setSeverity(severity);

        flag.setCreatorId(userSessionManager.getUserId());

        Date curDate = new Date();
        flag.setCreationDate(curDate);
        flag.setEffectiveDate(effectiveDate != null ? effectiveDate : curDate);
        flag.setExpirationDate(expirationDate);

        persistEntity(flag);

        return flag;
    }


    /**
     * Creates a new alert based on the given parameters
     *
     * @param transactionId   Transaction ID
     * @param alertText       Alert text
     * @param accessLevelCode InformationAccessLevel code
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date   @return new Alert instance
     */
    @Override
    @Transactional(readOnly = false)
    public Alert createAlert(Long transactionId, String alertText, String accessLevelCode, Date effectiveDate,
                             Date expirationDate) {

        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Alert alert = createAlert(transaction.getAccount().getId(), alertText, accessLevelCode,
                effectiveDate, expirationDate);

        alert.setTransaction(transaction);

        return alert;

    }

    /**
     * Creates a new alert based on the given parameters
     *
     * @param accountId       Account ID
     * @param alertText       Alert text
     * @param accessLevelCode InformationAccessLevel code
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @return new Alert instance
     */
    @Override
    @Transactional(readOnly = false)
    public Alert createAlert(String accountId, String alertText, String accessLevelCode, Date effectiveDate,
                             Date expirationDate) {

        if (accountId == null) {
            String errMsg = "Account ID cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Account account = accountService.getFullAccount(accountId);
        if (account == null) {
            String errMsg = "Account with ID = " + accountId + " does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        InformationAccessLevel accessLevel = getInformationAccessLevel(accessLevelCode);
        if (accessLevel == null) {
            String errMsg = "InformationAccessLevel with code = " + accessLevelCode + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        checkCreatePermission(accessLevel);

        Alert alert = new Alert();
        alert.setText(alertText);
        alert.setAccount(account);
        alert.setAccessLevel(accessLevel);

        alert.setCreatorId(userSessionManager.getUserId());

        Date curDate = new Date();
        alert.setCreationDate(curDate);
        alert.setEffectiveDate(effectiveDate != null ? effectiveDate : curDate);
        alert.setExpirationDate(expirationDate);

        persistEntity(alert);

        return alert;
    }

    /**
     * Creates a new FlagType based on the given parameters
     *
     * @param code            FlagType code
     * @param name            FlagType name
     * @param description     FlagType description
     * @param accessLevelCode InformationAccessLevel code
     * @return new FlagType instance
     */
    @Override
    @Transactional(readOnly = false)
    public FlagType createFlagType(String code, String name, String description, String accessLevelCode) {

        InformationAccessLevel accessLevel = getInformationAccessLevel(accessLevelCode);
        if (accessLevel == null) {
            String errMsg = "InformationAccessLevel with code = " + accessLevelCode + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        checkCreatePermission(accessLevel);

        FlagType flagType = new FlagType();
        flagType.setCode(code);
        flagType.setName(name);
        flagType.setDescription(description);
        flagType.setAccessLevel(accessLevel);

        auditableEntityService.persistAuditableEntity(flagType);

        return flagType;
    }

    /**
     * Persists FlagType instance in the persistent store.
     *
     * @param flagType FlagType instance to persist
     * @return FlagType ID
     */
    @Override
    @Transactional(readOnly = false)
    public Long persistFlagType(FlagType flagType) {
        checkUpdatePermission(flagType.getAccessLevel());
        return auditableEntityService.persistAuditableEntity(flagType);
    }

    /**
     * Removes FlagType entity from the persistent store.
     *
     * @param flagTypeId FlagType ID
     * @return true if FlagType entity has been deleted
     */
    @Override
    @Transactional(readOnly = false)
    public boolean deleteFlagType(Long flagTypeId) {

        FlagType flagType = getFlagType(flagTypeId);
        if (flagType == null) {
            String errMsg = "FlagType with ID = " + flagTypeId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        checkDeletePermission(flagType.getAccessLevel());

        return auditableEntityService.deleteAuditableEntity(flagTypeId, FlagType.class);
    }

    /**
     * Retrieves FlagType entity from the persistent store by ID.
     *
     * @param flagTypeId FlagType ID
     * @return FlagType instance
     */
    @Override
    public FlagType getFlagType(Long flagTypeId) {

        Query query = em.createQuery(GET_FLAG_TYPE_SELECT + " where ft.id = :id");

        query.setParameter("id", flagTypeId);

        List<FlagType> flagTypes = query.getResultList();
        if (CollectionUtils.isNotEmpty(flagTypes)) {
            FlagType flagType = flagTypes.get(0);
            checkReadPermission(flagType.getAccessLevel());
            return flagType;
        }

        return null;
    }

    /**
     * Retrieves FlagType entity from the persistent store by code.
     *
     * @param flagTypeCode FlagType code
     * @return FlagType instance
     */
    @Override
    public FlagType getFlagType(String flagTypeCode) {

        Query query = em.createQuery(GET_FLAG_TYPE_SELECT + " where ft.code = :code");

        query.setParameter("code", flagTypeCode);

        List<FlagType> flagTypes = query.getResultList();
        if (CollectionUtils.isNotEmpty(flagTypes)) {
            FlagType flagType = flagTypes.get(0);
            checkReadPermission(flagType.getAccessLevel());
            return flagType;
        }

        return null;
    }

    /**
     * Returns all existing flag types from the persistent store.
     *
     * @return list of FlagType instances
     */
    @Override
    public List<FlagType> getFlagTypes() {

        Query query = em.createQuery(GET_FLAG_TYPE_SELECT + " order by ft.code desc");

        List<FlagType> flagTypes = query.getResultList();
        if (CollectionUtils.isNotEmpty(flagTypes)) {
            for (FlagType flagType : flagTypes) {
                checkReadPermission(flagType.getAccessLevel());
            }
        }

        return flagTypes;
    }


    /**
     * Returns the default Memo's InformationAccessLevel code.
     *
     * @return InformationAccessLevel code
     */
    @Override
    @PermissionsAllowed(Permission.READ_ACCESS_LEVEL)
    public String getDefaultMemoLevel() {
        String defaultMemoLevel = configService.getParameter(Constants.DEFAULT_MEMO_LEVEL);
        if (StringUtils.isBlank(defaultMemoLevel)) {
            String errMsg = "Configuration parameter '" + Constants.DEFAULT_MEMO_LEVEL + "' is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }
        return defaultMemoLevel;
    }

    /**
     * Associates the information represented by Information ID with the given Account ID
     *
     * @param informationId Information ID
     * @param accountId     Account ID
     * @return an updated instance of Information
     */
    @Override
    @Transactional(readOnly = false)
    public Information associateWithAccount(Long informationId, String accountId) {

        Information information = getInformation(informationId);
        if (information == null) {
            String errMsg = "Information with ID = " + informationId + " does not exist";
            logger.error(errMsg);
            throw new InformationNotFoundException(errMsg);
        }

        Account account = accountService.getFullAccount(accountId);
        if (account == null) {
            String errMsg = "Account with ID = " + accountId + " does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        information.setAccount(account);

        checkUpdatePermission(information.getAccessLevel());

        return information;

    }

    /**
     * Associates the information represented by Information ID with the Transaction specified by ID
     *
     * @param informationId Information ID
     * @param transactionId Transaction ID
     * @return an updated instance of Information
     */
    @Override
    @Transactional(readOnly = false)
    public Information associateWithTransaction(Long informationId, Long transactionId) {

        Information information = getInformation(informationId);
        if (information == null) {
            String errMsg = "Information with ID = " + informationId + " does not exist";
            logger.error(errMsg);
            throw new InformationNotFoundException(errMsg);
        }

        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        information.setTransaction(transaction);

        checkUpdatePermission(information.getAccessLevel());

        return information;
    }


    /**
     * Returns InformationAccessLevel instance by code.
     *
     * @param code InformationAccessLevel code
     * @return InformationAccessLevel instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_ACCESS_LEVEL)
    public InformationAccessLevel getInformationAccessLevel(String code) {
        return auditableEntityService.getAuditableEntity(code, InformationAccessLevel.class);
    }

    /**
     * Returns InformationAccessLevel instance by ID.
     *
     * @param id InformationAccessLevel ID
     * @return InformationAccessLevel instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_ACCESS_LEVEL)
    public InformationAccessLevel getInformationAccessLevel(Long id) {
        return auditableEntityService.getAuditableEntity(id, InformationAccessLevel.class);
    }

    /**
     * Persists InformationAccessLevel instance in the persistent store.
     *
     * @param accessLevel InformationAccessLevel instance to persist
     * @return InformationAccessLevel ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_ACCESS_LEVEL)
    public Long persistInformationAccessLevel(InformationAccessLevel accessLevel) {
        return auditableEntityService.persistAuditableEntity(accessLevel);
    }


    /**
     * Creates and persists a new InformationAccessLevel instance.
     *
     * @param code             InformationAccessLevel code
     * @param name             InformationAccessLevel name
     * @param description      InformationAccessLevel description
     * @param createPermission InformationAccessLevel Create permission
     * @param readPermission   InformationAccessLevel Read permission
     * @param updatePermission InformationAccessLevel Update permission
     * @param deletePermission InformationAccessLevel Delete permission
     * @param expirePermission InformationAccessLevel Expire permission
     * @return InformationAccessLevel instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_ACCESS_LEVEL)
    public InformationAccessLevel createInformationAccessLevel(String code,
                                                               String name,
                                                               String description,
                                                               String createPermission,
                                                               String readPermission,
                                                               String updatePermission,
                                                               String deletePermission,
                                                               String expirePermission) {

        InformationAccessLevel accessLevel = new InformationAccessLevel();
        accessLevel.setCode(code);
        accessLevel.setName(name);
        accessLevel.setDescription(description);
        accessLevel.setCreatePermission(createPermission);
        accessLevel.setReadPermission(readPermission);
        accessLevel.setUpdatePermission(updatePermission);
        accessLevel.setDeletePermission(deletePermission);
        accessLevel.setExpirePermission(expirePermission);

        persistEntity(accessLevel);

        return accessLevel;
    }

    /**
     * Creates and persists a new InformationAccessLevel instance.
     *
     * @param code        InformationAccessLevel code
     * @param name        InformationAccessLevel name
     * @param description InformationAccessLevel description
     * @return InformationAccessLevel instance
     */
    @Override
    @PermissionsAllowed(Permission.CREATE_ACCESS_LEVEL)
    public InformationAccessLevel createMemoAccessLevel(String code, String name, String description) {
        return createInformationAccessLevel(code, name, description, Permission.CREATE_MEMO.name(),
                Permission.READ_MEMO.name(), Permission.UPDATE_MEMO.name(),
                Permission.DELETE_MEMO.name(), Permission.EXPIRE_MEMO.name());
    }

    /**
     * Creates and persists a new InformationAccessLevel instance.
     *
     * @param code        InformationAccessLevel code
     * @param name        InformationAccessLevel name
     * @param description InformationAccessLevel description
     * @return InformationAccessLevel instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_ACCESS_LEVEL)
    public InformationAccessLevel createAlertAccessLevel(String code, String name, String description) {
        return createInformationAccessLevel(code, name, description, Permission.CREATE_ALERT.name(),
                Permission.READ_ALERT.name(), Permission.UPDATE_ALERT.name(),
                Permission.DELETE_ALERT.name(), Permission.EXPIRE_ALERT.name());
    }

    /**
     * Creates and persists a new InformationAccessLevel instance.
     *
     * @param code        InformationAccessLevel code
     * @param name        InformationAccessLevel name
     * @param description InformationAccessLevel description
     * @return InformationAccessLevel instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_ACCESS_LEVEL)
    public InformationAccessLevel createFlagAccessLevel(String code, String name, String description) {
        return createInformationAccessLevel(code, name, description, Permission.CREATE_FLAG.name(),
                Permission.READ_FLAG.name(), Permission.UPDATE_FLAG.name(),
                Permission.DELETE_FLAG.name(), Permission.EXPIRE_FLAG.name());
    }

    /**
     * Removes InformationAccessLevel entity from the persistent store by ID.
     *
     * @param id InformationAccessLevel ID
     * @return true if InformationAccessLevel entity has been deleted
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_ACCESS_LEVEL)
    public boolean deleteInformationAccessLevel(Long id) {
        return deleteEntity(id, InformationAccessLevel.class);
    }

    /**
     * Returns the default Memo Access Level code defined in the KSA configuration.
     *
     * @return Memo Access Level code
     */
    @Override
    public String getDefaultMemoAccessLevelCode() {
        return configService.getParameter(Constants.MEMO_DEFAULT_ACCESS_LEVEL);
    }

    protected void checkInformationPermission(InformationAccessLevel accessLevel, InformationPermission permission) {

        if (accessLevel != null) {

            String permissionName;

            switch (permission) {
                case CREATE:
                    permissionName = accessLevel.getCreatePermission();
                    break;
                case READ:
                    permissionName = accessLevel.getReadPermission();
                    break;
                case UPDATE:
                    permissionName = accessLevel.getUpdatePermission();
                    break;
                case DELETE:
                    permissionName = accessLevel.getDeletePermission();
                    break;
                case EXPIRE:
                    permissionName = accessLevel.getExpirePermission();
                    break;
                default:
                    String errMsg = "Unknown permission name '" + permission.name() + "'";
                    logger.error(errMsg);
                    throw new IllegalArgumentException(errMsg);

            }

            PermissionUtils.checkPermission(permissionName != null ? Permission.valueOf(permissionName) : null);
        }
    }

    protected void checkCreatePermission(InformationAccessLevel accessLevel) {
        checkInformationPermission(accessLevel, InformationPermission.CREATE);
    }

    protected void checkReadPermission(InformationAccessLevel accessLevel) {
        checkInformationPermission(accessLevel, InformationPermission.READ);
    }

    protected void checkUpdatePermission(InformationAccessLevel accessLevel) {
        checkInformationPermission(accessLevel, InformationPermission.UPDATE);
    }

    protected void checkDeletePermission(InformationAccessLevel accessLevel) {
        checkInformationPermission(accessLevel, InformationPermission.DELETE);
    }

    protected void checkExpirePermission(InformationAccessLevel accessLevel) {
        checkInformationPermission(accessLevel, InformationPermission.EXPIRE);
    }

}
