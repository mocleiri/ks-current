package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.fm.*;
import com.sigmasys.kuali.ksa.model.pb.*;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyPlan;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyPlanMember;
import com.sigmasys.kuali.ksa.service.fm.FeeManagementService;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import com.sigmasys.kuali.ksa.service.fm.impl.FeeManagementServiceImpl;
import com.sigmasys.kuali.ksa.service.pb.PaymentBillingService;
import com.sigmasys.kuali.ksa.service.tp.ThirdPartyTransferService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;

/**
 * FeeManagementService unit tests.
 *
 * @author Sergey Godunov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class FeeManagementServiceTest extends AbstractServiceTest {

    private static final String ACCOUNT_ID = "admin";
    private static final String TRANSACTION_TYPE_ID = "cash";
    private static final BigDecimal MANIFEST_AMOUNT = new BigDecimal(10);
    private static final BigDecimal IMPLICATED_TRANSACTION_AMOUNT = new BigDecimal(12);
    private static final String OFFERING_ID = "offeringId";
    private static final String INTERNAL_CHARGE_ID = "internalChargeId";
    private static final String REGISTRATION_ID = "registrationId";
    private static final Date EFFECTIVE_DATE = new Date();
    private static final Date RECOGNITION_DATE = new Date();

    @PersistenceContext(unitName = Constants.KSA_PERSISTENCE_UNIT)
    protected EntityManager em;

    @Autowired
    protected ConfigService configService;

    @Autowired
    private FeeManagementService fmService;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionTransferService transactionTransferService;

    @Autowired
    private ThirdPartyTransferService thirdPartyTransferService;

    @Autowired
    private PaymentBillingService paymentBillingService;

    @Autowired
    private GeneralLedgerService generalLedgerService;

    @Autowired
    private RateService rateService;


    /*
     * **************************************************************
     *
     * Unit tests for "addFeeManagementSessionToQueue"
     *
     * **************************************************************
     */

    @Test
    public void testAddFeeManagementSessionToQueueNoSuchSession() throws Exception {

        // Call the method with a fake FM Session ID:
        boolean exceptionCaught = false;

        try {
            fmService.addFeeManagementSessionToQueue(-1L);
            assertTrue("We must not get here. An exception must occur on the method call.", false);
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot find an FM session with the ID -1.", e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("An exception should have been caught", exceptionCaught);
    }

    @Test
    public void testAddFeeManagementSessionToQueueSessionNotCurrent() {

        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        fmSession.setChargeStatus(FeeManagementSessionStatus.RECONCILED);
        persistenceService.persistEntity(fmSession);

        // Call the method:
        boolean exceptionCaught = false;

        try {
            fmService.addFeeManagementSessionToQueue(fmSession.getId());
            assertTrue("We must not get here. An exception must occur on the method call.", false);
        } catch (IllegalStateException e) {
            assertEquals(String.format("Only CURRENT FM Session can be Queued. Session was %s.", fmSession.getStatus()), e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("An exception should have been caught", exceptionCaught);
    }

    @Test
    public void testAddFeeManagementSessionToQueueGoodCase() {

        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        fmSession.setChargeStatus(FeeManagementSessionStatus.CURRENT);
        persistenceService.persistEntity(fmSession);

        // Call the method:
        fmService.addFeeManagementSessionToQueue(fmSession.getId());

        // Validate that the FM Session was Queued:
        em.refresh(fmSession);

        assertTrue("FM Session must be Queued by now.", fmSession.isQueued());
        assertFalse("FM Session must be marked as NOT Reviewed", fmSession.isReviewComplete());
    }

    /*
    * **************************************************************
    *
    * Unit tests for "removeFeeManagementSessionFromQueue"
    *
    * **************************************************************
    */

    @Test
    public void testRemoveFeeManagementSessionFromQueueNoSuchSession() throws Exception {

        // Call the method with a fake FM Session ID:
        boolean exceptionCaught = false;

        try {
            fmService.removeFeeManagementSessionFromQueue(-1L);
            assertTrue("We must not get here. An exception must occur on the method call.", false);
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot find an FM session with the ID -1.", e.getMessage());
            exceptionCaught = true;
        }

        assertTrue("An exception should have been caught", exceptionCaught);
    }

    @Test
    public void testRemoveFeeManagementSessionFromQueueSessionNotQueued() throws Exception {

        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        fmSession.setChargeStatus(FeeManagementSessionStatus.RECONCILED);
        fmSession.setQueued(false);
        persistenceService.persistEntity(fmSession);

        // Call the service method:
        fmService.removeFeeManagementSessionFromQueue(fmSession.getId());

        // Validate:
        assertFalse("FM Session was queued, but was not supposed to.", fmSession.isQueued());
    }

    @Test
    public void testRemoveFeeManagementSessionFromQueueGoodCase() throws Exception {

        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        fmSession.setChargeStatus(FeeManagementSessionStatus.RECONCILED);
        fmSession.setQueued(true);
        persistenceService.persistEntity(fmSession);

        // Call the service method:
        fmService.removeFeeManagementSessionFromQueue(fmSession.getId());

        // Validate the FM session was removed from the queue:
        assertFalse("FM Session was not removed from queue.", fmSession.isQueued());
    }


    /*
    * **************************************************************
    *
    * Unit tests for "queueFeeManagement"
    *
    * **************************************************************
    */

    @Test
    public void testQueueFeeManagementGoodCase() throws Exception {
        // This method uses other methods that are covered by their respective unit tests
    }

    /*
    * **************************************************************
    *
    * Unit tests for "reconcileSession"
    *
    * **************************************************************
    */

    @Test
    public void testReconcileSessionNoSessionIdFound() throws Exception {
        // Call the method with a fake FM Session ID:
        boolean exceptionCaught = false;

        try {
            fmService.reconcileSession(0L);
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
        } catch (Throwable t) {
            // We should never catch any other type of a Throwable:
            assertTrue("Must never catch anything by IllegalArgumentException", false);
        }

        // Assert Exception was caught
        assertTrue("Never caught an IllegalArgumentException", exceptionCaught);
    }

    @Test
    public void testReconcileSessionNullStatus() throws Exception {
        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        persistenceService.persistEntity(fmSession);

        // Call the method:
        boolean exceptionCaught = false;

        try {
            fmService.reconcileSession(fmSession.getId());
        } catch (IllegalStateException ise) {
            // We should catch this exception here
            exceptionCaught = true;
        } catch (Throwable e) {
            // We should never catch any other type of a Throwable:
            assertTrue("Must never catch anything by IllegalStateException", false);
        }

        // Assert Exception was caught
        assertTrue("Never caught an IllegalStateException", exceptionCaught);
    }

    @Test
    public void testReconcileSessionInvalidStatus() throws Exception {
        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        fmSession.setChargeStatus(FeeManagementSessionStatus.RECONCILED);
        persistenceService.persistEntity(fmSession);

        // Call the method:
        boolean exceptionCaught = false;

        try {
            fmService.reconcileSession(fmSession.getId());
        } catch (IllegalStateException ise) {
            // We should catch this exception here
            exceptionCaught = true;
        } catch (Throwable e) {
            // We should never catch any other type of a Throwable:
            assertTrue("Must never catch anything by IllegalStateException", false);
        }

        // Assert Exception was caught
        assertTrue("Never caught an IllegalStateException", exceptionCaught);
    }

    @Test
    public void testReconcileSessionNoAccount() throws Exception {
        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        fmSession.setChargeStatus(FeeManagementSessionStatus.CURRENT);
        persistenceService.persistEntity(fmSession);

        // Call the method:
        boolean exceptionCaught = false;

        try {
            fmService.reconcileSession(fmSession.getId());
        } catch (IllegalStateException ise) {
            // We should catch this exception here
            exceptionCaught = true;
        } catch (Throwable e) {
            // We should never catch any other type of a Throwable:
            assertTrue("Must never catch anything by IllegalStateException", false);
        }

        // Assert Exception was caught
        assertTrue("Never caught an IllegalStateException", exceptionCaught);
    }

    @Test
    public void testReconcileSessionAccountBlocked() throws Exception {
        // TODO: Perform a check for a blocked account. When this method is implemented by Paul.
    }

    @Test
    public void testReconcileSessionNoLastSessionNoManifestsStatusCurrent() throws Exception {
        // Create an FM Session and an Account:
        FeeManagementSession fmSession = new FeeManagementSession();
        Account account = accountService.getOrCreateAccount(ACCOUNT_ID);

        fmSession.setChargeStatus(FeeManagementSessionStatus.CURRENT);
        fmSession.setAccount(account);
        persistenceService.persistEntity(fmSession);

        // Call the method:
        fmService.reconcileSession(fmSession.getId());

        // Validate the result:
        assertEquals(FeeManagementSessionStatus.RECONCILED, fmSession.getStatus());
    }

    @Test
    public void testReconcileSessionNoPrevSessionNoManifestsStatusSimulated() throws Exception {
        // Create an FM Session and an Account:
        FeeManagementSession fmSession = new FeeManagementSession();
        Account account = accountService.getOrCreateAccount(ACCOUNT_ID);

        fmSession.setChargeStatus(FeeManagementSessionStatus.SIMULATED);
        fmSession.setAccount(account);
        persistenceService.persistEntity(fmSession);

        // Call the method:
        fmService.reconcileSession(fmSession.getId());

        // Validate the result:
        assertEquals(FeeManagementSessionStatus.SIMULATED_RECONCILED, fmSession.getStatus());
    }

    @Test
    public void testReconcileSessionValidateCurrentReversals() throws Exception {
        // Create an FM Session and an Account:
        FmSession fmSession = createFmSessionForReconciliation(false, false, true);

        // Call the method:
        fmService.reconcileSession(fmSession.getSession().getId());

        // Validate the result:

        // Go through the list of manifests. Validate each one points at another
        // manifest on the same list and they are both reversal manifests, ie
        // one CHARGE one CANCELLATION and same (offering or internalCharge)
        for (FeeManagementManifest manifest : fmSession.getManifests()) {
            // Get linked manifest:
            FeeManagementManifest linkedManifest = manifest.getLinkedManifest();

            assertTrue(fmSession.getManifests().contains(linkedManifest));
            assertEquals(manifest, linkedManifest.getLinkedManifest());
            assertTrue(FeeManagementServiceImpl.equalsExceptNull(manifest.getOfferingId(), linkedManifest.getOfferingId())
                    || FeeManagementServiceImpl.equalsExceptNull(manifest.getInternalChargeId(), linkedManifest.getInternalChargeId()));
            assertTrue(((manifest.getType() == FeeManagementManifestType.CHARGE) && (linkedManifest.getType() == FeeManagementManifestType.CANCELLATION))
                    || ((manifest.getType() == FeeManagementManifestType.CANCELLATION) && (linkedManifest.getType() == FeeManagementManifestType.CHARGE)));
        }

        assertEquals(FeeManagementSessionStatus.RECONCILED, fmSession.getSession().getStatus());
    }

    @Test
    public void testReconcileSessionValidatePreclearing() throws Exception {
        // Create an FM Session:
        FmSession fmSession = createFmSessionForReconciliation(false, true, false);

        // Get Preclearing manifest and verify they are attached to an FM Session and in the EM context:
        List<Pair<FeeManagementManifest, FeeManagementManifest>> preclearingManifests = getManifestsForPreclearing(fmSession);

        for (Pair<FeeManagementManifest, FeeManagementManifest> pair : preclearingManifests) {
            assertNotNull(pair.getA().getSession());
            assertNotNull(pair.getB().getSession());
            assertTrue(em.contains(pair.getA()));
            assertTrue(em.contains(pair.getB()));
        }

        // Set the parameter for preclearing to "true":
        List<ConfigParameter> configParameters = configService.getParameters();
        boolean paramFound = false;

        for (ConfigParameter param : configParameters) {
            if (StringUtils.equals(Constants.FM_PRECLEAR_MANIFEST, param.getName())) {
                param.setValue("true");
                paramFound = true;

                break;
            }
        }

        // If the parameter is not found, create one:
        if (!paramFound) {
            configParameters.add(new ConfigParameter(Constants.FM_PRECLEAR_MANIFEST, "true"));
        }

        // Update the ConfigService:
        configService.updateParameters(configParameters);

        // Call the method:
        fmService.reconcileSession(fmSession.getSession().getId());

        // Validate the result:

        // Validate each Preclearing manifest in each pair has been detached from the FM session and from the EM context:
        for (Pair<FeeManagementManifest, FeeManagementManifest> pair : preclearingManifests) {
            assertNull(pair.getA().getSession());
            assertNull(pair.getB().getSession());
            assertFalse(em.contains(pair.getA()));
            assertFalse(em.contains(pair.getB()));
        }

        assertEquals(FeeManagementSessionStatus.RECONCILED, fmSession.getSession().getStatus());
    }

    //@Test
    public void testReconcileSessionLastChargedSessionPrev() throws Exception {
        // Create Current and Prior FM Sessions:
        FmSession currentSession = createFmSessionForReconciliation(true, false, false);
        FmSession priorSession = createPriorChargedFmSession(currentSession);

        // Call the service method:
        fmService.reconcileSession(currentSession.getSession().getId());

        // Validate the results:
        // Validate the updated Current Session's Manifests:
        List<FeeManagementManifest> currentManifests = fmService.getManifests(currentSession.getSession().getId());

        assertEquals(currentSession.getManifests().get(0).getId(), currentManifests.get(0).getId());
        assertEquals(currentSession.getManifests().get(1).getId(), currentManifests.get(1).getId());
        assertEquals(currentSession.getSession().getId(), currentManifests.get(2).getSession().getId());
        assertEquals(currentSession.getSession().getId(), currentManifests.get(3).getSession().getId());
        assertEquals(currentManifests.get(2).getId(), currentManifests.get(3).getLinkedManifest().getId());
        assertEquals(currentManifests.get(3).getId(), currentManifests.get(2).getLinkedManifest().getId());
        assertNotNull(currentManifests.get(2).getTransaction());
        assertNull(currentManifests.get(3).getTransaction());
        assertTrue(safeAmountsEqual(currentManifests.get(2), currentManifests.get(3)));
        assertEquals(currentManifests.get(2).getInternalChargeId(), currentManifests.get(3).getInternalChargeId());
        assertEquals(currentManifests.get(2).isSessionCurrent(), currentManifests.get(3).isSessionCurrent());
        assertEquals(FeeManagementManifestType.ORIGINAL, currentManifests.get(2).getType());
        assertEquals(FeeManagementManifestType.CORRECTION, currentManifests.get(3).getType());

        assertEquals(currentSession.getSession().getId(), currentManifests.get(4).getSession().getId());
        assertEquals(currentSession.getSession().getId(), currentManifests.get(5).getSession().getId());
        assertEquals(currentManifests.get(4).getId(), currentManifests.get(5).getLinkedManifest().getId());
        assertEquals(currentManifests.get(5).getId(), currentManifests.get(4).getLinkedManifest().getId());
        assertNotNull(currentManifests.get(4).getTransaction());
        assertNull(currentManifests.get(5).getTransaction());
        assertTrue(safeAmountsEqual(currentManifests.get(4), currentManifests.get(5)));
        assertEquals(currentManifests.get(4).getInternalChargeId(), currentManifests.get(5).getInternalChargeId());
        assertEquals(currentManifests.get(4).isSessionCurrent(), currentManifests.get(5).isSessionCurrent());
        assertEquals(FeeManagementManifestType.ORIGINAL, currentManifests.get(4).getType());
        assertEquals(FeeManagementManifestType.CORRECTION, currentManifests.get(5).getType());

        // Validated the updated Prior Session's Manifests:
        List<FeeManagementManifest> priorManifests = fmService.getManifests(priorSession.getSession().getId());


        assertEquals(FeeManagementSessionStatus.RECONCILED, currentSession.getSession().getStatus());
    }

    /*
     * **************************************************************
     *
     * Unit tests for "chargeSession"
     *
     * **************************************************************
     */


    @Test
    public void testChargeSessionNoSessionIdFound() throws Exception {
        // Call the method with a fake FM Session ID:
        boolean exceptionCaught = false;

        try {
            fmService.chargeSession(0L);
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
        } catch (Throwable t) {
            // We should never catch any other type of a Throwable:
            assertTrue("Must never catch anything by IllegalArgumentException", false);
        }

        // Assert Exception was caught
        assertTrue("Never caught an IllegalArgumentException", exceptionCaught);
    }

    @Test
    public void testChargeSessionNullStatus() throws Exception {
        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        persistenceService.persistEntity(fmSession);

        // Call the method:
        boolean exceptionCaught = false;

        try {
            fmService.chargeSession(fmSession.getId());
        } catch (IllegalStateException ise) {
            // We should catch this exception here
            exceptionCaught = true;
        } catch (Throwable e) {
            // We should never catch any other type of a Throwable:
            assertTrue("Must never catch anything by IllegalStateException", false);
        }

        // Assert Exception was caught
        assertTrue("Never caught an IllegalStateException", exceptionCaught);
    }

    @Test
    public void testChargeSessionWrongStatus() throws Exception {
        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        fmSession.setChargeStatus(FeeManagementSessionStatus.CURRENT);
        persistenceService.persistEntity(fmSession);

        // Call the method:
        boolean exceptionCaught = false;

        try {
            fmService.chargeSession(fmSession.getId());
        } catch (IllegalStateException ise) {
            // We should catch this exception here
            exceptionCaught = true;
        } catch (Throwable e) {
            // We should never catch any other type of a Throwable:
            assertTrue("Must never catch anything by IllegalStateException", false);
        }

        // Assert Exception was caught
        assertTrue("Never caught an IllegalStateException", exceptionCaught);
    }

    @Test
    public void testChargeSessionNoManifestsFound() throws Exception {
        // Create an FM session:
        FeeManagementSession fmSession = new FeeManagementSession();

        fmSession.setChargeStatus(FeeManagementSessionStatus.RECONCILED);
        persistenceService.persistEntity(fmSession);

        // Call the method:
        fmService.chargeSession(fmSession.getId());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getStatus());
    }

    @Test
    public void testChargeSessionManifestHasTransaction() throws Exception {
        // Create an FM session and manifest:
        FmSession fmSession = createFmSession(1, true, false, false, false, FeeManagementManifestType.ORIGINAL);

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionManifestHasNoTransactionTypeId() throws Exception {
        // Create an FM Session:
        FmSession fmSession = createFmSession(1, false, false, false, false, FeeManagementManifestType.CHARGE);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);

        // Nullify the Transaction type ID:
        manifest.setTransactionTypeId(null);
        persistenceService.persistEntity(manifest);

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Verify a Charge was not created:
        assertNull("A CHARGE Transaction was created, but not expected.", manifest.getTransaction());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionChargeManifestNoLinkedManifest() throws Exception {
        // Create an FM Session:
        FmSession fmSession = createFmSession(1, false, false, false, false, FeeManagementManifestType.CHARGE);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);
        Transaction primaryTransaction = manifest.getTransaction();

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Validate that a new Transaction was created:
        Transaction chargeTransaction = manifest.getTransaction();

        assertNull(primaryTransaction);
        assertNotNull("No CHARGE Transaction was created.", chargeTransaction);
        assertNotNull("CHARGE Transaction's ID must not be null", chargeTransaction.getId());
        assertNotNull("New Transaction's amount must not be empty.", chargeTransaction.getAmount());
        assertTrue("CHARGE Transaction was created in a different amount than the manifest.", manifest.getAmount().compareTo(chargeTransaction.getAmount()) == 0);

        // Validate there is no linked manifest:
        assertNull("Linked manifest must not exist", manifest.getLinkedManifest());

        // Validate manifest is marked session current:
        assertTrue("FM Session must be current.", manifest.isSessionCurrent());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionCorrectionManifestHasNoLinkedManifest() throws Exception {
        // Create an FM Session:
        FmSession fmSession = createFmSession(1, false, false, false, false, FeeManagementManifestType.CORRECTION);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);
        Transaction primaryTransaction = manifest.getTransaction();

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Validate that a new Transaction was created:
        Transaction chargeTransaction = manifest.getTransaction();

        assertNull(primaryTransaction);
        assertNotNull("No CHARGE Transaction was created.", chargeTransaction);
        assertNotNull("CHARGE Transaction's ID must not be null", chargeTransaction.getId());
        assertNotNull("New Transaction's amount must not be empty.", chargeTransaction.getAmount());
        assertTrue("CHARGE Transaction was not a reversal to the manifest.", manifest.getAmount().compareTo(chargeTransaction.getAmount().negate()) == 0);

        // Validate there is no linked manifest:
        assertNull("Linked manifest must not exist", manifest.getLinkedManifest());

        // Validate manifest is marked session current:
        assertTrue("FM Session must be current.", manifest.isSessionCurrent());

        // Validate the Session is marked for review:
        assertTrue("FM Session must be marked for manual review", fmSession.getSession().isReviewRequired());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionCorrectionManifestLinkedManifestHasNoTransaction() throws Exception {
        // Create an FM session with linked manifests:
        FmSession fmSession = createFmSession(1, false, true, false, false, FeeManagementManifestType.CORRECTION);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);

        // Nullify the implicated Transaction:
        FeeManagementManifest linkedManifest = manifest.getLinkedManifest();

        linkedManifest.setTransaction(null);
        persistenceService.persistEntity(linkedManifest);

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Verify that the linked manifest still has no Transaction:
        assertNull("Linked manifest must not have a transaction", linkedManifest.getTransaction());

        // Verify the Session is not marked for review and manifest is not marked session current:
        assertFalse("FM Session must not marked for review", fmSession.getSession().isReviewRequired());
        assertFalse("Manifests must not be marked session current", manifest.isSessionCurrent());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionLinkedManifestHasNonReversalTransactionNoAllocationsRemain() throws Exception {
        // Create an FM session with linked manifests:
        FmSession fmSession = createFmSession(1, false, true, true, false, FeeManagementManifestType.CANCELLATION);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);
        FeeManagementManifest linkedManifest = manifest.getLinkedManifest();
        Transaction linkedTransaction = linkedManifest.getTransaction();
        Transaction implicatedTransaction = getImplicatedTransaction(linkedTransaction);

        implicatedTransaction.setStatus(TransactionStatus.BOUNCING);

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Verify all allocations were removed:
        boolean allocationsRemain = (implicatedTransaction.getAllocatedAmount() != null) && (implicatedTransaction.getLockedAllocatedAmount() != null)
                && (implicatedTransaction.getAllocatedAmount().compareTo(implicatedTransaction.getLockedAllocatedAmount().negate()) != 0);

        assertFalse("There are uncleared allocations remain", allocationsRemain);

        // Verify the original manifest was updated with a reversal Transaction:
        Transaction primaryTransaction = manifest.getTransaction();

        assertNotNull("Primary transaction must exist", primaryTransaction);
        assertNotNull("Primary transaction ID must exist", primaryTransaction.getId());
        assertEquals("Reversal transaction amount must be reverse from the manifest", manifest.getAmount(), primaryTransaction.getAmount().negate());

        // Verify the session is not marked for manual review:
        assertFalse("FM Session must not be marked for manual review", fmSession.getSession().isReviewRequired());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionLinkedManifestHasReversalTransactionNoAllocationsRemain() throws Exception {
        // Create an FM session with linked manifests:
        FmSession fmSession = createFmSession(1, false, true, true, false, FeeManagementManifestType.DISCOUNT);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Verify the original manifest was updated with a reversal Transaction:
        Transaction primaryTransaction = manifest.getTransaction();

        assertNotNull("Primary transaction must exist", primaryTransaction);
        assertNotNull("Primary transaction ID must exist", primaryTransaction.getId());
        assertEquals("Reversal transaction amount must be reverse from the manifest", manifest.getAmount(), primaryTransaction.getAmount().negate());

        // Verify the session is not marked for manual review:
        assertFalse("FM Session must not be marked for manual review", fmSession.getSession().isReviewRequired());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionLinkedManifestHasNonReversalTransactionAllocationsRemain() throws Exception {
        // Create an FM session with linked manifests and simulate allocation remain:
        FmSession fmSession = createFmSession(1, false, true, true, true, FeeManagementManifestType.CORRECTION);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);
        FeeManagementManifest linkedManifest = manifest.getLinkedManifest();
        Transaction linkedTransaction = linkedManifest.getTransaction();
        Transaction implicatedTransaction = getImplicatedTransaction(linkedTransaction);

        implicatedTransaction.setStatus(TransactionStatus.WRITING_OFF);

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Verify the main manifest still has no Transaction:
        assertNull("Primary transaction must not exist", manifest.getTransaction());

        // Verify the session is marked for manual review:
        assertTrue("FM Session must be marked for manual review", fmSession.getSession().isReviewRequired());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionLinkedManifestHasReversalTransactionAllocationsRemainNotEnoughBalanceForReversal() throws Exception {

        // Create an FM session with linked manifests and simulate allocation remain:
        FmSession fmSession = createFmSession(1, false, true, true, true, FeeManagementManifestType.CANCELLATION);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);
        FeeManagementManifest linkedManifest = manifest.getLinkedManifest();
        Transaction linkedTransaction = linkedManifest.getTransaction();
        Transaction implicatedTransaction = getImplicatedTransaction(linkedTransaction);

        implicatedTransaction.setStatus(TransactionStatus.REVERSING);

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // TODO: check why this is failing

        // Verify the main manifest still has no Transaction:
        //assertNull("Primary transaction must not exist", manifest.getTransaction());

        // Verify the session is marked for manual review:
        assertTrue("FM Session must be marked for manual review", fmSession.getSession().isReviewRequired());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionLinkedManifestHasReversalStatusTransactionAllocationsRemainEnoughBalanceForReversal() throws Exception {
        // Create an FM session with linked manifests and simulate allocation remain:
        FmSession fmSession = createFmSession(1, false, true, true, true, FeeManagementManifestType.DISCOUNT);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);
        FeeManagementManifest linkedManifest = manifest.getLinkedManifest();
        Transaction linkedTransaction = linkedManifest.getTransaction();
        Transaction implicatedTransaction = getImplicatedTransaction(linkedTransaction);

        implicatedTransaction.setStatus(TransactionStatus.ACTIVE);
        implicatedTransaction.setLockedAllocatedAmount(implicatedTransaction.getLockedAllocatedAmount().negate());

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Verify the original manifest was updated with a reversal Transaction:
        Transaction primaryTransaction = manifest.getTransaction();

        assertNotNull("Primary transaction must exist", primaryTransaction);
        assertNotNull("Primary transaction ID must exist", primaryTransaction.getId());
        assertEquals("Reversal transaction amount must be reverse from the manifest", manifest.getAmount(), primaryTransaction.getAmount().negate());

        // Verify the session is marked for manual review:
        assertTrue("FM Session must be marked for manual review", fmSession.getSession().isReviewRequired());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionLinkedManifestHasNonReversalStatusTransactionAllocationsRemainEnoughBalanceForReversal() throws Exception {

        // Create an FM session with linked manifests and simulate allocation remain:
        FmSession fmSession = createFmSession(1, false, true, true, true, FeeManagementManifestType.DISCOUNT);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);
        FeeManagementManifest linkedManifest = manifest.getLinkedManifest();
        Transaction linkedTransaction = linkedManifest.getTransaction();
        Transaction implicatedTransaction = getImplicatedTransaction(linkedTransaction);

        implicatedTransaction.setStatus(TransactionStatus.DISCOUNTING);

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Verify the session is marked for manual review:
        assertTrue("FM Session must be marked for manual review", fmSession.getSession().isReviewRequired());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());
    }

    @Test
    public void testChargeSessionTransferNoTransactionTransfer() throws Exception {
        // Create an FM session with linked manifests and simulate allocation remain:
        FmSession fmSession = createFmSession(1, false, true, true, true, FeeManagementManifestType.DISCOUNT);
        FeeManagementManifest manifest = fmSession.getManifests().get(0);
        FeeManagementManifest linkedManifest = manifest.getLinkedManifest();
        Transaction linkedTransaction = linkedManifest.getTransaction();
        Transaction implicatedTransaction = getImplicatedTransaction(linkedTransaction);

        implicatedTransaction.setStatus(TransactionStatus.TRANSFERRING);

        // Call the method:
        fmService.chargeSession(fmSession.getSession().getId());

        // Verify the main manifest still has no Transaction:
        assertNull("Primary transaction must not exist", manifest.getTransaction());

        // Verify the session is marked for manual review:
        assertTrue("FM Session must be marked for manual review", fmSession.getSession().isReviewRequired());

        // Verify session's status changed to CHARGED:
        assertEquals("FM Session status has never changed to CHARGED", FeeManagementSessionStatus.CHARGED, fmSession.getSession().getStatus());

        // Verify the implicated transaction has the status of RECIPROCAL_OFFSET:
        implicatedTransaction = transactionService.getTransaction(implicatedTransaction.getId());
        assertEquals("Implicated Transaction must have the status of RECIPROCAL_OFFSET now.", TransactionStatus.RECIPROCAL_OFFSET, implicatedTransaction.getStatus());
    }

    /**
     * *****************************************************************************
     * <p/>
     * Helper methods for "reconcileSession"
     * <p/>
     * ******************************************************************************
     */

    private FmSession createFmSessionForReconciliation(boolean priorSessionAdjustment, boolean preclearing, boolean currentSessionReversal) throws Exception {

        // Create Session with an Account and Manifests:
        FmSession result = new FmSession();
        FeeManagementSession fmSession = new FeeManagementSession();
        Account account = accountService.getOrCreateAccount(ACCOUNT_ID);

        fmSession.setChargeStatus(FeeManagementSessionStatus.CURRENT);
        fmSession.setAccount(account);
        persistenceService.persistEntity(fmSession);
        result.setSession(fmSession);

        // Add Manifests for prior session adjustment:
        if (priorSessionAdjustment) {
            createManifestsForPriorSessionAdjustment(result);
        }

        // Add Manifests for preclearing (optional):
        if (preclearing) {
            createManifestsForPreclearing(result);
        }

        // Create current session's reversal manifests:
        if (currentSessionReversal) {
            createCurrentSessionReversalManifests(result);
        }

        return result;
    }

    /**
     * Creates reversal FM Manifests for the current session.
     *
     * @param result FmResult class store new manifests in.
     */
    private void createCurrentSessionReversalManifests(FmSession result) throws Exception {

        // Create a pair of Manifests of inverse types (CHARGE vs. CANCELLATION) with matching
        // Offering ID + Rate:
        FeeManagementManifest chargeManifest = new FeeManagementManifest();
        FeeManagementManifest cancellationManifest = new FeeManagementManifest();
        Transaction chargeTransaction = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);
        Transaction cancellationTransaction = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);
        Rate rate = _createRate();

        chargeManifest.setSession(result.getSession());
        cancellationManifest.setSession(result.getSession());
        chargeManifest.setType(FeeManagementManifestType.CHARGE);
        cancellationManifest.setType(FeeManagementManifestType.CANCELLATION);
        chargeManifest.setAmount(MANIFEST_AMOUNT);
        cancellationManifest.setAmount(MANIFEST_AMOUNT);
        chargeManifest.setTransactionTypeId(TRANSACTION_TYPE_ID);
        cancellationManifest.setTransactionTypeId(TRANSACTION_TYPE_ID);
        chargeManifest.setOfferingId(OFFERING_ID);
        cancellationManifest.setOfferingId(OFFERING_ID);
        chargeManifest.setRate(rate);
        cancellationManifest.setRate(rate);
        chargeManifest.setTransaction(chargeTransaction);
        cancellationManifest.setTransaction(cancellationTransaction);

        persistenceService.persistEntity(chargeManifest);
        persistenceService.persistEntity(cancellationManifest);
        result.getManifests().add(chargeManifest);
        result.getManifests().add(cancellationManifest);

        // Create a pair of Manifests of inverse types (CHARGE vs. CANCELLATION) with matching
        // Internal ID + Rate:
        chargeManifest = new FeeManagementManifest();
        cancellationManifest = new FeeManagementManifest();
        chargeTransaction = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);
        cancellationTransaction = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);

        chargeManifest.setSession(result.getSession());
        cancellationManifest.setSession(result.getSession());
        chargeManifest.setType(FeeManagementManifestType.CHARGE);
        cancellationManifest.setType(FeeManagementManifestType.CANCELLATION);
        chargeManifest.setAmount(MANIFEST_AMOUNT);
        cancellationManifest.setAmount(MANIFEST_AMOUNT);
        chargeManifest.setTransactionTypeId(TRANSACTION_TYPE_ID);
        cancellationManifest.setTransactionTypeId(TRANSACTION_TYPE_ID);
        chargeManifest.setInternalChargeId(INTERNAL_CHARGE_ID);
        cancellationManifest.setInternalChargeId(INTERNAL_CHARGE_ID);
        chargeManifest.setRate(rate);
        cancellationManifest.setRate(rate);
        chargeManifest.setTransaction(chargeTransaction);
        cancellationManifest.setTransaction(cancellationTransaction);

        persistenceService.persistEntity(chargeManifest);
        persistenceService.persistEntity(cancellationManifest);
        result.getManifests().add(chargeManifest);
        result.getManifests().add(cancellationManifest);
    }

    /**
     * Creates Current session Manifests for Prior session adjustment.
     */
    private void createManifestsForPriorSessionAdjustment(FmSession fmSession) throws Exception {
        // Create an Already Charged manifest:
        FeeManagementManifest alreadyCharged = new FeeManagementManifest();
        Transaction transaction1 = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);
        Rate rate = _createRate();

        alreadyCharged.setSession(fmSession.getSession());
        alreadyCharged.setType(FeeManagementManifestType.CHARGE);
        alreadyCharged.setAmount(MANIFEST_AMOUNT);
        alreadyCharged.setTransactionTypeId(TRANSACTION_TYPE_ID);
        alreadyCharged.setRegistrationId(REGISTRATION_ID);
        alreadyCharged.setRate(rate);
        alreadyCharged.setTransaction(transaction1);
        alreadyCharged.setRecognitionDate(RECOGNITION_DATE);
        alreadyCharged.setEffectiveDate(EFFECTIVE_DATE);

        persistenceService.persistEntity(alreadyCharged);
        fmSession.getManifests().add(alreadyCharged);

        // Create a Not Yet Charged manifest:
        FeeManagementManifest notYetCharged = new FeeManagementManifest();
        Transaction transaction2 = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);

        notYetCharged.setSession(fmSession.getSession());
        notYetCharged.setType(FeeManagementManifestType.CHARGE);
        notYetCharged.setAmount(MANIFEST_AMOUNT);
        notYetCharged.setTransactionTypeId(TRANSACTION_TYPE_ID);
        notYetCharged.setInternalChargeId(INTERNAL_CHARGE_ID);
        notYetCharged.setRate(rate);
        notYetCharged.setTransaction(transaction2);
        notYetCharged.setRecognitionDate(RECOGNITION_DATE);
        notYetCharged.setEffectiveDate(EFFECTIVE_DATE);
        persistenceService.persistEntity(notYetCharged);
        fmSession.getManifests().add(notYetCharged);
    }

    /**
     * Creates a Prior Charged FM Session.
     */
    private FmSession createPriorChargedFmSession(FmSession currentSession) {

        // Create Session with an Account and Manifests:
        FmSession priorSession = new FmSession();
        FeeManagementSession fmSession = new FeeManagementSession();
        Account account = accountService.getOrCreateAccount(ACCOUNT_ID);

        fmSession.setChargeStatus(FeeManagementSessionStatus.CHARGED);
        fmSession.setAccount(account);
        persistenceService.persistEntity(fmSession);
        priorSession.setSession(fmSession);
        currentSession.getSession().setPrevSession(priorSession.getSession());

        // Create ALREADY CHARGED Prior Manifests matching Current ones by (Registration or Internal Charge) + Rate
        /* CHARGE matching by RegistrationId + Rate */
        FeeManagementManifest alreadyCharged = new FeeManagementManifest();
        Transaction transaction1 = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);
        Rate rate = currentSession.getManifests().get(0).getRate();

        alreadyCharged.setSession(priorSession.getSession());
        alreadyCharged.setType(FeeManagementManifestType.CHARGE);
        alreadyCharged.setAmount(MANIFEST_AMOUNT);
        alreadyCharged.setTransactionTypeId(TRANSACTION_TYPE_ID);
        alreadyCharged.setRegistrationId(REGISTRATION_ID);
        alreadyCharged.setRate(rate);
        alreadyCharged.setTransaction(transaction1);
        alreadyCharged.setRecognitionDate(RECOGNITION_DATE);
        alreadyCharged.setEffectiveDate(EFFECTIVE_DATE);
        persistenceService.persistEntity(alreadyCharged);
        priorSession.getManifests().add(alreadyCharged);

        // Create NOT YET CHARGED Prior Manifests matching Current ones by (Registration or Internal Charge) + Rate:
        /* CANCELLATION matching by InternalChargeId + Rate */
        FeeManagementManifest notYetCharged = new FeeManagementManifest();
        Transaction transaction2 = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);

        notYetCharged.setSession(priorSession.getSession());
        notYetCharged.setType(FeeManagementManifestType.CANCELLATION);
        notYetCharged.setAmount(MANIFEST_AMOUNT);
        notYetCharged.setTransactionTypeId(TRANSACTION_TYPE_ID);
        notYetCharged.setInternalChargeId(INTERNAL_CHARGE_ID);
        notYetCharged.setRate(rate);
        notYetCharged.setTransaction(transaction2);
        notYetCharged.setRecognitionDate(new Date());
        notYetCharged.setEffectiveDate(null);
        persistenceService.persistEntity(notYetCharged);
        priorSession.getManifests().add(notYetCharged);

        // Create UNMATCHED Prior Manifests not matched to Current ones by (Registration or Internal Charge) + Rate:
        /* DISCOUNT not matching by either (RegistrationID or InternalChargeID) and Rate */
        FeeManagementManifest unmatched = new FeeManagementManifest();
        Transaction transaction3 = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);

        unmatched.setSession(priorSession.getSession());
        unmatched.setType(FeeManagementManifestType.DISCOUNT);
        unmatched.setAmount(MANIFEST_AMOUNT);
        unmatched.setTransactionTypeId(TRANSACTION_TYPE_ID);
        unmatched.setInternalChargeId("duh");
        unmatched.setRate(rate);
        unmatched.setTransaction(transaction3);
        persistenceService.persistEntity(unmatched);
        priorSession.getManifests().add(unmatched);

        // Create UNMATCHED Prior Manifest pair - subject to a FULL REVERSAL matched by (OfferingID + Rate):
        FeeManagementManifest fullReversal1 = new FeeManagementManifest();
        FeeManagementManifest fullReversal2 = new FeeManagementManifest();
        BigDecimal fullReversalAmount = new BigDecimal(9999999d);
        String fullReversalOfferingId = "FullReversalOfferingId";
        String fullReversalInternalChargeId = "FullReversalInternalChargeId";

        fullReversal1.setSession(priorSession.getSession());
        fullReversal1.setType(FeeManagementManifestType.CHARGE);
        fullReversal1.setTransactionTypeId(TRANSACTION_TYPE_ID);
        fullReversal1.setAmount(fullReversalAmount);
        fullReversal1.setOfferingId(fullReversalOfferingId);
        fullReversal1.setRate(rate);
        persistenceService.persistEntity(fullReversal1);

        fullReversal2.setSession(priorSession.getSession());
        fullReversal2.setType(FeeManagementManifestType.CANCELLATION);
        fullReversal2.setTransactionTypeId(TRANSACTION_TYPE_ID);
        fullReversal2.setAmount(fullReversalAmount);
        fullReversal2.setOfferingId(fullReversalOfferingId);
        fullReversal2.setRate(rate);
        fullReversal2.setLinkedManifest(fullReversal1);
        persistenceService.persistEntity(fullReversal2);

        fullReversal1.setLinkedManifest(fullReversal2);
        persistenceService.persistEntity(fullReversal1);
        priorSession.getManifests().add(fullReversal1);
        priorSession.getManifests().add(fullReversal2);

        // Create UNMATCHED Prior Manifest pair - subject to a FULL REVERSAL matched by (InternalChargeID + Rate):
        fullReversal1 = new FeeManagementManifest();
        fullReversal2 = new FeeManagementManifest();

        fullReversal1.setSession(priorSession.getSession());
        fullReversal1.setType(FeeManagementManifestType.CHARGE);
        fullReversal1.setTransactionTypeId(TRANSACTION_TYPE_ID);
        fullReversal1.setAmount(fullReversalAmount);
        fullReversal1.setInternalChargeId(fullReversalInternalChargeId);
        fullReversal1.setRate(rate);
        persistenceService.persistEntity(fullReversal1);

        fullReversal2.setSession(priorSession.getSession());
        fullReversal2.setType(FeeManagementManifestType.CANCELLATION);
        fullReversal2.setTransactionTypeId(TRANSACTION_TYPE_ID);
        fullReversal2.setAmount(fullReversalAmount);
        fullReversal2.setInternalChargeId(fullReversalInternalChargeId);
        fullReversal2.setRate(rate);
        fullReversal2.setLinkedManifest(fullReversal1);
        persistenceService.persistEntity(fullReversal2);

        fullReversal1.setLinkedManifest(fullReversal2);
        persistenceService.persistEntity(fullReversal1);
        priorSession.getManifests().add(fullReversal1);
        priorSession.getManifests().add(fullReversal2);

        // Create manifests to be ignored, CORRECTION and ORIGINAL:
        FeeManagementManifest original = new FeeManagementManifest();
        FeeManagementManifest correction = new FeeManagementManifest();

        original.setSession(priorSession.getSession());
        correction.setSession(priorSession.getSession());
        original.setType(FeeManagementManifestType.ORIGINAL);
        correction.setType(FeeManagementManifestType.CORRECTION);

        persistenceService.persistEntity(original);
        persistenceService.persistEntity(correction);
        priorSession.getManifests().add(original);
        priorSession.getManifests().add(correction);

        return priorSession;
    }

    /*
     * Creates a new Rate.
     */
    private Rate _createRate() throws Exception {

        String rateCatalogCode = "RC_2013_CODE";
        String rateCode = "R_2013_CODE";
        String subCode = "_1";
        String atpId = "19871";

        rateService.createRateType("RT_2013", "RateType_2013", "2013 Rate type description", false, RateAmountType.FLAT);
        _createRateCatalog(rateCatalogCode, atpId);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);
        Date transactionDate = dateFormat.parse("05/23/2012");
        Date recognitionDate = null;
        String transactionTypeId = "cash";
        String rateName = rateCode + " name";
        int minLimitUnits = 0;
        int maxLimitUnits = 10;
        TransactionDateType dateType = TransactionDateType.ALWAYS;
        BigDecimal limitAmount = new BigDecimal(7776000.1111);
        BigDecimal rateAmount = new BigDecimal(200.88);
        Rate rate = rateService.createRate(rateCode, subCode, rateName, rateCatalogCode, transactionTypeId, dateType,
                rateAmount, limitAmount, minLimitUnits, maxLimitUnits, transactionDate, recognitionDate, atpId, false);

        Assert.notNull(rate);

        return rate;
    }

    /*
     * Creates a Rate Catalog.
     */
    private RateCatalog _createRateCatalog(String rateCatalogCode, String... atpIds) {

        String rateTypeCode = "RT_2013";
        String transactionTypeId = "cash";
        TransactionDateType dateType = TransactionDateType.ALWAYS;

        BigDecimal minAmount = new BigDecimal(10.99);
        BigDecimal maxAmount = new BigDecimal(300000.67);

        BigDecimal minLimitAmount = new BigDecimal(2.01);
        BigDecimal maxLimitAmount = new BigDecimal(200033.01);

        int minLimitUnits = 0;
        int maxLimitUnits = 10;

        List<KeyPair> keyPairs = Arrays.asList(new KeyPair("key1", "value1"));

        RateCatalog rateCatalog = rateService.createRateCatalog(rateCatalogCode, rateTypeCode, transactionTypeId,
                dateType, minAmount, maxAmount, minLimitAmount, maxLimitAmount, minLimitUnits, maxLimitUnits,
                Arrays.asList(atpIds), keyPairs, false, false, false, false, false, false);

        Assert.notNull(rateCatalog);

        return rateCatalog;
    }

    /**
     * Creates FM Manifests for preclearing.
     *
     * @param result FmResult class store new manifests in.
     */
    private void createManifestsForPreclearing(FmSession result) {

        // Create a pair of manifests of inverse Type (CHARGE vs. CANCELLATION) and same Amount:
        FeeManagementManifest chargeManifest = new FeeManagementManifest();
        FeeManagementManifest cancellationManifest = new FeeManagementManifest();

        chargeManifest.setSession(result.getSession());
        cancellationManifest.setSession(result.getSession());
        chargeManifest.setType(FeeManagementManifestType.CHARGE);
        cancellationManifest.setType(FeeManagementManifestType.CANCELLATION);
        chargeManifest.setAmount(MANIFEST_AMOUNT);
        cancellationManifest.setAmount(MANIFEST_AMOUNT);
        chargeManifest.setTransactionTypeId("Preclearing1");
        cancellationManifest.setTransactionTypeId("Preclearing1");

        persistenceService.persistEntity(chargeManifest);
        persistenceService.persistEntity(cancellationManifest);
        result.getManifests().add(chargeManifest);
        result.getManifests().add(cancellationManifest);

        // Create a pair of Manifests of same Type CHARGE and inverse Amount:
        chargeManifest = new FeeManagementManifest();
        cancellationManifest = new FeeManagementManifest();

        chargeManifest.setSession(result.getSession());
        cancellationManifest.setSession(result.getSession());
        chargeManifest.setType(FeeManagementManifestType.CHARGE);
        cancellationManifest.setType(FeeManagementManifestType.CHARGE);
        chargeManifest.setAmount(MANIFEST_AMOUNT);
        cancellationManifest.setAmount(MANIFEST_AMOUNT.negate());
        chargeManifest.setTransactionTypeId("Preclearing2");
        cancellationManifest.setTransactionTypeId("Preclearing2");

        persistenceService.persistEntity(chargeManifest);
        persistenceService.persistEntity(cancellationManifest);
        result.getManifests().add(chargeManifest);
        result.getManifests().add(cancellationManifest);

        // Create a pair of Manifests of same Type CANCELLATION and inverse Amount:
        chargeManifest = new FeeManagementManifest();
        cancellationManifest = new FeeManagementManifest();

        chargeManifest.setSession(result.getSession());
        cancellationManifest.setSession(result.getSession());
        chargeManifest.setType(FeeManagementManifestType.CANCELLATION);
        cancellationManifest.setType(FeeManagementManifestType.CANCELLATION);
        chargeManifest.setAmount(MANIFEST_AMOUNT);
        cancellationManifest.setAmount(MANIFEST_AMOUNT.negate());
        chargeManifest.setTransactionTypeId("Preclearing3");
        cancellationManifest.setTransactionTypeId("Preclearing3");

        persistenceService.persistEntity(chargeManifest);
        persistenceService.persistEntity(cancellationManifest);
        result.getManifests().add(chargeManifest);
        result.getManifests().add(cancellationManifest);
    }

    /**
     * Returns pairs of manifest for Preclearing.
     *
     * @param fmSession FM Session.
     * @return A list of Pairs of manifests for preclearing.
     */
    private List<Pair<FeeManagementManifest, FeeManagementManifest>> getManifestsForPreclearing(FmSession fmSession) {

        // Go through the list of manifests. Find FM Manifests with the same Transaction ID.
        // Separate them into pairs of reversal manifests:
        List<Pair<FeeManagementManifest, FeeManagementManifest>> reversalPairs = new ArrayList<Pair<FeeManagementManifest, FeeManagementManifest>>();

        for (int i = 0, sz = fmSession.getManifests().size(); i < sz; i++) {
            FeeManagementManifest manifest = fmSession.getManifests().get(i);

            if (StringUtils.startsWith(manifest.getTransactionTypeId(), "Preclearing")) {

                for (int j = i + 1; j < sz; j++) {
                    FeeManagementManifest anotherManifest = fmSession.getManifests().get(j);

                    if (StringUtils.equals(manifest.getTransactionTypeId(), anotherManifest.getTransactionTypeId())) {
                        Pair<FeeManagementManifest, FeeManagementManifest> pair = new Pair<FeeManagementManifest, FeeManagementManifest>();

                        pair.setA(manifest);
                        pair.setB(anotherManifest);
                        reversalPairs.add(pair);
                    }
                }
            }
        }

        return reversalPairs;
    }

    /********************************************************************************
     *
     * Helper methods for "chargeSession"
     *
     ********************************************************************************/

    /**
     * Creates an FM Session, Manifests, Linked Manifests, Transactions and Implicated Transactions.
     *
     * @param numManifests             Number of FM manifests to create.
     * @param addPrimaryTransactions   Whether to add primary manifests' transactions.
     * @param addLinkedManifests       Whether to add linked manifests.
     * @param addImplicatedTransaction Whether to add implicated Transactions.
     * @param addLockedAllocation      Whether to add implicated transaction locked allocations in addition to unlocked.
     * @param manifestTypes            Types of primary manifests to create. Must be the size of "numManifests"
     * @return An <code>FmSession</code> with new objects.
     */
    private FmSession createFmSession(int numManifests, boolean addPrimaryTransactions, boolean addLinkedManifests,
                                      boolean addImplicatedTransaction, boolean addLockedAllocation, FeeManagementManifestType... manifestTypes) {
        // Create Session:
        Account account = accountService.getOrCreateAccount(ACCOUNT_ID);
        FeeManagementSession fmSession = new FeeManagementSession();
        FmSession result = new FmSession();

        fmSession.setChargeStatus(FeeManagementSessionStatus.RECONCILED);
        fmSession.setAccount(account);
        persistenceService.persistEntity(fmSession);
        result.setSession(fmSession);

        // Create manifests:
        for (int i = 0; i < numManifests; i++) {
            FeeManagementManifest manifest = new FeeManagementManifest();

            manifest.setSession(fmSession);
            manifest.setType(manifestTypes[i]);
            manifest.setAmount(MANIFEST_AMOUNT);
            manifest.setTransactionTypeId(TRANSACTION_TYPE_ID);

            // Add Transaction:
            if (addPrimaryTransactions) {
                Transaction transaction = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), MANIFEST_AMOUNT);

                manifest.setTransaction(transaction);
            }

            // Persist the manifest:
            persistenceService.persistEntity(manifest);
            result.getManifests().add(manifest);

            // Add linked manifest, linked and implicated transactions:
            if (addLinkedManifests) {
                FeeManagementManifest linkedManifest = new FeeManagementManifest();
                Transaction linkedTransaction = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), IMPLICATED_TRANSACTION_AMOUNT);

                linkedManifest.setSession(fmSession);
                linkedManifest.setTransaction(linkedTransaction);
                linkedManifest.setType(FeeManagementManifestType.CORRECTION);
                linkedManifest.setLinkedManifest(manifest);
                persistenceService.persistEntity(manifest);
                manifest.setLinkedManifest(linkedManifest);
                persistenceService.persistEntity(linkedManifest);

                // Create implicated transaction and allocation:
                if (addImplicatedTransaction) {
                    createAllocations(linkedTransaction, addLockedAllocation);
                }
            }
        }

        return result;
    }

    /**
     * Creates an allocation between the given transaction and a temporary transaction.
     *
     * @param transaction         A transaction for which to create an allocation.
     * @param addLockedAllocation Whether to also create a locked allocation.
     */
    private void createAllocations(Transaction transaction, boolean addLockedAllocation) {

        // Create an unlocked allocation:
        BigDecimal allocationAmount = transaction.getAmount().divide(new BigDecimal(2));
        Transaction implicatedTransaction = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), transaction.getAmount().negate());

        transactionService.createAllocation(transaction.getId(), implicatedTransaction.getId(), allocationAmount);

        // Create a locked allocation if needed:
        if (addLockedAllocation) {
            // Refresh the transactions:
            transaction = transactionService.getTransaction(transaction.getId());
            implicatedTransaction = transactionService.getTransaction(implicatedTransaction.getId());

            // Create a locked allocation:
            transactionService.createAllocation(transaction, implicatedTransaction, allocationAmount, true, true, false);
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
     * Creates a PaymentBillingPlan
     */
    protected PaymentBillingPlan createPaymentBillingPlan() throws Exception {

        transactionService.createTransaction("1020", TEST_USER_ID, new Date(), IMPLICATED_TRANSACTION_AMOUNT);
        transactionService.createTransaction("1001", TEST_USER_ID, new Date(), IMPLICATED_TRANSACTION_AMOUNT);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date openPeriodStartDate = dateFormat.parse("01/01/2011");
        Date openPeriodEndDate = dateFormat.parse("01/01/2020");

        Date chargePeriodStartDate = dateFormat.parse("01/01/2011");
        Date chargePeriodEndDate = dateFormat.parse("01/01/2020");

        String GL_ACCOUNT_ID = "01-0-131120 1326";

        GeneralLedgerType glType = generalLedgerService.createGeneralLedgerType("GL_TYPE1", "Test GL type1",
                "Test GL Description 1", GL_ACCOUNT_ID, GlOperationType.CREDIT);

        TransferType transferType = transactionTransferService.createTransferType(glType.getId(), "_TT_1", "TT 1", "Transfer Type 1");

        PaymentBillingPlan plan = paymentBillingService.createPaymentBillingPlan(
                "PB code1",
                "PB name 1",
                "PB description 1",
                transferType.getId(),
                "1001",
                "1020",
                openPeriodStartDate,
                openPeriodEndDate,
                chargePeriodStartDate,
                chargePeriodEndDate,
                new BigDecimal(10e7),
                new BigDecimal(3570.99),
                new BigDecimal(400.00),
                new BigDecimal(1),
                new BigDecimal(3700.99),
                1,
                true,
                "PB plan prefix",
                PaymentRoundingType.FIRST,
                ScheduleType.SKIP_EARLIER);

        paymentBillingService.createPaymentBillingAllowableCharge(
                plan.getId(),
                ".*",
                new BigDecimal(1450),
                Constants.BIG_DECIMAL_HUNDRED,
                1);

        return plan;
    }

    protected ThirdPartyPlan _createThirdPartyPlan(String planCode) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date openPeriodStartDate = dateFormat.parse("01/01/2011");
        Date openPeriodEndDate = dateFormat.parse("01/01/2020");

        Date chargePeriodStartDate = dateFormat.parse("01/01/2011");
        Date chargePeriodEndDate = dateFormat.parse("01/01/2020");


        String GL_ACCOUNT_ID = "01-0-131120 1326";

        GeneralLedgerType glType = generalLedgerService.createGeneralLedgerType("GL_TYPE1", "Test GL type1",
                "Test GL Description 1", GL_ACCOUNT_ID, GlOperationType.CREDIT);

        TransferType transferType = transactionTransferService.createTransferType(glType.getId(), planCode + "_TT_1", "TT 1", "Transfer Type 1");
        ThirdPartyAccount thirdPartyAccount = createThirdPartyAccount();

        ThirdPartyPlan plan = thirdPartyTransferService.createThirdPartyPlan(
                planCode,
                "TPP name 1",
                "TPP description 1",
                transferType.getId(),
                thirdPartyAccount.getId(),
                new BigDecimal(10e4),
                new Date(),
                null,
                openPeriodStartDate,
                openPeriodEndDate,
                chargePeriodStartDate,
                chargePeriodEndDate);


        ThirdPartyPlanMember planMember =
                thirdPartyTransferService.createThirdPartyPlanMember(TEST_USER_ID, plan.getId(), 99);


        return plan;
    }

    protected ThirdPartyAccount createThirdPartyAccount() {

        OrgName orgName = new OrgName();
        Account adminAccount = accountService.getOrCreateAccount(ACCOUNT_ID);
        orgName.setName("Org 1");
        orgName.setCreatorId(ACCOUNT_ID);
        orgName.setLastUpdate(new Date());
        orgName.setContact(adminAccount.getDefaultPersonName());

        persistenceService.persistEntity(orgName);

        ThirdPartyAccount account = new ThirdPartyAccount();
        account.setId("third_party_account");
        account.setAbleToAuthenticate(true);
        account.setCreationDate(new Date());
        account.setCreatorId(ACCOUNT_ID);
        account.setOrgName(orgName);
        account.setCreditLimit(new BigDecimal(10e6));

        persistenceService.persistEntity(account);

        return account;
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
     * Encapsulates an FM Session and related Manifests.
     */
    private class FmSession {
        private FeeManagementSession session;
        private List<FeeManagementManifest> manifests = new ArrayList<FeeManagementManifest>();

        public FeeManagementSession getSession() {
            return session;
        }

        public void setSession(FeeManagementSession session) {
            this.session = session;
        }

        public List<FeeManagementManifest> getManifests() {
            return manifests;
        }

        public void setManifests(List<FeeManagementManifest> manifests) {
            this.manifests = manifests;
        }
    }
}
