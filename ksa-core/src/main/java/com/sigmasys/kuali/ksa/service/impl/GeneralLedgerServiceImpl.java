package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.exception.*;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.*;
import com.sigmasys.kuali.ksa.util.CalendarUtils;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import com.sigmasys.kuali.ksa.util.LongIdGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

import static com.sigmasys.kuali.ksa.model.Constants.*;

/**
 * General Ledger service implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("generalLedgerService")
@Transactional(readOnly = true)
@WebService(serviceName = GeneralLedgerService.SERVICE_NAME, portName = GeneralLedgerService.PORT_NAME,
        targetNamespace = WS_NAMESPACE)
@SuppressWarnings("unchecked")
public class GeneralLedgerServiceImpl extends GenericPersistenceService implements GeneralLedgerService {

    private static final Log logger = LogFactory.getLog(GeneralLedgerServiceImpl.class);

    private static final String SELECT_FAILED_GL_TRANSACTION =
            "select f from FailedGlTransaction f " +
                    "inner join fetch f.transaction t ";


    @Autowired
    private TransactionService transactionService;


    /**
     * Creates a new general ledger type.
     *
     * @param code                GL type code
     * @param name                GL type name
     * @param description         GL type description
     * @param glAccountId         GL Account ID
     * @param glOperationOnCharge GL operation on charge
     * @return GeneralLedgerType instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_GL_TYPE)
    public GeneralLedgerType createGeneralLedgerType(String code, String name, String description, String glAccountId,
                                                     GlOperationType glOperationOnCharge) {

        if (!isGlAccountValid(glAccountId)) {
            String errMsg = "GL Account '" + glAccountId + "' is invalid";
            logger.error(errMsg);
            throw new InvalidGeneralLedgerAccountException(errMsg);
        }

        GeneralLedgerType glType = new GeneralLedgerType();
        glType.setCode(code);
        glType.setName(name);
        glType.setDescription(description);
        glType.setGlAccountId(glAccountId);
        glType.setGlOperationOnCharge(glOperationOnCharge);

        persistEntity(glType);

        return glType;
    }

    /**
     * Persists GeneralLedgerType instance in the persistent store.
     *
     * @param glType GL type
     * @return GL type ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.EDIT_GL_TYPE)
    public Long persistGeneralLedgerType(GeneralLedgerType glType) {
        return persistEntity(glType);
    }

    /**
     * Returns all general ledger types existing in KSA in chronological order.
     *
     * @return list of GeneralLedgerType instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_GL_TYPE)
    public List<GeneralLedgerType> getGeneralLedgerTypes() {
        return getEntities(GeneralLedgerType.class, new Pair<String, SortOrder>("creationDate", SortOrder.ASC),
                new Pair<String, SortOrder>("id", SortOrder.ASC));
    }

    /**
     * Creates a new general ledger transaction based on the given parameters
     *
     * @param transactionId ID of the corresponding transaction
     * @param glAccountId   General ledger account ID
     * @param amount        Transaction amount
     * @param operationType GL operation type
     * @param statement     GL transaction statement
     * @param isQueued      Set status to Q unless isQueued is passed and is false, in which case, set status to W
     * @return new GL Transaction instance
     * @throws GlTransactionFailedException
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false, noRollbackFor = GlTransactionFailedException.class)
    @PermissionsAllowed(Permission.CREATE_GL_TRANSACTION)
    public GlTransaction createGlTransaction(Long transactionId,
                                             String glAccountId,
                                             BigDecimal amount,
                                             GlOperationType operationType,
                                             String statement,
                                             boolean isQueued) throws GlTransactionFailedException {

        if (!isGlAccountValid(glAccountId)) {
            String errMsg = "GL Account '" + glAccountId + "' is invalid";
            logger.error(errMsg);
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

        Transaction transaction = em.find(Transaction.class, transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

        GlTransaction glTransaction = new GlTransaction();
        glTransaction.setDate(new Date());
        glTransaction.setAmount(amount != null ? amount : BigDecimal.ZERO);
        glTransaction.setGlAccountId(glAccountId);
        glTransaction.setGlOperation(operationType);

        if (StringUtils.isNotBlank(statement)) {
            glTransaction.setStatement(statement);
        }

        glTransaction.setTransactions(new HashSet<Transaction>(Arrays.asList(transaction)));
        glTransaction.setStatus(isQueued ? GlTransactionStatus.QUEUED : GlTransactionStatus.WAITING);

        Date recognitionDate = transaction.getRecognitionDate();
        if (recognitionDate == null) {
            String errMsg = "GL recognition date cannot be null, Transaction ID = " + transactionId;
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

        GlRecognitionPeriod recognitionPeriod = getGlRecognitionPeriod(recognitionDate);
        if (recognitionPeriod == null) {
            String errMsg = "GL recognition period cannot be found for the given date = " + recognitionDate;
            throw new GlTransactionFailedException(transactionId, errMsg);
        }

        glTransaction.setRecognitionPeriod(recognitionPeriod);

        em.persist(glTransaction);

        em.flush();

        return glTransaction;
    }

    /**
     * Creates a new general ledger transaction based on the given parameters
     *
     * @param transactionId ID of the corresponding transaction
     * @param glAccountId   General ledger account ID
     * @param amount        Transaction amount
     * @param operationType GL operation type
     * @param statement     GL transaction statement
     * @return new GL Transaction instance
     * @throws GlTransactionFailedException
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false, noRollbackFor = GlTransactionFailedException.class)
    @PermissionsAllowed(Permission.CREATE_GL_TRANSACTION)
    public GlTransaction createGlTransaction(Long transactionId,
                                             String glAccountId,
                                             BigDecimal amount,
                                             GlOperationType operationType,
                                             String statement) throws GlTransactionFailedException {
        return createGlTransaction(transactionId, glAccountId, amount, operationType, statement, true);
    }

    /**
     * Persists GL Transaction in the persistent store
     *
     * @param glTransaction GlTransaction instance
     * @return GL Transaction ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.EDIT_GL_TRANSACTION)
    public Long persistGlTransaction(GlTransaction glTransaction) {
        return persistEntity(glTransaction);
    }

    /**
     * Summarizes the general ledger transactions for the given GL transaction list
     *
     * @param transactions List of general ledger transactions
     * @return the modified list of GL transactions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.SUMMARIZE_GL_TRANSACTIONS)
    public List<GlTransaction> summarizeGlTransactions(List<GlTransaction> transactions) {

        // Creating a new array list based on the given GL transaction list
        List<GlTransaction> glTransactions = new ArrayList<GlTransaction>(transactions);

        // Map of [GL Account ID, GL Recognition Period ID] and set of transactions
        Map<Pair<String, Long>, Set<Transaction>> transactionMap = new HashMap<Pair<String, Long>, Set<Transaction>>();

        // Map of [GL Account ID, GL Recognition Period ID] and total transaction amount
        Map<Pair<String, Long>, BigDecimal> amountMap = new HashMap<Pair<String, Long>, BigDecimal>();

        // Map of [GL Account ID, GL Recognition Period ID] and the first GL transaction for this GL account from the given list
        Map<Pair<String, Long>, List<GlTransaction>> glTransactionMap = new HashMap<Pair<String, Long>, List<GlTransaction>>();

        for (GlTransaction glTransaction : glTransactions) {

            if (GlTransactionStatus.WAITING.equals(glTransaction.getStatus())) {

                String glAccountId = glTransaction.getGlAccountId();
                Long recognitionPeriodId = glTransaction.getRecognitionPeriod().getId();

                if (glAccountId != null && recognitionPeriodId != null) {

                    final Pair<String, Long> key = new Pair<String, Long>(glAccountId, recognitionPeriodId);

                    Set<Transaction> transactionsForAccount = transactionMap.get(key);
                    if (transactionsForAccount == null) {
                        transactionsForAccount = new HashSet<Transaction>();
                        transactionMap.put(key, transactionsForAccount);
                    }

                    // Adding all transactions to the list of transactions for the current GL account and Recognition Period
                    transactionsForAccount.addAll(glTransaction.getTransactions());

                    List<GlTransaction> glTransactionsForAccount = glTransactionMap.get(key);
                    if (glTransactionsForAccount == null) {
                        glTransactionsForAccount = new LinkedList<GlTransaction>();
                        glTransactionMap.put(key, glTransactionsForAccount);
                    }

                    // Adding GL transaction to the list of GL transactions for the current GL account and Recognition Period
                    glTransactionsForAccount.add(glTransaction);

                    BigDecimal amount = amountMap.get(key);
                    if (amount == null) {
                        amount = BigDecimal.ZERO;
                    }

                    for (Transaction trans : glTransaction.getTransactions()) {
                        amount = amount.add(trans.getAmount() != null ? trans.getAmount() : BigDecimal.ZERO);
                    }

                    // Sum up the transaction amount for the current GL account and Recognition Period
                    amountMap.put(key, amount.add(amount));
                }
            }
        }

        Set<Long> glTransactionIdsToDelete = new HashSet<Long>();
        for (Pair<String, Long> key : glTransactionMap.keySet()) {

            List<GlTransaction> glTransactionsForAccount = glTransactionMap.get(key);

            if (!CollectionUtils.isEmpty(glTransactionsForAccount)) {

                BigDecimal totalAmount = amountMap.get(key);

                if (!BigDecimal.ZERO.equals(totalAmount)) {

                    // Persisting the first GL transaction with the total amount and all
                    // transactions for the current GL account
                    GlTransaction firstGlTransaction = glTransactionsForAccount.get(0);
                    firstGlTransaction.setTransactions(transactionMap.get(key));
                    firstGlTransaction.setAmount(totalAmount);
                    persistEntity(firstGlTransaction);
                    // We have to remove the rest of GL transactions
                    for (int i = 1; i < glTransactionsForAccount.size(); i++) {
                        glTransactionIdsToDelete.add(glTransactionsForAccount.get(i).getId());
                    }

                } else {
                    for (GlTransaction glTransaction : glTransactionsForAccount) {
                        glTransactionIdsToDelete.add(glTransaction.getId());
                    }
                }
            }
        }

        // Deleting GL transactions that must be eliminated from the list and database and
        // persisting the rest with the status 'Q'
        for (Iterator<GlTransaction> iterator = glTransactions.iterator(); iterator.hasNext(); ) {
            GlTransaction glTransaction = iterator.next();
            if (glTransactionIdsToDelete.contains(glTransaction.getId())) {
                em.remove(glTransaction);
                iterator.remove();
            } else {
                glTransaction.setStatus(GlTransactionStatus.QUEUED);
                persistEntity(glTransaction);
            }
        }

        return glTransactions;
    }

    /**
     * Returns the general ledger type instance by code.
     *
     * @param glTypeCode General Ledger type code
     * @return GeneralLedgerType instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_GL_TYPE)
    public GeneralLedgerType getGeneralLedgerType(String glTypeCode) {

        Query query = em.createQuery("select t from GeneralLedgerType t where t.code = :code");
        query.setParameter("code", glTypeCode);
        List<GeneralLedgerType> glTypes = query.getResultList();
        if (glTypes != null && !glTypes.isEmpty()) {
            return glTypes.get(0);
        }
        String errMsg = "Cannot find GeneralLedgerType for the code = " + glTypeCode;
        logger.error(errMsg);
        throw new InvalidGeneralLedgerTypeException(errMsg);
    }

    /**
     * Returns the general ledger type instance by ID.
     *
     * @param glTypeId General Ledger type ID
     * @return GeneralLedgerType instance
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_GL_TYPE)
    public GeneralLedgerType getGeneralLedgerType(Long glTypeId) {
        return getEntity(glTypeId, GeneralLedgerType.class);
    }

    /**
     * Returns the default general ledger type.
     *
     * @return GeneralLedgerType instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_GL_TYPE)
    public GeneralLedgerType getDefaultGeneralLedgerType() {
        String defaultGlTypeCode = configService.getParameter(DEFAULT_GL_TYPE);
        if (StringUtils.isBlank(defaultGlTypeCode)) {
            String errMsg = "Configuration parameter '" + DEFAULT_GL_TYPE + "' is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }
        return getGeneralLedgerType(defaultGlTypeCode);
    }

    /**
     * Returns the default general ledger mode.
     *
     * @return GeneralLedgerMode instance
     */
    @Override
    public GeneralLedgerMode getDefaultGeneralLedgerMode() {
        String glMode = configService.getParameter(DEFAULT_GL_MODE);
        if (StringUtils.isBlank(glMode)) {
            String errMsg = "Configuration parameter '" + DEFAULT_GL_MODE + "' is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }
        return EnumUtils.findById(GeneralLedgerMode.class, glMode);
    }

    private GlTransmission createGlTransmission(String glAccountId, Date earliestDate, Date latestDate,
                                                GlOperationType glOperation, BigDecimal amount,
                                                GlRecognitionPeriod recognitionPeriod, String batchId) {

        if (!isGlAccountValid(glAccountId)) {
            String errMsg = "GL Account '" + glAccountId + "' is invalid";
            logger.error(errMsg);
            throw new InvalidGeneralLedgerAccountException(errMsg);
        }

        GlTransmission transmission = new GlTransmission();
        transmission.setStatus(GlTransmissionStatus.GENERATED);
        transmission.setBatchId(batchId);
        transmission.setGlAccountId(glAccountId);
        transmission.setEarliestDate(earliestDate);
        transmission.setLatestDate(latestDate);
        transmission.setRecognitionPeriod(recognitionPeriod);
        transmission.setDate(new Date());
        transmission.setGlOperation(glOperation);
        transmission.setAmount(amount);

        persistEntity(transmission);

        return transmission;

    }

    private GlTransmission createGlTransmission(GlTransaction transaction, GlRecognitionPeriod recognitionPeriod,
                                                String batchId) {
        return createGlTransmission(transaction.getGlAccountId(), transaction.getDate(), transaction.getDate(),
                transaction.getGlOperation(), transaction.getAmount(), recognitionPeriod, batchId);

    }

    /**
     * Finds the GL recognition period for the given date.
     *
     * @param date <code>java.util.Date</code> instance
     * @return GlRecognitionPeriod instance or null if not found
     */
    private GlRecognitionPeriod getGlRecognitionPeriod(Date date) {
        date = CalendarUtils.removeTime(date);
        Query query = em.createQuery("select rp from GlRecognitionPeriod rp " +
                " where rp.startDate <= :date and rp.endDate >= :date");
        query.setParameter("date", date);
        List<GlRecognitionPeriod> results = (List<GlRecognitionPeriod>) query.getResultList();
        return (results != null && !results.isEmpty()) ? results.get(0) : null;
    }

    /**
     * Creates and returns the list og GL transmissions for export based on the given parameters.
     *
     * @param fromDate           Start date, can be null
     * @param toDate             End date, can be null
     * @param isEffectiveDate    indicates whether Transaction effective date should be used if true,
     *                           otherwise - recognition date
     * @param recognitionPeriods an array of recognition period codes
     * @return list of GL transmissions
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_GL_TRANSMISSION)
    public List<GlTransmission> createGlTransmissions(Date fromDate, Date toDate, boolean isEffectiveDate,
                                                      String... recognitionPeriods) {

        if (fromDate != null && toDate != null && fromDate.after(toDate)) {
            String errMsg = "Start Date cannot be greater than End Date: Start Date = " + fromDate +
                    ", End Date = " + toDate;
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        String glModeCode = configService.getParameter(DEFAULT_GL_MODE);
        if (StringUtils.isBlank(glModeCode)) {
            String errMsg = "Configuration parameter '" + Constants.DEFAULT_GL_MODE + "' is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }

        GeneralLedgerMode glMode = EnumUtils.findById(GeneralLedgerMode.class, glModeCode);
        if (glMode == null) {
            String errMsg = "General Ledger mode must be specified";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        List<GlTransmission> transmissions = new LinkedList<GlTransmission>();

        Query query;
        if (fromDate != null && toDate != null) {
            // If isEffectiveDate is true then use the transaction effective date, otherwise the recognition date
            String dateField = isEffectiveDate ? "effectiveDate" : "recognitionDate";
            query = em.createQuery("select distinct t from GlTransaction t " +
                    " inner join fetch t.recognitionPeriod rp " +
                    " left outer join t.transactions ts " +
                    " where t.statusCode = :status and " +
                    " ts." + dateField + " <> null and ts." + dateField + " between :fromDate and :toDate " +
                    " order by t.date asc");
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
        } else {
            boolean periodExists = (recognitionPeriods != null && recognitionPeriods.length > 0);
            String inClause = periodExists ? " and rp.code in (:recognitionPeriods) " : "";
            query = em.createQuery("select t from GlTransaction t " +
                    " inner join fetch t.recognitionPeriod rp " +
                    " where t.statusCode = :status " + inClause + " order by t.date asc");
            if (periodExists) {
                query.setParameter("recognitionPeriods", Arrays.asList(recognitionPeriods));
            }
        }

        query.setParameter("status", GlTransactionStatus.QUEUED_CODE);
        if (glMode == GeneralLedgerMode.INDIVIDUAL) {
            query.setMaxResults(1);
        }

        List<GlTransaction> glTransactions = query.getResultList();
        if (CollectionUtils.isEmpty(glTransactions)) {
            if (fromDate != null && toDate != null) {
                logger.warn("Cannot find GL transactions for the given date range: fromDate = " + fromDate +
                        ", toDate = " + toDate);
            } else {
                logger.warn("No GL transactions found");
            }
            return transmissions;
        }

        if (glMode == GeneralLedgerMode.INDIVIDUAL) {
            GlTransaction earliestGlTransaction = glTransactions.get(0);
            GlRecognitionPeriod recognitionPeriod = earliestGlTransaction.getRecognitionPeriod();
            GlTransmission transmission = createGlTransmission(earliestGlTransaction, recognitionPeriod, "RT");
            earliestGlTransaction.setTransmission(transmission);
            earliestGlTransaction.setStatus(GlTransactionStatus.COMPLETED);
            //persistEntity(earliestGlTransaction);
            transmissions.add(transmission);
        } else if (glMode == GeneralLedgerMode.BATCH) {
            for (GlTransaction transaction : glTransactions) {
                GlRecognitionPeriod recognitionPeriod = transaction.getRecognitionPeriod();
                GlTransmission transmission = createGlTransmission(transaction, recognitionPeriod, null);
                transaction.setTransmission(transmission);
                transaction.setStatus(GlTransactionStatus.COMPLETED);
                //persistEntity(transaction);
                transmissions.add(transmission);
            }

        } else if (glMode == GeneralLedgerMode.BATCH_ROLLUP) {

            // Creating a map of <GL account, set of GL transactions>
            HashMap<String, Set<GlTransaction>> accountTransactions = new HashMap<String, Set<GlTransaction>>();
            for (GlTransaction transaction : glTransactions) {
                String accountId = transaction.getGlAccountId();
                Set<GlTransaction> glTransactionSet = accountTransactions.get(accountId);
                if (glTransactionSet == null) {
                    glTransactionSet = new HashSet<GlTransaction>();
                    accountTransactions.put(accountId, glTransactionSet);
                }
                glTransactionSet.add(transaction);
            }

            for (Map.Entry<String, Set<GlTransaction>> entry : accountTransactions.entrySet()) {

                String accountId = entry.getKey();

                Set<GlTransaction> glTransactionSet = entry.getValue();

                if (glTransactionSet.isEmpty()) {
                    continue;
                }

                GlRecognitionPeriod recognitionPeriod = null;
                GlOperationType groupOperation = null;
                BigDecimal amount1 = BigDecimal.ZERO;
                BigDecimal amount2 = BigDecimal.ZERO;
                Date minDate = null;
                Date maxDate = null;

                for (GlTransaction transaction : glTransactionSet) {
                    if (recognitionPeriod == null) {
                        recognitionPeriod = transaction.getRecognitionPeriod();
                    }
                    if (groupOperation == null) {
                        groupOperation = transaction.getGlOperation();
                    }
                    GlOperationType glOperation = transaction.getGlOperation();
                    if (glOperation != null) {
                        if (glOperation == groupOperation) {
                            amount1 = amount1.add(transaction.getAmount());
                        } else {
                            amount2 = amount2.add(transaction.getAmount());
                        }
                    }
                    if (minDate == null || minDate.after(transaction.getDate())) {
                        minDate = transaction.getDate();
                    }
                    if (maxDate == null || maxDate.before(transaction.getDate())) {
                        maxDate = transaction.getDate();
                    }
                }

                GlTransmission transmission = createGlTransmission(accountId, minDate, maxDate, groupOperation,
                        amount1.subtract(amount2), recognitionPeriod, null);

                // Updating GL transactions for the current account
                for (GlTransaction transaction : glTransactionSet) {
                    transaction.setStatus(GlTransactionStatus.COMPLETED);
                    transaction.setTransmission(transmission);
                    //persistEntity(transaction);
                }

                transmissions.add(transmission);
            }
        } else {
            String errMsg = "Unexpected GeneralLedgerMode: " + glMode;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        return transmissions;
    }

    /**
     * Retrieves all GL transmissions with the statuses for export by batch ID.
     *
     * @param batchId  GL transmission batch ID
     * @param statuses Status of GL Transmissions to retrieve.
     * @return list of GlTransmission instances
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_GL_TRANSMISSION)
    public List<GlTransmission> getGlTransmissionsForBatch(String batchId, GlTransmissionStatus... statuses) {

        List<String> statusCodes = new ArrayList<String>(statuses.length);

        for (GlTransmissionStatus status : statuses) {
            statusCodes.add(status.getId());
        }

        Query query = em.createQuery("select glt from GlTransmission glt " +
                " inner join fetch glt.recognitionPeriod rp " +
                " where glt.statusCode in (:statuses) " +
                " and glt.batchId = :batchId " +
                " order by glt.date asc");

        query.setParameter("batchId", batchId);
        query.setParameter("statuses", statusCodes);

        return query.getResultList();
    }

    /**
     * Validates the GL account number.
     *
     * @param glAccount GL account number
     * @return true if the GL account is valid, false - otherwise.
     */
    @Override
    public boolean isGlAccountValid(String glAccount) {

        if (StringUtils.isBlank(glAccount)) {
            logger.warn("GL Account cannot be empty");
            return false;
        }

        Query query = em.createQuery("select pattern from AllowableGlAccount");
        List<String> patterns = query.getResultList();
        if (!CollectionUtils.isEmpty(patterns)) {
            for (String pattern : patterns) {
                if (Pattern.compile(pattern).matcher(glAccount).matches()) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Validates the list of GL Breakdowns.
     *
     * @param glBreakdowns list of AbstractGlBreakdown instances
     * @return true if the list of GL Breakdowns is valid, false - otherwise
     */
    @Override
    public boolean isGlBreakdownValid(List<? extends AbstractGlBreakdown> glBreakdowns) {

        if (CollectionUtils.isEmpty(glBreakdowns)) {
            logger.warn("List of GL breakdowns cannot be empty");
            return false;
        }

        int numberOfZeroBreakdowns = 0;

        BigDecimal totalBreakdownAmount = BigDecimal.ZERO;

        for (AbstractGlBreakdown glBreakdown : glBreakdowns) {

            if (glBreakdown == null) {
                logger.warn("GL Breakdown cannot be empty");
                return false;
            }

            if (!isGlAccountValid(glBreakdown.getGlAccount())) {
                logger.warn("GL Breakdown's Account ID is invalid");
                return false;
            }

            if (glBreakdown.getBreakdown() == null) {
                logger.warn("Percentage breakdown is required");
                return false;
            }

            BigDecimal percentageBreakdown = glBreakdown.getBreakdown();

            if (percentageBreakdown.compareTo(BigDecimal.ZERO) == 0) {

                if (numberOfZeroBreakdowns++ > 1) {
                    logger.warn("There should only be one Zero Breakdown in the list");
                    return false;
                }
            }

            totalBreakdownAmount = totalBreakdownAmount.add(percentageBreakdown);
        }

        if (numberOfZeroBreakdowns == 0) {
            logger.warn("One Zero Breakdown is required in the list");
            return false;
        }

        if (totalBreakdownAmount.compareTo(BigDecimal.ZERO) < 0 ||
                totalBreakdownAmount.compareTo(Constants.BIG_DECIMAL_HUNDRED) >= 0) {
            logger.warn("Total breakdown amount should be greater than or equal to 0 and less than 100");
            return false;
        }

        return true;
    }

    /**
     * Retrieves all GL transactions for the given GL transaction date range and GL account ID
     * sorted by dates in ascending order.
     *
     * @param startDate   GL Transaction start date
     * @param endDate     GL Transaction end date
     * @param glAccountId GL Account ID
     * @return list of GlTransaction instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_GL_TRANSACTION)
    public List<GlTransaction> getGlTransactions(Date startDate, Date endDate, String glAccountId) {

        if (startDate == null || endDate == null) {
            String errMsg = "Start Date and End Date cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (startDate.after(endDate)) {
            String errMsg = "Start Date cannot be greater than End Date: Start Date = " + startDate +
                    ", End Date = " + endDate;
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Query query = em.createQuery("select glt from GlTransaction glt " +
                " left outer join fetch glt.recognitionPeriod rp " +
                " left outer join fetch glt.transmission t " +
                " left outer join fetch glt.transactions ts " +
                " where glt.date between :startDate and :endDate " +
                (glAccountId != null ? " and glt.glAccountId = :glAccountId " : "") +
                " order by glt.date asc");

        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        if (glAccountId != null) {
            query.setParameter("glAccountId", glAccountId);
        }

        return query.getResultList();
    }

    /**
     * Retrieves all GL transactions for the given GL transaction date range sorted by dates in ascending order.
     *
     * @param startDate GL Transaction start date
     * @param endDate   GL Transaction end date
     * @return list of GlTransaction instances
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_GL_TRANSACTION)
    public List<GlTransaction> getGlTransactions(Date startDate, Date endDate) {
        return getGlTransactions(startDate, endDate, null);
    }

    /**
     * Retrieves all GL transactions for the given GL transaction status.
     *
     * @param status GL Transaction status
     * @return list of GlTransaction instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_GL_TRANSACTION)
    public List<GlTransaction> getGlTransactionsByStatus(GlTransactionStatus status) {

        if (status == null) {
            String errMsg = "Status cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Query query = em.createQuery("select glt from GlTransaction glt " +
                " left outer join fetch glt.recognitionPeriod rp " +
                " left outer join fetch glt.transmission t " +
                " left outer join fetch glt.transactions ts " +
                " where glt.statusCode = :status " +
                " order by glt.date asc");

        query.setParameter("status", status.getId());

        return query.getResultList();
    }

    /**
     * Retrieves GL transactions that belong to the specified Batch.
     *
     * @param batchId ID of a Batch which GL Transactions to retrieve.
     * @return list of GL Transactions belonging to the Batch.
     */
    @Override
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_GL_TRANSACTION)
    public List<GlTransaction> getGlTransactionsForBatch(String batchId) {

        if (StringUtils.isBlank(batchId)) {
            String errMsg = "Batch ID cannot be empty or null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Query query = em.createQuery("select glt from GlTransaction glt " +
                " left outer join fetch glt.recognitionPeriod rp " +
                " left outer join fetch glt.transmission t " +
                " where t.batchId = :batchId " +
                " order by glt.date asc");

        query.setParameter("batchId", batchId);

        return query.getResultList();
    }

    /**
     * Retrieves all GL Transmissions with the specified statuses.
     *
     * @param statuses Status of GL Transmissions to retrieve.
     * @return A list of GL Transmissions with the specified statuses.
     */
    @WebMethod(exclude = true)
    @PermissionsAllowed(Permission.READ_GL_TRANSMISSION)
    public List<GlTransmission> getGlTransmissionsByStatuses(GlTransmissionStatus... statuses) {

        List<String> statusCodes = new ArrayList<String>(statuses.length);

        for (GlTransmissionStatus status : statuses) {
            statusCodes.add(status.getId());
        }

        Query query = em.createQuery("select glt from GlTransmission glt " +
                " inner join fetch glt.recognitionPeriod rp " +
                " where glt.statusCode in (:statuses) " +
                " order by glt.date asc");

        query.setParameter("statuses", statusCodes);

        return query.getResultList();
    }

    /**
     * Creates a list of GlBatchBaseline objects that contain some summary information about transactions
     * and their amounts.
     *
     * @param batchId Transmission batch ID generated during transaction export
     * @return list of GlBatchBaseline instances
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed({Permission.READ_TRANSACTION, Permission.READ_GL_TRANSMISSION})
    public List<GlBatchBaseline> createGlBaselineAmounts(String batchId) {

        Query query = em.createQuery("select 1 from GlTransmission where batchId = :batchId");
        query.setParameter("batchId", batchId);
        query.setMaxResults(1);

        boolean batchIdExists = !CollectionUtils.isEmpty(query.getResultList());

        if (batchIdExists) {

            List<GlBatchBaseline> baselines = new LinkedList<GlBatchBaseline>();

            // Getting charge amounts for GL types
            query = em.createQuery("select glt.code, " +
                    " sum(t.amount)-sum(t.allocatedAmount)-sum(t.lockedAllocatedAmount) " +
                    " from Charge t " +
                    " inner join t.generalLedgerType glt " +
                    " where t.glEntryGenerated = true " +
                    " group by glt.code");

            List<Object[]> results = query.getResultList();

            if (!CollectionUtils.isEmpty(results)) {
                for (Object[] values : results) {

                    String glTypeCode = (String) values[0];
                    BigDecimal unallocatedAmount = new BigDecimal(values[1].toString());

                    GlBatchBaseline baseline = new GlBatchBaseline();
                    baseline.setBatchId(batchId);
                    baseline.setType(GlBatchBaselineType.GL_TYPE);
                    baseline.setGeneralLedgerType(getGeneralLedgerType(glTypeCode));
                    baseline.setAmount(unallocatedAmount);

                    persistEntity(baseline);

                    baselines.add(baseline);
                }
            }

            // Getting payment and deferment amounts
            query = em.createQuery("select type(t), " +
                    " sum(t.amount)-sum(t.allocatedAmount)-sum(t.lockedAllocatedAmount) " +
                    " from Transaction t " +
                    " where t.glEntryGenerated = true " +
                    " and type(t) in (Payment, Deferment) " +
                    " group by type(t)");

            results = query.getResultList();
            if (!CollectionUtils.isEmpty(results)) {
                for (Object[] values : results) {

                    Class<Transaction> transactionType = (Class<Transaction>) values[0];
                    BigDecimal unallocatedAmount = (values[1] != null) ?
                            new BigDecimal(values[1].toString()) : BigDecimal.ZERO;

                    GlBatchBaseline baseline = new GlBatchBaseline();
                    baseline.setBatchId(batchId);
                    baseline.setType(Payment.class.equals(transactionType) ?
                            GlBatchBaselineType.UNALLOCATED_CASH : GlBatchBaselineType.DEFERMENT);
                    baseline.setAmount(unallocatedAmount);

                    persistEntity(baseline);

                    baselines.add(baseline);
                }
            }

            return baselines;
        }

        String errMsg = "Batch ID = " + batchId + " does not exist";
        logger.error(errMsg);
        throw new IllegalArgumentException(errMsg);
    }

    /**
     * Retrieves the previously persisted GlBatchBaseline objects that contain some summary information about
     * transactions and their amounts by Batch ID.
     *
     * @param batchId Transmission batch ID generated during transaction export
     * @return list of GlBatchBaseline instances
     */
    @Override
    public List<GlBatchBaseline> getGlBaselineAmounts(String batchId) {
        Query query = em.createQuery("select b from GlBatchBaseline b " +
                " left outer join fetch b.generalLedgerType " +
                " where b.batchId = :batchId");
        query.setParameter("batchId", batchId);
        return query.getResultList();
    }


    /**
     * Returns all GL Breakdown Overrides associated with the specified transaction.
     *
     * @param transactionId Transaction ID
     * @return list of GlBreakdownOverride instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_GL_BREAKDOWN)
    public List<GlBreakdownOverride> getGlBreakdownOverrides(Long transactionId) {

        Query query = em.createQuery("select g from GlBreakdownOverride g inner join fetch g.transaction t " +
                " where t.id = :transactionId");

        query.setParameter("transactionId", transactionId);

        return query.getResultList();
    }

    /**
     * Associates the transaction specified by ID with the list of GlBreakdownOverride instances.
     *
     * @param transactionId        Transaction ID
     * @param glBreakdownOverrides list of GlBreakdownOverride instances
     * @return list of GlBreakdownOverride IDs
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_GL_BREAKDOWN)
    public List<Long> createGlBreakdownOverrides(Long transactionId, List<GlBreakdownOverride> glBreakdownOverrides) {

        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) {
            String errMsg = "Transaction with ID = " + transactionId + " does not exist";
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        if (!isGlBreakdownValid(glBreakdownOverrides)) {
            String errMsg = "GL Breakdown Overrides are invalid: " + glBreakdownOverrides;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        Query query = em.createQuery("delete from GlBreakdownOverride where transaction.id = :transactionId");
        query.setParameter("transactionId", transactionId);
        query.executeUpdate();

        List<Long> overrideIds = new ArrayList<Long>(glBreakdownOverrides.size());
        for (GlBreakdownOverride glBreakdownOverride : glBreakdownOverrides) {
            glBreakdownOverride.setTransaction(transaction);
            Long breakdownId = persistEntity(glBreakdownOverride);
            overrideIds.add(breakdownId);
        }

        if (!CollectionUtils.isEmpty(glBreakdownOverrides)) {
            transaction.setGlOverridden(true);
        }

        return overrideIds;
    }

    /**
     * This method persists new GL breakdowns and associates them with the given GL and transaction types.
     * It also provides validation of the breakdowns.
     *
     * @param glTypeId          GL type ID
     * @param transactionTypeId Transaction type ID
     * @param breakdowns        a list of GL breakdowns
     * @return a list of GL breakdown IDs
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_GL_BREAKDOWN)
    public List<Long> createGlBreakdowns(Long glTypeId, TransactionTypeId transactionTypeId, List<GlBreakdown> breakdowns) {

        List<Long> breakdownIds = new ArrayList<Long>(breakdowns.size());

        GeneralLedgerType glType = getGeneralLedgerType(glTypeId);
        if (glType == null) {
            String errMsg = "General Ledger Type with ID = " + glTypeId + " does not exist";
            logger.error(errMsg);
            throw new InvalidGeneralLedgerTypeException(errMsg);
        }

        TransactionType transactionType = transactionService.getTransactionType(transactionTypeId);
        if (transactionType == null) {
            String errMsg = "Transaction Type with ID = " + glTypeId + " does not exist";
            logger.error(errMsg);
            throw new InvalidTransactionTypeException(errMsg);
        }

        if (!isGlBreakdownValid(breakdowns)) {
            String errMsg = "GL Breakdowns are invalid: " + breakdowns;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        // Removing all existing GL breakdowns for the same transaction type
        Query query = em.createQuery("delete from GlBreakdown where transactionType.id = :transactionTypeId");
        query.setParameter("transactionTypeId", transactionTypeId);
        query.executeUpdate();

        for (GlBreakdown breakdown : breakdowns) {

            breakdown.setTransactionType(transactionType);
            breakdown.setGeneralLedgerType(glType);

            Long breakdownId = persistEntity(breakdown);

            breakdownIds.add(breakdownId);
        }

        return breakdownIds;
    }

    /**
     * Retrieves a list of GL Breakdowns for the given transaction type ID.
     *
     * @param transactionTypeId TransactionType ID
     * @return list of GlBreakdown instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_GL_BREAKDOWN)
    public List<GlBreakdown> getGlBreakdowns(TransactionTypeId transactionTypeId) {

        Query query = em.createQuery("select g from GlBreakdown g " +
                " left outer join fetch g.transactionType tt " +
                " left outer join fetch g.generalLedgerType glt " +
                " where tt.id =  :transactionTypeId " +
                " order by g.breakdown desc");

        query.setParameter("transactionTypeId", transactionTypeId);

        return query.getResultList();
    }

    /**
     * Retrieves a list of GL Breakdowns or Breakdown Overrides (if Transaction is overridden)
     * for the given Transaction instance.
     *
     * @param transaction Transaction instance
     * @return list of GlBreakdown or GlBreakdownOverride instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_GL_BREAKDOWN)
    public List<AbstractGlBreakdown> getGlBreakdowns(Transaction transaction) {

        GeneralLedgerType glType = transaction.getGeneralLedgerType();
        if (glType == null) {
            String errMsg = "Transaction (ID = " + transaction.getId() + ") must have GL type";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        Query query = em.createQuery("select g from " +
                (transaction.isGlOverridden() ? "GlBreakdownOverride" : "GlBreakdown") +
                " g where " +
                (transaction.isGlOverridden() ?
                        "g.transaction.id = :id" :
                        "g.generalLedgerType.id = :id and g.transactionType.id = :transactionTypeId") +
                " order by g.breakdown desc");

        query.setParameter("id", transaction.isGlOverridden() ? transaction.getId() : glType.getId());

        if (!transaction.isGlOverridden()) {
            query.setParameter("transactionTypeId", transaction.getTransactionType().getId());
        }

        List<AbstractGlBreakdown> breakdowns = query.getResultList();

        if (!transaction.isGlOverridden() && org.apache.commons.collections.CollectionUtils.isEmpty(breakdowns)) {
            GeneralLedgerType defaultGlType = getDefaultGeneralLedgerType();
            query.setParameter("id", defaultGlType.getId());
            breakdowns = query.getResultList();
        }

        return breakdowns;
    }

    /**
     * Retrieves a list of all failed GL transactions.
     *
     * @return list of FailedGlTransaction instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_FAILED_GL_TRANSACTION)
    public List<FailedGlTransaction> getFailedGlTransactions() {
        Query query = em.createQuery(SELECT_FAILED_GL_TRANSACTION + " order by f.id desc");
        return query.getResultList();
    }

    /**
     * Retrieves a list of failed GL transactions for the given Account ID.
     *
     * @param accountId Account ID
     * @return list of FailedGlTransaction instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_FAILED_GL_TRANSACTION)
    public List<FailedGlTransaction> getFailedGlTransactionsForAccount(String accountId) {

        Query query = em.createQuery(SELECT_FAILED_GL_TRANSACTION + " where t.account.id = :accountId order by f.id desc");

        query.setParameter("accountId", accountId);

        return query.getResultList();
    }

    /**
     * Retrieves a list of failed GL transactions for the given Transaction's effective start and end dates.
     *
     * @param startDate Transaction effective start date
     * @param endDate   Transaction effective end date
     * @return list of FailedGlTransaction instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_FAILED_GL_TRANSACTION)
    public List<FailedGlTransaction> getFailedGlTransactionsForDates(Date startDate, Date endDate) {

        if (startDate == null || endDate == null) {
            String errMsg = "Start and End dates are required";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        startDate = CalendarUtils.removeTime(startDate);
        endDate = CalendarUtils.removeTime(endDate);

        if (startDate.after(endDate)) {
            String errMsg = "Start date cannot be greater than End date";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Query query = em.createQuery(SELECT_FAILED_GL_TRANSACTION +
                " where t.effectiveDate between :startDate and :endDate order by f.id desc");

        query.setParameter("startDate", startDate, TemporalType.DATE);
        query.setParameter("endDate", endDate, TemporalType.DATE);

        return query.getResultList();
    }


}