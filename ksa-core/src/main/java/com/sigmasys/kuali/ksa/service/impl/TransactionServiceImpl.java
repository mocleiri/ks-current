package com.sigmasys.kuali.ksa.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.exception.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.*;
import com.sigmasys.kuali.ksa.service.brm.BrmContext;
import com.sigmasys.kuali.ksa.service.brm.BrmService;
import com.sigmasys.kuali.ksa.service.hold.HoldService;
import com.sigmasys.kuali.ksa.service.security.AccessControlService;
import com.sigmasys.kuali.ksa.service.security.PermissionUtils;
import com.sigmasys.kuali.ksa.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;


/**
 * Transaction service implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("transactionService")
@Transactional(readOnly = true)
@WebService(serviceName = TransactionService.SERVICE_NAME, portName = TransactionService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
@SuppressWarnings("unchecked")
public class TransactionServiceImpl extends GenericPersistenceService implements TransactionService {

    private static final Log logger = LogFactory.getLog(TransactionServiceImpl.class);

    private static final String GET_TRANSACTION_JOIN =
            " left outer join fetch t.transactionType tt " +
                    " left outer join fetch t.generalLedgerType glt " +
                    " left outer join fetch t.account a " +
                    " left outer join fetch t.currency c " +
                    " left outer join fetch t.rollup r " +
                    " left outer join fetch t.document d ";

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private InformationService informationService;

    @Autowired
    private GeneralLedgerService glService;

    @Autowired
    private CashLimitService cashLimitService;

    @Autowired
    private HoldService holdService;

    @Autowired
    private BrmService brmService;


    private AccessControlService getAccessControlService() {
        return ContextUtils.getBean(AccessControlService.class);
    }

    private void logFailedGlTransaction(Long sourceTransactionId, Exception exception) {

        Transaction sourceTransaction = getEntity(sourceTransactionId, Transaction.class);

        FailedGlTransaction failedGlTransaction = new FailedGlTransaction();
        failedGlTransaction.setTransaction(sourceTransaction);
        failedGlTransaction.setFailureReason(exception.getMessage());
        failedGlTransaction.setFixed(false);
        failedGlTransaction.setCreationDate(new Date());
        failedGlTransaction.setCreatorId(userSessionManager.getUserId());

        persistEntity(failedGlTransaction);
    }


    private <T extends Transaction> List<T> getTransactions(Class<T> entityType, Date fromDate, Date toDate,
                                                            String... userIds) {

        Query query = em.createQuery("select t from " + entityType.getName() + " t " + GET_TRANSACTION_JOIN +
                ((userIds != null && userIds.length > 0) ? " where t.account.id in (:userIds) " : "") +
                (fromDate != null ? " and to_date(t.effectiveDate) >= :fromDate " : "") +
                (toDate != null ? " and to_date(t.effectiveDate) <= :toDate " : "") +
                " order by t.id desc");

        if (fromDate != null) {
            query.setParameter("fromDate", CalendarUtils.removeTime(fromDate), TemporalType.DATE);
        }

        if (toDate != null) {
            query.setParameter("toDate", CalendarUtils.removeTime(toDate), TemporalType.DATE);
        }

        if (userIds != null && userIds.length > 0) {
            query.setParameter("userIds", Arrays.asList(userIds));
        }

        return (List<T>) query.getResultList();
    }

    private <T extends Transaction> T getTransaction(Long id, Class<T> entityType) {

        Query query = em.createQuery("select t from " + entityType.getName() + " t " + GET_TRANSACTION_JOIN +
                " where t.id = :id ");
        query.setParameter("id", id);
        List<T> transactions = query.getResultList();
        return CollectionUtils.isNotEmpty(transactions) ? transactions.get(0) : null;
    }

    /**
     * Creates and persists a new charge based on the given parameters.
     *
     * @param debitTypeId   The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                      based on the effective date
     * @param userId        Account ID
     * @param effectiveDate Charge Effective Date
     * @param amount        Charge amount
     * @return Charge instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_CHARGE)
    public Charge createCharge(String debitTypeId, String userId, Date effectiveDate, BigDecimal amount) {

        TransactionType transactionType = getTransactionType(debitTypeId, effectiveDate);
        if (transactionType == null || !transactionType.getTypeValue().equals(TransactionType.DEBIT_TYPE)) {
            String errMsg = "Debit type '" + debitTypeId + "' does not exist for effective date = " + effectiveDate;
            logger.error(errMsg);
            throw new TransactionTypeNotFoundException(errMsg);
        }

        return (Charge) createTransaction(transactionType.getId(), null, userId, effectiveDate, null, null, amount, false);
    }

    /**
     * Creates and persists a new payment based on the given parameters.
     *
     * @param creditTypeId  The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                      based on the effective date
     * @param userId        Account ID
     * @param effectiveDate Payment Effective Date
     * @param amount        Payment amount
     * @return Payment instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_PAYMENT)
    public Payment createPayment(String creditTypeId, String userId, Date effectiveDate, BigDecimal amount) {

        TransactionType transactionType = getTransactionType(creditTypeId, effectiveDate);
        if (transactionType == null || !transactionType.getTypeValue().equals(TransactionType.CREDIT_TYPE)) {
            String errMsg = "Credit type '" + creditTypeId + "' does not exist for effective date = " + effectiveDate;
            logger.error(errMsg);
            throw new TransactionTypeNotFoundException(errMsg);
        }

        return (Payment) createTransaction(transactionType.getId(), null, userId, effectiveDate, null, null, amount, false);
    }

    /**
     * Creates and persists a new deferment based on the given parameters.
     *
     * @param creditTypeId   The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                       based on the effective date
     * @param userId         Account ID
     * @param effectiveDate  Deferment Effective Date
     * @param expirationDate Deferment Expiration Date
     * @param amount         Deferment amount
     * @return Deferment instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_DEFERMENT)
    public Deferment createDeferment(String creditTypeId, String userId, Date effectiveDate, Date expirationDate, BigDecimal amount) {

        if (expirationDate == null) {
            String errMsg = "Expiration date is required for deferments";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        TransactionType transactionType = getTransactionType(creditTypeId, effectiveDate);
        if (transactionType == null || !transactionType.getTypeValue().equals(TransactionType.CREDIT_TYPE)) {
            String errMsg = "Credit type '" + creditTypeId + "' does not exist for effective date = " + effectiveDate;
            logger.error(errMsg);
            throw new TransactionTypeNotFoundException(errMsg);
        }

        return (Deferment) createTransaction(transactionType.getId(), null, userId, effectiveDate, null, expirationDate, amount, false);
    }

    /**
     * Creates a new transaction based on the given parameters
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param userId            Account ID
     * @param effectiveDate     Transaction effective date
     * @param amount            Transaction amount
     * @return new Transaction instance
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public Transaction createTransaction(String transactionTypeId, String userId, Date effectiveDate, BigDecimal amount) {
        return createTransaction(transactionTypeId, null, userId, effectiveDate, null, amount);
    }

    /**
     * Creates a new transaction based on the given parameters
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param userId            Account ID
     * @param effectiveDate     Transaction Effective date
     * @param recognitionDate   Transaction Recognition date
     * @param amount            Transaction amount
     * @return new Transaction instance
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public Transaction createTransaction(String transactionTypeId, String userId, Date effectiveDate,
                                         Date recognitionDate, BigDecimal amount) {
        return createTransaction(transactionTypeId, null, userId, effectiveDate, recognitionDate, null, amount, false);
    }

    /**
     * Creates a new transaction based on the given parameters
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param externalId        Transaction External ID
     * @param userId            Account ID
     * @param effectiveDate     Transaction Effective Date
     * @param expirationDate    used for deferments only
     * @param amount            Transaction amount
     * @return new Transaction instance
     */
    @Override
    @Transactional(readOnly = false)
    public Transaction createTransaction(String transactionTypeId, String externalId, String userId,
                                         Date effectiveDate, Date expirationDate, BigDecimal amount) {
        return createTransaction(transactionTypeId, externalId, userId, effectiveDate, null, expirationDate, amount, false);
    }

    /**
     * Creates a new transaction based on the given parameters
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param externalId        Transaction External ID
     * @param userId            Account ID
     * @param effectiveDate     Transaction Effective Date
     * @param recognitionDate   Transaction Recognition Date
     * @param expirationDate    Used for deferments only
     * @param amount            Transaction amount
     * @param overrideBlocks    indicates whether the account blocks must be overridden
     * @return new Transaction instance
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public Transaction createTransaction(String transactionTypeId, String externalId, String userId,
                                         Date effectiveDate, Date recognitionDate, Date expirationDate,
                                         BigDecimal amount, boolean overrideBlocks) {
        if (recognitionDate == null) {
            recognitionDate = effectiveDate;
        }

        TransactionTypeId id = getTransactionType(transactionTypeId, recognitionDate).getId();

        return createTransaction(id, externalId, userId, effectiveDate, recognitionDate, expirationDate, amount, overrideBlocks);
    }


    /**
     * Returns the transaction type instance for the given transaction type ID and date
     *
     * @param transactionTypeId The first part of TransactionTypeId PK, the second part (sub-code) will be calculated
     *                          based on the effective date
     * @param date              Transaction effective or recognition Date
     * @return TransactionType instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION_TYPE)
    public TransactionType getTransactionType(String transactionTypeId, Date date) {

        date = CalendarUtils.removeTime(date);

        Query query = em.createQuery("select t from TransactionType t " +
                "left outer join fetch t.rollup " +
                "left outer join fetch t.tags " +
                " where t.id.id = :transactionTypeId and :date >= t.startDate and " +
                " (t.endDate is null or t.endDate >= :date)");

        query.setParameter("transactionTypeId", transactionTypeId);
        query.setParameter("date", date, TemporalType.DATE);

        List<TransactionType> transactionTypes = query.getResultList();

        return (CollectionUtils.isNotEmpty(transactionTypes)) ? transactionTypes.get(0) : null;
    }

    /**
     * Returns the transaction type instance for the given transaction type ID and effective date
     *
     * @param transactionTypeId TransactionTypeId instance
     * @return TransactionType instance
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION_TYPE)
    public TransactionType getTransactionType(TransactionTypeId transactionTypeId) {

        Query query = em.createQuery("select t from TransactionType t " +
                "left outer join fetch t.rollup " +
                "left outer join fetch t.tags " +
                "where t.id = :transactionTypeId");

        query.setParameter("transactionTypeId", transactionTypeId);

        List<TransactionType> transactionTypes = query.getResultList();

        return (CollectionUtils.isNotEmpty(transactionTypes)) ? transactionTypes.get(0) : null;
    }

    /**
     * Returns the list of transaction type instances for the given string pattern.
     *
     * @param pattern    String containing characters within the code, name or description
     * @param entityType Class instance of TransactionType subclass
     * @return List of TransactionType instances
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION_TYPE)
    public <T extends TransactionType> List<T> getTransactionTypesByNamePattern(String pattern, Class<T> entityType) {
        return getTransactionTypesByNamePattern(pattern, entityType, null);
    }

    /**
     * Returns the list of transaction type instances for the given string pattern.
     *
     * @param pattern       String containing characters within the code, name or description
     * @param entityType    Class instance of TransactionType subclass
     * @param effectiveDate Date for which the transaction types are active
     * @return List of TransactionType instances
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION_TYPE)
    public <T extends TransactionType> List<T> getTransactionTypesByNamePattern(String pattern,
                                                                                Class<T> entityType,
                                                                                Date effectiveDate) {

        if (StringUtils.isBlank(pattern)) {
            String errMsg = "Name pattern cannot be empty";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        StringBuilder queryBuilder = new StringBuilder("select t from " + entityType.getName() +
                " t where (upper(t.id.id) like :pattern or " +
                " upper(t.code) like :pattern or " +
                " upper(t.name) like :pattern or " +
                " upper(t.description) like :pattern)");

        if (effectiveDate != null) {
            queryBuilder.append(" and :date >= t.startDate and (t.endDate is null or t.endDate >= :date)");
        }

        Query query = em.createQuery(queryBuilder.toString());

        query.setParameter("pattern", "%" + pattern.toUpperCase() + "%");

        if (effectiveDate != null) {
            query.setParameter("date", CalendarUtils.removeTime(effectiveDate), TemporalType.DATE);
        }

        return query.getResultList();
    }

    /**
     * Creates a new debit type based on the given parameters.
     *
     * @param debitTypeId Transaction Type ID
     * @param name        Transaction Type name
     * @param startDate   Transaction type start date
     * @param priority    Priority integer value
     * @param description Default statement text
     * @return a new DebitType instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_TRANSACTION_TYPE)
    public DebitType createDebitType(String debitTypeId, String name, Date startDate, int priority, String description) {
        return createTransactionType(debitTypeId, 0, name, startDate, priority, description, DebitType.class, true);
    }

    /**
     * Creates a new credit type based on the given parameters.
     *
     * @param creditTypeId Transaction Type ID
     * @param name         Transaction Type name
     * @param startDate    Transaction type start date
     * @param priority     Priority integer value
     * @param description  Default statement text
     * @return a new CreditType instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_TRANSACTION_TYPE)
    public CreditType createCreditType(String creditTypeId, String name, Date startDate, int priority, String description) {
        return createTransactionType(creditTypeId, 0, name, startDate, priority, description, CreditType.class, true);
    }

    /**
     * Creates a new debit sub-type based on the given parameters.
     * The original debit type must exist prior to its sub-type creation.
     *
     * @param debitTypeId ID of the existing Debit Type
     * @param startDate   Debit Type start date
     * @return a new DebitType instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_TRANSACTION_TYPE)
    public DebitType createDebitSubType(String debitTypeId, Date startDate) {
        return createTransactionType(debitTypeId, startDate, DebitType.class);
    }

    /**
     * Creates a new credit sub-type based on the given parameters.
     * The original credit type must exist prior to its sub-type creation.
     *
     * @param creditTypeId ID of the existing Credit Type
     * @param startDate    Credit Type start date
     * @return a new CreditType instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_TRANSACTION_TYPE)
    public CreditType createCreditSubType(String creditTypeId, Date startDate) {
        return createTransactionType(creditTypeId, startDate, CreditType.class);
    }

    private synchronized <T extends TransactionType> T createTransactionType(String transactionTypeId,
                                                                             Date startDate,
                                                                             Class<T> entityType) {

        T transactionType = getActiveTransactionType(transactionTypeId, entityType);
        if (transactionType == null) {
            String errMsg = "Transaction type with ID = " + transactionTypeId + " does not exist";
            logger.error(errMsg);
            throw new InvalidTransactionTypeException(errMsg);
        }

        startDate = CalendarUtils.removeTime(startDate);

        if (!startDate.after(transactionType.getStartDate())) {
            String errMsg = "New transaction type's start date must be greater than the current transaction type's one";
            logger.error(errMsg);
            throw new InvalidTransactionTypeException(errMsg);
        }

        Date endDate = CalendarUtils.addCalendarDays(startDate, -1);

        logger.debug("Previous Transaction Type end date = " + endDate);

        transactionType.setEndDate(endDate);

        int nextSubCode = transactionType.getId().getSubCode() + 1;

        logger.debug("Next Transaction Type start date = " + startDate);
        logger.debug("Next Transaction Type sub-code = " + nextSubCode);

        logger.debug("Creating new Transaction Type with ID = " + transactionTypeId + " and sub-code = " + nextSubCode);

        Integer priority = transactionType.getPriority();
        if (priority == null) {
            priority = 0;
        }

        return createTransactionType(transactionTypeId,
                nextSubCode,
                transactionType.getName(),
                startDate,
                priority,
                transactionType.getDescription(),
                entityType,
                false);
    }

    private <T extends TransactionType> T createTransactionType(String transactionTypeId, int subCode, String name,
                                                                Date startDate, int priority, String description,
                                                                Class<T> entityType, boolean createNewType) {

        logger.debug("Creating new Transaction Type with ID = " + transactionTypeId + " and sub-code = " + subCode);

        if (subCode > 0 && createNewType) {
            String errMsg = "Transaction type with ID = " + transactionTypeId + " already exists";
            logger.error(errMsg);
            throw new InvalidTransactionTypeException(errMsg);
        }

        T transactionType = (T) (CreditType.class.equals(entityType) ? new CreditType() : new DebitType());

        TransactionTypeId typeId = new TransactionTypeId(transactionTypeId, subCode);

        transactionType.setId(typeId);
        transactionType.setCode(transactionTypeId);
        transactionType.setName(name);
        transactionType.setStartDate(CalendarUtils.removeTime(startDate));
        transactionType.setPriority(priority);
        transactionType.setDescription(description);
        transactionType.setCreationDate(new Date());
        transactionType.setCreatorId(userSessionManager.getUserId());

        persistEntity(transactionType);

        return transactionType;
    }

    private <T extends TransactionType> T getActiveTransactionType(String transactionTypeId, Class<T> entityType) {

        Query query = em.createQuery("select t from " + entityType.getName() + " t where t.endDate is null and t.id.id = :id");
        query.setParameter("id", transactionTypeId);
        query.setMaxResults(1);

        List<T> results = query.getResultList();
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * Checks if the transaction type exists.
     *
     * @param transactionTypeId Transaction Type ID
     * @return "true" if the transaction type exists, false - otherwise
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION_TYPE)
    public boolean transactionTypeExists(String transactionTypeId) {
        Query query = em.createQuery("select 1 from TransactionType where id.id = :id");
        query.setParameter("id", transactionTypeId);
        query.setMaxResults(1);
        return CollectionUtils.isNotEmpty(query.getResultList());
    }

    /**
     * Assigns the general ledger type to the transaction specified by IDs.
     *
     * @param transactionId Transaction ID
     * @param glTypeId      GL Type ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.EDIT_TRANSACTION)
    public void setGeneralLedgerType(Long transactionId, Long glTypeId) {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction does not exist for the given ID = " + transactionId;
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        Boolean isGlEntryGenerated = transaction.isGlEntryGenerated();
        if (isGlEntryGenerated != null && isGlEntryGenerated) {
            String errMsg = "GL entry has already been generated for Transaction with ID = " + transactionId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        GeneralLedgerType generalLedgerType = glService.getGeneralLedgerType(glTypeId);
        if (generalLedgerType == null) {
            String errMsg = "General Ledger Type does not exist for the given ID = " + glTypeId;
            logger.error(errMsg);
            throw new GeneralLedgerTypeNotFoundException(errMsg);
        }

        transaction.setGeneralLedgerType(generalLedgerType);

    }


    /**
     * Creates a new transaction based on the given parameters
     *
     * @param id              Transaction type ID
     * @param userId          Account ID
     * @param effectiveDate   Transaction Effective Date
     * @param recognitionDate Transaction Recognition Date
     * @param expirationDate  Used for deferments only
     * @param amount          Transaction amount
     * @param overrideBlocks  indicates whether the account blocks must be overridden
     * @return new Transaction instance
     */
    protected Transaction createTransaction(TransactionTypeId id, String externalId, String userId,
                                            Date effectiveDate, Date recognitionDate, Date expirationDate,
                                            BigDecimal amount, boolean overrideBlocks) {

        if (amount == null) {
            String errMsg = "Transaction amount must not be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        TransactionType transactionType = getTransactionType(id);
        if (transactionType == null) {
            String errMsg = "Transaction type does not exist for the given ID = " + id;
            logger.error(errMsg);
            throw new TransactionTypeNotFoundException(errMsg);
        }

        Account account = em.find(Account.class, userId);
        if (account == null) {
            String errMsg = "Account does not exist for the given ID = " + userId;
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        String currencyCode = java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        com.sigmasys.kuali.ksa.model.Currency currency = auditableEntityService.getCurrency(currencyCode);
        if (currency == null) {
            String errMsg = "Currency does not exist for the given ISO code = " + currencyCode;
            logger.error(errMsg);
            throw new CurrencyNotFoundException(errMsg);
        }

        SimpleTransactionTypeVisitor transactionTypeVisitor = SimpleTransactionTypeVisitor.getInstance();

        transactionType.accept(transactionTypeVisitor);

        String creatorId = userSessionManager.getUserId();

        if (overrideBlocks) {
            if (!getAccessControlService().isTransactionTypeAllowed(creatorId, id.getId())) {
                String errMsg = "Transaction type is not allowed for the given account = " + creatorId;
                logger.error(errMsg);
                throw new TransactionTypeNotAllowedException(errMsg);
            }
        } else if (!isTransactionAllowed(creatorId, id.getId(), effectiveDate)) {
            String errMsg = "Transaction is not allowed for the given account = " + creatorId +
                    " and effective date = " + effectiveDate;
            logger.error(errMsg);
            throw new TransactionNotAllowedException(errMsg);
        }

        Transaction transaction;
        if (transactionType.getTypeValue().equals(TransactionType.CREDIT_TYPE)) {
            transaction = (expirationDate != null) ? new Deferment() : new Payment();
        } else {
            transaction = new Charge();
        }

        final Permission permissionToCheck;

        switch (transaction.getTransactionTypeValue()) {
            case PAYMENT:
                permissionToCheck = Permission.CREATE_PAYMENT;
                break;
            case DEFERMENT:
                permissionToCheck = Permission.CREATE_DEFERMENT;
                break;
            case CHARGE:
                permissionToCheck = Permission.CREATE_CHARGE;
                break;
            default:
                String errMsg = "Unknown transaction type = " + transaction.getTransactionTypeValue();
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
        }

        PermissionUtils.checkPermission(permissionToCheck);

        transaction.setTransactionType(transactionType);
        transaction.setAccount(account);
        transaction.setAccountId(account.getId());
        transaction.setCurrency(currency);

        transaction.setExternalId(externalId);
        transaction.setEffectiveDate(effectiveDate);
        transaction.setRecognitionDate(recognitionDate != null ? recognitionDate : effectiveDate);
        transaction.setNativeAmount(amount);
        transaction.setAmount(amount);
        transaction.setAllocatedAmount(BigDecimal.ZERO);
        transaction.setLockedAllocatedAmount(BigDecimal.ZERO);

        transaction.setRollup(transactionType.getRollup());
        transaction.setStatementText(transactionType.getDescription());
        transaction.setGlEntryGenerated(false);
        transaction.setGlOverridden(false);
        transaction.setInternal(false);

        transaction.setCreationDate(new Date());
        transaction.setGeneralLedgerType(glService.getDefaultGeneralLedgerType());

        transaction.setCreatorId(creatorId);

        transaction.setStatus(TransactionStatus.ACTIVE);

        // Copying tags from the transaction type to the new transaction if they exist
        Set<Tag> tags = transactionType.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {
            transaction.setTags(new HashSet<Tag>(tags));
        }

        if (transaction instanceof Payment) {
            CreditType creditType = transactionTypeVisitor.getCreditType();
            Payment payment = (Payment) transaction;
            int clearPeriod = (creditType.getClearPeriod() != null) ? creditType.getClearPeriod() : 0;
            payment.setClearDate(CalendarUtils.addCalendarDays(effectiveDate, clearPeriod));
            payment.setRefundRule(creditType.getRefundRule());
            payment.setRefundable(creditType.isRefundable());
        } else if (transaction instanceof Deferment) {
            Deferment deferment = (Deferment) transaction;
            deferment.setExpirationDate(expirationDate);
            deferment.setOriginalAmount(amount);
        } else {
            DebitType debitType = transactionTypeVisitor.getDebitType();
            Charge charge = (Charge) transaction;
            String cancellationRule = (debitType.getCancellationRule() != null) ?
                    debitType.getCancellationRule() : getDefaultChargeCancellationRule();
            if (cancellationRule != null) {
                charge.setCancellationRule(calculateCancellationRule(cancellationRule, effectiveDate));
            }
        }

        persistTransaction(transaction);

        return transaction;
    }

    protected String getDefaultChargeCancellationRule() {
        return configService.getParameter(Constants.CHARGE_DEFAULT_CANCELLATION_RULE);
    }


    /**
     * Returns Transaction by ID
     *
     * @param id Transaction ID
     * @return Transaction instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public Transaction getTransaction(Long id) {
        return getTransaction(id, Transaction.class);
    }

    /**
     * Returns Charge by ID
     *
     * @param id Charge ID
     * @return Charge instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public Charge getCharge(Long id) {
        return getTransaction(id, Charge.class);
    }

    /**
     * Returns Payment by ID
     *
     * @param id Payment ID
     * @return Payment instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public Payment getPayment(Long id) {
        return getTransaction(id, Payment.class);
    }

    /**
     * Returns Deferment by ID
     *
     * @param id Deferment ID
     * @return Deferment instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public Deferment getDeferment(Long id) {
        return getTransaction(id, Deferment.class);
    }


    /**
     * Returns all transactions sorted by ID
     *
     * @return List of transactions
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Transaction> getTransactions() {
        return getTransactions(Transaction.class, null, null);
    }

    /**
     * Returns all charges sorted by ID
     *
     * @return List of all charges
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Charge> getCharges() {
        return getTransactions(Charge.class, null, null);
    }

    /**
     * Returns all charges by account ID
     *
     * @param userId Account ID
     * @return List of all charges by account ID
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Charge> getCharges(String userId) {
        return getTransactions(Charge.class, null, null, userId);
    }

    /**
     * Returns all payments sorted by ID
     *
     * @return List of all payments
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Payment> getPayments() {
        return getTransactions(Payment.class, null, null);
    }

    /**
     * Returns all payments by account ID
     *
     * @param userId Account ID
     * @return List of all payments by account ID
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Payment> getPayments(String userId) {
        return getTransactions(Payment.class, null, null, userId);
    }


    /**
     * Returns all deferments sorted by ID
     *
     * @return List of all deferments
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Deferment> getDeferments() {
        return getTransactions(Deferment.class, null, null);
    }

    /**
     * Returns all deferments by account ID
     *
     * @param userId Account ID
     * @return List of all deferments by account ID
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Deferment> getDeferments(String userId) {
        return getTransactions(Deferment.class, null, null, userId);
    }

    /**
     * Returns all transactions by account ID
     *
     * @param userId Account ID
     * @return List of transactions
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Transaction> getTransactions(String userId) {
        return getTransactions(Transaction.class, null, null, userId);
    }

    /**
     * Returns all transactions by account ID and date range
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of transactions
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Transaction> getTransactions(String userId, Date fromDate, Date toDate) {
        return getTransactions(Transaction.class, fromDate, toDate, userId);
    }

    /**
     * Returns all charges by account ID and date range
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of charges
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Charge> getCharges(String userId, Date fromDate, Date toDate) {
        return getTransactions(Charge.class, fromDate, toDate, userId);
    }

    /**
     * Returns all payments by account ID and date range
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of payments
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Payment> getPayments(String userId, Date fromDate, Date toDate) {
        return getTransactions(Payment.class, fromDate, toDate, userId);
    }

    /**
     * Returns all deferments by account ID and date range
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of deferments
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Deferment> getDeferments(String userId, Date fromDate, Date toDate) {
        return getTransactions(Deferment.class, fromDate, toDate, userId);
    }


    /**
     * Returns all transactions by account ID, date range and statuses
     *
     * @param userId   Account ID
     * @param fromDate Start date
     * @param toDate   End date
     * @param statuses Array of transaction statuses
     * @return List of transactions
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Transaction> getTransactions(String userId, Date fromDate, Date toDate, TransactionStatus... statuses) {

        List<String> statusCodes = new ArrayList<String>(statuses.length);
        for (TransactionStatus status : statuses) {
            statusCodes.add(status.getId());
        }

        Query query = em.createQuery("select t from Transaction t " + GET_TRANSACTION_JOIN +
                " where t.account.id = :userId and t.statusCode in (:statusCodes) " +
                (fromDate != null ? " and t.effectiveDate >= :fromDate " : "") +
                (toDate != null ? " and t.effectiveDate <= :toDate " : "") +
                " order by t.id desc");

        if (fromDate != null) {
            query.setParameter("fromDate", CalendarUtils.removeTime(fromDate), TemporalType.DATE);
        }

        if (toDate != null) {
            query.setParameter("toDate", CalendarUtils.removeTime(toDate), TemporalType.DATE);
        }

        query.setParameter("userId", userId);
        query.setParameter("statusCodes", statusCodes);

        return query.getResultList();
    }

    /**
     * Persists the transaction in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param transaction Transaction instance
     * @return Transaction ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.EDIT_TRANSACTION)
    public Long persistTransaction(Transaction transaction) {
        Set<Tag> tags = transaction.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {
            String cashTag = configService.getParameter(Constants.CASH_TRACKING_TAG);
            if (StringUtils.isNotBlank(cashTag)) {
                for (Tag tag : tags) {
                    if (cashTag.equals(tag.getCode())) {
                        cashLimitService.checkCashLimit(transaction.getAccount().getId());
                    }
                }
            }
        }
        return persistEntity(transaction);
    }

    /**
     * Persists the transaction type in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param transactionType TransactionType instance
     * @return Transaction Type ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.EDIT_TRANSACTION_TYPE)
    public TransactionTypeId persistTransactionType(TransactionType transactionType) {
        return persistEntity(transactionType);
    }

    /**
     * Removes the transaction from the database.
     *
     * @param id Transaction ID
     * @return true if the Transaction entity has been deleted
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.EDIT_TRANSACTION)
    public boolean deleteTransaction(Long id) {
        return deleteEntity(id, Transaction.class);
    }

    /**
     * This will allocate the value of amount on the transaction. A check will
     * be made to ensure that the allocated amount is equal to or less than the
     * localAmount, less any lockedAllocationAmount. The expectation is that
     * this method will only be called by the payment application module.
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param amount         amount of money to be allocated
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public CompositeAllocation createAllocation(Long transactionId1, Long transactionId2, BigDecimal amount) {
        return createAllocation(transactionId1, transactionId2, amount, true, false, false);
    }

    /**
     * This will allocate the value of amount on the transaction. A check will
     * be made to ensure that the allocated amount is equal to or less than the
     * localAmount, less any lockedAllocationAmount. The expectation is that
     * this method will only be called by the payment application module.
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param amount         amount of money to be allocated
     * @param isQueued       indicates whether the GL transaction should be in Q or W status
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    @Override
    @Transactional(readOnly = false)
    public CompositeAllocation createAllocation(Long transactionId1, Long transactionId2, BigDecimal amount, boolean isQueued) {
        return createAllocation(transactionId1, transactionId2, amount, isQueued, false, false);
    }

    protected CompositeAllocation createAllocation(Long transactionId1, Long transactionId2, BigDecimal newAmount,
                                                   boolean isQueued, boolean locked, boolean internallyLocked) {

        Transaction transaction1 = getTransaction(transactionId1);
        if (transaction1 == null) {
            String errMsg = "Transaction with ID = " + transactionId1 + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        Transaction transaction2 = getTransaction(transactionId2);
        if (transaction2 == null) {
            String errMsg = "Transaction with ID = " + transactionId2 + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        return createAllocation(transaction1, transaction2, newAmount, isQueued, locked, internallyLocked);
    }

    private void addAllocatedAmount(Transaction transaction, BigDecimal newAmount, boolean locked) {

        if (newAmount == null) {
            newAmount = BigDecimal.ZERO;
        }

        BigDecimal oldAmount = locked ? transaction.getLockedAllocatedAmount() : transaction.getAllocatedAmount();

        if (oldAmount == null) {
            oldAmount = BigDecimal.ZERO;
        }

        BigDecimal totalAmount = oldAmount.add(newAmount);

        if (locked) {
            transaction.setLockedAllocatedAmount(totalAmount);
        } else {
            transaction.setAllocatedAmount(totalAmount);
        }
    }

    /**
     * Creates an allocation between two transactions with the specified parameters.
     *
     * @param transaction1     First transaction
     * @param transaction2     Second transaction
     * @param newAmount        amount to be allocated
     * @param isQueued         indicates whether the GL transaction should be in Q or W status
     * @param locked           indicates whether the allocation should be locked or unlocked
     * @param internallyLocked indicates whether the allocation should be internally locked or unlocked
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public CompositeAllocation createAllocation(Transaction transaction1, Transaction transaction2, BigDecimal newAmount,
                                                boolean isQueued, boolean locked, boolean internallyLocked) {

        final Long transactionId1 = transaction1.getId();
        final Long transactionId2 = transaction2.getId();

        if (internallyLocked) {
            if (!locked) {
                String errMsg = "Internally locked allocations must also be locked";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }
            PermissionUtils.checkPermission(Permission.CREATE_INTERNALLY_LOCKED_ALLOCATION);
        } else if (locked) {
            PermissionUtils.checkPermission(Permission.CREATE_LOCKED_ALLOCATION);
        } else {
            PermissionUtils.checkPermission(Permission.CREATE_ALLOCATION);
        }

        if (newAmount == null || newAmount.compareTo(BigDecimal.ZERO) <= 0) {
            String errMsg = "The allocation amount should be a positive number";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        newAmount = newAmount.setScale(2, RoundingMode.HALF_DOWN);

        if (transaction1.getAccount() == null || transaction2.getAccount() == null) {
            String errMsg = "Transaction must be associated with Account";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        Account account = transaction1.getAccount();
        String userId = account.getId();
        if (!userId.equals(transaction2.getAccount().getId())) {
            String errMsg = "Both transactions must be associated with the same account";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        // Check if Transaction1 can pay Transaction2
        if (!canPay(transactionId1, transactionId2)) {
            String errMsg = "Transaction1 [ID = " + transactionId1 +
                    ", type = " + transaction1.getTransactionType().getId().getId() +
                    ", amount = " + transaction1.getAmount() +
                    "] cannot pay Transaction2 [ID = " + transactionId2 +
                    ", type = " + transaction2.getTransactionType().getId().getId() +
                    ", amount = " + transaction2.getAmount() + "]";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        Query query = em.createQuery("select distinct a from Allocation a " +
                " where a.account.id = :userId and ((a.firstTransaction.id = :id1 and a.secondTransaction.id = :id2) or " +
                " (a.firstTransaction.id = :id2 and a.secondTransaction.id = :id1)) and a.locked = :locked");

        query.setParameter("userId", userId);
        query.setParameter("id1", transactionId1);
        query.setParameter("id2", transactionId2);
        query.setParameter("locked", locked);

        List<Allocation> allocations = query.getResultList();

        for (Allocation allocation : allocations) {

            PermissionUtils.checkPermissions(allocation,
                    Permission.REMOVE_ALLOCATION,
                    Permission.REMOVE_LOCKED_ALLOCATION,
                    Permission.REMOVE_INTERNALLY_LOCKED_ALLOCATION);

            BigDecimal allocatedAmount1 = locked ? transaction1.getLockedAllocatedAmount() :
                    transaction1.getAllocatedAmount();

            BigDecimal allocatedAmount2 = locked ? transaction2.getLockedAllocatedAmount() :
                    transaction2.getAllocatedAmount();

            if (allocatedAmount1 == null) {
                allocatedAmount1 = BigDecimal.ZERO;
            }

            if (allocatedAmount2 == null) {
                allocatedAmount2 = BigDecimal.ZERO;
            }

            BigDecimal newAllocatedAmount1 = (allocatedAmount1.compareTo(BigDecimal.ZERO) >= 0) ?
                    allocatedAmount1.subtract(allocation.getAmount()) :
                    allocatedAmount1.add(allocation.getAmount());

            BigDecimal newAllocatedAmount2 = (allocatedAmount2.compareTo(BigDecimal.ZERO) >= 0) ?
                    allocatedAmount2.subtract(allocation.getAmount()) :
                    allocatedAmount2.add(allocation.getAmount());

            if (locked) {
                transaction1.setLockedAllocatedAmount(newAllocatedAmount1);
                transaction2.setLockedAllocatedAmount(newAllocatedAmount2);
            } else {
                transaction1.setAllocatedAmount(newAllocatedAmount1);
                transaction2.setAllocatedAmount(newAllocatedAmount2);
            }

            deleteEntity(allocation.getId(), Allocation.class);
        }

        BigDecimal unallocatedAmount1 = transaction1.getUnallocatedAmount().setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal unallocatedAmount2 = transaction2.getUnallocatedAmount().setScale(2, RoundingMode.HALF_DOWN);

        if (unallocatedAmount1.compareTo(newAmount) < 0 || unallocatedAmount2.compareTo(newAmount) < 0) {
            logger.info("Transaction 1 unallocated amount = " + unallocatedAmount1);
            logger.info("Transaction 2 unallocated amount = " + unallocatedAmount2);
            String errMsg = "Not enough balance to cover the allocation amount " + newAmount;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        TransactionType transactionType1 = transaction1.getTransactionType();
        TransactionType transactionType2 = transaction2.getTransactionType();

        BigDecimal transactionAmount1 = transaction1.getAmount();
        BigDecimal transactionAmount2 = transaction2.getAmount();

        boolean canAllocate;

        if (!transactionType1.getTypeValue().equals(transactionType2.getTypeValue())) {
            canAllocate = transactionAmount1.compareTo(BigDecimal.ZERO) > 0 && transactionAmount2.compareTo(BigDecimal.ZERO) > 0;
        } else {
            canAllocate = (transactionAmount1.compareTo(BigDecimal.ZERO) > 0 && transactionAmount2.compareTo(BigDecimal.ZERO) < 0) ||
                    (transactionAmount1.compareTo(BigDecimal.ZERO) < 0 && transactionAmount2.compareTo(BigDecimal.ZERO) > 0);
        }

        if (canAllocate) {

            Allocation allocation = new Allocation();
            allocation.setAccount(account);
            allocation.setFirstTransaction(transaction1);
            allocation.setSecondTransaction(transaction2);
            allocation.setAmount(newAmount);
            allocation.setLocked(locked);
            allocation.setInternallyLocked(internallyLocked);

            persistEntity(allocation);

            addAllocatedAmount(transaction1, newAmount, locked);
            addAllocatedAmount(transaction2, newAmount, locked);

            persistTransaction(transaction1);
            persistTransaction(transaction2);

            CompositeAllocation compositeAllocation = new CompositeAllocation();
            compositeAllocation.setAllocation(allocation);

            // Creating GL transactions for charge+payment or payment+charge only
            if ((transaction1.getTransactionTypeValue() == TransactionTypeValue.CHARGE &&
                    transaction2.getTransactionTypeValue() == TransactionTypeValue.PAYMENT) ||
                    (transaction1.getTransactionTypeValue() == TransactionTypeValue.PAYMENT &&
                            transaction2.getTransactionTypeValue() == TransactionTypeValue.CHARGE)) {
                String statement = configService.getParameter(Constants.DEFAULT_GL_PA_STATEMENT);
                Pair<GlTransaction, GlTransaction> pair =
                        createGlTransactions(transaction1, transaction2, newAmount, statement, true, isQueued);
                compositeAllocation.setCreditGlTransaction(pair.getA());
                compositeAllocation.setDebitGlTransaction(pair.getB());
            }

            return compositeAllocation;

        } else {
            String errMsg = "Illegal allocation. Transaction IDs: " + transactionId1 + ", " + transactionId2 +
                    "; Transaction types: " + transactionType1.getId().getId() + ", " +
                    transactionType2.getId().getId() + "; Transaction amounts: " + transactionAmount1 + ", " +
                    transactionAmount2 + "; Allocation Amount: " + newAmount;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
    }

    /**
     * Creates the credit and debit transactions
     *
     * @param transaction1          First Transaction instance
     * @param transaction2          Second Transaction instance
     * @param amount                Allocation amount
     * @param statement             GL transaction statement
     * @param isQueued              Indicates whether the GL transaction should be in Q or W status
     * @param isOppositeGlOperation When "true" use the opposite GL operation
     * @return Pair instance with credit and debit GL transactions
     */
    protected Pair<GlTransaction, GlTransaction> createGlTransactions(Transaction transaction1,
                                                                      Transaction transaction2,
                                                                      BigDecimal amount,
                                                                      String statement,
                                                                      boolean isOppositeGlOperation,
                                                                      boolean isQueued) {

        final TransactionType transactionType1 = transaction1.getTransactionType();
        final TransactionType transactionType2 = transaction2.getTransactionType();

        Pair<GlTransaction, GlTransaction> pair = new Pair<GlTransaction, GlTransaction>();

        if (!transactionType1.getTypeValue().equals(transactionType2.getTypeValue())) {

            SimpleTransactionTypeVisitor transactionTypeVisitor = SimpleTransactionTypeVisitor.getInstance();

            transactionType1.accept(transactionTypeVisitor);

            if (transactionTypeVisitor.getCreditType() == null) {
                transactionType2.accept(transactionTypeVisitor);
            }

            final CreditType creditType = transactionTypeVisitor.getCreditType();

            final Transaction creditTransaction;
            final Transaction debitTransaction;

            if (transactionType1.getTypeValue().equals(TransactionType.CREDIT_TYPE)) {
                creditTransaction = transaction1;
                debitTransaction = transaction2;
            } else {
                creditTransaction = transaction2;
                debitTransaction = transaction1;
            }

            // Calculating the appropriate GL operation for Credit
            GlOperationType operationType = creditType.getUnallocatedGlOperation();

            if (isOppositeGlOperation) {
                operationType = (GlOperationType.CREDIT == operationType) ?
                        GlOperationType.DEBIT :
                        GlOperationType.CREDIT;
            }

            // Creating GL transaction for credit
            GlTransaction creditGlTransaction = glService.createGlTransaction(creditTransaction.getId(),
                    creditType.getUnallocatedGlAccount(), amount, operationType, statement, isQueued);


            pair.setA(creditGlTransaction);

            GeneralLedgerType glType = debitTransaction.getGeneralLedgerType();

            if (glType != null) {

                // Calculating the appropriate GL operation for Debit
                operationType = glType.getGlOperationOnCharge();

                if (isOppositeGlOperation) {
                    operationType = (GlOperationType.CREDIT.equals(operationType)) ?
                            GlOperationType.DEBIT :
                            GlOperationType.CREDIT;
                }

                // Creating GL transaction for debit
                GlTransaction debitGlTransaction = glService.createGlTransaction(debitTransaction.getId(),
                        glType.getGlAccountId(), amount, operationType, statement, isQueued);

                pair.setB(debitGlTransaction);
            }
        }

        return pair;
    }

    /**
     * This will allocate a locked amount on the transaction. A check will be
     * made to ensure that the lockedAmount and the allocateAmount don't exceed
     * the ledgerAmount of the transaction. Setting an amount as locked prevents
     * the payment application system from reallocating the balance elsewhere.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param amount         amount of money to be allocated
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public CompositeAllocation createLockedAllocation(Long transactionId1, Long transactionId2, BigDecimal amount) {
        return createAllocation(transactionId1, transactionId2, amount, true, true, false);
    }

    /**
     * This will allocate a locked amount on the transaction. A check will be
     * made to ensure that the lockedAmount and the allocateAmount don't exceed
     * the ledgerAmount of the transaction. Setting an amount as locked prevents
     * the payment application system from reallocating the balance elsewhere.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param amount         amount of money to be allocated
     * @param isQueued       indicates whether the GL transaction should be in Q or W status
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public CompositeAllocation createLockedAllocation(Long transactionId1, Long transactionId2, BigDecimal amount,
                                                      boolean isQueued) {
        return createAllocation(transactionId1, transactionId2, amount, isQueued, true, false);
    }

    /**
     * This will allocate a locked amount on the transaction. A check will be
     * made to ensure that the lockedAmount and the allocateAmount don't exceed
     * the ledgerAmount of the transaction. Setting an amount as locked prevents
     * the payment application system from reallocating the balance elsewhere.
     *
     * @param transactionId1   transaction1 ID
     * @param transactionId2   transaction2 ID
     * @param amount           amount of money to be allocated
     * @param isQueued         indicates whether the GL transaction should be in Q or W status
     * @param internallyLocked indicates whether the allocation should be internally locked or not
     * @return a new CompositeAllocation instance that has references to Allocation and GL transactions
     */
    public CompositeAllocation createLockedAllocation(Long transactionId1, Long transactionId2, BigDecimal amount,
                                                      boolean isQueued, boolean internallyLocked) {
        return createAllocation(transactionId1, transactionId2, amount, isQueued, true, true);
    }

    /**
     * Retrieves all allocations associated with the given transaction by ID.
     * <p/>
     *
     * @param transactionId Transaction ID
     * @return list of allocations
     */
    @Override
    @PermissionsAllowed(Permission.READ_ALLOCATION)
    public List<Allocation> getAllocations(Long transactionId) {

        Query query = em.createQuery("select a from Allocation a " +
                " left outer join fetch a.account ac " +
                " inner join fetch a.firstTransaction t1 " +
                " inner join fetch a.secondTransaction t2 " +
                " inner join fetch t1.transactionType tt1 " +
                " inner join fetch t2.transactionType tt2 " +
                " where t1.id = :id or t2.id = :id");

        query.setParameter("id", transactionId);

        List<Allocation> allocations = query.getResultList();

        Set<Allocation> allocationSet = new HashSet<Allocation>();
        if (CollectionUtils.isNotEmpty(allocations)) {
            for (Allocation allocation : allocations) {
                if (!allocationSet.contains(allocation)) {
                    allocationSet.add(allocation);
                }
            }
        }

        ArrayList<Allocation> sortedAllocations = new ArrayList<Allocation>(allocationSet);

        Collections.sort(sortedAllocations, new Comparator<Allocation>() {
            @Override
            public int compare(Allocation o1, Allocation o2) {
                return o2.getId().compareTo(o1.getId());
            }
        });

        return sortedAllocations;
    }

    /**
     * Removes all allocations (locked and non-locked) associated with the given transaction.
     * <p/>
     *
     * @param transactionId Transaction ID
     * @return list of generated GL transactions
     */
    @Override
    @Transactional(readOnly = false)
    public List<GlTransaction> removeAllAllocations(Long transactionId) {
        return removeAllocations(transactionId, false);
    }

    /**
     * Removes non-locked allocations associated with the given transaction.
     * <p/>
     *
     * @param transactionId transaction ID
     * @return list of generated GL transactions
     */
    @Override
    @Transactional(readOnly = false)
    @WebMethod(exclude = true)
    public List<GlTransaction> removeAllocations(Long transactionId) {
        return removeAllocations(transactionId, true);
    }

    protected List<GlTransaction> removeAllocations(Long transactionId, boolean excludeLockAllocations) {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        return removeAllocations(transaction, excludeLockAllocations);
    }

    protected List<GlTransaction> removeAllocations(Transaction transaction, boolean excludeLockAllocations) {

        StringBuilder builder = new StringBuilder("select distinct a from Allocation a " +
                " where (a.firstTransaction.id = :id or a.secondTransaction.id = :id)");

        if (excludeLockAllocations) {
            builder.append(" and a.locked <> true)");
        }

        Query query = em.createQuery(builder.toString());

        query.setParameter("id", transaction.getId());

        List<Allocation> allocations = query.getResultList();

        List<GlTransaction> glTransactions = new LinkedList<GlTransaction>();
        if (CollectionUtils.isNotEmpty(allocations)) {
            for (Allocation allocation : new HashSet<Allocation>(allocations)) {
                glTransactions.addAll(removeAllocation(allocation, true));
            }
        }

        return glTransactions;
    }


    /**
     * Removes all allocations (locked and non-locked) associated with the given Account ID
     * <p/>
     *
     * @param userId Account ID
     * @return list of generated GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public List<GlTransaction> removeAllAllocations(String userId) {

        List<GlTransaction> glTransactions = new LinkedList<GlTransaction>();

        Query query = em.createQuery("select t.id from Transaction t where t.account.id = :userId");
        query.setParameter("userId", userId);

        List<Long> transactionIds = query.getResultList();
        if (CollectionUtils.isNotEmpty(transactionIds)) {
            for (Long transactionId : transactionIds) {
                glTransactions.addAll(removeAllAllocations(transactionId));
            }
        }

        return glTransactions;
    }

    /**
     * Removes all allocations (locked and non-locked) associated with the given transaction list.
     * <p/>
     *
     * @param transactions list of transactions for which allocations have to be removed
     * @return list of generated GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public List<GlTransaction> removeAllAllocations(List<Transaction> transactions) {

        List<GlTransaction> glTransactions = new LinkedList<GlTransaction>();
        for (Transaction transaction : transactions) {
            glTransactions.addAll(removeAllAllocations(transaction.getId()));
        }

        return glTransactions;
    }

    /**
     * Removes non-locked allocations associated with the given transaction list.
     * <p/>
     *
     * @param transactions list of transactions for which allocations have to be removed
     * @return list of generated GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public List<GlTransaction> removeAllocations(List<Transaction> transactions) {

        List<GlTransaction> glTransactions = new LinkedList<GlTransaction>();
        for (Transaction transaction : transactions) {
            glTransactions.addAll(removeAllocations(transaction.getId()));
        }

        return glTransactions;
    }

    /**
     * Removes allocation between two given transactions
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @return list of GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public List<GlTransaction> removeAllocation(Long transactionId1, Long transactionId2) {
        return removeAllocation(transactionId1, transactionId2, true, false);
    }


    /**
     * Removes allocation between two given transactions
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param isQueued       indicates whether the GL transaction should be in Q or W status
     * @return list of GL transactions
     */
    @Override
    @Transactional(readOnly = false)
    public List<GlTransaction> removeAllocation(Long transactionId1, Long transactionId2, boolean isQueued) {
        return removeAllocation(transactionId1, transactionId2, isQueued, false);
    }

    /**
     * Removes locked allocation between two given transactions
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @return list of GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    public List<GlTransaction> removeLockedAllocation(Long transactionId1, Long transactionId2) {
        return removeAllocation(transactionId1, transactionId2, true, true);
    }

    /**
     * Removes locked allocation between two given transactions
     * <p/>
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param isQueued       indicates whether the GL transaction should be in Q or W status
     * @return list of GL transactions
     */
    @Override
    @Transactional(readOnly = false)
    public List<GlTransaction> removeLockedAllocation(Long transactionId1, Long transactionId2, boolean isQueued) {
        return removeAllocation(transactionId1, transactionId2, isQueued, true);
    }

    protected List<GlTransaction> removeAllocation(Allocation allocation, boolean isQueued) {

        PermissionUtils.checkPermissions(allocation,
                Permission.REMOVE_ALLOCATION,
                Permission.REMOVE_LOCKED_ALLOCATION,
                Permission.REMOVE_INTERNALLY_LOCKED_ALLOCATION);

        List<GlTransaction> glTransactions = new LinkedList<GlTransaction>();

        boolean locked = allocation.isLocked();

        Transaction transaction1 = allocation.getFirstTransaction();
        Transaction transaction2 = allocation.getSecondTransaction();

        BigDecimal allocatedAmount = (allocation.getAmount() != null) ? allocation.getAmount() : BigDecimal.ZERO;

        BigDecimal allocatedAmount1 = locked ? transaction1.getLockedAllocatedAmount() :
                transaction1.getAllocatedAmount();

        BigDecimal allocatedAmount2 = locked ? transaction2.getLockedAllocatedAmount() :
                transaction2.getAllocatedAmount();

        if (allocatedAmount1 == null) {
            allocatedAmount1 = BigDecimal.ZERO;
        }

        if (allocatedAmount2 == null) {
            allocatedAmount2 = BigDecimal.ZERO;
        }

        BigDecimal newAmount1 = allocatedAmount1.subtract(allocatedAmount);
        BigDecimal newAmount2 = allocatedAmount2.subtract(allocatedAmount);

        if (locked) {
            transaction1.setLockedAllocatedAmount(newAmount1);
            transaction2.setLockedAllocatedAmount(newAmount2);
        } else {
            transaction1.setAllocatedAmount(newAmount1);
            transaction2.setAllocatedAmount(newAmount2);
        }

        deleteEntity(allocation.getId(), Allocation.class);

        // Creating GL transactions for charge+payment or payment+charge only
        if ((transaction1.getTransactionTypeValue() == TransactionTypeValue.CHARGE &&
                transaction2.getTransactionTypeValue() == TransactionTypeValue.PAYMENT) ||
                (transaction1.getTransactionTypeValue() == TransactionTypeValue.PAYMENT &&
                        transaction2.getTransactionTypeValue() == TransactionTypeValue.CHARGE)) {

            String statement = configService.getParameter(Constants.DEFAULT_GL_PA_STATEMENT);

            Pair<GlTransaction, GlTransaction> pair =
                    createGlTransactions(transaction1, transaction2, allocatedAmount, statement, false, isQueued);

            glTransactions.add(pair.getA());
            glTransactions.add(pair.getB());
        }

        return glTransactions;
    }

    protected List<GlTransaction> removeAllocation(Long transactionId1, Long transactionId2,
                                                   boolean isQueued, boolean locked) {

        Transaction transaction1 = getTransaction(transactionId1);
        if (transaction1 == null) {
            String errMsg = "Transaction with ID = " + transactionId1 + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        Transaction transaction2 = getTransaction(transactionId2);
        if (transaction2 == null) {
            String errMsg = "Transaction with ID = " + transactionId2 + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        Query query = em.createQuery("select distinct a from Allocation a " +
                " where ((a.firstTransaction.id = :id1 and a.secondTransaction.id = :id2) or " +
                " (a.firstTransaction.id = :id2 and a.secondTransaction.id = :id1)) and a.locked = :locked");

        query.setParameter("id1", transactionId1);
        query.setParameter("id2", transactionId2);
        query.setParameter("locked", locked);

        List<Allocation> allocations = query.getResultList();

        List<GlTransaction> glTransactions = new LinkedList<GlTransaction>();

        if (CollectionUtils.isNotEmpty(allocations)) {

            for (Allocation allocation : new HashSet<Allocation>(allocations)) {
                glTransactions.addAll(removeAllocation(allocation, isQueued));
            }

        } else {
            logger.warn("Allocation does not exist for transactions: '" + transactionId1 +
                    "' and '" + transactionId2 + "'");
        }

        return glTransactions;

    }

    private List<Long> makeTransactionsEffective(List<Long> transactionIds, boolean forceEffective) {

        boolean isEffective = false;

        List<Long> effectiveTransactionIds = new LinkedList<Long>();

        if (CollectionUtils.isNotEmpty(transactionIds)) {

            DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            transactionDefinition.setPropagationBehavior(getTransactionBatchPropagation());
            transactionDefinition.setTimeout(3600);

            org.springframework.transaction.TransactionStatus userTransaction = getTransaction(transactionDefinition);

            final int transactionBatchSize = getTransactionBatchSize();

            int transactionCommitCount = 0;

            for (Long transactionId : transactionIds) {

                boolean result = false;

                try {
                    result = makeEffective(transactionId, forceEffective);
                } catch (GlTransactionFailedException e) {
                    logFailedGlTransaction(transactionId, e);
                }

                if (result) {

                    if (++transactionCommitCount > transactionBatchSize) {
                        commit(userTransaction);
                        userTransaction = getTransaction(transactionDefinition);
                        transactionCommitCount = 0;
                    }

                    if (!isEffective) {
                        isEffective = true;
                    }

                    effectiveTransactionIds.add(transactionId);
                }

                if (transactionCommitCount != 0) {
                    commit(userTransaction);
                }
            }
        }

        return effectiveTransactionIds;
    }

    /**
     * Makes effective all failed transactions for which GL entries have not been generated yet.
     *
     * @return the number of effective transactions
     */
    @Override
    @Transactional(readOnly = false, timeout = 3600)
    @PermissionsAllowed(Permission.CREATE_GL_TRANSACTION)
    public int makeFailedTransactionsEffective() {

        Query query = em.createQuery("select distinct f.transaction.id from FailedGlTransaction f " +
                " where f.fixed <> true and f.transaction.id <> null");

        List<Long> transactionIds = query.getResultList();

        if (CollectionUtils.isNotEmpty(transactionIds)) {

            List<Long> effectiveTransactionIds = makeTransactionsEffective(new ArrayList<Long>(transactionIds), true);

            if (CollectionUtils.isNotEmpty(effectiveTransactionIds)) {
                query = em.createQuery("update FailedGlTransaction set fixed = true where transaction.id in (:ids)");
                query.setParameter("ids", effectiveTransactionIds);
                query.executeUpdate();
                return effectiveTransactionIds.size();
            }
        }

        return 0;
    }

    /**
     * Makes effective all transactions for which GL entries have not been generated yet.
     *
     * @param forceEffective indicates whether it has to be forced
     * @return the number of effective transactions
     */
    @Override
    @Transactional(readOnly = false, timeout = 3600)
    @PermissionsAllowed(Permission.CREATE_GL_TRANSACTION)
    public int makeAllTransactionsEffective(boolean forceEffective) {

        StringBuilder queryBuilder = new StringBuilder("select id from Transaction where glEntryGenerated <> true");

        if (!forceEffective) {
            queryBuilder.append(" and effectiveDate <= CURRENT_DATE");
        }

        Query query = em.createQuery(queryBuilder.toString());

        List<Long> transactionIds = query.getResultList();

        if (CollectionUtils.isNotEmpty(transactionIds)) {
            List<Long> effectiveTransactionIds = makeTransactionsEffective(transactionIds, forceEffective);
            return effectiveTransactionIds.size();
        }

        return 0;
    }

    /**
     * Moves a transaction from a pre-effective state to an effective state. Once a transaction is effective, its
     * general ledger entries are created. In certain cases, a transaction might be moved to an effective state
     * before its effective date, in which case, forceEffective is passed as true.
     *
     * @param transactionId  transaction ID
     * @param forceEffective indicates whether it has to be forced
     * @return true if the transaction has been made effective, false - otherwise
     * @throws GlTransactionFailedException
     */
    @Override
    @Transactional(readOnly = false, noRollbackFor = GlTransactionFailedException.class)
    @PermissionsAllowed(Permission.CREATE_GL_TRANSACTION)
    public boolean makeEffective(Long transactionId, boolean forceEffective) throws GlTransactionFailedException {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

        if (transaction.isGlEntryGenerated()) {
            String errMsg = "GL entry has already been generated for Transaction with ID = " + transactionId;
            logger.error(errMsg);
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

        if (!forceEffective && transaction.getEffectiveDate().after(new Date())) {
            String errMsg = "Effective date should be before or equal to the current date, Transaction ID = " + transactionId;
            logger.error(errMsg);
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

        if (transaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT) {
            logger.info("Transaction is a deferment, ID = " + transactionId);
            transaction.setGlEntryGenerated(true);
            return true;
        }

        GeneralLedgerType glType = transaction.getGeneralLedgerType();

        String glAccount = null;
        GlOperationType glOperationType = null;

        if (transaction.getTransactionType() instanceof CreditType) {
            CreditType creditType = (CreditType) transaction.getTransactionType();
            glAccount = creditType.getUnallocatedGlAccount();
            glOperationType = creditType.getUnallocatedGlOperation();
        } else if (glType != null) {
            glAccount = glType.getGlAccountId();
            glOperationType = glType.getGlOperationOnCharge();
        }

        if (glAccount == null) {
            String errMsg = "No GL Account found for Transaction with ID = " + transactionId;
            logger.error(errMsg);
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

        if (glOperationType == null) {
            String errMsg = "No GL Operation Type found for Transaction with ID = " + transactionId;
            logger.error(errMsg);
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

        List<AbstractGlBreakdown> glBreakdowns = glService.getGlBreakdowns(transaction);

        if (CollectionUtils.isNotEmpty(glBreakdowns)) {

            if (!glService.isGlBreakdownValid(glBreakdowns)) {
                String errMsg = "GL Breakdowns are invalid: " + glBreakdowns;
                logger.error(errMsg);
                throw new GlTransactionFailedException(transactionId, errMsg);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_EXPORT);

            final String statement = dateFormat.format(transaction.getEffectiveDate()) + ":" +
                    transaction.getAccount().getId() + ":" +
                    transaction.getTransactionType().getId().getId() + ":" +
                    transaction.getTransactionTypeValue() + ":" +
                    transaction.getStatementText();

            // Creating one GL transaction with the whole transaction amount
            glService.createGlTransaction(transactionId, glAccount, transaction.getAmount(), glOperationType, statement, true);

            BigDecimal initialAmount = transaction.getAmount();
            BigDecimal remainingAmount = initialAmount;

            for (AbstractGlBreakdown glBreakdown : glBreakdowns) {

                BigDecimal percentage = glBreakdown.getBreakdown();
                if (percentage == null) {
                    String errMsg = "Breakdown percentage cannot be null, Transaction ID = " + transactionId;
                    logger.error(errMsg);
                    throw new GlTransactionFailedException(transactionId, errMsg);
                }

                glAccount = glBreakdown.getGlAccount();

                GlOperationType operationType;

                if (glBreakdown instanceof GlBreakdown) {
                    operationType = ((GlBreakdown) glBreakdown).getGlOperation();
                } else {
                    operationType = (transaction.getTransactionTypeValue() == TransactionTypeValue.CHARGE) ?
                            GlOperationType.CREDIT : GlOperationType.DEBIT;
                }

                if (percentage.compareTo(BigDecimal.ZERO) > 0) {

                    BigDecimal amount = initialAmount.divide(Constants.BIG_DECIMAL_HUNDRED).multiply(percentage).setScale(2, RoundingMode.HALF_DOWN);

                    glService.createGlTransaction(transactionId, glBreakdown.getGlAccount(), amount, operationType, statement, true);

                    remainingAmount = remainingAmount.subtract(amount);

                } else {

                    // If the remaining amount == 0 then apply it to the new GL transaction and exit
                    // considering that GL breakdowns are sorted by percentage in descending order :)
                    glService.createGlTransaction(transactionId, glAccount, remainingAmount, operationType, statement, true);
                    break;
                }
            }

            transaction.setGlEntryGenerated(true);

            return true;

        } else {
            String errMsg = "No GL Breakdowns found for Transaction with ID = " + transactionId;
            logger.error(errMsg);
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

    }

    /**
     * This method is used to apply “obvious” payments to their reversal. Under normal circumstances, this will not be needed,
     * as reversals created inside of KSA will automatically be locked together. However, after an import from an external system,
     * this allocation may not exist. This method is provided to ensure that transactions that are obviously designed to be together,
     * are allocated together. “Obvious” means they are entirely unallocated, have the same amounts,
     * but one is negated, and they have the same transaction type.
     * <p/>
     *
     * @param accountId Account ID
     * @return list of generated GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_ALLOCATION)
    public List<GlTransaction> allocateReversals(String accountId) {
        return allocateReversals(accountId, true);
    }


    /**
     * This method is used to apply “obvious” payments to their reversal. Under normal circumstances, this will not be needed,
     * as reversals created inside of KSA will automatically be locked together. However, after an import from an external system,
     * this allocation may not exist. This method is provided to ensure that transactions that are obviously designed to be together,
     * are allocated together. “Obvious” means they are entirely unallocated, have the same amounts,
     * but one is negated, and they have the same transaction type.
     * <p/>
     *
     * @param accountId Account ID
     * @param isQueued  indicates whether the GL transaction should be in Q or W status
     * @return list of generated GL transactions
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_ALLOCATION)
    public List<GlTransaction> allocateReversals(String accountId, boolean isQueued) {
        return allocateReversals(getTransactions(accountId), isQueued);
    }

    /**
     * An overridden version of allocateReversals() that takes a list of transactions as an argument.
     *
     * @param transactions list of transactions
     * @return list of generated GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_ALLOCATION)
    public List<GlTransaction> allocateReversals(List<Transaction> transactions) {
        return allocateReversals(transactions, true);
    }

    /**
     * An overridden version of allocateReversals() that takes a list of transactions as an argument.
     *
     * @param transactions list of transactions
     * @param isQueued     indicates whether the GL transaction should be in Q or W status
     * @return list of generated GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_ALLOCATION)
    public List<GlTransaction> allocateReversals(List<Transaction> transactions, boolean isQueued) {

        List<GlTransaction> glTransactions = new LinkedList<GlTransaction>();
        List<Transaction> unallocatedTransactions = new LinkedList<Transaction>();

        for (Transaction transaction : transactions) {

            BigDecimal allocatedAmount = transaction.getAllocatedAmount() != null ?
                    transaction.getAllocatedAmount() : BigDecimal.ZERO;

            BigDecimal lockedAllocatedAmount = transaction.getLockedAllocatedAmount() != null ?
                    transaction.getLockedAllocatedAmount() : BigDecimal.ZERO;

            if (allocatedAmount.compareTo(BigDecimal.ZERO) == 0 && lockedAllocatedAmount.compareTo(BigDecimal.ZERO) == 0) {
                unallocatedTransactions.add(transaction);
            }

        }

        Set<Transaction> unprocessedTransactions = new HashSet<Transaction>(unallocatedTransactions);
        for (Transaction transaction : unallocatedTransactions) {
            Transaction reversal = findReversal(unprocessedTransactions, transaction);
            if (reversal != null) {
                CompositeAllocation allocation =
                        createAllocation(transaction, reversal, transaction.getAmount().abs(), isQueued, false, false);
                glTransactions.add(allocation.getCreditGlTransaction());
                glTransactions.add(allocation.getDebitGlTransaction());
                unprocessedTransactions.remove(transaction);
                unprocessedTransactions.remove(reversal);
            }
        }

        return glTransactions;
    }

    /**
     * Finds a transaction with a negated amount for the given transaction from the given list.
     *
     * @param transactions Collection of transactions
     * @param transaction  Transaction instance
     * @return Transaction reversal
     */

    protected Transaction findReversal(Collection<Transaction> transactions, Transaction transaction) {
        for (Transaction reversal : transactions) {
            if (transaction.getAmount() != null && reversal.getAmount() != null) {
                if (transaction.getAmount().add(reversal.getAmount()).compareTo(BigDecimal.ZERO) == 0) {
                    return reversal;
                }
            }
        }
        return null;
    }

    protected Transaction reverseTransaction(Transaction transaction, String reversalTransactionTypeId, String memoText,
                                             BigDecimal reversalAmount, String statementPrefix, TransactionStatus reversalStatus) {

        if (reversalTransactionTypeId == null) {
            String errMsg = "Reversal transaction type ID cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        // TODO: ask Paul whether we have to use the current date or transaction effective date
        TransactionType reversalTransactionType = getTransactionType(reversalTransactionTypeId, new Date());
        if (reversalTransactionType == null) {
            String errMsg = "Invalid Transaction Type, ID = " + reversalTransactionTypeId;
            logger.error(errMsg);
            throw new InvalidTransactionTypeException(errMsg);
        }

        if (transaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT) {
            String errMsg = "Transaction is a deferment and cannot be reversed, ID = " + transaction.getId();
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        if (transaction.getAmount() == null) {
            String errMsg = "Transaction with ID = " + transaction.getId() + " does not have any amount";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        if (reversalAmount == null || BigDecimal.ZERO.compareTo(reversalAmount) == 0) {
            String errMsg = "Reversal amount cannot be null or 0";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        TransactionStatus transactionStatus = transaction.getStatus();
        if (transactionStatus == null) {
            String errMsg = "Transaction status must not be null";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        if (transactionStatus == TransactionStatus.RECIPROCAL_OFFSET ||
                transactionStatus == TransactionStatus.REFUND_REQUESTED) {
            String errMsg = "Transactions in status " + transactionStatus.name() + " cannot be reversed";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        BigDecimal absoluteReversalAmount = reversalAmount.abs();

        if (transactionStatus != TransactionStatus.ACTIVE) {

            if (absoluteReversalAmount.compareTo(transaction.getAmount().abs()) != 0) {
                String errMsg = "Reversal amount must equal transaction amount";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            List<Allocation> allocations = getAllocations(transaction.getId());
            if (CollectionUtils.isNotEmpty(allocations)) {
                for (Allocation allocation : allocations) {

                    Transaction transaction1 = allocation.getFirstTransaction();
                    Transaction transaction2 = allocation.getSecondTransaction();

                    // Getting the other (implicated) transaction from the mutual allocation
                    Transaction implicatedTransaction = transaction1.getId().equals(transaction.getId()) ?
                            transaction2 : transaction1;

                    // Removing the allocation between the original and implicated transactions
                    removeAllocation(allocation, true);

                    boolean isAllocatedToNonActiveTransaction = false;

                    List<Allocation> implicatedAllocations = getAllocations(implicatedTransaction.getId());
                    if (CollectionUtils.isNotEmpty(implicatedAllocations)) {
                        for (Allocation implicatedAllocation : implicatedAllocations) {

                            Transaction implicatedTransaction1 = implicatedAllocation.getFirstTransaction();
                            Transaction implicatedTransaction2 = implicatedAllocation.getSecondTransaction();

                            // Getting the other transaction from the allocation with the implicated transaction
                            Transaction transactionToCheck =
                                    implicatedTransaction.getId().equals(implicatedTransaction1.getId()) ?
                                            implicatedTransaction2 : implicatedTransaction1;

                            // If the implicated transaction has allocations with any other transaction with non-active
                            // status then set its "isOffset" property to true
                            if (!transactionToCheck.getId().equals(transaction.getId()) &&
                                    transactionToCheck.getStatus() != TransactionStatus.ACTIVE) {
                                isAllocatedToNonActiveTransaction = true;
                                break;
                            }
                        }
                    }

                    // Setting "isOffset" property of the implicated transaction
                    implicatedTransaction.setOffset(isAllocatedToNonActiveTransaction);
                }
            }
        }

        BigDecimal unallocatedAmount = transaction.getUnallocatedAmount().setScale(2, RoundingMode.HALF_DOWN);

        if (absoluteReversalAmount.setScale(2, RoundingMode.HALF_DOWN).compareTo(unallocatedAmount) > 0) {

            // Removing all regular allocations which the transaction is involved in
            removeAllAllocations(transaction.getId());

            // Updating the transaction instance
            transaction = getTransaction(transaction.getId());

            // Checking the unallocated amount again
            unallocatedAmount = transaction.getUnallocatedAmount().setScale(2, RoundingMode.HALF_DOWN);

            if (absoluteReversalAmount.setScale(2, RoundingMode.HALF_DOWN).compareTo(unallocatedAmount) > 0) {
                logger.info("Transaction ID = " + transaction.getId() + ", reversal amount = " +
                        TransactionUtils.formatAmount(absoluteReversalAmount));
                logger.info("Transaction ID = " + transaction.getId() + ", unallocated amount = " +
                        TransactionUtils.formatAmount(unallocatedAmount));
                String errMsg = "Reversal amount cannot be greater than transaction unallocated amount";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }
        }

        // If the given Transaction Type is the same as Original Transaction's type then we have to negate
        // the reversal amount only if the reversal amount and transaction amount have different signs
        if (reversalTransactionType.getTypeValue().equals(transaction.getTransactionType().getTypeValue())) {
            if ((transaction.getAmount().compareTo(BigDecimal.ZERO) > 0 && reversalAmount.compareTo(BigDecimal.ZERO) > 0) ||
                    (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0 && reversalAmount.compareTo(BigDecimal.ZERO) < 0)) {
                reversalAmount = reversalAmount.negate();
            }
        }

        // Creating a new reverse transaction with the negated amount of the original transaction
        Transaction reverseTransaction = createTransaction(reversalTransactionTypeId, transaction.getAccountId(),
                transaction.getEffectiveDate(), reversalAmount);

        Set<Tag> transactionTags = transaction.getTags();
        if (CollectionUtils.isNotEmpty(transactionTags)) {
            reverseTransaction.setTags(new HashSet<Tag>(transactionTags));
        }

        reverseTransaction.setGlOverridden(transaction.isGlOverridden());
        reverseTransaction.setRollup(transaction.getRollup());

        if (StringUtils.isNotBlank(statementPrefix) && StringUtils.isNotBlank(transaction.getStatementText())) {
            reverseTransaction.setStatementText(statementPrefix + " " + transaction.getStatementText());
        }

        if (reversalStatus != null) {
            reverseTransaction.setStatus(reversalStatus);
        } else {
            if (transactionStatus == TransactionStatus.ACTIVE) {
                reverseTransaction.setStatus(TransactionStatus.REVERSING);
            } else {
                reverseTransaction.setStatus(TransactionStatus.RECIPROCAL_OFFSET);
                reverseTransaction.setInternal(true);
            }
        }

        if (transactionStatus == TransactionStatus.ACTIVE) {
            transaction.setOffset(true);
        } else {
            transaction.setStatus(TransactionStatus.RECIPROCAL_OFFSET);
            transaction.setInternal(true);
        }

        // Creating a locked allocation between the original and reversed transactions
        createLockedAllocation(transaction.getId(), reverseTransaction.getId(), absoluteReversalAmount);

        // Creating memo
        if (StringUtils.isNotBlank(memoText)) {
            String memoAccessLevelCode = informationService.getDefaultMemoLevel();
            informationService.createMemo(transaction.getId(), memoText, memoAccessLevelCode, new Date(), null, null);
        }

        return reverseTransaction;
    }

    /**
     * If the reverse method is called, the system will generate a negative
     * transaction for the type of the original transaction. A memo transaction
     * will be generated, and the transactions will be locked together. Subject
     * to user customization, the transactions may be marked as hidden. (likely
     * that credits will not be hidden, debits will.) A charge to an account may
     * be reversed when a mistake is made, or a refund is issued. A payment may
     * be reversed when a payment bounces, or for some other reason is entered
     * on to the account and is not payable.
     *
     * @param transactionId   Transaction ID
     * @param memoText        Text of the memo to be created
     * @param reversalAmount  Reversal amount
     * @param statementPrefix Statement prefix that will be added to the existing Transaction statement
     * @param reversalStatus  Transaction status of the reversal (can be null)
     * @return a created reversed transaction
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REVERSE_TRANSACTION)
    public Transaction reverseTransaction(Long transactionId, String memoText, BigDecimal reversalAmount,
                                          String statementPrefix, TransactionStatus reversalStatus) {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        String transactionTypeId = transaction.getTransactionType().getId().getId();

        return reverseTransaction(transaction, transactionTypeId, memoText, reversalAmount, statementPrefix, reversalStatus);
    }

    /**
     * If the reverse method is called, the system will generate a negative
     * transaction for the type of the original transaction. A memo transaction
     * will be generated, and the transactions will be locked together. Subject
     * to user customization, the transactions may be marked as hidden. (likely
     * that credits will not be hidden, debits will.) A charge to an account may
     * be reversed when a mistake is made, or a refund is issued. A payment may
     * be reversed when a payment bounces, or for some other reason is entered
     * on to the account and is not payable.
     *
     * @param transactionId  Transaction ID
     * @param memoText       Text of the memo to be created
     * @param reversalAmount Reversal amount
     * @return a created reversal transaction
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REVERSE_TRANSACTION)
    public Transaction reverseTransaction(Long transactionId, String memoText, BigDecimal reversalAmount) {
        return reverseTransaction(transactionId, memoText, reversalAmount, null);
    }

    /**
     * If the reverse method is called, the system will generate a negative
     * transaction for the type of the original transaction. A memo transaction
     * will be generated, and the transactions will be locked together. Subject
     * to user customization, the transactions may be marked as hidden. (likely
     * that credits will not be hidden, debits will.) A charge to an account may
     * be reversed when a mistake is made, or a refund is issued. A payment may
     * be reversed when a payment bounces, or for some other reason is entered
     * on to the account and is not payable.
     *
     * @param transactionId   Transaction ID
     * @param memoText        Text of the memo to be created
     * @param reversalAmount  Reversal amount
     * @param statementPrefix Statement prefix that will be added to the existing Transaction statement
     * @return a created reversed transaction
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REVERSE_TRANSACTION)
    public Transaction reverseTransaction(Long transactionId, String memoText, BigDecimal reversalAmount,
                                          String statementPrefix) {
        return reverseTransaction(transactionId, memoText, reversalAmount, statementPrefix, null);
    }

    /**
     * If the reverse method is called, the system will generate a negative
     * transaction for the type of the original transaction. A memo transaction
     * will be generated, and the transactions will be locked together. Subject
     * to user customization, the transactions may be marked as hidden. (likely
     * that credits will not be hidden, debits will.) A charge to an account may
     * be reversed when a mistake is made, or a refund is issued. A payment may
     * be reversed when a payment bounces, or for some other reason is entered
     * on to the account and is not payable.
     *
     * @param transactionId             Transaction ID
     * @param reversalTransactionTypeId Transaction Type ID of the reverse transaction
     * @param memoText                  Text of the memo to be created
     * @param reversalAmount            Reversal amount
     * @param statementPrefix           Statement prefix that will be added to the existing Transaction statement
     * @param reversalStatus            Transaction status of the reversal (can be null)
     * @return a created reversed transaction
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.REVERSE_TRANSACTION)
    public Transaction reverseTransaction(Long transactionId, String reversalTransactionTypeId, String memoText,
                                          BigDecimal reversalAmount, String statementPrefix, TransactionStatus reversalStatus) {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        return reverseTransaction(transaction, reversalTransactionTypeId, memoText, reversalAmount, statementPrefix, reversalStatus);
    }

    protected void expireDeferment(Deferment deferment) {

        // Removing all allocations for the deferment
        removeAllAllocations(deferment.getId());

        deferment.setInternal(true);
        deferment.setAmount(BigDecimal.ZERO);
        deferment.setExpirationDate(new Date());
        deferment.setStatus(TransactionStatus.EXPIRED);

        persistTransaction(deferment);
    }

    /**
     * A deferment may be expired automatically (when the date of the deferment
     * passes) or be expired manually but the system, either through user
     * intervention, or by a payment being received on the account that removes
     * the need for the deferment. If, for example, an account is paid in full,
     * the deferment would have to be expired, otherwise a credit balance would
     * technically occur on the account.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.EXPIRE_DEFERMENT)
    public void expireDeferment(Long defermentId) {

        Deferment deferment = getDeferment(defermentId);
        if (deferment == null) {
            String errMsg = "Deferment with ID = " + defermentId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        expireDeferment(deferment);
    }

    /**
     * Expires all deferments on the account whose expiration date has passed
     *
     * @param userId Account ID
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.EXPIRE_DEFERMENT)
    public void expireDeferments(String userId) {

        Date currentDate = new Date();

        List<Deferment> deferments = getDeferments(userId);

        for (Deferment deferment : deferments) {
            Date expirationDate = deferment.getExpirationDate();
            if (expirationDate != null && expirationDate.before(currentDate)) {
                expireDeferment(deferment);
            }
        }
    }


    /**
     * Returns the transaction type for the given transaction type ID
     *
     * @param transactionTypeId The first part of TransactionTypeId PK
     * @return a subclass of TransactionType
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION_TYPE)
    public <T extends TransactionType> Class<T> getTransactionTypeClass(String transactionTypeId) {

        Query query = em.createQuery("select t from TransactionType t " +
                " where t.id.id = :transactionTypeId");
        query.setParameter("transactionTypeId", transactionTypeId);
        List<T> transactionTypes = query.getResultList();
        if (transactionTypes != null && !transactionTypes.isEmpty()) {
            T transactionType = transactionTypes.get(0);
            return (transactionType != null) ? (Class<T>) transactionType.getClass() : null;
        }
        return null;
    }

    /**
     * Checks if the first transaction can pay the second transaction.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @return true if transactionId1 can pay transactionId2, false - otherwise
     */
    @Override
    @WebMethod(exclude = true)
    public boolean canPay(Long transactionId1, Long transactionId2) {
        return canPay(transactionId1, transactionId2, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Checks if the first transaction can pay the second transaction.
     *
     * @param transaction1 First transaction
     * @param transaction2 Second transaction
     * @return true if transaction1 can pay transaction2, false - otherwise
     */
    @Override
    @WebMethod(exclude = true)
    public boolean canPay(Transaction transaction1, Transaction transaction2) {
        return canPayInternal(transaction1, transaction2, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }


    /**
     * Checks if the first transaction can pay the second transaction.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param priority       priority
     * @return true if transactionId1 can pay transactionId2, false - otherwise
     */
    @Override
    @WebMethod(exclude = true)
    public boolean canPay(Long transactionId1, Long transactionId2, int priority) {
        return canPay(transactionId1, transactionId2, priority, priority);
    }


    /**
     * Checks if the first transaction can pay the second transaction.
     *
     * @param transactionId1 transaction1 ID
     * @param transactionId2 transaction2 ID
     * @param priorityFrom   lower priority boundary
     * @param priorityTo     upper priority boundary
     * @return true if transactionId1 can pay transactionId2, false - otherwise
     */
    @Override
    public boolean canPay(Long transactionId1, Long transactionId2, int priorityFrom, int priorityTo) {

        if (priorityFrom > priorityTo) {
            String errMsg = "priorityFrom must be less or equal to priorityTo";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Transaction transaction1 = getTransaction(transactionId1);
        if (transaction1 == null) {
            String errMsg = "Transaction with ID = " + transactionId1 + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        Transaction transaction2 = getTransaction(transactionId2);
        if (transaction2 == null) {
            String errMsg = "Transaction with ID = " + transactionId2 + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        return canPayInternal(transaction1, transaction2, priorityFrom, priorityTo);
    }

    protected boolean canPayInternal(Transaction transaction1, Transaction transaction2, int priorityFrom, int priorityTo) {

        boolean compatible = false;

        TransactionType transactionType1 = transaction1.getTransactionType();
        TransactionType transactionType2 = transaction2.getTransactionType();

        BigDecimal transactionAmount1 = transaction1.getAmount();
        BigDecimal transactionAmount2 = transaction2.getAmount();

        if (transactionAmount1 != null && transactionAmount2 != null) {
            double amount1 = transactionAmount1.doubleValue();
            double amount2 = transactionAmount2.doubleValue();
            if (transactionType1.getTypeValue().equals(transactionType2.getTypeValue())) {
                if ((amount1 > 0 && amount2 < 0) || (amount1 < 0 && amount2 > 0)) {
                    compatible = true;
                }
            } else {
                if ((amount1 > 0 && amount2 > 0) || (amount1 < 0 && amount2 < 0)) {
                    compatible = true;
                }
            }
        }

        if (!compatible) {
            logger.warn("Transactions " + transaction1.getId() + " and " + transaction2.getId() + " have incompatible amounts");
            return false;
        }

        SimpleTransactionTypeVisitor transactionTypeVisitor = SimpleTransactionTypeVisitor.getInstance();

        transactionType1.accept(transactionTypeVisitor);
        transactionType2.accept(transactionTypeVisitor);

        final CreditType creditType = transactionTypeVisitor.getCreditType();
        final DebitType debitType = transactionTypeVisitor.getDebitType();

        if (creditType != null && debitType != null) {

            TransactionTypeId creditTypeId = creditType.getId();
            TransactionTypeId debitTypeId = debitType.getId();

            List<CreditPermission> creditPermissions = getCreditPermissions(creditTypeId, priorityFrom, priorityTo);

            if (CollectionUtils.isNotEmpty(creditPermissions)) {
                for (CreditPermission creditPermission : creditPermissions) {
                    String debitTypeMask = creditPermission.getAllowableDebitType();
                    logger.info("Debit type mask: " + debitTypeMask);
                    if (debitTypeMask != null && Pattern.matches(debitTypeMask, debitTypeId.getId())) {
                        return true;
                    }
                }
            }

            logger.warn("Credit Type '" + creditTypeId.getId() +
                    "' does not have appropriate credit permissions for Debit Type '" + debitTypeId.getId() + "'");

            return false;
        }

        return true;
    }

    /**
     * Creates and persist a new instance of CreditPermission class.
     *
     * @param creditTypeId       Credit type ID
     * @param allowableDebitType Allowable debit type mask (can be a regular expression)
     * @param priority           Priority value
     * @return new persistent CreditPermission instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_CREDIT_PERMISSION)
    public CreditPermission createCreditPermission(TransactionTypeId creditTypeId, String allowableDebitType, int priority) {

        if (creditTypeId == null) {
            String errMsg = "Credit Type ID is required";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (StringUtils.isBlank(allowableDebitType)) {
            String errMsg = "Allowable debit type mask cannot be empty";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        CreditType creditType = getEntity(creditTypeId, CreditType.class);
        if (creditType == null) {
            String errMsg = "Credit Type with ID = " + creditTypeId + " does not exist";
            logger.error(errMsg);
            throw new InvalidTransactionTypeException(errMsg);
        }

        CreditPermission creditPermission = new CreditPermission();
        creditPermission.setCreditType(creditType);
        creditPermission.setAllowableDebitType(allowableDebitType);
        creditPermission.setPriority(priority);

        persistEntity(creditPermission);

        return creditPermission;
    }

    /**
     * Persists CreditPermission instance in the persistent store.
     *
     * @param creditPermission CreditPermission instance
     * @return CreditPermission ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_CREDIT_PERMISSION)
    public Long persistCreditPermission(CreditPermission creditPermission) {
        return persistEntity(creditPermission);
    }

    /**
     * Removes CreditPermission instance from the persistent store by ID.
     *
     * @param creditPermissionId CreditPermission ID
     * @return true, if the credit permission has been deleted, false - otherwise
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_CREDIT_PERMISSION)
    public boolean deleteCreditPermission(Long creditPermissionId) {
        return deleteEntity(creditPermissionId, CreditPermission.class);
    }

    /**
     * Returns a list of credit permissions for the given transaction type sorted by
     * priority in descending order.
     *
     * @param transactionTypeId Transaction Type ID
     * @return list of CreditPermission instances
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_CREDIT_PERMISSION)
    public List<CreditPermission> getCreditPermissions(TransactionTypeId transactionTypeId) {
        return getCreditPermissions(transactionTypeId, null, null);
    }

    /**
     * Returns a list of credit permissions for the given transaction type and priority range sorted by
     * priority in descending order.
     *
     * @param transactionTypeId Transaction Type ID
     * @param priorityFrom      lower priority
     * @param priorityTo        upper priority
     * @return list of CreditPermission instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_CREDIT_PERMISSION)
    public List<CreditPermission> getCreditPermissions(TransactionTypeId transactionTypeId,
                                                       Integer priorityFrom, Integer priorityTo) {
        Set<TransactionTypeId> typeIds = new HashSet<TransactionTypeId>(1);
        typeIds.add(transactionTypeId);
        return getCreditPermissions(typeIds, priorityFrom, priorityTo);
    }

    /**
     * Returns a list of credit permissions for the given set of transaction types and priority range sorted by
     * priority in descending order.
     *
     * @param transactionTypeIds a set of Transaction Type IDs
     * @param priorityFrom       lower priority
     * @param priorityTo         upper priority
     * @return list of CreditPermission instances
     */
    private List<CreditPermission> getCreditPermissions(Set<TransactionTypeId> transactionTypeIds,
                                                        Integer priorityFrom, Integer priorityTo) {

        StringBuilder queryBuilder =
                new StringBuilder("select distinct cp from CreditPermission cp where cp.creditType.id in (:typeIds)");

        if (priorityFrom != null && priorityTo != null) {
            queryBuilder.append(" and cp.priority between :priorityFrom and :priorityTo");
        }

        queryBuilder.append(" order by cp.priority desc");

        Query query = em.createQuery(queryBuilder.toString());

        query.setParameter("typeIds", transactionTypeIds);

        if (priorityFrom != null && priorityTo != null) {
            query.setParameter("priorityFrom", priorityFrom);
            query.setParameter("priorityTo", priorityTo);
        }

        return query.getResultList();
    }

    /**
     * Determine if the transaction is allowed for the given account ID, transaction type and effective date
     *
     * @param accountId         Account ID
     * @param transactionTypeId Transaction Type ID
     * @param effectiveDate     Effective Date
     * @return true/false
     */
    @Override
    public boolean isTransactionAllowed(String accountId, String transactionTypeId, Date effectiveDate) {

        String userId = userSessionManager.getUserId();

        return accountService.accountExists(accountId) && transactionTypeExists(transactionTypeId) &&
                getAccessControlService().isTransactionTypeAllowed(userId, transactionTypeId);
    }

    /**
     * Returns the list of matching transactions for the given name pattern.
     *
     * @param pattern Statement text pattern
     * @return List of Transaction instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Transaction> findTransactionsByStatementPattern(String pattern) {

        boolean patternIsNotEmpty = (pattern != null) && !pattern.isEmpty();

        StringBuilder builder = new StringBuilder("select t from Transaction t " + GET_TRANSACTION_JOIN);

        if (patternIsNotEmpty) {
            builder.append(" where t.statementText like :pattern ");
        }

        builder.append(" order by t.id");

        Query query = em.createQuery(builder.toString());

        if (patternIsNotEmpty) {
            query.setParameter("pattern", "%" + pattern.toLowerCase() + "%");
        }

        return query.getResultList();
    }

    /**
     * The logic of this is very similar to reverseTransaction(), except a partial write off is allowed, and only
     * credits can be written off. Also, the institution can choose to write off charges to a different general
     * ledger account, instead of the original, permitting the writing off to a general "bad debt" account, if they
     * so choose.
     *
     * @param transaction     Transaction instance
     * @param transactionType TransactionType instance
     * @param memoText        Memo test
     * @param statementPrefix Transaction statement prefix
     * @return a write-off transaction instance
     */
    protected Transaction writeOffTransaction(Transaction transaction, TransactionType transactionType,
                                              String memoText, String statementPrefix) {

        if (transactionType == null) {
            String errMsg = "Transaction Type cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (transaction.getTransactionTypeValue() != TransactionTypeValue.CHARGE) {
            String errMsg = "Transaction must be a charge, Transaction ID = " + transaction.getId();
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        if (transaction.getStatus() != TransactionStatus.ACTIVE) {
            String errMsg = "Transaction must be active, Transaction ID = " + transaction.getId();
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        Query query = em.createQuery("select a from Allocation a " +
                " join fetch a.firstTransaction t1 " +
                " join fetch a.secondTransaction t2 " +
                " where (a.firstTransaction.id = :transactionId or " +
                " a.secondTransaction.id = :transactionId) and a.locked = true");

        query.setParameter("transactionId", transaction.getId());

        List<Allocation> lockedAllocations = query.getResultList();

        if (CollectionUtils.isNotEmpty(lockedAllocations)) {
            for (Allocation allocation : lockedAllocations) {
                Transaction firstTransaction = allocation.getFirstTransaction();
                Transaction secondTransaction = allocation.getSecondTransaction();
                if (firstTransaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT) {
                    expireDeferment(firstTransaction.getId());
                }
                if (secondTransaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT) {
                    expireDeferment(secondTransaction.getId());
                }
            }
        }

        BigDecimal writeOffAmount = transaction.getUnallocatedAmount();

        // Reverse the transaction
        Transaction writeOffTransaction = reverseTransaction(transaction, transactionType.getId().getId(), memoText,
                writeOffAmount, statementPrefix, TransactionStatus.WRITING_OFF);

        // Getting the default WriteOff Rollup code
        String rollupCode = configService.getParameter(Constants.DEFAULT_WRITE_OFF_ROLLUP);

        if (StringUtils.isNotBlank(rollupCode)) {
            Rollup rollup = auditableEntityService.getAuditableEntity(rollupCode, Rollup.class);
            if (rollup != null) {
                writeOffTransaction.setRollup(rollup);
            }
        }

        return writeOffTransaction;
    }

    /**
     * The logic of this is very similar to reverseTransaction(), except a partial write off is allowed, and only
     * credits can be written off. Also, the institution can choose to write off charges to a different general
     * ledger account, instead of the original, permitting the writing off to a general "bad debt" account, if they
     * so choose.
     *
     * @param transactionId     Transaction ID
     * @param transactionTypeId TransactionType ID
     * @param memoText          Memo test
     * @param statementPrefix   Transaction statement prefix
     * @return a write-off transaction instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.WRITE_OFF_TRANSACTION)
    public Transaction writeOffTransaction(Long transactionId, TransactionTypeId transactionTypeId,
                                           String memoText, String statementPrefix) {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        return writeOffTransaction(transaction, getTransactionType(transactionTypeId), memoText, statementPrefix);
    }

    /**
     * The logic of this is very similar to reverseTransaction(), except a partial write off is allowed, and only
     * credits can be written off. Also, the institution can choose to write off charges to a different general
     * ledger account, instead of the original, permitting the writing off to a general "bad debt" account, if they
     * so choose.
     *
     * @param transactionId   Transaction ID
     * @param memoText        Memo test
     * @param statementPrefix Transaction statement prefix
     * @return a write-off transaction instance
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.WRITE_OFF_TRANSACTION)
    public Transaction writeOffTransaction(Long transactionId, String memoText, String statementPrefix) {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        return writeOffTransaction(transaction, transaction.getTransactionType(), memoText, statementPrefix);
    }

    /**
     * Checks if Transactions that meet the specified search criteria exist.
     *
     * @param accountId         Account ID.
     * @param transactionTypeId Transaction Type ID.
     * @return <code>true</code> if at least one Transaction of the given type for the given account exists.
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public boolean transactionExists(String accountId, String transactionTypeId) {
        return transactionExistsInternal(accountId, transactionTypeId, null, null, null, null);
    }

    /**
     * Checks if Transactions that meet the specified search criteria exist.
     *
     * @param accountId         Account ID.
     * @param transactionTypeId Transaction Type ID.
     * @param effectiveDateFrom Transaction Effective Date beginning range (inclusive).
     * @param effectiveDateTo   Transaction Effective Date end range (inclusive).
     * @return <code>true</code> if at least one Transaction of the given type for the given account
     *         with the Effective Dates that fall into the specified range exists.
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public boolean transactionExists(String accountId, String transactionTypeId, Date effectiveDateFrom, Date effectiveDateTo) {
        return transactionExistsInternal(accountId, transactionTypeId, null, null, effectiveDateFrom, effectiveDateTo);
    }

    /**
     * Checks if Transactions that meet the specified search criteria exist.
     *
     * @param accountId         Account ID.
     * @param transactionTypeId Transaction Type ID.
     * @param amountFrom        Amount of a Transaction begging of a search range.
     * @param amountTo          Amount of a Transaction end of a search range.
     * @return <code>true</code> if at least one Transaction of the given type, given amount for the given account exists.
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public boolean transactionExists(String accountId, String transactionTypeId, BigDecimal amountFrom, BigDecimal amountTo) {
        return transactionExistsInternal(accountId, transactionTypeId, amountFrom, amountTo, null, null);
    }

    /**
     * Checks if Transactions that meet the specified search criteria exist.
     *
     * @param accountId         Account ID.
     * @param transactionTypeId Transaction Type ID.
     * @param amountFrom        Amount of a Transaction begging of a search range.
     * @param amountTo          Amount of a Transaction end of a search range.
     * @param effectiveDateFrom Transaction Effective Date beginning range (inclusive).
     * @param effectiveDateTo   Transaction Effective Date end range (inclusive).
     * @return <code>true</code> if at least one Transaction of the given type, given amount for the given account
     *         with the Effective Dates that fall into the specified range exists.
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public boolean transactionExists(String accountId, String transactionTypeId, BigDecimal amountFrom,
                                     BigDecimal amountTo, Date effectiveDateFrom, Date effectiveDateTo) {
        return transactionExistsInternal(accountId, transactionTypeId, amountFrom, amountTo, effectiveDateFrom, effectiveDateTo);
    }

    /**
     * InternalMethod: Checks if Transactions that meet the specified search criteria exist.
     *
     * @param accountId         Account ID.
     * @param transactionTypeId Transaction Type ID.
     * @param amountFrom        Amount of a Transaction begging of a search range.
     * @param amountTo          Amount of a Transaction end of a search range.
     * @param effectiveDateFrom Transaction Effective Date beginning range (inclusive).
     * @param effectiveDateTo   Transaction Effective Date end range (inclusive).
     * @return <code>true</code> if at least one Transaction of the given type, given amount for the given account
     *         with the Effective Dates that fall into the specified range exists.
     */
    private boolean transactionExistsInternal(String accountId, String transactionTypeId, BigDecimal amountFrom,
                                              BigDecimal amountTo, Date effectiveDateFrom, Date effectiveDateTo) {

        // Create a query that may contain some of the parameters. "accountId" and "transactionType" are required
        StringBuilder builder = new StringBuilder("select 1 from Transaction t where t.account.id = :accountId " +
                " and t.transactionType.id.id = :transactionTypeId");

        boolean hasAmounts = (amountFrom != null) && (amountTo != null);
        boolean hasDates = (effectiveDateFrom != null) && (effectiveDateTo != null);

        if (hasAmounts) {
            builder.append(" and t.amount between :amountFrom and :amountTo");
        }

        if (hasDates) {
            builder.append(" and t.effectiveDate between :dateFrom and :dateTo");
        }

        // Create a Query:
        Query query = em.createQuery(builder.toString());

        query.setParameter("accountId", accountId);
        query.setParameter("transactionTypeId", transactionTypeId);
        query.setMaxResults(1);

        if (hasAmounts) {
            query.setParameter("amountFrom", amountFrom);
            query.setParameter("amountTo", amountTo);
        }

        if (hasDates) {
            query.setParameter("dateFrom", effectiveDateFrom);
            query.setParameter("dateTo", effectiveDateTo);
        }

        return CollectionUtils.isNotEmpty(query.getResultList());
    }


    /**
     * Returns the unallocated amount of all transactions in the list for the given transaction type and
     * debit type mask
     *
     * @param transactions    a list of transactions
     * @param transactionType transaction type <code>TransactionTypeValue</code>(Charge, Payment or Deferment)
     * @param restricted      if "true" credit permissions with a mask != ".*" will be summarized,
     *                        otherwise the mask ".*" will be used
     * @return BigDecimal of the sum of the Restricted Payment amount
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed({Permission.READ_TRANSACTION, Permission.READ_TRANSACTION_TYPE})
    public BigDecimal getUnallocatedAmount(List<Transaction> transactions, TransactionTypeValue transactionType,
                                           boolean restricted) {

        BigDecimal unallocatedAmount = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (transaction.getTransactionTypeValue() == transactionType) {

                TransactionTypeId transactionTypeId = transaction.getTransactionType().getId();

                List<CreditPermission> creditPermissions = getCreditPermissions(transactionTypeId);

                if (CollectionUtils.isNotEmpty(creditPermissions)) {
                    boolean allPermissions = false;
                    for (CreditPermission creditPermission : creditPermissions) {
                        if (".*".equals(creditPermission.getAllowableDebitType())) {
                            allPermissions = true;
                            break;
                        }
                    }
                    if ((allPermissions && !restricted) || (!allPermissions && restricted)) {
                        unallocatedAmount = unallocatedAmount.add(transaction.getUnallocatedAmount());
                    }
                }
            }
        }

        return unallocatedAmount;
    }

    /**
     * Takes the cancellationRule and using the baseDate calculates the
     * appropriate dates to be stored in the actual transaction version of the cancellation rule.
     *
     * @param cancellationRule the cancellation rule
     * @param baseDate         the base date that is used for calculation by "DATE" values in the rule sentence
     * @return the modified cancellation rule with the updated dates
     */
    @Override
    public String calculateCancellationRule(String cancellationRule, Date baseDate) {

        if (!isCancellationRuleValid(cancellationRule)) {
            String errMsg = "Cancellation Rule '" + cancellationRule + "' is invalid";
            logger.error(errMsg);
            throw new InvalidCancellationRuleException(errMsg);
        }

        String rule = cancellationRule.toUpperCase().trim();

        Date date = baseDate;

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        StringBuilder builder = new StringBuilder(cancellationRule.length());

        String[] clauses = rule.split(";");

        for (int i = 0; i < clauses.length; i++) {

            String clause = clauses[i];

            if (clause.startsWith("DAYS(")) {

                int index = clause.indexOf(")");

                String daysValue = clause.substring(5, index);
                String ruleValue = clause.substring(index + 1);

                int days = Integer.parseInt(daysValue);
                date = CalendarUtils.addCalendarDays(date, days);

                builder.append("DATE(");
                builder.append(dateFormat.format(date));
                builder.append(")");
                builder.append(ruleValue);

            } else {
                // If it already starts with DATE simply add that clause to the builder
                builder.append(clause);
            }

            if (i < clauses.length - 1) {
                builder.append(";");
            }
        }

        return builder.toString();
    }

    /**
     * Parses the cancellationRule and returns a sorted map of dates and pairs of cancellation rule types with
     * the corresponding amount. The map is sorted by dates in ascending order.
     *
     * @param cancellationRule The cancellation rule. The String value to be normalized:
     *                         DAYS values have to be replaced with DATE.
     * @return a sorted map of dates and pairs of cancellation rule types and amount
     */
    @Override
    @WebMethod(exclude = true)
    public TreeMap<Date, List<Pair<CancellationRuleType, BigDecimal>>> parseCancellationRule(String cancellationRule) {

        if (!isCancellationRuleValid(cancellationRule)) {
            String errMsg = "Cancellation Rule '" + cancellationRule + "' is invalid";
            logger.error(errMsg);
            throw new InvalidCancellationRuleException(errMsg);
        }

        try {

            String rule = cancellationRule.toUpperCase().trim();

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

            TreeMap<Date, List<Pair<CancellationRuleType, BigDecimal>>> parsedRules =
                    new TreeMap<Date, List<Pair<CancellationRuleType, BigDecimal>>>();

            for (String clause : rule.split(";")) {

                if (clause.startsWith("DATE(")) {

                    int index = clause.indexOf(")");

                    String dateValue = clause.substring(5, index);
                    String ruleValue = clause.substring(index + 1);

                    BigDecimal amount = null;
                    CancellationRuleType ruleType = null;
                    if (ruleValue.startsWith(CancellationRuleType.PERCENTAGE.name())) {
                        int firstIndex = CancellationRuleType.PERCENTAGE.name().length() + 1;
                        amount = new BigDecimal(ruleValue.substring(firstIndex, ruleValue.indexOf(")")));
                        ruleType = CancellationRuleType.PERCENTAGE;
                    } else if (ruleValue.startsWith(CancellationRuleType.AMOUNT.name())) {
                        int firstIndex = CancellationRuleType.AMOUNT.name().length() + 1;
                        amount = new BigDecimal(ruleValue.substring(firstIndex, ruleValue.indexOf(")")));
                        ruleType = CancellationRuleType.AMOUNT;
                    }

                    Date date = dateFormat.parse(dateValue);

                    List<Pair<CancellationRuleType, BigDecimal>> rules = parsedRules.get(date);
                    if (rules == null) {
                        rules = new LinkedList<Pair<CancellationRuleType, BigDecimal>>();
                        parsedRules.put(date, rules);
                    }

                    rules.add(new Pair<CancellationRuleType, BigDecimal>(ruleType, amount));
                }
            }

            return parsedRules;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }


    /**
     * Checks if the given cancellation rule is legal.
     *
     * @param cancellationRule the cancellation rule
     * @return true if the rule is valid or null, false - otherwise
     */
    @Override
    public boolean isCancellationRuleValid(String cancellationRule) {

        if (StringUtils.isBlank(cancellationRule) || cancellationRule.length() < 6) {
            return false;
        }

        String rule = cancellationRule.toUpperCase().trim();

        int curDays = -1;
        Date curDate = null;
        Boolean isDateRule = null;

        logger.debug("Cancellation Rule = '" + rule + "'");

        while (rule.length() > 0) {

            boolean startsWithDate = rule.startsWith("DATE(");
            boolean startsWithDays = rule.startsWith("DAYS(");

            if (isDateRule == null) {
                isDateRule = startsWithDate;
            } else if (isDateRule && startsWithDays) {
                return false;
            } else if (!isDateRule && startsWithDate) {
                return false;
            }

            if (startsWithDate || startsWithDays) {
                rule = rule.substring(5);
                int index = rule.indexOf(")");
                if (index <= 0) {
                    return false;
                }
                String dateDays = rule.substring(0, index);
                if (StringUtils.isBlank(dateDays)) {
                    return false;
                }
                try {
                    int days = Integer.parseInt(dateDays);
                    if (isDateRule) {
                        return false;
                    }
                    if (days < 0 || days > 365 || days < curDays) {
                        return false;
                    }
                    curDays = days;
                } catch (NumberFormatException nfe) {
                    if (!isDateRule) {
                        return false;
                    }
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);
                        // We have to set "lenient" to false to prevent auto-conversion of dates like "13/23/1999"
                        dateFormat.setLenient(false);
                        Date date = dateFormat.parse(dateDays);
                        if (curDate != null && date.compareTo(curDate) < 0) {
                            return false;
                        }
                        curDate = date;
                    } catch (ParseException pe) {
                        logger.warn("isCancellationRuleValid: Rule = '" + cancellationRule + "' " + pe.getMessage());
                        return false;
                    }
                }
                if (rule.length() <= index + 2) {
                    return false;
                }
                rule = rule.substring(index + 1);
                boolean startsWithAmount = rule.startsWith(CancellationRuleType.AMOUNT.name());
                boolean startsWithPercentage = rule.startsWith(CancellationRuleType.PERCENTAGE.name());
                if (startsWithAmount || startsWithPercentage) {
                    int firstIndex = (startsWithAmount ? CancellationRuleType.AMOUNT.name().length() :
                            CancellationRuleType.PERCENTAGE.name().length()) + 1;
                    rule = rule.substring(firstIndex);
                    index = rule.indexOf(")");
                    if (index <= 0) {
                        return false;
                    }
                    String amountValue = rule.substring(0, index);
                    if (StringUtils.isBlank(amountValue)) {
                        return false;
                    }
                    try {
                        double amount = Double.parseDouble(amountValue);
                        if (amount <= 0) {
                            return false;
                        }
                        if (startsWithPercentage && amount > 100) {
                            return false;
                        }
                    } catch (NumberFormatException nfe) {
                        logger.warn("isCancellationRuleValid: Rule = '" + cancellationRule + "' " + nfe.getMessage());
                        return false;
                    }
                    rule = rule.substring(index + 1);
                    if (rule.length() > 0) {
                        if (rule.startsWith(";")) {
                            rule = rule.substring(1);
                            if (StringUtils.isBlank(rule)) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }

            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Using the cancellationRule, calculates the appropriate amount that can be cancelled
     * from a charge based on the current date.
     *
     * @param chargeId Charge ID
     * @return Cancellation amount
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public BigDecimal getCancellationAmount(Long chargeId) {
        return getCancellationAmount(chargeId, new Date());
    }

    /**
     * Using the cancellationRule, calculates the appropriate amount that can be cancelled
     * from a charge based on the given cancellation date.
     *
     * @param chargeId         Charge ID
     * @param cancellationDate Cancellation date
     * @return Cancellation amount
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public BigDecimal getCancellationAmount(Long chargeId, Date cancellationDate) {

        Charge charge = getCharge(chargeId);
        if (charge == null) {
            String errMsg = "Charge does not exist for the given ID = " + chargeId;
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        // Getting the cancellation rule
        String cancellationRule = (charge.getCancellationRule() != null) ?
                charge.getCancellationRule() : getDefaultChargeCancellationRule();

        if (TransactionStatus.CANCELLING.equals(charge.getStatus()) || StringUtils.isBlank(cancellationRule) ||
                charge.getAmount() == null || charge.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        TreeMap<Date, List<Pair<CancellationRuleType, BigDecimal>>> parsedRules = parseCancellationRule(cancellationRule);

        if (!parsedRules.isEmpty()) {

            Date paymentDate = parsedRules.firstKey();
            if (paymentDate.before(cancellationDate)) {
                return charge.getAmount();
            }

            for (Date date : parsedRules.keySet()) {
                if (date.before(cancellationDate)) {
                    return getCancellationAmount(parsedRules.get(paymentDate), charge.getAmount());
                }
                paymentDate = date;
            }

            return getCancellationAmount(parsedRules.get(paymentDate), charge.getAmount());
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getCancellationAmount(List<Pair<CancellationRuleType, BigDecimal>> rules, BigDecimal chargeAmount) {
        if (CollectionUtils.isNotEmpty(rules)) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Pair<CancellationRuleType, BigDecimal> rule : rules) {
                switch (rule.getA()) {
                    case AMOUNT:
                        totalAmount = totalAmount.add(rule.getB());
                    case PERCENTAGE:
                        BigDecimal amountPerPercent = chargeAmount.divide(Constants.BIG_DECIMAL_HUNDRED);
                        totalAmount = totalAmount.add(amountPerPercent.multiply(rule.getB()));
                }
            }
            return totalAmount;
        }
        return BigDecimal.ZERO;
    }

    /**
     * Cancels a charge by ID.
     *
     * @param chargeId Charge ID
     * @param memoText Memo text
     * @return Charge instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CANCEL_CHARGE)
    public Charge cancelCharge(Long chargeId, String memoText) {

        Charge charge = getCharge(chargeId);
        if (charge == null) {
            String errMsg = "Charge with ID = " + chargeId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        if (charge.getStatus() != TransactionStatus.ACTIVE) {
            String errMsg = "Charge must be active, ID = " + chargeId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        List<Allocation> allocations = getAllocations(chargeId);
        if (CollectionUtils.isNotEmpty(allocations)) {
            for (Allocation allocation : allocations) {
                for (Transaction transaction : allocation.getTransactions()) {
                    if (transaction.getStatus() == TransactionStatus.CANCELLING) {
                        String errMsg = "Transaction is allocated with another transaction with " +
                                TransactionStatus.CANCELLING + " status";
                        logger.error(errMsg);
                        throw new IllegalStateException(errMsg);
                    }
                }
            }
        }

        BigDecimal cancellationAmount = getCancellationAmount(chargeId);

        if (cancellationAmount.compareTo(BigDecimal.ZERO) != 0) {

            BigDecimal unallocatedAmount = charge.getUnallocatedAmount();

            if (unallocatedAmount.compareTo(cancellationAmount) < 0) {
                String errMsg = "Unallocated amount cannot be less than cancellation amount";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            String chargeTypeId = charge.getTransactionType().getId().getId();

            // Creating a charge reversal for the cancellation amount
            reverseTransaction(charge, chargeTypeId, memoText, cancellationAmount, null,
                    TransactionStatus.CANCELLING);

        } else {
            logger.warn("Cancellation amount is 0, Charge ID = " + chargeId +
                    ", Cancellation rule = " + charge.getCancellationRule());
        }

        return charge;
    }


    /**
     * Creates a deferment using createTransaction() and the default contest payment type as the transaction type.
     * After that it creates a locked allocation between the deferment and charge.
     *
     * @param chargeId       Charge ID
     * @param expirationDate Deferment expiration date
     * @param memoText       Memo text
     * @return Charge instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CONTEST_CHARGE)
    public Charge contestCharge(Long chargeId, Date expirationDate, String memoText) {

        Charge charge = getCharge(chargeId);
        if (charge == null) {
            String errMsg = "Charge with ID = " + chargeId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        // Getting the default contest payment type ID
        String contestPaymentTypeId = configService.getParameter(Constants.CONTEST_PAYMENT_TYPE);
        if (StringUtils.isBlank(contestPaymentTypeId)) {
            String errMsg = "Configuration parameter '" + Constants.CONTEST_PAYMENT_TYPE + "' is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }

        // Creating a deferment
        Transaction deferment = createTransaction(contestPaymentTypeId, charge.getExternalId(), charge.getAccountId(),
                charge.getEffectiveDate(), expirationDate, charge.getAmount());

        // Creating a locked allocation between the deferment and charge
        createLockedAllocation(deferment.getId(), chargeId, charge.getAmount());

        // Creating a memo
        String memoAccessLevelCode = informationService.getDefaultMemoLevel();
        informationService.createMemo(deferment.getId(), memoText, memoAccessLevelCode, new Date(), expirationDate, null);

        return charge;
    }

    /**
     * Performs the charge reversal and sets the status of the reversing transaction to DISCOUNTING.
     *
     * @param chargeId          Charge ID
     * @param transactionTypeId Transaction Type ID of the reversing transaction
     * @param amount            Charge amount
     * @param memoText          Memo text
     * @param statementPrefix   Transaction statement prefix
     * @return a discounted Charge instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DISCOUNT_CHARGE)
    public Charge discountCharge(Long chargeId, String transactionTypeId, BigDecimal amount, String memoText,
                                 String statementPrefix) {

        Charge charge = getCharge(chargeId);
        if (charge == null) {
            String errMsg = "Charge with ID = " + chargeId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        // Creating a charge reversal for the given amount
        reverseTransaction(charge, transactionTypeId, memoText, amount, statementPrefix, TransactionStatus.DISCOUNTING);

        return charge;
    }

    /**
     * Bounces a payment by ID.
     *
     * @param paymentId Payment ID
     * @param memoText  Memo text
     * @return Payment instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.BOUNCE_PAYMENT)
    public Payment bouncePayment(Long paymentId, String memoText) {

        Payment payment = getPayment(paymentId);
        if (payment == null) {
            String errMsg = "Payment with ID = " + paymentId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        if (payment.getStatus() != TransactionStatus.ACTIVE) {
            String errMsg = "Payment must be active, ID = " + paymentId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        // Removing all regular payment allocations
        removeAllocations(paymentId);

        String paymentTypeId = payment.getTransactionType().getId().getId();

        // Creating a payment reversal with the original payment type and the whole payment amount
        reverseTransaction(payment, paymentTypeId, memoText, payment.getAmount(), null, TransactionStatus.BOUNCING);

        // Preparing BRM context and executing Payment Bouncing rules

        Account account = payment.getAccount();

        // Calling BrmService with account blocking rules
        BrmContext brmContext = new BrmContext();
        brmContext.setAccount(account);

        Map<String, Object> globalParams = new HashMap<String, Object>();

        Set<String> permissions = getAccessControlService().getUserPermissions(account.getId());

        globalParams.put(Constants.BRM_PERMISSION_NAMES, new ArrayList<String>(permissions));

        List<String> holdIssueNames = holdService.getHoldIssueNamesByUserId(account.getId());

        globalParams.put(Constants.BRM_HOLD_ISSUE_NAMES, holdIssueNames);

        List<String> paymentTypeIds = Arrays.asList(payment.getTransactionType().getId().getId());

        globalParams.put(Constants.BRM_TRANSACTION_TYPE_IDS, paymentTypeIds);

        List<String> accountTypeNames = new LinkedList<String>();
        AccountType accountType = account.getAccountType();
        if (accountType != null) {
            accountTypeNames.add(accountType.getName());
        }

        globalParams.put(Constants.BRM_ACCOUNT_TYPE_NAMES, accountTypeNames);

        List<Flag> flags = informationService.getFlags(account.getId());
        Set<String> flagTypeCodes = new HashSet<String>();
        for (Flag flag : flags) {
            FlagType flagType = flag.getType();
            if (flagType != null) {
                flagTypeCodes.add(flagType.getCode());
            }
        }

        globalParams.put(Constants.BRM_FLAG_TYPE_CODES, new ArrayList<String>(flagTypeCodes));

        globalParams.put(Constants.BRM_TRANSACTION_AMOUNT, payment.getAmount());

        // Adding global variables to BRM context
        brmContext.setGlobalVariables(globalParams);

        // Firing Account Blocking rules
        brmService.fireRules(Constants.BRM_PB_RULE_SET_NAME, brmContext);

        return payment;
    }

    /**
     * Adds the list of tags to the transaction specified by Transaction ID.
     *
     * @param transactionId Transaction ID
     * @param tags          a list of tags
     * @return the updated transaction instance with tags
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.ASSIGN_TAG_TO_TRANSACTION_TYPE)
    public Transaction addTagsToTransaction(Long transactionId, List<Tag> tags) {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        transaction.setTags(mergeNewAndPersistentTags(tags, transaction.getTags()));

        return transaction;
    }

    /**
     * Removes the specified list of tags from the transaction specified by Transaction ID.
     *
     * @param transactionId Transaction ID
     * @param tagIds        IDs of tags being removed
     * @return the updated transaction instance
     */
    @Override
    @Transactional(readOnly = false)
    public Transaction removeTagsFromTransaction(Long transactionId, Long... tagIds) {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        Set<Tag> updatedTags = removeTags(tagIds, transaction.getTags());

        transaction.setTags(updatedTags);

        return transaction;
    }

    /**
     * Removes the specified list of tags from the transaction type specified by TransactionType ID.
     *
     * @param typeId Transaction ID
     * @return the updated transaction type instance with tags
     */
    @Override
    @Transactional(readOnly = false)
    public TransactionType removeTagsFromTransactionType(TransactionTypeId typeId, Long... tagIds) {

        TransactionType transactionType = getTransactionType(typeId);
        if (transactionType == null) {
            String errMsg = "Transaction type with ID = " + typeId + " does not exist";
            logger.error(errMsg);
            throw new TransactionTypeNotFoundException(errMsg);
        }

        Set<Tag> updatedTags = removeTags(tagIds, transactionType.getTags());

        transactionType.setTags(updatedTags);

        return transactionType;
    }

    /**
     * Adds the list of tags to the transaction type specified by TransactionType ID.
     *
     * @param typeId Transaction ID
     * @param tags   a list of tags
     * @return the updated transaction type instance with tags
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.ASSIGN_TAG_TO_TRANSACTION_TYPE)
    public TransactionType addTagsToTransactionType(TransactionTypeId typeId, List<Tag> tags) {

        TransactionType transactionType = getTransactionType(typeId);
        if (transactionType == null) {
            String errMsg = "Transaction type with ID = " + typeId + " does not exist";
            logger.error(errMsg);
            throw new TransactionTypeNotFoundException(errMsg);
        }

        transactionType.setTags(mergeNewAndPersistentTags(tags, transactionType.getTags()));

        return transactionType;
    }

    /**
     * Returns the number of all transactions associated with the given Transaction Type ID (code and sub-code)
     *
     * @param transactionTypeId TransactionType ID
     * @return the number of transactions
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public long getNumberOfTransactions(TransactionTypeId transactionTypeId) {

        Query query = em.createQuery("select count(id) from Transaction where transactionType.id = :transactionTypeId");

        query.setParameter("transactionTypeId", transactionTypeId);

        return (Long) query.getSingleResult();
    }

    /**
     * This method is used to progressively undo allocations on a transaction to find an unallocated balance.
     *
     * @param transactionId Transaction ID
     * @param amount        Required balance
     * @return set of AllocationReversalType values
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed({Permission.REMOVE_ALLOCATION, Permission.REVERSE_TRANSACTION})
    public Set<AllocationReversalType> reverseAllocations(Long transactionId, BigDecimal amount) {

        Transaction transaction = getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        Set<AllocationReversalType> reversalTypes = new HashSet<AllocationReversalType>();

        BigDecimal prevAmount = transaction.getUnallocatedAmount();

        // Removing non-locked allocations
        removeAllocations(transaction, true);

        // Getting the updated transaction instance
        transaction = getTransaction(transactionId);

        BigDecimal unallocatedAmount = transaction.getUnallocatedAmount();

        if (unallocatedAmount.compareTo(prevAmount) > 0) {
            reversalTypes.add(AllocationReversalType.UNALLOCATED);
        }

        if (unallocatedAmount.compareTo(amount) >= 0) {
            return reversalTypes;
        }

        // At this point only locked allocations left
        List<Allocation> lockedAllocations = getAllocations(transactionId);

        if (CollectionUtils.isNotEmpty(lockedAllocations)) {

            Set<Allocation> activeLockedAllocations = new HashSet<Allocation>();
            Set<Allocation> reversingLockedAllocations = new HashSet<Allocation>();
            Set<Allocation> activeInternallyLockedAllocations = new HashSet<Allocation>();
            Set<Allocation> reversingInternallyLockedAllocations = new HashSet<Allocation>();

            for (Allocation lockedAllocation : lockedAllocations) {
                if (lockedAllocation.isLocked()) {
                    TransactionStatus status1 = lockedAllocation.getFirstTransaction().getStatus();
                    TransactionStatus status2 = lockedAllocation.getSecondTransaction().getStatus();
                    if (status1 != null && status2 != null) {
                        if (status1 == TransactionStatus.ACTIVE || status2 == TransactionStatus.ACTIVE) {
                            if (lockedAllocation.isInternallyLocked()) {
                                activeInternallyLockedAllocations.add(lockedAllocation);
                            } else {
                                activeLockedAllocations.add(lockedAllocation);
                            }
                        } else if (status1 == TransactionStatus.REVERSING || status2 == TransactionStatus.REVERSING) {
                            if (lockedAllocation.isInternallyLocked()) {
                                reversingInternallyLockedAllocations.add(lockedAllocation);
                            } else {
                                reversingLockedAllocations.add(lockedAllocation);
                            }
                        }
                    }
                }
            }

            // Removing ACTIVE locked allocations
            for (Allocation allocation : activeLockedAllocations) {
                removeAllocation(allocation, true);
            }

            // Getting the updated transaction instance
            transaction = getTransaction(transactionId);

            prevAmount = unallocatedAmount;

            unallocatedAmount = transaction.getUnallocatedAmount();

            if (unallocatedAmount.compareTo(prevAmount) > 0) {
                reversalTypes.add(AllocationReversalType.LOCKED_ALLOCATION);
            }

            if (unallocatedAmount.compareTo(amount) >= 0) {
                return reversalTypes;
            }

            // Reversing transactions implicated in the locked allocations
            for (Allocation allocation : reversingLockedAllocations) {

                Transaction transaction1 = allocation.getFirstTransaction();
                Transaction transaction2 = allocation.getSecondTransaction();

                Transaction reverseTransaction = (transaction1.getStatus() == TransactionStatus.REVERSING) ?
                        transaction1 : transaction2;

                String memoText = "Reversed transaction ID = " + reverseTransaction.getId() + ", amount = " +
                        transaction.getAmount();

                reverseTransaction(reverseTransaction.getId(), memoText, reverseTransaction.getAmount());
            }

            // Getting the updated transaction instance
            transaction = getTransaction(transactionId);

            prevAmount = unallocatedAmount;

            unallocatedAmount = transaction.getUnallocatedAmount();

            if (unallocatedAmount.compareTo(prevAmount) > 0) {
                reversalTypes.add(AllocationReversalType.MANUAL_REVERSAL);
            }

            if (unallocatedAmount.compareTo(amount) >= 0) {
                return reversalTypes;
            }

            // Removing ACTIVE internally locked allocations
            for (Allocation allocation : activeInternallyLockedAllocations) {
                removeAllocation(allocation, true);
            }

            // Getting the updated transaction instance
            transaction = getTransaction(transactionId);

            prevAmount = unallocatedAmount;

            unallocatedAmount = transaction.getUnallocatedAmount();

            if (unallocatedAmount.compareTo(prevAmount) > 0) {
                reversalTypes.add(AllocationReversalType.INTERNALLY_LOCKED_ALLOCATION);
            }

            if (unallocatedAmount.compareTo(amount) >= 0) {
                return reversalTypes;
            }

            // Reversing transactions implicated in the internally locked allocations
            for (Allocation allocation : reversingInternallyLockedAllocations) {

                Transaction transaction1 = allocation.getFirstTransaction();
                Transaction transaction2 = allocation.getSecondTransaction();

                Transaction reverseTransaction = (transaction1.getStatus() == TransactionStatus.REVERSING) ?
                        transaction1 : transaction2;

                String memoText = "Reversed transaction ID = " + reverseTransaction.getId() + ", amount = " +
                        transaction.getAmount();

                reverseTransaction(reverseTransaction.getId(), memoText, reverseTransaction.getAmount());
            }

            // Getting the updated transaction instance
            transaction = getTransaction(transactionId);

            prevAmount = unallocatedAmount;

            unallocatedAmount = transaction.getUnallocatedAmount();

            if (unallocatedAmount.compareTo(prevAmount) > 0) {
                reversalTypes.add(AllocationReversalType.INTERNALLY_LOCKED_MANUAL_REVERSAL);
            }

            if (unallocatedAmount.compareTo(amount) >= 0) {
                return reversalTypes;
            }
        }

        return reversalTypes;
    }

    /**
     * Retrieves the list of Transaction objects from the persistent store by GL transaction ID.
     *
     * @param glTransactionId GL Transaction ID
     * @return list ofTransaction instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Transaction> getTransactionsByGlTransactionId(Long glTransactionId) {

        Query query = em.createQuery("select t from Transaction t, GlTransaction gt " + GET_TRANSACTION_JOIN +
                " inner join gt.transactions gts where t.id = gts.id and gt.id = :id");

        query.setParameter("id", glTransactionId);

        return query.getResultList();
    }

    /**
     * Returns a list of potential refunds (payments) for specified Account IDs.
     *
     * @param accountIds Account IDs
     * @return list of Payment instances
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Payment> getPotentialRefunds(Set<String> accountIds) {
        return getPotentialRefunds(accountIds, null, null);
    }

    /**
     * Returns a list of potential refunds (payments) for specified Account IDs and date range.
     *
     * @param accountIds Account IDs
     * @param startDate  Start date
     * @param endDate    End date
     * @return list of Payment instances
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Payment> getPotentialRefunds(Set<String> accountIds, Date startDate, Date endDate) {
        return getPotentialRefunds(accountIds, startDate, endDate, null);
    }

    /**
     * Returns a list of potential refunds (payments) for specified Account IDs, date range and Tag IDs.
     *
     * @param accountIds Account IDs
     * @param startDate  Start date
     * @param endDate    End date
     * @param tagIds     Tag IDs
     * @return List of Payment instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_TRANSACTION)
    public List<Payment> getPotentialRefunds(Set<String> accountIds, Date startDate, Date endDate, Set<Long> tagIds) {

        if (CollectionUtils.isEmpty(accountIds)) {
            String errMsg = "Account IDs are required";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        StringBuilder queryBuilder = new StringBuilder("select distinct t from Payment t ");

        queryBuilder.append(GET_TRANSACTION_JOIN);

        boolean tagIdsExist = CollectionUtils.isNotEmpty(tagIds);

        if (tagIdsExist) {
            queryBuilder.append(" inner join fetch t.tags tags ");
        }

        queryBuilder.append(" where t.refundable = true and a.id in (:accountIds) and t.clearDate < CURRENT_DATE ");
        queryBuilder.append(" and t.statusCode = :statusCode ");

        if (startDate != null) {
            queryBuilder.append(" and t.effectiveDate >= :startDate ");
        }

        if (endDate != null) {
            queryBuilder.append(" and t.effectiveDate <= :endDate ");
        }

        if (tagIdsExist) {
            queryBuilder.append(" and tags in (select tag from Tag tag where tag.id in (:tagIds))");
        }

        queryBuilder.append(" order by t.id desc");

        Query query = em.createQuery(queryBuilder.toString());

        query.setParameter("accountIds", new ArrayList<String>(accountIds));
        query.setParameter("statusCode", TransactionStatus.ACTIVE.getId());

        if (startDate != null) {
            query.setParameter("startDate", CalendarUtils.removeTime(startDate), TemporalType.DATE);
        }

        if (endDate != null) {
            query.setParameter("endDate", CalendarUtils.removeTime(endDate), TemporalType.DATE);
        }

        if (tagIdsExist) {
            query.setParameter("tagIds", new ArrayList<Long>(tagIds));
        }

        List<Payment> results = query.getResultList();

        List<Payment> payments = new LinkedList<Payment>();

        if (CollectionUtils.isNotEmpty(results)) {
            for (Payment payment : results) {
                if (payment.getUnallocatedAmount().compareTo(BigDecimal.ZERO) > 0) {
                    payments.add(payment);
                }
            }
        }

        return payments;
    }


    private Set<Tag> removeTags(Long[] tagIdsToRemove, Set<Tag> tags) {

        Set<Long> tagIds = new HashSet<Long>(Arrays.asList(tagIdsToRemove));

        if (CollectionUtils.isNotEmpty(tags)) {
            for (Tag tag : new ArrayList<Tag>(tags)) {
                if (tagIds.contains(tag.getId())) {
                    PermissionUtils.checkPermissions(tag, Permission.EDIT_TAG, Permission.EDIT_ADMIN_TAG);
                    tags.remove(tag);
                }
            }
        }

        return tags;
    }


    private Set<Tag> mergeNewAndPersistentTags(List<Tag> tagsToAdd, Set<Tag> persistentTags) {

        List<Tag> newTags = new ArrayList<Tag>(tagsToAdd);

        // Persisting new or merging existing tags in the persistent store
        Set<Long> tagIds = new HashSet<Long>();
        for (Tag tag : newTags) {
            if (tag.getId() != null) {
                tagIds.add(tag.getId());
            }
            PermissionUtils.checkPermissions(tag, Permission.EDIT_TAG, Permission.EDIT_ADMIN_TAG);
            auditableEntityService.persistAuditableEntity(tag);
        }

        if (CollectionUtils.isNotEmpty(persistentTags)) {
            for (Tag currentTag : new ArrayList<Tag>(persistentTags)) {
                if (tagIds.contains(currentTag.getId())) {
                    PermissionUtils.checkPermissions(currentTag, Permission.EDIT_TAG, Permission.EDIT_ADMIN_TAG);
                    persistentTags.remove(currentTag);
                }
            }
        } else {
            persistentTags = new HashSet<Tag>(newTags.size());
        }

        persistentTags.addAll(newTags);

        return persistentTags;
    }

}