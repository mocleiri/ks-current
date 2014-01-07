package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.KeyPair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
public class KeyPairServiceTest extends AbstractServiceTest {


    @Autowired
    private KeyPairService keyPairService;

    @Autowired
    private AccountService accountService;


    @Test
    public void addKeyPairs() throws Exception {

        Account account = accountService.getOrCreateAccount(TEST_USER_ID);

        KeyPair keyPair1 = new KeyPair("key1", "value1");
        KeyPair keyPair2 = new KeyPair("key2", "value2");
        KeyPair keyPair3 = new KeyPair("key3", "value3");


        account = keyPairService.addKeyPairs(account, keyPair1, keyPair2, keyPair3);

        Assert.notNull(account);
        Assert.notNull(account.getId());
        Assert.notNull(account.getKeyPairs());

        Assert.notEmpty(account.getKeyPairs());
        Assert.isTrue(account.getKeyPairs().size() >= 3);

        boolean keyPair1Exists = false;
        boolean keyPair2Exists = false;
        boolean keyPair3Exists = false;

        for (KeyPair keyPair : account.getKeyPairs()) {

            Assert.notNull(keyPair);
            Assert.notNull(keyPair.getId());
            Assert.notNull(keyPair.getKey());
            Assert.notNull(keyPair.getValue());

            if (keyPair.equals(keyPair1)) {
                keyPair1Exists = true;
            } else if (keyPair.equals(keyPair2)) {
                keyPair2Exists = true;
            } else if (keyPair.equals(keyPair3)) {
                keyPair3Exists = true;
            }
        }

        Assert.isTrue(keyPair1Exists);
        Assert.isTrue(keyPair2Exists);
        Assert.isTrue(keyPair3Exists);

    }

    @Test
    public void updateKeyPairs() throws Exception {

        Account account = accountService.getOrCreateAccount(TEST_USER_ID);

        KeyPair keyPair1 = new KeyPair("key1", "value1");
        KeyPair keyPair2 = new KeyPair("key2", "value2");
        KeyPair keyPair3 = new KeyPair("key3", "value3");


        account = keyPairService.addKeyPairs(account, keyPair1, keyPair2, keyPair3);

        Assert.notNull(account);
        Assert.notNull(account.getId());
        Assert.notNull(account.getKeyPairs());

        Assert.notEmpty(account.getKeyPairs());
        Assert.isTrue(account.getKeyPairs().size() >= 3);

        boolean keyPair1Exists = false;
        boolean keyPair2Exists = false;
        boolean keyPair3Exists = false;

        for (KeyPair keyPair : account.getKeyPairs()) {

            Assert.notNull(keyPair);
            Assert.notNull(keyPair.getId());
            Assert.notNull(keyPair.getKey());
            Assert.notNull(keyPair.getValue());

            if (keyPair.equals(keyPair1)) {
                keyPair1Exists = true;
            } else if (keyPair.equals(keyPair2)) {
                keyPair2Exists = true;
            } else if (keyPair.equals(keyPair3)) {
                keyPair3Exists = true;
            }
        }

        Assert.isTrue(keyPair1Exists);
        Assert.isTrue(keyPair2Exists);
        Assert.isTrue(keyPair3Exists);

        keyPairService.updateKeyPair(account, keyPair1.getId(), "key1_", "value1_");

        account = keyPairService.updateKeyPair(account, keyPair2.getId(), "key2_", "value2_");

        Assert.notNull(account);
        Assert.notNull(account.getId());
        Assert.notNull(account.getKeyPairs());

        Assert.notEmpty(account.getKeyPairs());
        Assert.isTrue(account.getKeyPairs().size() >= 3);

        keyPair1Exists = false;
        keyPair2Exists = false;

        for (KeyPair keyPair : account.getKeyPairs()) {

            Assert.notNull(keyPair);
            Assert.notNull(keyPair.getId());
            Assert.notNull(keyPair.getKey());
            Assert.notNull(keyPair.getValue());

            if (keyPair.getId().equals(keyPair1.getId())) {
                Assert.isTrue("key1_".equals(keyPair.getKey()));
                Assert.isTrue("value1_".equals(keyPair.getValue()));
                keyPair1Exists = true;
            } else if (keyPair.getId().equals(keyPair2.getId())) {
                Assert.isTrue("key2_".equals(keyPair.getKey()));
                Assert.isTrue("value2_".equals(keyPair.getValue()));
                keyPair2Exists = true;
            }

        }

        Assert.isTrue(keyPair1Exists);
        Assert.isTrue(keyPair2Exists);

    }

    @Test
    public void removeKeyPairs() throws Exception {

        Account account = accountService.getOrCreateAccount(TEST_USER_ID);

        KeyPair keyPair1 = new KeyPair("key1", "value1");
        KeyPair keyPair2 = new KeyPair("key2", "value2");
        KeyPair keyPair3 = new KeyPair("key3", "value3");


        account = keyPairService.addKeyPairs(account, keyPair1, keyPair2, keyPair3);

        Assert.notNull(account);
        Assert.notNull(account.getId());
        Assert.notNull(account.getKeyPairs());

        Assert.notEmpty(account.getKeyPairs());
        Assert.isTrue(account.getKeyPairs().size() >= 3);

        boolean keyPair1Exists = false;
        boolean keyPair2Exists = false;
        boolean keyPair3Exists = false;

        for (KeyPair keyPair : account.getKeyPairs()) {

            Assert.notNull(keyPair);
            Assert.notNull(keyPair.getId());
            Assert.notNull(keyPair.getKey());
            Assert.notNull(keyPair.getValue());

            if (keyPair.equals(keyPair1)) {
                keyPair1Exists = true;
            } else if (keyPair.equals(keyPair2)) {
                keyPair2Exists = true;
            } else if (keyPair.equals(keyPair3)) {
                keyPair3Exists = true;
            }
        }

        Assert.isTrue(keyPair1Exists);
        Assert.isTrue(keyPair2Exists);
        Assert.isTrue(keyPair3Exists);

        account = keyPairService.removeKeyPairs(account, keyPair1.getId(), keyPair3.getId());

        Assert.notNull(account);
        Assert.notNull(account.getId());
        Assert.notNull(account.getKeyPairs());

        Assert.notEmpty(account.getKeyPairs());
        Assert.isTrue(account.getKeyPairs().size() >= 1);

        keyPair1Exists = false;
        keyPair2Exists = false;
        keyPair3Exists = false;

        for (KeyPair keyPair : account.getKeyPairs()) {

            Assert.notNull(keyPair);
            Assert.notNull(keyPair.getId());
            Assert.notNull(keyPair.getKey());
            Assert.notNull(keyPair.getValue());

            if (keyPair.equals(keyPair1)) {
                keyPair1Exists = true;
            } else if (keyPair.equals(keyPair2)) {
                keyPair2Exists = true;
            } else if (keyPair.equals(keyPair3)) {
                keyPair3Exists = true;
            }
        }

        Assert.isTrue(!keyPair1Exists);
        Assert.isTrue(keyPair2Exists);
        Assert.isTrue(!keyPair3Exists);

    }


}
