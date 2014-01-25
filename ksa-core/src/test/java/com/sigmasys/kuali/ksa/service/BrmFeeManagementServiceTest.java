package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.fm.*;
import com.sigmasys.kuali.ksa.service.brm.BrmContext;
import com.sigmasys.kuali.ksa.service.brm.BrmService;
import com.sigmasys.kuali.ksa.service.fm.BrmFeeManagementService;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import com.sigmasys.kuali.ksa.util.GuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * BRM FeeManagementService unit tests.
 *
 * @author Michael Ivanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class BrmFeeManagementServiceTest extends AbstractServiceTest {

    private static final String ACCOUNT_ID = "admin";
    private static final String TRANSACTION_TYPE_ID = "cash";
    private static final BigDecimal MANIFEST_AMOUNT = new BigDecimal(10);
    private static final BigDecimal IMPLICATED_TRANSACTION_AMOUNT = new BigDecimal(12);


    @Autowired
    protected ConfigService configService;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RateService rateService;

    @Autowired
    private BrmFeeManagementService brmFmService;

    @Autowired
    private BrmService brmService;


    @Test
    public void assesFees1() throws Exception {

        // Create an FM session and manifest:
        FeeManagementSession session = createFmSession(1, true, false, false, false, FeeManagementManifestType.ORIGINAL);

        Assert.notNull(session);
        Assert.notNull(session.getId());
        Assert.notNull(session.getSignups());
        Assert.notEmpty(session.getSignups());

        FeeManagementSignup signup = session.getSignups().iterator().next();

        Assert.notNull(signup);

        FeeManagementSession updatedSession = brmFmService.assessFees(session.getId());

        Assert.notNull(updatedSession);
        Assert.notNull(updatedSession.getId());

        Assert.isTrue(session == updatedSession);
    }

    @Test
    @SuppressWarnings("all")
    public void assesFees2() throws Exception {

        // Create an FM session and manifest:
        FeeManagementSession session = createFmSession(1, true, false, false, false, FeeManagementManifestType.ORIGINAL);

        Assert.notNull(session);
        Assert.notNull(session.getId());
        Assert.notNull(session.getSignups());
        Assert.notEmpty(session.getSignups());

        FeeManagementSignup signup = session.getSignups().iterator().next();

        Assert.notNull(signup);

        // Calling BrmService with payment application rules
        BrmContext context = new BrmContext();
        context.setAccount(session.getAccount());
        context.setGlobalVariable("fmSession", session);

        FeeManagementSession updatedSession = null;

        Set<FeeManagementSignup> signups = session.getSignups();

        Assert.notNull(signups);
        Assert.notEmpty(signups);

        for (FeeManagementSignup fmSignup : signups) {
            context.setGlobalVariable("fmSignup", fmSignup);
            brmService.fireRules("FM Signup 1", context);
            updatedSession = context.getGlobalVariable("fmSession");
        }

        Assert.notNull(updatedSession);
        Assert.notNull(updatedSession.getId());

        Assert.isTrue(session == updatedSession);
    }

    @Test
    public void javaRule1() throws Exception {

        // Create an FM session and manifest:
        FeeManagementSession session = createFmSession(1, true, false, false, false, FeeManagementManifestType.ORIGINAL);

        Assert.notNull(session);
        Assert.notNull(session.getId());
        Assert.notNull(session.getSignups());
        Assert.notEmpty(session.getSignups());

        FeeManagementSignup signup = session.getSignups().iterator().next();

        Assert.notNull(signup);

        // Calling BrmService with payment application rules
        BrmContext context = new BrmContext();
        context.setAccount(session.getAccount());
        context.setGlobalVariable("fmSession", session);

        String[] rateCodes = {
                "cp.undergrad.resident.pt",
                "cp.undergrad.nonresident.pt",
                "cp.graduate.resident.pt",
                "cp.graduate.nonresident.pt"
        };

        UnitNumber threshold = new UnitNumber(12);

        for (String rateCode : rateCodes) {

            List<Rate> rates = rateService.getRatesByCode(rateCode);

            Assert.notNull(rates);
            Assert.notEmpty(rates);

            Rate rate = rateService.getRate(rateCode, "default", session.getAtpId());

            Assert.notNull(rate);
            Assert.notNull(rate.getId());

            UnitNumber numberOfDroppedUnits = context.getFmService().countDroppedUnits(rateCode, context);
            UnitNumber numberOfTakenUnits = context.getFmService().countTakenUnits(rateCode, context);

            UnitNumber numberOfUnits = numberOfTakenUnits.add(numberOfDroppedUnits);

            if (numberOfUnits.compareTo(threshold) > 0) {
                numberOfDroppedUnits = numberOfDroppedUnits.subtract(numberOfUnits.subtract(threshold));
            }

            UnitNumber numberOfUnitsToCharge = numberOfDroppedUnits.divide(new UnitNumber(5));

            context.getFmService().chargeIncidentalRate(rateCode, "default", rateCode + ".default", numberOfUnitsToCharge, null, context);
        }

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
    private FeeManagementSession createFmSession(int numManifests, boolean addPrimaryTransactions, boolean addLinkedManifests,
                                                 boolean addImplicatedTransaction,
                                                 boolean addLockedAllocation,
                                                 FeeManagementManifestType... manifestTypes) throws Exception {

        Rate rate = _createRate("late.registration", "1", "late.fee");

        // Create Session:
        Account account = accountService.getOrCreateAccount(ACCOUNT_ID);
        FeeManagementSession session = new FeeManagementSession();

        session.setChargeStatus(FeeManagementSessionStatus.RECONCILED);
        session.setAccount(account);

        session.setAtpId("20134");

        persistenceService.persistEntity(session);

        FeeManagementSignup signup = new FeeManagementSignup();
        signup.setOfferingId("12345");
        signup.setRegistrationId(new GuidGenerator().getNewGuid());
        signup.setCreationDate(new Date());
        signup.setEffectiveDate(new Date());
        signup.setAtpId("20134");
        signup.setOfferingType(OfferingType.ACTIVITY_OFFERING);
        signup.setSession(session);
        signup.setOperation(FeeManagementSignupOperation.ADD_WITHOUT_PENALTY);
        signup.setUnits(new UnitNumber(5));

        persistenceService.persistEntity(signup);

        FeeManagementSignupRate signupRate = new FeeManagementSignupRate();
        signupRate.setSignup(signup);
        signupRate.setRate(rate);
        signupRate.setComplete(false);

        persistenceService.persistEntity(signupRate);

        HashSet<FeeManagementSignupRate> signupRates = new HashSet<FeeManagementSignupRate>();
        signupRates.add(signupRate);

        signup.setSignupRates(signupRates);

        Set<FeeManagementSignup> signups = new HashSet<FeeManagementSignup>();
        signups.add(signup);

        session.setSignups(signups);

        // Create manifests:
        for (int i = 0; i < numManifests; i++) {

            FeeManagementManifest manifest = new FeeManagementManifest();

            manifest.setSession(session);
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

            if (session.getManifests() == null) {
                session.setManifests(new HashSet<FeeManagementManifest>());
            }

            session.getManifests().add(manifest);

            // Add linked manifest, linked and implicated transactions:
            if (addLinkedManifests) {

                FeeManagementManifest linkedManifest = new FeeManagementManifest();
                Transaction linkedTransaction = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID, new Date(), IMPLICATED_TRANSACTION_AMOUNT);

                linkedManifest.setSession(session);
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

        persistenceService.persistEntity(session);

        return session;
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
        Transaction implicatedTransaction = transactionService.createTransaction(TRANSACTION_TYPE_ID, ACCOUNT_ID,
                new Date(), transaction.getAmount().negate());

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

    private Rate _createRate(String rateCode, String subCode, String rateCatalogCode) throws Exception {

        String atpId = "20134";

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

        RateCatalog rateCatalog = _createRateCatalog(rateCatalogCode);

        Rate rate = rateService.createRate(rateCode, subCode, rateName, rateCatalogCode, transactionTypeId, dateType,
                rateAmount, limitAmount, minLimitUnits, maxLimitUnits, transactionDate, recognitionDate, atpId, false);

        Assert.notNull(rate);
        Assert.notNull(rate.getId());
        Assert.notNull(rate.getAtpId());
        Assert.notNull(rate.getCode());
        Assert.notNull(rate.getSubCode());
        Assert.notNull(rate.getRateCatalogAtp());

        rateCatalog = rate.getRateCatalogAtp().getRateCatalog();

        Assert.notNull(rateCatalog);
        Assert.notNull(rate.getRateCatalogAtp().getId());
        Assert.isTrue(rateCatalogCode.equals(rateCatalog.getCode()));
        Assert.isTrue(rateCatalogCode.equals(rate.getRateCatalogAtp().getId().getCode()));
        Assert.notNull(rate.getDefaultRateAmount());
        Assert.notNull(rate.getDefaultRateAmount().getAmount());
        Assert.isTrue(rate.getDefaultRateAmount().getAmount().compareTo(rateAmount) == 0);
        Assert.notNull(rate.getKeyPairs());
        Assert.notEmpty(rate.getKeyPairs());
        Assert.notNull(rate.getTransactionDateType());

        if (!rateCatalog.isRecognitionDateDefinable()) {
            Assert.isNull(rate.getRecognitionDate());
        }

        if (rateCatalog.isTransactionDateTypeFinal()) {
            Assert.isTrue(rate.getTransactionDateType() == rateCatalog.getTransactionDateType());
        }

        if (rateCatalog.isTransactionTypeFinal()) {
            Assert.isTrue(rate.getDefaultRateAmount().getTransactionTypeId().equals(rateCatalog.getTransactionTypeId()));
        }

        if (rateCatalog.getKeyPairs() != null && rateCatalog.isKeyPairFinal()) {
            Assert.isTrue(CollectionUtils.isEqualCollection(rate.getKeyPairs(), rateCatalog.getKeyPairs()));
        }

        Assert.isTrue(!rateCatalog.isLimitAmountFinal() || rateCatalog.isLimitAmount() == rate.isLimitAmount());

        return rate;
    }

    private RateType _createRateType(String code, String name, String description) {

        RateType rateType = rateService.createRateType(code, name, description, false, RateAmountType.FLAT);

        Assert.notNull(rateType);
        Assert.notNull(rateType.getId());
        Assert.notNull(rateType.getCode());
        Assert.notNull(rateType.getName());
        Assert.notNull(rateType.getDescription());
        Assert.notNull(rateType.getCreatorId());
        Assert.notNull(rateType.getCreationDate());

        return rateType;
    }

    private RateCatalog _createRateCatalog(String rateCatalogCode) {

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

        _createRateType(rateTypeCode, rateTypeCode + "_name", rateTypeCode + "_desc");

        RateCatalog rateCatalog = rateService.createRateCatalog(rateCatalogCode, rateTypeCode, transactionTypeId,
                dateType, minAmount, maxAmount, minLimitAmount, maxLimitAmount, minLimitUnits, maxLimitUnits,
                Arrays.asList("19871", "19872", "19873", "19874", "20134"), keyPairs, false, false, false, false, false, false);

        Assert.notNull(rateCatalog);
        Assert.notNull(rateCatalog.getId());
        Assert.notNull(rateCatalog.getCode());
        Assert.isTrue(rateCatalog.getCode().equals(rateCatalogCode));
        Assert.notNull(rateCatalog.getMinAmount());
        Assert.notNull(rateCatalog.getMaxAmount());
        Assert.notNull(rateCatalog.getMinLimitAmount());
        Assert.notNull(rateCatalog.getMaxLimitAmount());
        Assert.notNull(rateCatalog.getKeyPairs());
        Assert.notEmpty(rateCatalog.getKeyPairs());
        Assert.notNull(rateCatalog.getTransactionDateType());

        Set<String> atpIdsSet = rateService.getAtpsForRateCatalog(rateCatalog.getId());

        Assert.notNull(atpIdsSet);
        Assert.notEmpty(atpIdsSet);

        return rateCatalog;
    }

}
