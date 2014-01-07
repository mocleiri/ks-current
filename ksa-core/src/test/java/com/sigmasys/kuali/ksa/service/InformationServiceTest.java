package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class InformationServiceTest extends AbstractServiceTest {

    @Autowired
    private InformationService informationService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;


    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        accountService.getOrCreateAccount("admin");
        accountService.getOrCreateAccount("user1");
    }

    @Test
    public void getInformations() throws Exception {

        String memoAccessLevelCode = informationService.getDefaultMemoLevel();

        Assert.isTrue(StringUtils.isNotBlank(memoAccessLevelCode));

        informationService.createMemo("admin", "New memo for 1020", memoAccessLevelCode, new Date(), null, null);

        List<Information> informations = informationService.getInformationEntities();

        Assert.notNull(informations);
        Assert.isTrue(!informations.isEmpty());

        for (Information information : informations) {
            Assert.notNull(information);
            Assert.notNull(information.getId());
        }

    }

    @Test
    public void updateMemo() throws Exception {

        Memo memo = new Memo();
        memo.setText("Memo text");
        memo.setCreatorId("admin");
        memo.setCreationDate(new Date());
        memo.setEffectiveDate(new Date());

        final Long id = informationService.persistInformation(memo);

        memo = informationService.getMemo(id);

        Assert.notNull(memo);
        Assert.notNull(memo.getId());
        Assert.isTrue(memo.getId().equals(id));

        memo.setText("Blah Blah Blah");

        informationService.persistInformation(memo);

        memo = informationService.getMemo(id);

        Assert.notNull(memo);
        Assert.isTrue(memo.getText().equals("Blah Blah Blah"));

    }

    @Test
    public void createMemo() throws Exception {

        Memo memo = new Memo();
        memo.setText("Memo text");
        memo.setCreatorId("admin");
        memo.setCreationDate(new Date());
        memo.setEffectiveDate(new Date());

        Long id = informationService.persistInformation(memo);

        Assert.notNull(id);

        memo = informationService.getMemo(id);

        Assert.notNull(memo);
        Assert.isTrue(memo.getId().equals(id));
        Assert.isTrue(memo.getText().equals("Memo text"));

    }

    @Test
    public void deleteMemo() throws Exception {

        Memo memo = new Memo();
        memo.setText("Memo text");
        memo.setCreatorId("admin");
        memo.setCreationDate(new Date());
        memo.setEffectiveDate(new Date());

        Long id = informationService.persistInformation(memo);

        Assert.notNull(id);

        memo = informationService.getMemo(id);

        Assert.notNull(memo);
        Assert.isTrue(memo.getId().equals(id));
        Assert.isTrue(memo.getText().equals("Memo text"));

        boolean isDeleted = informationService.deleteInformation(memo.getId());

        Assert.isTrue(isDeleted);

    }

    @Test
    public void createMemoForTransaction() throws Exception {

        String id = "1020";

        createMemoForTransaction(id);

    }

    private Memo createMemoForTransaction(String transactionId) throws Exception {

        String memoAccessLevelCode = informationService.getDefaultMemoLevel();

        Assert.isTrue(StringUtils.isNotBlank(memoAccessLevelCode));

        Transaction transaction = transactionService.createTransaction(transactionId, "admin", new Date(),
                new BigDecimal(10e8));

        Assert.notNull(transaction);
        Assert.notNull(transaction.getId());

        Memo memo = informationService.createMemo(transaction.getId(), "New memo for " + transactionId,
                memoAccessLevelCode, new Date(), null, null);

        Assert.notNull(memo);
        Assert.notNull(memo.getId());
        Assert.notNull(memo.getAccount());
        Assert.notNull(memo.getTransaction());

        Assert.isTrue("New memo for 1020".equals(memo.getText()));
        Assert.isTrue("admin".equals(memo.getCreatorId()));

        Assert.isTrue(new Date().compareTo(memo.getEffectiveDate()) >= 0);
        Assert.isTrue(new Date().compareTo(memo.getCreationDate()) >= 0);

        Assert.notNull(memo.getAccessLevel());
        Assert.notNull(memo.getAccessLevel().getCode());

        Assert.isTrue(memoAccessLevelCode.equals(memo.getAccessLevel().getCode()));

        Assert.isNull(memo.getExpirationDate());
        Assert.isNull(memo.getPreviousMemo());

        return memo;

    }

    @Test
    public void createAlertForTransaction() throws Exception {

        String id = "1020";

        String memoAccessLevelCode = informationService.getDefaultMemoLevel();

        Assert.isTrue(StringUtils.isNotBlank(memoAccessLevelCode));

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e8));

        Assert.notNull(transaction);
        Assert.notNull(transaction.getId());

        Alert alert = informationService.createAlert(transaction.getId(), "New alert for 1020", memoAccessLevelCode,
                new Date(), null);

        Assert.notNull(alert);
        Assert.notNull(alert.getId());
        Assert.notNull(alert.getAccount());
        Assert.notNull(alert.getTransaction());

        Assert.isTrue("New alert for 1020".equals(alert.getText()));
        Assert.isTrue("admin".equals(alert.getCreatorId()));

        Assert.isTrue(new Date().compareTo(alert.getEffectiveDate()) >= 0);
        Assert.isTrue(new Date().compareTo(alert.getCreationDate()) >= 0);

        Assert.notNull(alert.getAccessLevel());
        Assert.notNull(alert.getAccessLevel().getCode());

        Assert.isTrue(memoAccessLevelCode.equals(alert.getAccessLevel().getCode()));

        Assert.isNull(alert.getExpirationDate());

    }

    @Test
    public void createFlagForTransaction() throws Exception {

        String id = "1020";

        String memoAccessLevelCode = informationService.getDefaultMemoLevel();

        Assert.isTrue(StringUtils.isNotBlank(memoAccessLevelCode));

        Transaction transaction = transactionService.createTransaction(id, "admin", new Date(), new BigDecimal(10e8));

        Assert.notNull(transaction);
        Assert.notNull(transaction.getId());

        Flag flag = informationService.createFlag(transaction.getId(), 1L, memoAccessLevelCode, 1, new Date(), null);

        Assert.notNull(flag);
        Assert.notNull(flag.getId());
        Assert.notNull(flag.getAccount());
        Assert.notNull(flag.getTransaction());

        Assert.isTrue("admin".equals(flag.getCreatorId()));

        Assert.isTrue(new Date().compareTo(flag.getEffectiveDate()) >= 0);
        Assert.isTrue(new Date().compareTo(flag.getCreationDate()) >= 0);

        Assert.notNull(flag.getAccessLevel());

        Assert.isNull(flag.getExpirationDate());
    }

    @Test
    public void getMemosForTransaction() throws Exception {

        String id = "1020";

        Memo memo = createMemoForTransaction(id);

        Assert.notNull(memo.getTransaction());
        Assert.notNull(memo.getTransaction().getId());

        List<Memo> memos = informationService.getMemos(memo.getTransaction().getId());

        Assert.notNull(memos);
        Assert.notEmpty(memos);
        Assert.isTrue(memos.size() == 1);
    }

    @Test
    public void associateWithTransaction() {

        String userId = "admin";

        String memoAccessLevelCode = informationService.getDefaultMemoLevel();

        Assert.isTrue(StringUtils.isNotBlank(memoAccessLevelCode));

        Transaction transaction1 = transactionService.createTransaction("chip", userId, new Date(), new BigDecimal(44.9));

        Assert.notNull(transaction1);
        Assert.notNull(transaction1.getId());

        Transaction transaction2 = transactionService.createTransaction("cash", userId, new Date(), new BigDecimal(0.1));

        Assert.notNull(transaction2);
        Assert.notNull(transaction2.getId());

        Flag flag = informationService.createFlag(transaction1.getId(), 1L, memoAccessLevelCode, 1, new Date(), null);

        Assert.notNull(flag);
        Assert.notNull(flag.getId());
        Assert.notNull(flag.getTransaction());
        Assert.isTrue(flag.getTransaction().equals(transaction1));

        Information info = informationService.associateWithTransaction(flag.getId(), transaction2.getId());

        Assert.notNull(info);
        Assert.notNull(info.getId());
        Assert.isTrue(info.getId().equals(flag.getId()));
        Assert.isTrue(info instanceof Flag);

        Assert.notNull(info.getTransaction());
        Assert.notNull(info.getTransaction().getId());
        Assert.isTrue(info.getTransaction().getId().equals(transaction2.getId()));

    }

    @Test
    public void associateWithAccount() throws Exception {

        String userId2 = "user1";

        Account account2 = accountService.getFullAccount(userId2);

        Assert.notNull(account2);
        Assert.notNull(account2.getId());
        Assert.isTrue(account2.getId().equals(userId2));

        Memo memo = createMemoForTransaction("1020");

        Assert.notNull(memo);
        Assert.notNull(memo.getId());
        Assert.notNull(memo.getAccount());
        //Assert.notNull(memo.getAccountId());

        Information info = informationService.associateWithAccount(memo.getId(), userId2);

        Assert.notNull(info);
        Assert.notNull(info.getId());
        Assert.isTrue(info.getId().equals(memo.getId()));
        Assert.isTrue(info instanceof Memo);

        //Assert.notNull(info.getAccountId());
        Assert.notNull(info.getAccount());
        Assert.notNull(info.getAccount().getId());
        //Assert.isTrue(info.getAccountId().equals(info.getAccount().getId()));

        Assert.isTrue(info.getAccount().getId().equals(userId2));
    }

    @Test
    public void createInformationAccessLevel() {

        String code = "IAL_01";
        String name = "Information_Access_Level 1";
        String description = "IAL_01 Description";

        InformationAccessLevel accessLevel = informationService.createInformationAccessLevel(code, name, description,
                Permission.CREATE_MEMO.name(), Permission.READ_MEMO.name(), Permission.UPDATE_MEMO.name(),
                Permission.DELETE_MEMO.name(), Permission.EXPIRE_MEMO.name());

        Assert.notNull(accessLevel);
        Assert.notNull(accessLevel.getId());
        Assert.notNull(accessLevel.getCode());
        Assert.notNull(accessLevel.getName());
        Assert.notNull(accessLevel.getDescription());

        Assert.notNull(accessLevel.getCreationDate());
        Assert.notNull(accessLevel.getCreatorId());

        Assert.isTrue(Permission.CREATE_MEMO.name().equals(accessLevel.getCreatePermission()));
        Assert.isTrue(Permission.READ_MEMO.name().equals(accessLevel.getReadPermission()));
        Assert.isTrue(Permission.UPDATE_MEMO.name().equals(accessLevel.getUpdatePermission()));
        Assert.isTrue(Permission.DELETE_MEMO.name().equals(accessLevel.getDeletePermission()));
        Assert.isTrue(Permission.EXPIRE_MEMO.name().equals(accessLevel.getExpirePermission()));

    }

    @Test
    public void createMemoAccessLevel() {

        String code = "IAL_01_Memo";
        String name = "Information_Access_Level 1 Memo";
        String description = "IAL_01 Description Memo";

        InformationAccessLevel accessLevel = informationService.createMemoAccessLevel(code, name, description);

        Assert.notNull(accessLevel);
        Assert.notNull(accessLevel.getId());
        Assert.notNull(accessLevel.getCode());
        Assert.notNull(accessLevel.getName());
        Assert.notNull(accessLevel.getDescription());

        Assert.notNull(accessLevel.getCreationDate());
        Assert.notNull(accessLevel.getCreatorId());

        Assert.isTrue(Permission.CREATE_MEMO.name().equals(accessLevel.getCreatePermission()));
        Assert.isTrue(Permission.READ_MEMO.name().equals(accessLevel.getReadPermission()));
        Assert.isTrue(Permission.UPDATE_MEMO.name().equals(accessLevel.getUpdatePermission()));
        Assert.isTrue(Permission.DELETE_MEMO.name().equals(accessLevel.getDeletePermission()));
        Assert.isTrue(Permission.EXPIRE_MEMO.name().equals(accessLevel.getExpirePermission()));

    }

    @Test
    public void createAlertAccessLevel() {

        String code = "IAL_01_Alert";
        String name = "Information_Access_Level 1 Alert";
        String description = "IAL_01 Description Alert";

        InformationAccessLevel accessLevel = informationService.createAlertAccessLevel(code, name, description);

        Assert.notNull(accessLevel);
        Assert.notNull(accessLevel.getId());
        Assert.notNull(accessLevel.getCode());
        Assert.notNull(accessLevel.getName());
        Assert.notNull(accessLevel.getDescription());

        Assert.notNull(accessLevel.getCreationDate());
        Assert.notNull(accessLevel.getCreatorId());

        Assert.isTrue(Permission.CREATE_ALERT.name().equals(accessLevel.getCreatePermission()));
        Assert.isTrue(Permission.READ_ALERT.name().equals(accessLevel.getReadPermission()));
        Assert.isTrue(Permission.UPDATE_ALERT.name().equals(accessLevel.getUpdatePermission()));
        Assert.isTrue(Permission.DELETE_ALERT.name().equals(accessLevel.getDeletePermission()));
        Assert.isTrue(Permission.EXPIRE_ALERT.name().equals(accessLevel.getExpirePermission()));

    }

    @Test
    public void createFlagAccessLevel() {

        String code = "IAL_01_Flag";
        String name = "Information_Access_Level 1 Flag";
        String description = "IAL_01 Description Flag";

        InformationAccessLevel accessLevel = informationService.createFlagAccessLevel(code, name, description);

        Assert.notNull(accessLevel);
        Assert.notNull(accessLevel.getId());
        Assert.notNull(accessLevel.getCode());
        Assert.notNull(accessLevel.getName());
        Assert.notNull(accessLevel.getDescription());

        Assert.notNull(accessLevel.getCreationDate());
        Assert.notNull(accessLevel.getCreatorId());

        Assert.isTrue(Permission.CREATE_FLAG.name().equals(accessLevel.getCreatePermission()));
        Assert.isTrue(Permission.READ_FLAG.name().equals(accessLevel.getReadPermission()));
        Assert.isTrue(Permission.UPDATE_FLAG.name().equals(accessLevel.getUpdatePermission()));
        Assert.isTrue(Permission.DELETE_FLAG.name().equals(accessLevel.getDeletePermission()));
        Assert.isTrue(Permission.EXPIRE_FLAG.name().equals(accessLevel.getExpirePermission()));

    }


    private FlagType createFlagType(String code, String name, String description) {

        String accessLevelCode = "DEF_FLAG_TYPE_LEVEL_CD";

        InformationAccessLevel accessLevel = informationService.getInformationAccessLevel(accessLevelCode);

        Assert.notNull(accessLevel);
        Assert.notNull(accessLevel.getId());
        Assert.notNull(accessLevel.getCode());

        FlagType flagType = informationService.createFlagType(code, name, description, accessLevelCode);

        Assert.notNull(flagType);
        Assert.notNull(flagType.getId());
        Assert.notNull(flagType.getCode());
        Assert.notNull(flagType.getName());
        Assert.notNull(flagType.getDescription());

        Assert.notNull(flagType.getCreationDate());
        Assert.notNull(flagType.getCreatorId());

        Assert.notNull(flagType.getAccessLevel());
        Assert.notNull(flagType.getAccessLevel().getCode());
        Assert.isTrue(flagType.getAccessLevel().getCode().equals(accessLevelCode));

        Assert.isTrue(Permission.CREATE_FLAG_TYPE.name().equals(accessLevel.getCreatePermission()));
        Assert.isTrue(Permission.READ_FLAG_TYPE.name().equals(accessLevel.getReadPermission()));
        Assert.isTrue(Permission.UPDATE_FLAG_TYPE.name().equals(accessLevel.getUpdatePermission()));
        Assert.isTrue(Permission.DELETE_FLAG_TYPE.name().equals(accessLevel.getDeletePermission()));

        return flagType;
    }

    @Test
    public void createFlagType() {

        String code = "01_Flag_Type";
        String name = "1 Flag Type";
        String description = "1 Description FlagType";

        createFlagType(code, name, description);
    }

    @Test
    public void persistFlagType() {

        String code = "02_Flag_Type";
        String name = "2 Flag Type";
        String description = "2 Description FlagType";

        FlagType flagType = createFlagType(code, name, description);

        Assert.notNull(flagType);

        Long flagTypeId = flagType.getId();

        Assert.notNull(flagTypeId);

        flagType.setName("New FlagType Name");

        informationService.persistFlagType(flagType);

        Assert.notNull(flagType.getId());

        flagType = informationService.getFlagType(flagTypeId);

        Assert.notNull(flagType);

        Assert.isTrue(flagTypeId.equals(flagType.getId()));

        Assert.isTrue("New FlagType Name".equals(flagType.getName()));

    }

    @Test
    public void deleteFlagType() {

        String code = "Del_Flag_Type";
        String name = "Del Flag Type";
        String description = "Del Description FlagType";

        FlagType flagType = createFlagType(code, name, description);

        Assert.notNull(flagType);
        Assert.notNull(flagType.getId());

        informationService.deleteFlagType(flagType.getId());

        flagType = informationService.getFlagType(flagType.getId());

        Assert.isNull(flagType);

    }

    @Test
    public void getFlagTypes() {

        String code = "Flag_Type_";
        String name = "Flag Type_";
        String description = "Del Description FlagType ";

        for (int i = 0; i < 10; i++) {

            FlagType flagType = createFlagType(code + i, name + i, description + i);

            Assert.notNull(flagType);
            Assert.notNull(flagType.getId());

        }

        List<FlagType> flagTypes = informationService.getFlagTypes();

        Assert.notNull(flagTypes);
        Assert.notEmpty(flagTypes);
        Assert.isTrue(flagTypes.size() >= 10);

    }


}
