package com.sigmasys.kuali.ksa.service.fm.impl;

import java.math.BigDecimal;
import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.Query;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.exception.AccountBlockedException;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingChargeStatus;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingPlan;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyPlan;
import com.sigmasys.kuali.ksa.service.AccountBlockingService;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.fm.BrmFeeManagementService;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.fm.*;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingTransferDetail;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyTransferDetail;
import com.sigmasys.kuali.ksa.service.TransactionService;
import com.sigmasys.kuali.ksa.service.TransactionTransferService;
import com.sigmasys.kuali.ksa.service.fm.FeeManagementService;
import com.sigmasys.kuali.ksa.service.impl.GenericPersistenceService;
import com.sigmasys.kuali.ksa.service.pb.PaymentBillingService;
import com.sigmasys.kuali.ksa.service.security.PermissionUtils;
import com.sigmasys.kuali.ksa.service.tp.ThirdPartyTransferService;
import com.sigmasys.kuali.ksa.util.BeanUtils;

/**
 * FeeManagementService implementation.
 * <p/>
 * <p/>
 * This class provides a concrete implementation of the <code>FeeManagementService</code> interface
 * according to the specification in the "Process Diagrams" document
 *
 * @author Sergey Godunov
 * @author Michael Ivanov
 */
@Service("feeManagementService")
@Transactional(readOnly = true)
@WebService(serviceName = FeeManagementService.SERVICE_NAME, portName = FeeManagementService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
@SuppressWarnings("unchecked")
public class FeeManagementServiceImpl extends GenericPersistenceService implements FeeManagementService {

    /**
     * This class's logger.
     */
    private static final Log logger = LogFactory.getLog(FeeManagementServiceImpl.class);

    // A query to get Manifests without regard to statuses. Status filtering can be added later:
    private static final String GET_MANIFESTS_JOIN = "select m from FeeManagementManifest m " +
            " left outer join fetch m.keyPairs kp " +
            " left outer join fetch m.tags t " +
            " left outer join fetch m.rate r " +
            " left outer join fetch m.rollup ru " +
            " left outer join fetch m.linkedManifest lm ";

    // A query to get FM Sessions:
    private static final String GET_FM_SESSION_JOIN = "select s from FeeManagementSession s " +
            " left outer join fetch s.prevSession ps " +
            " left outer join fetch s.nextSession ns " +
            " left outer join fetch s.keyPairs kp " +
            " left outer join fetch s.account a ";

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ThirdPartyTransferService thirdPartyService;

    @Autowired
    private PaymentBillingService paymentBillingService;

    @Autowired
    private TransactionTransferService transactionTransferService;

    @Autowired
    private RateService rateService;

    @Autowired
    private AccountBlockingService accountBlockingService;

    @Autowired
    private BrmFeeManagementService brmFeeManagementService;


    /**
     * Queues a FeeManagementSession.
     *
     * @param feeManagementSessionId ID of an FM Session to queue.
     */
    @Override
    @Transactional(readOnly = false)
    public void addFeeManagementSessionToQueue(Long feeManagementSessionId) {

        // Get the FM Session:
        FeeManagementSession fmSession = getEntity(feeManagementSessionId, FeeManagementSession.class);

        if (fmSession == null) {
            String errorMsg = String.format("Cannot find an FM session with the ID %d.", feeManagementSessionId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Check if the FM Session is current:
        if (fmSession.getStatus() != FeeManagementSessionStatus.CURRENT) {
            String errorMsg = String.format("Only CURRENT FM Session can be Queued. Session was %s.", fmSession.getStatus());
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        // Set the FM session as queued:
        fmSession.setQueued(true);
        fmSession.setReviewComplete(false);

        persistEntity(fmSession);
    }

    /**
     * Dequeues a FeeManagementSession.
     *
     * @param feeManagementSessionId ID of an FM Session to dequeue.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_FM_QUEUE)
    public void removeFeeManagementSessionFromQueue(Long feeManagementSessionId) {

        // Get the FM Session:
        FeeManagementSession fmSession = getEntity(feeManagementSessionId, FeeManagementSession.class);

        if (fmSession == null) {
            String errorMsg = String.format("Cannot find an FM session with the ID %d.", feeManagementSessionId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        fmSession.setQueued(false);

        persistEntity(fmSession);
    }

    /**
     * This method kicks off the workflow for assessing fees by creating a fee management session and then
     * queuing it up for later execution.
     *
     * @param feeManagementTermRecord FM Term Record for FM Session creation
     * @return FeeManagementSession instance
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.RUN_FEE_ASSESSMENT)
    public FeeManagementSession queueFeeManagementSession(FeeManagementTermRecord feeManagementTermRecord) {

        FeeManagementSession feeManagementSession = createFeeManagementSession(feeManagementTermRecord);

        // Queue the FM Session for later execution:
        addFeeManagementSessionToQueue(feeManagementSession.getId());

        return feeManagementSession;
    }

    /**
     * Creates a new FM Session and actually charges it.
     *
     * @param feeManagementTermRecord A transient FM data holder.
     * @return FeeManagementSession instance
     */
    @Override
    @Transactional(readOnly = false)
    public FeeManagementSession assessRealTimeFeeManagement(FeeManagementTermRecord feeManagementTermRecord) {

        // Create and reconcile an FM Session:
        FeeManagementSession fmSession = reconcileSessionForRealTimeFeeManagement(feeManagementTermRecord);

        // Charge the FM Session:
        chargeSession(fmSession.getId());

        return fmSession;
    }

    /**
     * Simulates Real Time FeeManagement. Doesn't actually charge the FM Session, but rather creates
     * a report displaying what would have happened if the FM Session had been charged.
     *
     * @param feeManagementTermRecord A transient FM data holder.
     * @return A report with Fee Management assessment.
     */
    @Override
    @Transactional(readOnly = false)
    public FeeManagementReportInfo simulateRealTimeFeeManagement(FeeManagementTermRecord feeManagementTermRecord) {

        // Create and reconcile an FM Session:
        FeeManagementSession fmSession = reconcileSessionForRealTimeFeeManagement(feeManagementTermRecord);

        // Create an FM Report:
        return createFeeManagementReport(fmSession.getId());
    }

    /**
     * Accesses an FM Session and invokes the Rules Engine to create a Manifest.
     *
     * @param feeManagementSessionId ID of an FM session to process.
     * @return FeeManagementSession instance
     */
    @Override
    @Transactional(readOnly = false)
    public FeeManagementSession processFeeManagementSession(Long feeManagementSessionId) {
        return brmFeeManagementService.assessFees(feeManagementSessionId);
    }

    /**
     * Creates a new FM Session using the given FM TermRecord.
     *
     * @param feeManagementTermRecord FM TermRecord.
     * @return The newly generated FM Session.
     */
    @Override
    @Transactional(readOnly = false)
    public FeeManagementSession createFeeManagementSession(FeeManagementTermRecord feeManagementTermRecord) {

        // Validate the FM TermRecord. This call with either raise an exception or create a valid Account:
        Account account = validateForFeeManagementSessionCreation(feeManagementTermRecord);

        // Get the last completed Prior FM Session:
        FeeManagementSession priorSession = getLastCompletedFmSession(feeManagementTermRecord);

        // Create a new FM Session using the Prior session, which may not exist:
        return createNewFmSession(priorSession, feeManagementTermRecord, account);
    }

    /**
     * Creates a report info from an FM Session. Documentation to this method reads:
     * In order to report back a simulated session, we can use this method to create a simplified version of the
     * manifest. This can be sent back to an external system.
     * NOTE: FeeManagementReportInfo is a non-persistent object.
     *
     * @param feeManagementSessionId ID of an FM Session for which to create a report.
     * @return The newly created report object.
     */
    @Override
    @Transactional(readOnly = false)
    public FeeManagementReportInfo createFeeManagementReport(Long feeManagementSessionId) {

        // Find the FM Session:
        FeeManagementSession fmSession = getEntity(feeManagementSessionId, FeeManagementSession.class);

        if (fmSession == null) {
            String errorMsg = String.format("Cannot find an FM Session with ID %d.", feeManagementSessionId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Create a new FeeManagementReportInfo object:
        return createFeeManagementReportInfo(fmSession);
    }

    /**
     * Reconciles a FeeManagement session.
     * Session reconciliation takes former sessions and ensures that charges that need to be removed are
     * accurately recorded in the new session.
     *
     * @param feeManagementSessionId FeeManagementSession ID
     */
    @Override
    @Transactional(readOnly = false)
    public void reconcileSession(Long feeManagementSessionId) {

        // Get a FeeManagement session with the given ID:
        FeeManagementSession currentSession = getFullFeeManagementSessionById(feeManagementSessionId);

        // Validate the FM session for reconciliation:
        validateSessionForReconciliation(currentSession, feeManagementSessionId);

        // Get the last CHARGED FM Session:
        FeeManagementSession lastChargedFmSession = getLastChargedSession(currentSession);

        // Get current FM Session's manifests:
        List<FeeManagementManifest> currentManifests = getManifests(currentSession.getId(),
                FeeManagementManifestType.CHARGE, FeeManagementManifestType.CANCELLATION, FeeManagementManifestType.DISCOUNT);

        // If there is a prior CHARGED FM Session, perform line adjustment:
        if (lastChargedFmSession != null) {
            // Perform line comparison and status adjustment.
            processPriorSessionAdjustment(currentSession, lastChargedFmSession, currentManifests);
        }

        // Optionally, remove inverse manifests from the list of current manifests:
        boolean preclearCurrentManifests = BooleanUtils.toBoolean(configService.getParameter(Constants.FM_PRECLEAR_MANIFEST));

        if (preclearCurrentManifests) {
            // Remove inverse manifests:
            removeInverseManifests(currentManifests);
        }

        // Validate reversal manifests of the current FM session:
        validateCurrentReversals(currentManifests);

        // Updated the status of the current FM Session to RECONCILED or SIMULATED_RECONCILED:
        FeeManagementSessionStatus newFmStatus = (currentSession.getStatus() == FeeManagementSessionStatus.CURRENT)
                ? FeeManagementSessionStatus.RECONCILED : FeeManagementSessionStatus.SIMULATED_RECONCILED;

        // Update the entity:
        currentSession.setChargeStatus(newFmStatus);
        persistEntity(currentSession);
    }

    /**
     * Creates charges on the current Fee Management session.
     * Creates reversal and cancellation and also rate transactions.
     *
     * @param feeManagementSessionId FeeManagementSession ID
     */
    @Override
    @Transactional(readOnly = false)
    public void chargeSession(Long feeManagementSessionId) {

        // Get a FeeManagement session with the given ID:
        FeeManagementSession session = getFullFeeManagementSessionById(feeManagementSessionId);

        // Validate the FM Session for charge operation:
        validateSessionForCharge(session, feeManagementSessionId);

        // Get all manifests for the session:
        List<FeeManagementManifest> manifests = getManifests(feeManagementSessionId);
        List<PaymentBillingTransferDetail> pbtDetails = new ArrayList<PaymentBillingTransferDetail>();
        List<ThirdPartyTransferDetail> tptDetails = new ArrayList<ThirdPartyTransferDetail>();

        for (FeeManagementManifest manifest : manifests) {
            // If there is no transaction, process manifest charge processing.
            // Otherwise, continue to the new manifest.
            // Inherently, all ORIGINAL manifests will have a Transaction.
            if (manifest.getTransaction() == null) {
                processManifestCharge(manifest, false, session, pbtDetails, tptDetails);
            }
        }

        // If there were reversals, perform Transfer Reinstatement:
        if (CollectionUtils.isNotEmpty(pbtDetails) || CollectionUtils.isNotEmpty(tptDetails)) {
            performTransferReinstatement(pbtDetails, tptDetails, session.getAccount());
        }

        // Set the status of the Session to CHARGE and persist:
        session.setChargeStatus(FeeManagementSessionStatus.CHARGED);
        persistEntity(session);
    }

    /**
     * Returns all FM Manifests associated with an FM Session with the given ID.
     *
     * @param feeManagementSessionId An FM Session ID.
     * @return All associated FM Manifests.
     */
    @Override
    public List<FeeManagementManifest> getManifests(Long feeManagementSessionId) {
        return getManifests(feeManagementSessionId, (FeeManagementManifestType[]) null);
    }

    /**
     * Returns all FM Manifests of given types associated with an FM Session with the given ID.
     *
     * @param feeManagementSessionId An FM Session ID.
     * @param manifestTypes          Types of FM Manifests to retrieve.
     * @return All associated FM Manifests of given types.
     */
    @Override
    @WebMethod(exclude = true)
    public List<FeeManagementManifest> getManifests(Long feeManagementSessionId, FeeManagementManifestType... manifestTypes) {

        // Check permissions to perform this operation:
        PermissionUtils.checkPermission(Permission.READ_RATE);

        boolean typesExist = (manifestTypes != null && manifestTypes.length > 0);

        List<String> typeCodes = null;

        if (typesExist) {
            typeCodes = new ArrayList<String>(manifestTypes.length);
            for (FeeManagementManifestType manifestType : manifestTypes) {
                typeCodes.add(manifestType.getId());
            }
        }

        // Create a query to select all FM Manifests linked to the given FM Session:
        Query query = em.createQuery(GET_MANIFESTS_JOIN + " where m.session.id = :fmSessionId" +
                (typesExist ? " and m.typeCode in (:typeCodes)" : ""));

        query.setParameter("fmSessionId", feeManagementSessionId);

        if (typesExist) {
            query.setParameter("typeCodes", typeCodes);
        }

        return query.getResultList();
    }

    /**
     * Creates and persists a new instance of FeeManagementManifest class for the given parameters.
     *
     * @param manifestType      FeeManagementManifestType enum value
     * @param manifestStatus    FeeManagementManifestStatus enum value
     * @param transactionTypeId Transaction type ID
     * @param rateId            Rate ID
     * @param internalChargeId  Internal Charge ID
     * @param registrationId    Registration ID
     * @param offeringId        Offering ID
     * @param effectiveDate     Effective Date
     * @param recognitionDate   Recognition Date
     * @param amount            Manifest amount
     * @param isSessionCurrent  Indicates if the FM session should be current or not.
     * @return FeeManagementManifest instance
     */
    @Override
    @Transactional(readOnly = false)
    public FeeManagementManifest createFeeManagementManifest(FeeManagementManifestType manifestType,
                                                             FeeManagementManifestStatus manifestStatus,
                                                             String transactionTypeId,
                                                             Long rateId,
                                                             String internalChargeId,
                                                             String registrationId,
                                                             String offeringId,
                                                             Date effectiveDate,
                                                             Date recognitionDate,
                                                             BigDecimal amount,
                                                             boolean isSessionCurrent) {

        if (StringUtils.isBlank(transactionTypeId)) {
            String errMsg = "Transaction type ID cannot be null or empty";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (rateId == null) {
            String errMsg = "Rate ID cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (effectiveDate == null) {
            String errMsg = "Effective date cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (recognitionDate == null) {
            String errMsg = "Recognition date cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (StringUtils.isBlank(internalChargeId) && StringUtils.isBlank(registrationId)) {
            String errMsg = "Either internalChargeId or registrationId must be set";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (!transactionService.transactionTypeExists(transactionTypeId)) {
            String errMsg = "TransactionType with ID = " + transactionTypeId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Rate rate = rateService.getRate(rateId);
        if (rate == null) {
            String errMsg = "Rate with ID = " + rateId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (amount == null) {
            String errMsg = "Amount must not be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        FeeManagementManifest manifest = new FeeManagementManifest();

        manifest.setType(manifestType);
        manifest.setStatus(manifestStatus);
        manifest.setTransactionTypeId(transactionTypeId);
        manifest.setRate(rate);
        manifest.setInternalChargeId(internalChargeId);
        manifest.setRegistrationId(registrationId);
        manifest.setOfferingId(offeringId);
        manifest.setEffectiveDate(effectiveDate);
        manifest.setRecognitionDate(recognitionDate);
        manifest.setAmount(amount);
        manifest.setSessionCurrent(isSessionCurrent);

        persistEntity(manifest);

        return manifest;
    }

    /**
     * Returns FeeManagementSession instance by ID from the persistent store.
     *
     * @param sessionId FeeManagementSession ID
     * @return FeeManagementSession instance
     */
    @Override
    public FeeManagementSession getFeeManagementSession(Long sessionId) {

        Query query = em.createQuery("select s from FeeManagementSession s " +
                " left outer join fetch s.account " +
                " left outer join fetch s.prevSession " +
                " left outer join fetch s.nextSession " +
                " left outer join fetch s.keyPairs " +
                " left outer join fetch s.signups " +
                " left outer join fetch s.manifests " +
                " where s.id = :sessionId");

        query.setParameter("sessionId", sessionId);

        List<FeeManagementSession> sessions = query.getResultList();

        return CollectionUtils.isNotEmpty(sessions) ? sessions.get(0) : null;
    }


    /***************************************************************************
     *
     *
     * Private helper methods for "assessRealTimeFeeManagement"
     *  and "simulateRealTimeFeeManagement".
     *
     *
     ***************************************************************************/

    /**
     * Creates and reconciles an FM Session for Real Time Fee Management.
     *
     * @param fmTermRecord A transient placeholder for FM data.
     * @return A newly created and reconciled FM Session object.
     */
    private FeeManagementSession reconcileSessionForRealTimeFeeManagement(FeeManagementTermRecord fmTermRecord) {

        // Create a new FM Session. If there are any validation errors, they will be thrown by this call:
        FeeManagementSession fmSession = createFeeManagementSession(fmTermRecord);

        // Process the FM Session using the rules engine:
        processFeeManagementSession(fmSession.getId());

        // Reconcile the FM Session:
        reconcileSession(fmSession.getId());

        return fmSession;
    }


    /***************************************************************************
     *
     *
     * Private helper methods for "createFeeManagementReport".
     *
     *
     ***************************************************************************/

    /**
     * Creates a report from an FM Session.
     * NOTE: FeeManagementReportInfo is a non-persistent object.
     *
     * @param fmSession FM Session.
     * @return The newly created report.
     */
    private FeeManagementReportInfo createFeeManagementReportInfo(FeeManagementSession fmSession) {

        // Create a new FeeManagementReportInfo object:
        FeeManagementReportInfo reportInfo = new FeeManagementReportInfo();

        reportInfo.setAccountId(fmSession.getAccount().getId());
        reportInfo.setCreationDate(new Date());

        // Get FM Manifests:
        List<FeeManagementManifest> manifests = getManifests(fmSession.getId());
        BigDecimal totalCharges = BigDecimal.ZERO;
        BigDecimal totalCorrections = BigDecimal.ZERO;

        if (CollectionUtils.isNotEmpty(manifests)) {

            // Add an empty list of FeeManagementReportItem report items:
            reportInfo.setReportItems(new ArrayList<FeeManagementReportItem>());

            // Iterate through the manifests:
            for (FeeManagementManifest manifest : manifests) {

                // Create a report item:
                FeeManagementReportItem reportItem = new FeeManagementReportItem();
                String statementText =
                        (manifest.getTransaction() != null) && (manifest.getTransaction().getTransactionType() != null)
                                ? manifest.getTransaction().getTransactionType().getDefaultStatement() : null;
                boolean isAlreadyCharged = !BooleanUtils.toBoolean(manifest.isSessionCurrent());

                reportItem.setTransactionTypeId(manifest.getTransactionTypeId());
                reportItem.setAmount(manifest.getAmount());
                reportItem.setEffectiveDate(manifest.getEffectiveDate());
                reportItem.setManifestType(manifest.getType());
                reportItem.setAlreadyCharged(isAlreadyCharged);
                reportItem.setStatementText(statementText);

                // Add to report items:
                reportInfo.getReportItems().add(reportItem);

                // Adjust the amounts based on the manifest type:
                switch (manifest.getType()) {
                    case CANCELLATION:
                    case DISCOUNT:
                    case CORRECTION:
                        totalCorrections = totalCorrections.add(manifest.getAmount());
                        break;

                    default:
                        totalCharges = totalCharges.add(manifest.getAmount());
                        break;
                }
            }
        }

        // Calculate the impact on account:
        reportInfo.setNetImpact(totalCharges.subtract(totalCorrections));

        return reportInfo;
    }

    /***************************************************************************
     *
     *
     * Private helper methods for "createFeeManagementSession".
     *
     *
     ***************************************************************************/

    /**
     * Performs validation of an FM Term Record for FM Session creation.
     *
     * @param feeManagementTermRecord An FM TermRecord for FM Session creation.
     * @return Account Account for which an FM Session is being created.
     *         Returns null if the account is invalid.
     * @throws RuntimeException or a subclass thereof if validation fails.
     */
    private Account validateForFeeManagementSessionCreation(FeeManagementTermRecord feeManagementTermRecord) {

        // Get the account associated with the new Session:
        String accountId = feeManagementTermRecord.getAccountId();

        if (StringUtils.isBlank(accountId)) {
            String errorMsg = String.format("Invalid Account ID [%s] associated with the in FM Session creation.", accountId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Get the Account:
        Account account = accountService.getOrCreateAccount(accountId);

        if (account == null) {
            String errorMsg = String.format("Cannot find an Account with ID %s in FM Session creation.", accountId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Check if the Account is a chargeable Account:
        if (!(account instanceof ChargeableAccount)) {
            String errorMsg = String.format("Account %s is not a ChargeableAccount in FM Session creation.", accountId);
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        // Check the FM Term Record status. Must be either CURRENT or SIMULATED:
        FeeManagementSessionStatus status = feeManagementTermRecord.getSessionStatus();

        if ((status != FeeManagementSessionStatus.CURRENT) && (status != FeeManagementSessionStatus.SIMULATED)) {
            String errorMsg = String.format("Invalid FeeManagement Session status [%s]. Session status must be CURRENT or SIMULATED to call \"createFeeManagementSession\".", status);
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        // Check the ATP ID on the FM TermRecord is valid:
        if (StringUtils.isBlank(feeManagementTermRecord.getAtpId())) {
            String errorMsg = String.format("Invalid ATP ID [%s] in FM TermRecord in FM Session creation.", feeManagementTermRecord.getAtpId());
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        // Check that the ATP ID on each IncomingSignup is valid too:
        if (CollectionUtils.isNotEmpty(feeManagementTermRecord.getIncomingSignups())) {
            for (FeeManagementIncomingSignup incomingSignup : feeManagementTermRecord.getIncomingSignups()) {
                if (StringUtils.isBlank(incomingSignup.getAtpId())) {
                    String errorMsg = String.format("Invalid ATP ID [%s] in FM IncomingSignup in FM Session creation.", incomingSignup.getAtpId());
                    logger.error(errorMsg);
                    throw new IllegalStateException(errorMsg);
                }
            }
        }

        return account;
    }

    /**
     * Returns the last completed FM Session for the Account and ATP ID from the given FM TermRecord.
     * The last recorded FM Session will have the "nextSession" attribute equal to "null".
     *
     * @param feeManagementTermRecord FM TermRecord.
     * @return The last completed FM Session with the given parameters from the FM Term Record.
     */
    private FeeManagementSession getLastCompletedFmSession(FeeManagementTermRecord feeManagementTermRecord) {

        // Get the prior session:
        FeeManagementSession priorSession = getLastPriorFeeManagementSession(
                feeManagementTermRecord.getAccountId(), feeManagementTermRecord.getAtpId());

        // If a prior FM Session is found, check its status. Keep checking until we either find an FM Session
        // that is not CURRENT or SIMULATED or get to the point where there is no prior FM Session:
        while (priorSession != null) {
            FeeManagementSessionStatus status = priorSession.getStatus();

            if ((status == FeeManagementSessionStatus.SIMULATED) || (status == FeeManagementSessionStatus.CURRENT)) {
                // Get the IDs of the current and previous sessions:
                Long fmSessionId = priorSession.getId();
                Long prevFmSessionId = (priorSession.getPrevSession() != null) ? priorSession.getPrevSession().getId() : null;

                // Completely remove the current (prior) session:
                em.remove(priorSession);

                // If there was a previous session, move on to it:
                if (prevFmSessionId != null) {
                    priorSession = getFullFeeManagementSessionById(prevFmSessionId);
                }
            }
        }

        return priorSession;
    }

    /**
     * Returns a full FeeManagementSession object by an Account ID and an ATP ID.
     *
     * @param accountId ID of an Account linked to the FM Session to find.
     * @param atpId     ATP ID.
     * @return A FeeManagementSession object with the given ID.
     */
    private FeeManagementSession getLastPriorFeeManagementSession(String accountId, String atpId) {

        // Create a query:
        Query query = em.createQuery(GET_FM_SESSION_JOIN + " where s.account.id = :accountId and s.atpId = :atpId and s.nextSession is null ");

        query.setParameter("accountId", accountId);
        query.setParameter("atpId", atpId);

        // Get the results:
        List results = query.getResultList();

        return CollectionUtils.isNotEmpty(results) ? (FeeManagementSession) results.get(0) : null;
    }

    /**
     * Returns a full FeeManagementSession object by its ID.
     *
     * @param fmSessionId ID of a FeeManagementSession object.
     * @return A FeeManagementSession object with the given ID.
     */
    private FeeManagementSession getFullFeeManagementSessionById(Long fmSessionId) {

        // Create a query:
        Query query = em.createQuery(GET_FM_SESSION_JOIN + " where s.id = :id ");

        query.setParameter("id", fmSessionId);

        // Get the result:
        List results = query.getResultList();

        return CollectionUtils.isNotEmpty(results) ? (FeeManagementSession) results.get(0) : null;
    }

    /**
     * Creates a new FM Session using the Prior Session, which may not exist.
     *
     * @param priorSession            A Prior FM Session (optional).
     * @param feeManagementTermRecord A FM TermRecord.
     * @param account                 User Account associated with the new FM Session.
     * @return The newly created FM Session.
     */
    private FeeManagementSession createNewFmSession(FeeManagementSession priorSession,
                                                    FeeManagementTermRecord feeManagementTermRecord, Account account) {

        // Create a new FM Session:
        FeeManagementSession newSession = new FeeManagementSession();

        newSession.setAccount(account);
        newSession.setPrevSession(priorSession);
        newSession.setAtpId(feeManagementTermRecord.getAtpId());
        newSession.setReviewRequired(false);
        newSession.setReviewComplete(false);
        newSession.setCreationDate(new Date());
        newSession.setChargeStatus(feeManagementTermRecord.getSessionStatus());
        newSession.setQueued(false);

        // Persist the new FM Session:
        persistEntity(newSession);

        // Add the KeyPairs:
        if (CollectionUtils.isNotEmpty(feeManagementTermRecord.getKeyPairs())) {
            // Create new KeyPairs for the new Session:
            for (KeyPair keyPair : feeManagementTermRecord.getKeyPairs()) {
                // If the current KeyPair is a valid one, create a new one from it:
                if (StringUtils.isNotBlank(keyPair.getKey()) && StringUtils.isNotBlank(keyPair.getValue())) {
                    // Create and persist a new KeyPair:
                    KeyPair newKeyPair = new KeyPair(keyPair.getKey(), keyPair.getValue());

                    persistEntity(newKeyPair);
                    newSession.getKeyPairs().add(newKeyPair);
                }
            }

            // Persis the new KeyPairs:
            persistEntity(newSession);
        }

        // Add Signup records to the new FM Session.
        addSignupToFmSession(newSession, feeManagementTermRecord);

        // If there was a Prior Session, point to the new one and persist:
        if (priorSession != null) {
            priorSession.setNextSession(newSession);
            persistEntity(priorSession);
        }

        return newSession;
    }

    /**
     * Adds <code>Signup</code> from the given FeeManagementTermRecord to the specified FM Session.
     *
     * @param currentSession          An FM Session to add Signup to.
     * @param feeManagementTermRecord Source of IncomingSignup.
     */
    private void addSignupToFmSession(FeeManagementSession currentSession, FeeManagementTermRecord feeManagementTermRecord) {

        // If the FM TermRecord has IncomingSignup, create Signup for the given FM Session:
        if (CollectionUtils.isNotEmpty(feeManagementTermRecord.getIncomingSignups())) {
            for (FeeManagementIncomingSignup incomingSignup : feeManagementTermRecord.getIncomingSignups()) {
                // Create new Signup for the FM Session:
                FeeManagementSignup newSignup = new FeeManagementSignup();

                org.springframework.beans.BeanUtils.copyProperties(incomingSignup, newSignup);
                newSignup.setSession(currentSession);
                newSignup.setComplete(false);

                // Persist the new object:
                persistEntity(newSignup);

                // Add FM SignupRate (RateInfo) objects.
                addSignupRateToSignup(newSignup, incomingSignup, feeManagementTermRecord);
            }
        }
    }

    /**
     * Adds FM SignupRates from the specified FM IncomingSignup to the given FM Signup.
     *
     * @param signup                  FM Signup to add SignupRates to.
     * @param incomingSignup          FM Incoming Signup.
     * @param feeManagementTermRecord FM Term Record.
     */
    private void addSignupRateToSignup(FeeManagementSignup signup, FeeManagementIncomingSignup incomingSignup,
                                       FeeManagementTermRecord feeManagementTermRecord) {

        // Go through the list of IncomingRateInfo objects:
        if (CollectionUtils.isNotEmpty(incomingSignup.getRates())) {
            for (FeeManagementIncomingRateInfo incomingRateInfo : incomingSignup.getRates()) {

                // Get the Rate:
                Rate rate = findSignupRate(incomingSignup, incomingRateInfo, feeManagementTermRecord);

                // Create a new SignupRate object:
                FeeManagementSignupRate signupRate = new FeeManagementSignupRate();

                signupRate.setRate(rate);
                signupRate.setSignup(signup);
                persistEntity(signupRate);
            }
        }
    }

    /**
     * Attempts to find a Rate using the provided objects.
     *
     * @param incomingSignup          FM IncomingSignup DTO.
     * @param incomingRateInfo        FM IncomingRateInfo DTO.
     * @param feeManagementTermRecord FM TermRecord DTO.
     * @return Rate found using the parameters from the provided objects.
     * @throws IllegalArgumentException If the parameters in the provided objects do not result in a hit.
     */
    private Rate findSignupRate(FeeManagementIncomingSignup incomingSignup, FeeManagementIncomingRateInfo incomingRateInfo,
                                FeeManagementTermRecord feeManagementTermRecord) {

        // Use the RateService to find the Rate:
        String rateCode = incomingRateInfo.getRateCode();
        String rateSubCode = incomingRateInfo.getRateSubCode();
        String atpId = incomingSignup.getAtpId();
        Rate rate = null;

        try {
            rate = rateService.getRate(rateCode, rateSubCode, atpId);
        } catch (Exception e) {
            logger.error(String.format("Cannot find a Rate code [%s], subCode [%s], ATP ID [%s]", rateCode, rateSubCode, atpId), e);
        }

        // If a Rate is not found, try the ATP ID from the TermRecord:
        if (rate == null) {
            atpId = feeManagementTermRecord.getAtpId();

            try {
                rate = rateService.getRate(rateCode, rateSubCode, atpId);
            } catch (Exception e) {
                logger.error(String.format("Cannot find a Rate code [%s], subCode [%s], ATP ID [%s]", rateCode, rateSubCode, atpId), e);
            }
        }

        // If the Rate is still not found, raise an exception:
        if (rate == null) {
            String errorMsg = String.format("Cannot find a Rate code [%s], subCode [%s], ATP ID [%s] or [%s]",
                    rateCode, rateSubCode, atpId, incomingSignup.getAtpId());

            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        return rate;
    }


    /***************************************************************************
     *
     *
     * Private helper methods for "chargeSession".
     *
     *
     ***************************************************************************/

    /**
     * Checks all conditions necessary for FM session charge.
     * Throws an exception if any of the condition fails validation.
     *
     * @param fmSession              An FM Session.
     * @param feeManagementSessionId ID of the FM Session.
     */
    private void validateSessionForCharge(FeeManagementSession fmSession, Long feeManagementSessionId) {

        if (fmSession == null) {
            logger.error("Cannot find an FM session with the ID " + feeManagementSessionId);
            throw new IllegalArgumentException("Cannot find an FM session with the ID " + feeManagementSessionId);
        }

        // Check the session passed is RECONCILED or SIMULATED_RECONCILED:
        FeeManagementSessionStatus sessionStatus = fmSession.getStatus();

        if (sessionStatus == null || ((sessionStatus != FeeManagementSessionStatus.RECONCILED) && (sessionStatus != FeeManagementSessionStatus.SIMULATED_RECONCILED))) {
            String errorMsg = String.format("Invalid FeeManagement Session status [%s]. Session status must be RECONCILED or SIMULATED_RECONCILED to call \"chargeSession\".", sessionStatus);
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
    }

    /**
     * The main method for manifest charge processing.
     *
     * @param manifest         An FM manifest.
     * @param isLinkedManifest Is "manifest" an already linked manifest of the main manifest being processed?
     * @param session          An FM session the manifest belongs to.
     * @param pbtDetails       A list of Payment billing transfer details. An OUT parameter.
     * @param tptDetails       A list of Third-party transfer details. An OUT Parameter.
     */
    private void processManifestCharge(FeeManagementManifest manifest, boolean isLinkedManifest, FeeManagementSession session,
                                       List<PaymentBillingTransferDetail> pbtDetails, List<ThirdPartyTransferDetail> tptDetails) {

        // Check the manifest type:
        switch (manifest.getType()) {
            case CHARGE:
                processChargeTypeManifest(manifest, session, false, pbtDetails, tptDetails);
                break;

            case CORRECTION:
            case CANCELLATION:
            case DISCOUNT:
                processNonChargeTypeManifest(manifest, isLinkedManifest, session, pbtDetails, tptDetails);
                break;

            default:
                break; // Go to the next manifest.
        }
    }

    /**
     * Processes FM manifests only of the CHARGE type.
     *
     * @param manifest   An FM manifest of the CHARGE type.
     * @param session    An FM session the manifest belongs to.
     * @param isReversal Is the Charge a reversal? If yes, negate the amount.
     * @param pbtDetails A list of Payment billing transfer details. An OUT parameter.
     * @param tptDetails A list of Third-party transfer details. An OUT Parameter.
     */
    private void processChargeTypeManifest(FeeManagementManifest manifest, FeeManagementSession session, boolean isReversal,
                                           List<PaymentBillingTransferDetail> pbtDetails, List<ThirdPartyTransferDetail> tptDetails) {
        // Create a charge on the Account associated with the FM session:
        String transactionTypeId = manifest.getTransactionTypeId();

        if (StringUtils.isNotEmpty(transactionTypeId) && (session.getAccount() != null) && (manifest.getAmount() != null)) {
            // Create a new charge on the Account:
            String accountId = session.getAccount().getId();
            BigDecimal amount = isReversal ? manifest.getAmount().negate() : manifest.getAmount();
            Transaction newCharge = transactionService.createTransaction(transactionTypeId, accountId, new Date(), amount);

            // Update the manifest and session:
            manifest.setTransaction(newCharge);
            manifest.setSessionCurrent(true);
            persistEntity(manifest);

            // If there is a linked manifest, process it:
            if (manifest.getLinkedManifest() != null) {
                processManifestCharge(manifest.getLinkedManifest(), true, session, pbtDetails, tptDetails);
            }
        }
    }

    /**
     * Processes FM manifests of types other than CHARGE or ORIGINAL.
     *
     * @param manifest         An FM manifest.
     * @param isLinkedManifest Is "manifest" an already linked Manifest of the main manifest being processed?
     * @param session          An FM session the manifest belongs to.
     * @param pbtDetails       A list of Payment billing transfer details. An OUT parameter.
     * @param tptDetails       A list of Third-party transfer details. An OUT Parameter.
     */
    private void processNonChargeTypeManifest(FeeManagementManifest manifest, boolean isLinkedManifest, FeeManagementSession session,
                                              List<PaymentBillingTransferDetail> pbtDetails, List<ThirdPartyTransferDetail> tptDetails) {
        // Check if the linked manifest is complete, then process manifest charge, otherwise, move to the next manifest:
        FeeManagementManifest linkedManifest = isLinkedManifest ? manifest : manifest.getLinkedManifest();

        if (linkedManifest != null) {
            if (linkedManifest.getTransaction() != null) {
                // Clear all unlockedAllocations against the transaction in the linked manifest (Linked Transaction):
                Transaction linkedTransaction = linkedManifest.getTransaction();

                transactionService.removeAllocations(linkedTransaction.getId());

                // Get the Implicated Transaction:
                Transaction implicatedTransaction = getImplicatedTransaction(linkedTransaction);

                // Process the Implicated Transaction:
                processImplicatedTransaction(manifest, linkedManifest, implicatedTransaction, session, pbtDetails, tptDetails);
            }
        } else {
            // If there is no linked manifest, mark the session for manual review:
            logger.warn(String.format("Processing reversal charge for FM Session %d. Manifest with ID %d does not have a linked manifest.", session.getId(), manifest.getId()));
            session.setReviewRequired(true);
            persistEntity(session);

            // Process the reversal as negated charge:
            processChargeTypeManifest(manifest, session, true, pbtDetails, tptDetails);
        }
    }

    /**
     * Returns the Implicated Transaction, which is a Transaction associated with the Transaction from the Linked Manifest
     * (Linked Transaction) via an Allocation.
     *
     * @param linkedTransaction Transaction from the Linked Manifest (Linked Transaction).
     * @return An Implicated Transaction if any.
     */
    private Transaction getImplicatedTransaction(Transaction linkedTransaction) {

        // Find the Allocation that involves the Linked Transaction:
        Long linkedTransactionId = linkedTransaction.getId();
        List<Allocation> allocations = transactionService.getAllocations(linkedTransactionId);

        // If there are allocations, grab the first one and get the Implicated Transaction out of it:
        if (CollectionUtils.isNotEmpty(allocations)) {
            Allocation allocation = allocations.get(0);

            if ((allocation.getFirstTransaction() != null) && (allocation.getFirstTransaction().getId().compareTo(linkedTransactionId) != 0)) {
                return allocation.getFirstTransaction();
            } else if ((allocation.getSecondTransaction() != null) && (allocation.getSecondTransaction().getId().compareTo(linkedTransactionId) != 0)) {
                return allocation.getSecondTransaction();
            }
        }

        return null;
    }

    /**
     * Processes an Implicated Transaction, i.e. Transaction associated with a linked manifest.
     *
     * @param manifest              Original manifest.
     * @param linkedManifest        Linked manifest.
     * @param implicatedTransaction Linked manifest's Transaction.
     * @param session               FM Session
     * @param pbtDetails            A list of Payment billing transfer details. An OUT parameter.
     * @param tptDetails            A list of Third-party transfer details. An OUT Parameter.
     */
    private void processImplicatedTransaction(FeeManagementManifest manifest, FeeManagementManifest linkedManifest, Transaction implicatedTransaction,
                                              FeeManagementSession session, List<PaymentBillingTransferDetail> pbtDetails, List<ThirdPartyTransferDetail> tptDetails) {
        // Check if allocations remain. If yes, process the Implicated Transaction based on its type:
        boolean allocationsRemain = (implicatedTransaction != null) &&
                (implicatedTransaction.getAllocatedAmount() != null) && (implicatedTransaction.getLockedAllocatedAmount() != null)
                && (implicatedTransaction.getAllocatedAmount().compareTo(implicatedTransaction.getLockedAllocatedAmount().negate()) != 0);

        if (allocationsRemain) {
            // Check the status of the Implicated Transaction:
            switch (implicatedTransaction.getStatus()) {
                case TRANSFERRING:
                    // Process a TRANSFERRING Implicated Transaction and get back to this method:
                    processTransferFound(linkedManifest, session, implicatedTransaction, pbtDetails, tptDetails);

                    // Process the implicated transaction again:
                    processImplicatedTransaction(manifest, linkedManifest, implicatedTransaction, session, pbtDetails, tptDetails);
                    break;

                case WRITING_OFF:
                case RECIPROCAL_OFFSET:
                case REFUNDING:
                case BOUNCING:
                    // Record a log message, mark the session for manual review and skip:
                    logger.warn("Inappropriate status of Implicated Transaction");
                    session.setReviewRequired(true);
                    break;

                case REVERSING:
                case DISCOUNTING:
                case CANCELLING:
                case ACTIVE:
                    validateUnallocatedBalance(manifest, linkedManifest, implicatedTransaction, session);
                    break;

                default:
                    break;
            }
        } else {
            // If no allocations remain, do transaction reversal:
            performTransactionReversal(manifest, linkedManifest);
        }
    }

    /**
     * Process "Transfer Found" logic found in the "Process Diagrams" document.
     *
     * @param linkedManifest        The linked manifest.
     * @param session               The FM session.
     * @param implicatedTransaction Transaction associated with the linked manifest.
     * @param pbtDetails            A list of Payment billing transfer details. An OUT parameter.
     * @param tptDetails            A list of Third-party transfer details. An OUT Parameter.
     */
    private void processTransferFound(FeeManagementManifest linkedManifest, FeeManagementSession session, Transaction implicatedTransaction, List<PaymentBillingTransferDetail> pbtDetails, List<ThirdPartyTransferDetail> tptDetails) {

        // Look for a TransactionTransfer with a reversal status of Not Reversed or Partially Reversed:
        Transaction linkedTransaction = linkedManifest.getTransaction();
        TransactionTransfer transactionTransfer = findUnreversedTransactionTransfer(linkedTransaction);
        String memoText;

        if (transactionTransfer != null) {
            // Check the TransactionTransfer Group ID:
            String transactionTransferGroupId = transactionTransfer.getGroupId();

            if (StringUtils.isNotBlank(transactionTransferGroupId)) {
                // Find PaymentBillingTransferDetail object:
                PaymentBillingTransferDetail pbtDetail = findPaymentBillingTransferDetail(transactionTransferGroupId);

                // If found, store the details until later and reverse the PaymentBillingPlan:
                if (pbtDetail != null) {
                    // Add the object found to the list:
                    pbtDetails.add(pbtDetail);

                    // Reverse the PaymentBillingPlan:
                    memoText = String.format("Payment Billing Transfer %d was reversed under fee management for session %d", pbtDetail.getId(), session.getId());
                    paymentBillingService.reversePaymentBillingTransfer(pbtDetail.getId(), memoText, true);
                } else {
                    // Search for ThirdPartyTransferDetail with the same Transfer GroupId:
                    ThirdPartyTransferDetail tptDetail = findThirdPartyTransferDetail(transactionTransferGroupId);

                    // If found, store the details until later and reverse the ThirdPartyPlan:
                    if (tptDetail != null) {
                        // Add the object found to the list:
                        tptDetails.add(tptDetail);

                        // Reverse the ThirdPartyPlan:
                        memoText = String.format("Payment Billing Transfer %d was reversed under fee management for session %d", tptDetail.getId(), session.getId());
                        thirdPartyService.reverseThirdPartyTransfer(tptDetail.getId(), memoText);
                    } else {
                        // Reverse TransferGroup:
                        memoText = String.format("Transaction Transfer Group %s was reversed under fee management for session %d", transactionTransferGroupId, session.getId());
                        transactionTransferService.reverseTransferGroup(transactionTransferGroupId, memoText, true);
                    }
                }
            } else {
                // Reverse the one-off manual transfer:
                memoText = String.format("Transaction Transfer %d was reversed under fee management for session %d", transactionTransfer.getId(), session.getId());
                transactionTransferService.reverseTransactionTransfer(transactionTransfer.getId(), memoText);
            }
        } else {
            // Reverse the Implicated Transaction:
            memoText = String.format("No Transaction Transfer object found for Transaction %d", implicatedTransaction.getId());
            reverseTransaction(implicatedTransaction, implicatedTransaction.getAmount(), session);
        }

        // Mark session for manual review:
        if (StringUtils.isNotBlank(memoText)) {
            logger.warn(memoText);
            session.setReviewRequired(true);
            persistEntity(session);
        }
    }

    /**
     * Finds a <code>PaymentBillingTransferDetail</code> object of type Charged and with the given Transfer Group ID.
     *
     * @param transferGroupId Transfer group ID to match <code>PaymentBillingTransferDetail</code> on.
     * @return Matching <code>PaymentBillingTransferDetail</code> or <code>null</code> if no such object can be found.
     */
    private PaymentBillingTransferDetail findPaymentBillingTransferDetail(String transferGroupId) {

        PermissionUtils.checkPermission(Permission.READ_THIRD_PARTY_TRANSFER_DETAIL);

        // Create and run a query to find PaymentBillingTransferDetail object:
        Query query = em.createQuery("select d from PaymentBillingTransferDetail d " +
                " left outer join fetch d.directChargeAccount dca " +
                " left outer join fetch d.plan p " +
                " left outer join fetch d.flatFeeCharge fc " +
                " left outer join fetch d.variableFeeCharge vc " +
                " left outer join fetch p.transferType t " +
                " left outer join fetch t.generalLedgerType g " +
                " where d.transferGroupId = :transferGroupId and d.chargeStatusCode = :chargeStatusCode ");

        query.setParameter("transferGroupId", transferGroupId);
        query.setParameter("chargeStatusCode", PaymentBillingChargeStatus.ACTIVE_CODE);

        List<PaymentBillingTransferDetail> details = query.getResultList();

        return CollectionUtils.isNotEmpty(details) ? details.get(0) : null;
    }

    /**
     * Finds a <code>ThirdPartyTransferDetail</code> object of type Charged and with the given Transfer Group ID.
     *
     * @param transferGroupId Transfer group ID to match <code>ThirdPartyTransferDetail</code> on.
     * @return Matching <code>ThirdPartyTransferDetail</code> or <code>null</code> if no such object can be found.
     */
    private ThirdPartyTransferDetail findThirdPartyTransferDetail(String transferGroupId) {

        PermissionUtils.checkPermission(Permission.READ_THIRD_PARTY_TRANSFER_DETAIL);

        // Create and run a query to find ThirdPartyTransferDetailObject:
        Query query = em.createQuery("select d from ThirdPartyTransferDetail d " +
                " left outer join fetch d.directChargeAccount dca " +
                " left outer join fetch d.plan p " +
                " left outer join fetch p.thirdPartyAccount tpa " +
                " left outer join fetch p.transferType t " +
                " left outer join fetch t.generalLedgerType g " +
                " where d.transferGroupId = :transferGroupId ");

        query.setParameter("transferGroupId", transferGroupId);

        List<ThirdPartyTransferDetail> details = query.getResultList();

        return CollectionUtils.isNotEmpty(details) ? details.get(0) : null;
    }

    /**
     * Finds a Partially Reversed or Not Reversed Transaction Transfer object that contains the
     * given Implicated Transaction.
     *
     * @param implicatedTransaction A Transaction for which to find an unreversed Transaction Transfer.
     * @return An unreversed TransactionTransfer for the given Transaction or <code>null</code> if no
     *         such Transaction Transfer object can be found.
     */
    private TransactionTransfer findUnreversedTransactionTransfer(Transaction implicatedTransaction) {

        PermissionUtils.checkPermission(Permission.READ_TRANSACTION_TRANSFER);

        // Create a query to select a TransactionTransfer using the search criteria:
        String jpql = " select t from TransactionTransfer t " +
                " left outer join fetch t.sourceTransaction " +
                " left outer join fetch t.destTransaction " +
                " left outer join fetch t.offsetTransaction " +
                " left outer join fetch t.sourceReciprocalTransaction " +
                " left outer join fetch t.destReciprocalTransaction " +
                " left outer join fetch t.transferType " +
                " where t.sourceTransaction.id = :transactionId and t.reversalStatusCode in (:reversalStatusCodes)";
        Query query = em.createQuery(jpql);

        query.setParameter("transactionId", implicatedTransaction.getId());
        query.setParameter("reversalStatusCodes", Arrays.asList(ReversalStatus.NOT_REVERSED_CODE, ReversalStatus.PARTIALLY_REVERSED_CODE));

        List<TransactionTransfer> transfers = query.getResultList();

        return CollectionUtils.isNotEmpty(transfers) ? transfers.get(0) : null;
    }

    /**
     * Performs unallocated balance validation for the Implicated Transaction.
     *
     * @param originalManifest      The original manifest being processed.
     * @param linkedManifest        The linked manifest.
     * @param implicatedTransaction Transaction associated with the linked manifest.
     * @param session               FM Session.
     */
    private void validateUnallocatedBalance(FeeManagementManifest originalManifest, FeeManagementManifest linkedManifest, Transaction implicatedTransaction, FeeManagementSession session) {
        // Is there enough unallocated balance of the transaction to perform correction/cancel/discount:
        if (hasUnallocatedBalanceForCorrection(implicatedTransaction)) {
            // Write a warning, mark the session for manual review and reverse the transaction:
            logger.warn("Performing transaction reversal. FM session manual review required.");
            performTransactionReversal(originalManifest, linkedManifest);
        } else {
            // Reverse all allocations on the Implicated Transaction in the amount of the original manifest's amount:
            BigDecimal reversalAmount = originalManifest.getAmount();

            transactionService.reverseAllocations(implicatedTransaction.getId(), reversalAmount);

            // Refresh the implicated transaction:
            implicatedTransaction = refreshTransaction(linkedManifest);

            // Is there enough unallocated balance of the transaction to perform correction/cancel/discount:
            if (hasUnallocatedBalanceForCorrection(implicatedTransaction)) {
                logger.warn("Performing transaction reversal. FM session manual review required.");
                performTransactionReversal(originalManifest, linkedManifest);
            } else {
                // Write a severe warning, mark the session for manual review:
                logger.warn("SEVERE: Not enough unallocated balance to perform correction/cancel/discount after clearing manual allocation. FM session manual review required.");
            }
        }

        // Mark the session for manual review and persist:
        session.setReviewRequired(true);
        persistEntity(session);
    }

    /**
     * Checks if the given Transaction has enough unallocated balance to perform correction/cancel/discount.
     *
     * @param transaction A Transaction to check its unallocated balance to perform correction/cancel/discount.
     * @return <code>true</code> if there is enough balance, <code>false</code> otherwise.
     */
    private boolean hasUnallocatedBalanceForCorrection(Transaction transaction) {
        return (transaction.getAmount() != null) && (transaction.getUnallocatedAmount() != null)
                && (transaction.getUnallocatedAmount().compareTo(transaction.getAmount()) > 0);
    }

    /**
     * Performs Linked Transaction reversal.
     *
     * @param originalManifest The original manifest being processed.
     * @param linkedManifest   The linked manifest.
     */
    private void performTransactionReversal(FeeManagementManifest originalManifest, FeeManagementManifest linkedManifest) {

        // Reverse the transaction:
        Transaction linkedTransaction = linkedManifest.getTransaction();
        Transaction reversalTransaction = reverseTransaction(linkedTransaction, originalManifest.getAmount(), originalManifest.getSession());

        if (reversalTransaction != null) {
            originalManifest.setTransaction(reversalTransaction);
            originalManifest.setSessionCurrent(true);

            // Persist the original manifest:
            persistEntity(originalManifest);
        } else {
            logger.error(String.format("Cannot perform reversal on Transaction with ID %d under FM session ID %d.",
                    linkedTransaction.getId(), originalManifest.getSession().getId()));
        }
    }

    /**
     * Reverses the given transaction and returns the reversal Transaction.
     *
     * @param transaction    Transaction to reverse.
     * @param reversalAmount Reversal amount.
     * @param session        FM Session
     * @return The reversal Transaction.
     */
    private Transaction reverseTransaction(Transaction transaction, BigDecimal reversalAmount, FeeManagementSession session) {

        TransactionStatus status = transaction.getStatus();
        Transaction reversalTransaction = null;

        // Check if the Transaction status allows for reversal:
        if ((status == TransactionStatus.ACTIVE) ||
                ((reversalAmount != null) && (transaction.getAmount() != null) && (reversalAmount.compareTo(transaction.getAmount()) == 0))) {

            String statementPrefix = ""; // According to Paul there are no statement prefix
            String memoText = String.format("Transaction %d was reversed in the amount of %f by fee management under session %d",
                    transaction.getId(), reversalAmount, session.getId());

            reversalTransaction = transactionService.reverseTransaction(transaction.getId(), memoText, reversalAmount, statementPrefix);
        }

        return reversalTransaction;
    }

    /**
     * Performs transfer reinstatement.
     *
     * @param pbtDetails A list of Payment billing transfer details.
     * @param tptDetails A list of Third-party transfer details. An OUT Parameter.
     * @param account    An Account associated with the the FM Session.
     */
    private void performTransferReinstatement(List<PaymentBillingTransferDetail> pbtDetails, List<ThirdPartyTransferDetail> tptDetails, Account account) {

        // For each PaymentBilling that was removed, generate payment billing schedule using the stored details:
        for (PaymentBillingTransferDetail pbtDetail : pbtDetails) {

            // Validate the plan still exists:
            PaymentBillingPlan plan = getEntity(pbtDetail.getPlan().getId(), PaymentBillingPlan.class);

            if (plan != null) {
                paymentBillingService.generatePaymentBillingTransfer(plan.getId(), account.getId(), pbtDetail.getMaxAmount(), pbtDetail.getInitiationDate());
            }
        }

        // For each Third-Party plan that was removed, generate third-party billing transfer for the Account associated with the FM session:
        if (account != null) {
            String accountId = account.getId();

            for (ThirdPartyTransferDetail tptDetail : tptDetails) {

                // Generate a Third-Party transfer detail:
                ThirdPartyPlan plan = getEntity(tptDetail.getPlan().getId(), ThirdPartyPlan.class);

                if (plan != null) {
                    thirdPartyService.generateThirdPartyTransfer(plan.getId(), accountId, tptDetail.getInitiationDate());
                }
            }
        }
    }

    /**
     * Refreshes the transaction from the store and sets it on the manifest.
     *
     * @param manifest An FM manifest which transactin to refresh.
     * @return A fresh copy of the transaction.
     */
    private Transaction refreshTransaction(FeeManagementManifest manifest) {

        Transaction transaction = manifest.getTransaction();

        if (transaction != null) {
            // Get a fresh copy:
            transaction = transactionService.getTransaction(transaction.getId());
            manifest.setTransaction(transaction);
        }

        return transaction;
    }


    /***************************************************************************
     *
     *
     * Private helper methods for "reconcileSession".
     *
     *
     ***************************************************************************/

    /**
     * Checks all conditions necessary for FM session reconciliation.
     * Throws an exception if any of the condition fails validation.
     *
     * @param fmSession              An FM Session.
     * @param feeManagementSessionId ID of the FM Session.
     */
    private void validateSessionForReconciliation(FeeManagementSession fmSession, Long feeManagementSessionId) {

        if (fmSession == null) {
            logger.error("Cannot find an FM session with the ID " + feeManagementSessionId);
            throw new IllegalArgumentException("Cannot find an FM session with the ID " + feeManagementSessionId);
        }

        // Check the session passed is CURRENT or SIMULATED
        FeeManagementSessionStatus sessionStatus = fmSession.getStatus();

        if (sessionStatus == null || ((sessionStatus != FeeManagementSessionStatus.CURRENT) && (sessionStatus != FeeManagementSessionStatus.SIMULATED))) {
            String errorMsg = String.format("Invalid FeeManagement Session status [%s]. Session status must be CURRENT or SIMULATED to call \"reconcileSession\".", sessionStatus);
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        // Get the associated Account:
        Account account = fmSession.getAccount();

        if (account == null) {
            String errorMsg = String.format("Fee Management Session with id [%s] does not have an associated Account.", feeManagementSessionId);
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        } else {
            // Check if the Account associated with the FM Session is blocked:
            boolean accountBlocked = isAccountBlocked(account);

            if (accountBlocked) {
                String errorMsg = String.format("The account %s associated with the FM session with id [%s] is blocked.", account.getId(), feeManagementSessionId);
                logger.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }
        }
    }

    /**
     * A recursive method that checks all previous FM Sessions in succession in
     * order to find the last CHARGED session.
     *
     * @param fmSession A FeeManagement Session to find its last CHARGED prior FM Session.
     * @return The last prior CHARGED FM session or <code>null</code> if there is no last prior
     *         CHARGED FM Session.
     */
    private FeeManagementSession getLastChargedSession(FeeManagementSession fmSession) {
        // Check if the current session is null or its status is CHARGED, end the recursion here:
        if ((fmSession == null) || (fmSession.getStatus() == FeeManagementSessionStatus.CHARGED)) {
            return fmSession;
        } else {
            // Get the prior session and continue the recursion:
            return getLastChargedSession(fmSession.getPrevSession());
        }
    }

    /**
     * Checks if the Account associated with the given FM Session is blocked.
     *
     * @param account Account associated with a Fee Management Session.
     * @return boolean Whether the Account associated with the FM Session is blocked.
     */
    private boolean isAccountBlocked(Account account) {
        // TODO: define permissions and account attributes required by FM Session
        Permission[] permissions = {};
        Map<String, Object> attributes = new HashMap<String, Object>();
        try {
            accountBlockingService.checkBlock(account.getId(), attributes, permissions);
            return false;
        } catch (AccountBlockedException e) {
            return true;
        }
    }

    /**
     * Performs adjustment of lines between the last charged FM Session and the current session.
     *
     * @param currentFmSession Current FM Session.
     * @param priorFmSession   Last FM Session in the CHARGED status. Guaranteed not to be null.
     * @param currentManifests A list of current FM session's manifests.
     */
    private void processPriorSessionAdjustment(FeeManagementSession currentFmSession, FeeManagementSession priorFmSession, List<FeeManagementManifest> currentManifests) {
        // Get the prior and current FM sessions' manifests:
        List<FeeManagementManifest> priorManifests = getManifests(priorFmSession.getId(),
                FeeManagementManifestType.CHARGE, FeeManagementManifestType.CANCELLATION, FeeManagementManifestType.DISCOUNT);

        // Get pairs of matching FM manifests in the prior and the current FM sessions:
        List<FeeManagementManifest> unmatchedPriorManifests = new ArrayList<FeeManagementManifest>();
        List<Pair<FeeManagementManifest, FeeManagementManifest>> matchingManifests = getMatchingManifests(priorManifests, currentManifests, unmatchedPriorManifests);

        // Process matching manifest pairs:
        for (Pair<FeeManagementManifest, FeeManagementManifest> matchingPair : matchingManifests) {
            processMatchingManifests(matchingPair, currentFmSession);
        }

        // Process unmatched prior manifests:
        processUnmatchedPriorManifest(unmatchedPriorManifests, currentFmSession);
    }

    /**
     * Returns a <code>List</code> of matching <code>FeeManagementManifest</code>s that match in both prior and current manifest lists.
     * Matching manifests is done by either:
     * 1. registrationId + offeringId + rate, or
     * 2. internalChargeId + rate.
     *
     * @param priorManifests          Prior FM session's manifests.
     * @param currentManifests        Current FM session's manifests.
     * @param unmatchedPriorManifests A List to be populated with unmatched prior FM session's manifests. This is an IN parameter and must not be null.
     * @return A List of matching manifest pairs. The "a" property of the <code>Pair</code> is the prior manifest, and "b" is the current one.
     */
    private List<Pair<FeeManagementManifest, FeeManagementManifest>> getMatchingManifests(List<FeeManagementManifest> priorManifests,
                                                                                          List<FeeManagementManifest> currentManifests, List<FeeManagementManifest> unmatchedPriorManifests) {
        // Create the resulting list:
        List<Pair<FeeManagementManifest, FeeManagementManifest>> matchingManifests = new ArrayList<Pair<FeeManagementManifest, FeeManagementManifest>>();

        // Create a copy of the prior manifest List since we'll be removing matched prior manifests:
        List<FeeManagementManifest> priorManifestsCopy = new ArrayList<FeeManagementManifest>(priorManifests);

        // Iterate through the prior manifests trying find a match in the current manifest list:
        for (Iterator<FeeManagementManifest> itPrior = priorManifestsCopy.iterator(); itPrior.hasNext(); ) {
            FeeManagementManifest priorManifest = itPrior.next();

            // Iterate through the list of current manifests trying to find a match:
            for (FeeManagementManifest currentManifest : currentManifests) {
                if (manifestsMatch(priorManifest, currentManifest, false)) {
                    // Add a new matching pair, remove the matching prior manifest from the underlying list and break out of the loop:
                    matchingManifests.add(new Pair<FeeManagementManifest, FeeManagementManifest>(priorManifest, currentManifest));
                    itPrior.remove();

                    break;
                }
            }
        }

        // Add non-matching prior FM session's manifests to the list of unmatched manifests:
        unmatchedPriorManifests.addAll(priorManifestsCopy);

        return matchingManifests;
    }

    /**
     * Compares two FM manifests, prior and current, and check if they match by either:
     * 1. (registrationId + rate) or (offeringId + rate), or
     * 2. internalChargeId + rate.
     *
     * @param prior             A prior FM session's manifest.
     * @param current           A current FM session's manifest.
     * @param matchByOfferingId Whether to match Manifests by Offering ID vs. Registration ID.
     * @return <code>true</code> if the manifests are a match, <code>false</code> otherwise.
     */
    private boolean manifestsMatch(FeeManagementManifest prior, FeeManagementManifest current, boolean matchByOfferingId) {
        if ((prior != null) && (current != null)) {
            // Check if rates match first:
            boolean ratesMatch = (prior.getRate() != null) && (current.getRate() != null) && prior.getRate().equals(current.getRate());

            // Check if Internal Charge IDs or Registration IDs match:
            return ratesMatch &&
                    (equalsExceptNull(prior.getInternalChargeId(), current.getInternalChargeId())
                            || (matchByOfferingId
                            ? equalsExceptNull(prior.getOfferingId(), current.getOfferingId())
                            : equalsExceptNull(prior.getRegistrationId(), current.getRegistrationId())));
        }

        return false;
    }

    /**
     * Processes matching manifests from the prior and current FM sessions by doing one of the following:<br>
     * 1. Either copies the transactionId from the prior manifest to the current one if already charged, or<br>
     * 2. Performs corrections.
     *
     * @param manifests        A <code>Pair</code> of matching manifests, the first one in the pair is the prior session's manifest.
     * @param currentFmSession The current FM session. Used for non-charged matching manifests adjustment.
     */
    private void processMatchingManifests(Pair<FeeManagementManifest, FeeManagementManifest> manifests, FeeManagementSession currentFmSession) {
        // First, check if the payment information on the prior and current manifests match, i.e. current has already been charged:
        FeeManagementManifest prior = manifests.getA();
        FeeManagementManifest current = manifests.getB();
        boolean currentAlreadyCharged = manifestPaymentInformationMatch(prior, current);

        if (currentAlreadyCharged) {
            // Copy the Transaction from the prior to the current:
            current.setTransaction(prior.getTransaction());
            persistEntity(current);
        } else {
            // Create a correction on the current session from the prior manifest:
            createPriorManifestCorrection(prior, currentFmSession);
        }
    }

    /**
     * Checks if the prior and current FM manifests' payment information matches. That includes:<p>
     * 1. effectiveDate<br>
     * 2. recognitionDate<br>
     * 3. transactionTypeId<br>
     * 4. amount
     *
     * @param prior   A prior FM session manifest.
     * @param current A current FM session manifest.
     * @return <code>true</code> if the payment information on both manifests matches.
     */
    private boolean manifestPaymentInformationMatch(FeeManagementManifest prior, FeeManagementManifest current) {
        return (prior.getEffectiveDate() != null) && (current.getEffectiveDate() != null) && prior.getEffectiveDate().equals(current.getEffectiveDate())
                && (prior.getRecognitionDate() != null) && (current.getRecognitionDate() != null) && prior.getRecognitionDate().equals(current.getRecognitionDate())
                && StringUtils.equals(prior.getTransactionTypeId(), current.getTransactionTypeId())
                && safeAmountsEqual(prior, current);
    }

    /**
     * Processes all prior FM session's unmatched manifests (those that do not have matching manifests on the current FM session).
     *
     * @param unmatchedPriorManifests Unmatched prior FM manifests.
     * @param currentFmSession        Current FM session.
     */
    private void processUnmatchedPriorManifest(List<FeeManagementManifest> unmatchedPriorManifests, FeeManagementSession currentFmSession) {
        // Find all full-reversal manifest pairs:
        List<Pair<FeeManagementManifest, FeeManagementManifest>> fullReversalManifests = findReversalManifests(unmatchedPriorManifests, true);

        for (Pair<FeeManagementManifest, FeeManagementManifest> pair : fullReversalManifests) {
            // Remove both manifests from the list of unmatched manifests:
            unmatchedPriorManifests.remove(pair.getA());
            unmatchedPriorManifests.remove(pair.getB());
        }

        // Iterate through the remaining non-full-reversal prior manifests and create a correction on the current FM session out of each one of them:
        for (FeeManagementManifest priorManifest : unmatchedPriorManifests) {
            // Create a correction on the current session from the prior manifest:
            createPriorManifestCorrection(priorManifest, currentFmSession);
        }
    }

    /**
     * Finds all full-reversal FM Manifest pairs in the given list and returns them in a List of Pairs.
     *
     * @param manifests    A list of <code>FeeManagementManifest</code> objects to find full-reversal manifests.
     * @param fullReversal Whether to check for full reversals.
     * @return A List of full-reversal Manifest Pairs
     */
    private List<Pair<FeeManagementManifest, FeeManagementManifest>> findReversalManifests(List<FeeManagementManifest> manifests, boolean fullReversal) {

        List<Pair<FeeManagementManifest, FeeManagementManifest>> reversalManifests = new ArrayList<Pair<FeeManagementManifest, FeeManagementManifest>>();

        // Iterate through a copy of the list of manifests. Begin with the first manifest and try
        // to find its full-reversal counterpart. If one is found, add a Pair, remove them both from
        // the list and start from the same "startIndex":
        List<FeeManagementManifest> copyList = new ArrayList<FeeManagementManifest>(manifests);
        int startIndex = 0;

        while (startIndex < copyList.size() - 1) {
            // Get the first Manifest to match against:
            FeeManagementManifest firstManifest = copyList.get(startIndex);
            boolean matchFound = false;

            for (int i = startIndex + 1, sz = copyList.size(); (i < sz) && !matchFound; i++) {
                // Check if the next manifest is a full-reversal:
                matchFound = isReversal(firstManifest, copyList.get(i), fullReversal);

                if (matchFound) {
                    // Add a Pair, remove both from the list, and start again from the same index:
                    reversalManifests.add(new Pair<FeeManagementManifest, FeeManagementManifest>(firstManifest, copyList.get(i)));
                    copyList.remove(i);
                    copyList.remove(startIndex);
                }
            }

            // If a match not found, increment the start index and keep searching:
            if (!matchFound) {
                startIndex++;
            }
        }

        return reversalManifests;
    }

    /**
     * Checks if the two <code>FeeManagementManifest</code>s are a subject to a reversal, which means that:<p>
     * <p/>
     * 1. The types of the two are CHARGE and CANCEL<br>
     * 2. Manifests match (see {@link #manifestsMatch(FeeManagementManifest, FeeManagementManifest, boolean)}<br>
     * 3. Same transactionTypeId and amount.<br>
     * 4. Their manifests point at each other (optional).<br>
     *
     * @param fmm1         The first FM manifest to check for full reversal.
     * @param fmm2         The second FM manifest to check for full reversal.
     * @param fullReversal Whether to check for full reversals.
     * @return <code>true</code> if the two FM manifests represent a reversal.
     */
    private boolean isReversal(FeeManagementManifest fmm1, FeeManagementManifest fmm2, boolean fullReversal) {

        // First check if the FM manifests point at each other:
        boolean pointAtEachOther = fullReversal
                ? (fmm1.getLinkedManifest() != null) && (fmm2.getLinkedManifest() != null)
                && fmm1.getLinkedManifest().getId().equals(fmm2.getId()) &&
                fmm2.getLinkedManifest().getId().equals(fmm1.getId())
                : true;

        // Check if the types are CHARGE and CANCEL
        if (pointAtEachOther) {
            boolean statusesForReversal = ((fmm1.getType() == FeeManagementManifestType.CHARGE) &&
                    (fmm2.getType() == FeeManagementManifestType.CANCELLATION))
                    || ((fmm2.getType() == FeeManagementManifestType.CHARGE) &&
                    (fmm1.getType() == FeeManagementManifestType.CANCELLATION));

            // Now check if the manifests match and have the same transactionTypeId and amount:
            if (statusesForReversal && manifestsMatch(fmm1, fmm2, true)) {
                return fullReversal
                        ? StringUtils.equals(fmm1.getTransactionTypeId(),
                        fmm2.getTransactionTypeId()) && safeAmountsEqual(fmm1, fmm2)
                        : true;
            }
        }

        return false;
    }

    /**
     * Creates a correction from a prior FM session's manifest into the current FM session.
     *
     * @param priorManifest    A prior FM manifest to create a correction out of.
     * @param currentFmSession Current FM session.
     */
    private void createPriorManifestCorrection(FeeManagementManifest priorManifest, FeeManagementSession currentFmSession) {

        // Clone the prior manifest and add to the current session.
        FeeManagementManifest priorCopy = copyManifest(priorManifest);

        priorCopy.setSession(currentFmSession);
        priorCopy.setType(FeeManagementManifestType.ORIGINAL);
        persistEntity(priorCopy);

        // Also, create a Correction Manifest:
        FeeManagementManifest correctionManifest = copyManifest(priorManifest);

        correctionManifest.setSession(currentFmSession);
        correctionManifest.setType(FeeManagementManifestType.CORRECTION);
        correctionManifest.setTransaction(null);
        correctionManifest.setLinkedManifest(priorCopy);
        persistEntity(correctionManifest);

        // Link the Prior Manifest with the Correction Manifest and persist:
        priorCopy.setLinkedManifest(correctionManifest);
        persistEntity(priorCopy);
    }

    /**
     * Removes inverse manifests from the list of manifests.
     *
     * @param manifests A list of manifests to remove inverse ones from.
     */
    private void removeInverseManifests(List<FeeManagementManifest> manifests) {

        // Iterate through the list of manifests. Begin with the first manifest and try
        // to find its full-reversal counterpart. If one is found, add a Pair, remove them both from
        // the list and start from the same "startIndex":
        int startIndex = 0;

        while (startIndex < manifests.size() - 1) {
            // Get the first Manifest to match against:
            FeeManagementManifest firstManifest = manifests.get(startIndex);
            boolean matchFound = false;

            // Check for the condition to continue:
            if (eligibleForPreclearing(firstManifest)) {
                for (int i = startIndex + 1, sz = manifests.size(); (i < sz) && !matchFound; i++) {
                    // Check if the next manifest is an inverse one:
                    FeeManagementManifest anotherManifest = manifests.get(i);

                    matchFound = eligibleForPreclearing(anotherManifest)
                            && manifestsInverse(firstManifest, anotherManifest);

                    if (matchFound) {
                        // Delete both entities, remove both from the list, and start again from the same index:
                        firstManifest.setSession(null);
                        anotherManifest.setSession(null);
                        em.remove(firstManifest);
                        em.remove(anotherManifest);
                        manifests.remove(i);
                        manifests.remove(startIndex);
                    }
                }
            }

            // If a match not found, increment the start index and keep searching:
            if (!matchFound) {
                startIndex++;
            }
        }
    }

    /**
     * Check if the given manifest is eligible for Manifest pre-clearing.
     *
     * @param manifest A FM Manifest for eligibility for pre-clearing.
     * @return <code>true</code> if the given FM Manifest is eligible for manifest pre-clearing.
     */
    private boolean eligibleForPreclearing(FeeManagementManifest manifest) {
        return (manifest.getTransaction() == null) || (manifest.getLinkedManifest() == null);
    }

    /**
     * Checks if two FM manifests are inverse. According to the documentation, FM Manifests are inverse if:
     * If TYPES are inverse {CHARGE vs. CANCEL} with amounts equal
     * OR
     * Same type {Both CHARGE or CANCEL} with inverse amounts && transactionTypeId is the same
     *
     * @param m1 An FM Manifest to compare.
     * @param m2 Another FM Manifest to compare.
     * @return true if the two manifests are inverse, false otherwise.
     */
    private boolean manifestsInverse(FeeManagementManifest m1, FeeManagementManifest m2) {
        if ((m1 != null) && (m2 != null) && StringUtils.equals(m1.getTransactionTypeId(), m2.getTransactionTypeId())) {
            // Check for inverse statuses and equal amounts or same statuses and inverse amounts:
            if (((m1.getType() == FeeManagementManifestType.CHARGE) && (m2.getType() == FeeManagementManifestType.CANCELLATION))
                    || ((m1.getType() == FeeManagementManifestType.CANCELLATION) && (m2.getType() == FeeManagementManifestType.CHARGE))) {
                return safeAmountsEqual(m1, m2);
            } else if (((m1.getType() == FeeManagementManifestType.CHARGE) && (m2.getType() == FeeManagementManifestType.CHARGE))
                    || ((m1.getType() == FeeManagementManifestType.CANCELLATION) && (m2.getType() == FeeManagementManifestType.CANCELLATION))) {
                return safeAmountsInverse(m1, m2) && StringUtils.equals(m1.getTransactionTypeId(), m2.getTransactionTypeId());
            }
        }

        return false;
    }

    /**
     * Safely compares "amount" properties of two <code>FeeManagementManifest</code>s.
     * Calls the "compareTo" method of BigDecimal.
     * Guaranteed not to cause a NullPointerException.
     *
     * @param m1 A <code>FeeManagementManifest</code>.
     * @param m2 A <code>FeeManagementManifest</code>.
     * @return true if "amount" attributes are equal.
     */
    private static boolean safeAmountsEqual(FeeManagementManifest m1, FeeManagementManifest m2) {
        return (m1.getAmount() != null) && (m2.getAmount() != null) && (m1.getAmount().compareTo(m2.getAmount()) == 0);
    }

    /**
     * Safely compares "amount" properties of two <code>FeeManagementManifest</code>s.
     * Calls the "compareTo" method of BigDecimal.
     * Guaranteed not to cause a NullPointerException.
     *
     * @param m1 A <code>FeeManagementManifest</code>.
     * @param m2 A <code>FeeManagementManifest</code>.
     * @return true if "amount" attributes are inverse.
     */
    private static boolean safeAmountsInverse(FeeManagementManifest m1, FeeManagementManifest m2) {
        return (m1.getAmount() != null) && (m2.getAmount() != null) && (m1.getAmount().compareTo(m2.getAmount().negate()) == 0);
    }

    /**
     * Compares two Strings for equality using <code>StringUtils.equals</code>
     * except this method returns <code>false</code> when both String are null.
     *
     * @param str1 The first String to compare.
     * @param str2 The second String to compare.
     * @return true if Strings are equal and not null together.
     */
    public static boolean equalsExceptNull(String str1, String str2) {
        return (str1 != null) && (str2 != null) && StringUtils.equals(str1, str2);
    }

    /**
     * Validates that full-reversal manifests point at each other. Repoints them at each other if necessary.
     *
     * @param currentManifests The current FM Session's manifests to validate reversals.
     */
    private void validateCurrentReversals(List<FeeManagementManifest> currentManifests) {

        // Get reversal Manifest pairs and point them to each other:
        List<Pair<FeeManagementManifest, FeeManagementManifest>> reversalManifests = findReversalManifests(currentManifests, false);

        for (Pair<FeeManagementManifest, FeeManagementManifest> pair : reversalManifests) {
            // Point the manifests at each other:
            pair.getA().setLinkedManifest(pair.getB());
            pair.getB().setLinkedManifest(pair.getA());

            // Persist the changes:
            persistEntity(pair.getA());
            persistEntity(pair.getB());
        }
    }

    /**
     * Create a shallow copy of a <code>FeeManagementManifest</code> with only Tags and KeyPairs deep-copied.
     * The new <code>FeeManagementManifest</code> is not attached to any FM session and not linked to any FM Manifests.
     *
     * @param origManifest An original FM manifest to copy.
     * @return A copy of the original FM manifest.
     */
    private FeeManagementManifest copyManifest(FeeManagementManifest origManifest) {

        // Create a new FM Manifest as a shallow copy of the original one:
        FeeManagementManifest copyManifest = new FeeManagementManifest();

        copyManifest.setEffectiveDate(origManifest.getEffectiveDate());
        copyManifest.setRecognitionDate(origManifest.getRecognitionDate());
        copyManifest.setAmount((origManifest.getAmount() != null) ? new BigDecimal(origManifest.getAmount().doubleValue()) : null);
        copyManifest.setInternalChargeId(origManifest.getInternalChargeId());
        copyManifest.setOfferingId(origManifest.getOfferingId());
        copyManifest.setRegistrationId(origManifest.getRegistrationId());
        copyManifest.setTransactionTypeId(origManifest.getTransactionTypeId());
        copyManifest.setSessionCurrent(origManifest.isSessionCurrent());
        copyManifest.setSession(null);
        copyManifest.setLinkedManifest(null);
        copyManifest.setTransaction(origManifest.getTransaction());
        copyManifest.setRate(origManifest.getRate());
        copyManifest.setRollup(origManifest.getRollup());
        copyManifest.setId(null);

        // Set the status:
        if (origManifest.getStatus() != null) {
            copyManifest.setStatus(origManifest.getStatus());
        }

        // Create copies of KeyPairs:
        if (origManifest.getKeyPairs() != null) {
            // Create a new Set of KeyPairs if one is not set yet:
            if (copyManifest.getKeyPairs() == null) {
                copyManifest.setKeyPairs(new HashSet<KeyPair>());
            } else {
                // Clear the previous tags:
                copyManifest.getKeyPairs().clear();
            }

            // Create and add a new KeyPair identical to the original:
            for (KeyPair keyPair : origManifest.getKeyPairs()) {
                copyManifest.getKeyPairs().add(new KeyPair(keyPair.getKey(), keyPair.getValue()));
            }
        }

        // Create copies of Tags:
        if (origManifest.getTags() != null) {
            // Create a new Set of Tags if one is not set yet:
            if (copyManifest.getTags() == null) {
                copyManifest.setTags(new HashSet<Tag>());
            } else {
                copyManifest.getTags().clear();
            }

            // Create a new Tag shallow copy:
            for (Tag tag : origManifest.getTags()) {
                Tag copyTag = BeanUtils.getShallowCopy(tag);

                copyTag.setId(null);
                copyManifest.getTags().add(copyTag);
            }
        }

        return copyManifest;
    }
}
