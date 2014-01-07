package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.tp.*;
import com.sigmasys.kuali.ksa.service.tp.ThirdPartyTransferService;
import org.junit.Before;
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

/**
 * ThirdPartyTransferService tests.
 *
 * @author Michael Ivanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class ThirdPartyTransferServiceTest extends AbstractServiceTest {

    protected static final String THIRD_PARTY_ACCOUNT_ID = "third_party_account";


    @PersistenceContext(unitName = Constants.KSA_PERSISTENCE_UNIT)
    protected EntityManager em;

    @Autowired
    private ThirdPartyTransferService thirdPartyTransferService;

    @Autowired
    private TransactionTransferService transferService;

    @Autowired
    private GeneralLedgerService glService;

    @Autowired
    private AccountService accountService;


    private ThirdPartyAccount thirdPartyAccount;
    private Account adminAccount;


    @Before
    public void setUpWithinTransaction() {

        // Setting up test data within the transaction
        String userId = "admin";
        adminAccount = accountService.getOrCreateAccount(userId);

        thirdPartyAccount = createThirdPartyAccount();

        Assert.notNull(thirdPartyAccount);
        Assert.notNull(thirdPartyAccount.getId());
    }

    protected ThirdPartyAccount createThirdPartyAccount() {

        // TODO: replace it with a call to createThirdPartyAccount() method of AccountService when it is ready

        OrgName orgName = new OrgName();
        orgName.setName("Org 1");
        orgName.setCreatorId("admin");
        orgName.setLastUpdate(new Date());
        orgName.setContact(adminAccount.getDefaultPersonName());

        em.persist(orgName);

        ThirdPartyAccount account = new ThirdPartyAccount();
        account.setId(THIRD_PARTY_ACCOUNT_ID);
        account.setAbleToAuthenticate(true);
        account.setCreationDate(new Date());
        account.setCreatorId("admin");
        account.setOrgName(orgName);
        account.setCreditLimit(new BigDecimal(10e6));

        em.persist(account);

        return account;
    }

    protected GeneralLedgerType createGeneralLedgerType() {

        String GL_ACCOUNT_ID = "01-0-131120 1326";

        GeneralLedgerType glType = glService.createGeneralLedgerType("GL_TYPE1", "Test GL type1",
                "Test GL Description 1", GL_ACCOUNT_ID, GlOperationType.CREDIT);

        Assert.notNull(glType);
        Assert.notNull(glType.getId());
        Assert.notNull(glType.getCode());
        Assert.notNull(glType.getName());
        Assert.notNull(glType.getDescription());
        Assert.notNull(glType.getGlAccountId());
        Assert.notNull(glType.getGlOperationOnCharge());
        Assert.notNull(glType.getCreationDate());
        Assert.notNull(glType.getCreatorId());

        Assert.isTrue(GlOperationType.CREDIT == glType.getGlOperationOnCharge());

        return glType;
    }


    protected ThirdPartyPlan _createThirdPartyPlan(String planCode) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);

        Date openPeriodStartDate = dateFormat.parse("01/01/2011");
        Date openPeriodEndDate = dateFormat.parse("01/01/2020");

        Date chargePeriodStartDate = dateFormat.parse("01/01/2011");
        Date chargePeriodEndDate = dateFormat.parse("01/01/2020");


        GeneralLedgerType glType = createGeneralLedgerType();

        TransferType transferType = transferService.createTransferType(glType.getId(), planCode + "_TT_1", "TT 1", "Transfer Type 1");

        Assert.notNull(transferType);
        Assert.notNull(transferType.getId());

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

        Assert.notNull(plan);
        Assert.notNull(plan.getId());
        Assert.notNull(plan.getThirdPartyAccount());
        Assert.notNull(plan.getTransferType());

        Assert.isTrue(plan.getThirdPartyAccount().getId().equals(THIRD_PARTY_ACCOUNT_ID));

        ThirdPartyPlanMember planMember =
                thirdPartyTransferService.createThirdPartyPlanMember(TEST_USER_ID, plan.getId(), 99);

        Assert.notNull(planMember);
        Assert.notNull(planMember.getId());

        return plan;
    }

    @Test
    public void createThirdPartyPlan() throws Exception {
        _createThirdPartyPlan("TPP code 1");
    }

    @Test
    public void generateThirdPartyTransfer() throws Exception {

        ThirdPartyPlan plan = _createThirdPartyPlan("Plan_1");

        ThirdPartyTransferDetail transfer =
                thirdPartyTransferService.generateThirdPartyTransfer(plan.getId(), TEST_USER_ID, new Date());

        Assert.notNull(transfer);
        Assert.notNull(transfer.getId());
        Assert.notNull(transfer.getAccountId());

        Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.ACTIVE);

        ThirdPartyPlanMember member = thirdPartyTransferService.getThirdPartyPlanMember(plan.getId(), transfer.getAccountId());

        Assert.notNull(member);
        Assert.notNull(member.getId());
        Assert.notNull(member.getAccountId());
        Assert.notNull(member.getPlan());
        Assert.notNull(member.getDirectChargeAccount());

        Assert.isTrue(member.isExecuted());
    }

    @Test
    public void generateThirdPartyTransfers1() throws Exception {

        _createThirdPartyPlan("Plan@!~'@^1");

        List<ThirdPartyTransferDetail> transfers =
                thirdPartyTransferService.generateThirdPartyTransfers(TEST_USER_ID, new Date(), true);

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        for (ThirdPartyTransferDetail transfer : transfers) {

            Assert.notNull(transfer);
            Assert.notNull(transfer.getId());

            Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.ACTIVE);
        }

    }

    @Test
    public void generateThirdPartyTransfers2() throws Exception {

        ThirdPartyPlan plan = _createThirdPartyPlan("Plan_33456.90");

        List<ThirdPartyTransferDetail> transfers =
                thirdPartyTransferService.generateThirdPartyTransfers(plan.getId(), true);

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        for (ThirdPartyTransferDetail transfer : transfers) {

            Assert.notNull(transfer);
            Assert.notNull(transfer.getId());

            Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.ACTIVE);
        }

    }

    @Test
    public void generateThirdPartyTransfers3() throws Exception {

        ThirdPartyPlan plan = _createThirdPartyPlan("Plan_45");

        List<ThirdPartyTransferDetail> transfers =
                thirdPartyTransferService.generateThirdPartyTransfers(plan.getId(), false);

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        transfers =
                thirdPartyTransferService.generateThirdPartyTransfers(plan.getId(), true);

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        for (ThirdPartyTransferDetail transfer : transfers) {

            Assert.notNull(transfer);
            Assert.notNull(transfer.getId());

            Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.ACTIVE);
        }

    }

    @Test
    public void generateThirdPartyTransfers4() throws Exception {

        _createThirdPartyPlan("Plan_11");

        List<ThirdPartyTransferDetail> transfers =
                thirdPartyTransferService.generateThirdPartyTransfers(TEST_USER_ID);

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        for (ThirdPartyTransferDetail transfer : transfers) {

            Assert.notNull(transfer);
            Assert.notNull(transfer.getId());

            Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.ACTIVE);
        }

    }

    @Test
    public void reverseThirdPartyTransfers() throws Exception {

        ThirdPartyPlan plan = _createThirdPartyPlan("Plan_-23");

        ThirdPartyTransferDetail transfer =
                thirdPartyTransferService.generateThirdPartyTransfer(plan.getId(), TEST_USER_ID, new Date());

        Assert.notNull(transfer);
        Assert.notNull(transfer.getId());

        transfer = thirdPartyTransferService.reverseThirdPartyTransfer(transfer.getId(), "Reversed Memo");

        Assert.notNull(transfer);
        Assert.notNull(transfer.getId());
        Assert.notNull(transfer.getAccountId());
        Assert.notNull(transfer.getChargeStatus());

        Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.REVERSED);

        ThirdPartyPlanMember member = thirdPartyTransferService.getThirdPartyPlanMember(plan.getId(), transfer.getAccountId());

        Assert.notNull(member);
        Assert.notNull(member.getId());
        Assert.notNull(member.getAccountId());
        Assert.notNull(member.getPlan());
        Assert.notNull(member.getDirectChargeAccount());

        Assert.isTrue(!member.isExecuted());

    }

    @Test
    public void createThirdPartyAllowableCharge() throws Exception {

        ThirdPartyPlan plan = _createThirdPartyPlan("Plan_1");

        ThirdPartyAllowableCharge allowableCharge =
                thirdPartyTransferService.createThirdPartyAllowableCharge(
                        plan.getId(),
                        ".*",
                        new BigDecimal(390.01),
                        new BigDecimal(55.5),
                        11,
                        ChargeDistributionPlan.DIVIDED);


        Assert.notNull(allowableCharge);
        Assert.notNull(allowableCharge.getId());
        Assert.notNull(allowableCharge.getDistributionPlan());

        Assert.isTrue(allowableCharge.getDistributionPlan() == ChargeDistributionPlan.DIVIDED);
        Assert.isTrue(".*".equals(allowableCharge.getTransactionTypeMask()));
        Assert.isTrue(allowableCharge.getPriority() == 11);
        Assert.isTrue(new BigDecimal(390.01).compareTo(allowableCharge.getMaxAmount()) == 0);
        Assert.isTrue(new BigDecimal(55.5).compareTo(allowableCharge.getMaxPercentage()) == 0);

    }

    @Test
    public void getThirdPartyPlanByNamePattern() throws Exception {

        _createThirdPartyPlan("Plan_@12");

        List<ThirdPartyPlan> plans = thirdPartyTransferService.getThirdPartyPlanByNamePattern("name");

        Assert.notNull(plans);
        Assert.notEmpty(plans);

    }

    @Test
    public void getThirdPartyPlans() throws Exception {

        _createThirdPartyPlan("Plan1");
        _createThirdPartyPlan("Plan2");

        Set<String> accountIds = new HashSet<String>(Arrays.asList(thirdPartyAccount.getId(), "user1"));

        List<ThirdPartyPlan> plans = thirdPartyTransferService.getThirdPartyPlans(accountIds);

        Assert.notNull(plans);
        Assert.notEmpty(plans);
        Assert.isTrue(plans.size() >= 2);

        plans = thirdPartyTransferService.getThirdPartyPlans();

        Assert.notNull(plans);
        Assert.notEmpty(plans);
        Assert.isTrue(plans.size() >= 2);

    }

    @Test
    public void getThirdPartyPlansByMember() throws Exception {

        _createThirdPartyPlan("Plan3");
        _createThirdPartyPlan("Plan4");

        List<ThirdPartyPlan> plans = thirdPartyTransferService.getThirdPartyPlansByMember(TEST_USER_ID);

        Assert.notNull(plans);
        Assert.notEmpty(plans);
        Assert.isTrue(plans.size() >= 2);

        boolean plan3Exists = false;
        boolean plan4Exists = false;

        for (ThirdPartyPlan plan : plans) {

            Assert.notNull(plan);
            Assert.notNull(plan.getId());

            if ("Plan3".equals(plan.getCode())) {
                plan3Exists = true;
            }

            if ("Plan4".equals(plan.getCode())) {
                plan4Exists = true;
            }
        }

        Assert.isTrue(plan3Exists && plan4Exists);
    }

    @Test
    public void getThirdPartyTransfers() throws Exception {

        ThirdPartyPlan plan = _createThirdPartyPlan("Plan_!!99");

        List<ThirdPartyTransferDetail> transfers =
                thirdPartyTransferService.generateThirdPartyTransfers(plan.getId(), false);

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        for (ThirdPartyTransferDetail transfer : transfers) {

            Assert.notNull(transfer);
            Assert.notNull(transfer.getId());

            Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.ACTIVE);
        }

        transfers = thirdPartyTransferService.getThirdPartyTransferDetails(TEST_USER_ID);

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        for (ThirdPartyTransferDetail transfer : transfers) {

            Assert.notNull(transfer);
            Assert.notNull(transfer.getId());

            Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.ACTIVE);
        }

        ThirdPartyTransferDetail reversedTransfer =
                thirdPartyTransferService.reverseThirdPartyTransfer(transfers.get(0).getId(), "Reversed Memo");

        Assert.notNull(reversedTransfer);
        Assert.notNull(reversedTransfer.getId());
        Assert.notNull(reversedTransfer.getAccountId());
        Assert.notNull(reversedTransfer.getChargeStatus());

        Assert.isTrue(reversedTransfer.getChargeStatus() == ThirdPartyChargeStatus.REVERSED);


        transfers = thirdPartyTransferService.getThirdPartyTransferDetails(TEST_USER_ID);

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        boolean reversedTransferExists = false;

        for (ThirdPartyTransferDetail transfer : transfers) {

            Assert.notNull(transfer);
            Assert.notNull(transfer.getId());

            if (reversedTransfer.getId().equals(transfer.getId())) {

                Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.REVERSED);

                reversedTransferExists = true;
            }
        }

        Assert.isTrue(reversedTransferExists);

    }

    @Test
    public void getThirdPartyTransfersByPlanId() throws Exception {

        ThirdPartyPlan plan = _createThirdPartyPlan("Plan_$$33");

        List<ThirdPartyTransferDetail> transfers =
                thirdPartyTransferService.generateThirdPartyTransfers(plan.getId(), false);

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        for (ThirdPartyTransferDetail transfer : transfers) {

            Assert.notNull(transfer);
            Assert.notNull(transfer.getId());

            Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.ACTIVE);
        }

        transfers = thirdPartyTransferService.getThirdPartyTransfersByPlanId(plan.getId());

        Assert.notNull(transfers);
        Assert.notEmpty(transfers);

        for (ThirdPartyTransferDetail transfer : transfers) {

            Assert.notNull(transfer);
            Assert.notNull(transfer.getId());

            Assert.isTrue(transfer.getChargeStatus() == ThirdPartyChargeStatus.ACTIVE);
        }

    }

    @Test
    public void deleteThirdPartyPlanMember() throws Exception {

        ThirdPartyPlan plan = _createThirdPartyPlan("Plan_$$888");

        ThirdPartyPlanMember planMember = thirdPartyTransferService.getThirdPartyPlanMember(plan.getId(), TEST_USER_ID);

        Assert.notNull(planMember);
        Assert.notNull(planMember.getId());

        boolean isDeleted = thirdPartyTransferService.deleteThirdPartyPlanMember(plan.getId(), TEST_USER_ID);

        Assert.isTrue(isDeleted);

        planMember = thirdPartyTransferService.getThirdPartyPlanMember(plan.getId(), TEST_USER_ID);

        Assert.isNull(planMember);
    }


}
