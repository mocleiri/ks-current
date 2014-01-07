package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class AuditableEntityServiceTest extends AbstractServiceTest {

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private AccountService accountService;


    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);
    }

    @Test
    public void createRollups() {

        Rollup rollup1 = auditableEntityService.createAuditableEntity("Code111", "Name 111", "Description 111", Rollup.class);
        Rollup rollup2 = auditableEntityService.createAuditableEntity("Code222", "Name 222", "Description 222", Rollup.class);
        Rollup rollup3 = auditableEntityService.createAuditableEntity("Code333", null, "Description 333", Rollup.class);

        for (Rollup rollup : Arrays.asList(rollup1, rollup2, rollup3)) {

            Assert.notNull(rollup);
            Assert.notNull(rollup.getId());
            Assert.notNull(rollup.getCode());
            Assert.notNull(rollup.getName());
            Assert.notNull(rollup.getDescription());
            Assert.notNull(rollup.getCreationDate());
            Assert.notNull(rollup.getCreatorId());

            Assert.isTrue(rollup.getCode().startsWith("Code"));
            Assert.isTrue(rollup.getDescription().startsWith("Description"));
        }

    }

    @Test
    public void updateRollup() throws Exception {

        Rollup rollup = auditableEntityService.getAuditableEntity(1L, Rollup.class);

        Assert.notNull(rollup);
        Assert.notNull(rollup.getCode());

        rollup.setDescription("Test rollup description");

        auditableEntityService.persistAuditableEntity(rollup);

        rollup = auditableEntityService.getAuditableEntity(1L, Rollup.class);

        Assert.notNull(rollup);
        Assert.isTrue(rollup.getDescription().equals("Test rollup description"));

    }

    @Test
    public void getRollups() throws Exception {

        createRollups();

        List<Rollup> rollups = auditableEntityService.getAuditableEntities(Rollup.class);

        Assert.notNull(rollups);
        Assert.notEmpty(rollups);
        Assert.isTrue(rollups.size() >= 3);

        Long currentId = null;
        for (Rollup rollup : rollups) {

            Assert.notNull(rollup);
            Assert.notNull(rollup.getId());
            Assert.notNull(rollup.getCode());
            Assert.notNull(rollup.getDescription());
            Assert.notNull(rollup.getCreationDate());
            Assert.notNull(rollup.getCreatorId());

            if (currentId != null && currentId.compareTo(rollup.getId()) < 0) {
                throw new AssertionError("Rollups must be sorted by IDs in descending order");
            }

            currentId = rollup.getId();
        }

    }

    @Test
    public void getCurrencies1() throws Exception {

        List<Currency> currencies = auditableEntityService.getAuditableEntities(Currency.class);

        Assert.notNull(currencies);
        Assert.notEmpty(currencies);

        Long currentId = null;
        for (Currency currency : currencies) {

            Assert.notNull(currency);
            Assert.notNull(currency.getId());
            Assert.notNull(currency.getCode());
            Assert.notNull(currency.getDescription());
            Assert.notNull(currency.getCreationDate());
            Assert.notNull(currency.getCreatorId());

            if (currentId != null && currentId.compareTo(currency.getId()) < 0) {
                throw new AssertionError("Currencies must be sorted by IDs in descending order");
            }

            currentId = currency.getId();

        }

    }

    @Test
    public void getCurrencies2() throws Exception {

        List<Currency> currencies = auditableEntityService.getCurrencies();

        Assert.notNull(currencies);
        Assert.notEmpty(currencies);

        String currentCode = null;
        for (Currency currency : currencies) {

            Assert.notNull(currency);
            Assert.notNull(currency.getId());
            Assert.notNull(currency.getCode());
            Assert.notNull(currency.getDescription());
            Assert.notNull(currency.getCreationDate());
            Assert.notNull(currency.getCreatorId());

            if (currentCode != null && currentCode.compareTo(currency.getCode()) > 0) {
                throw new AssertionError("Currencies must be sorted by ISO codes in ascending order");
            }

            currentCode = currency.getCode();
        }

    }

    @Test
    public void getCurrency() throws Exception {

        Currency currency = auditableEntityService.getCurrency("usd");

        Assert.notNull(currency);
        Assert.isTrue(currency.getCode().equalsIgnoreCase("usd"));

        currency = auditableEntityService.getAuditableEntity(1L, Currency.class);

        Assert.notNull(currency);
        Assert.isTrue(currency.getCode().equalsIgnoreCase("usd"));

    }

    @Test
    public void updateCurrency() throws Exception {

        Currency currency = auditableEntityService.getCurrency("usd");

        Assert.notNull(currency);
        Assert.isTrue(currency.getCode().equalsIgnoreCase("usd"));

        currency.setDescription("Test description");

        auditableEntityService.persistAuditableEntity(currency);

        currency = auditableEntityService.getAuditableEntity(1L, Currency.class);

        Assert.notNull(currency);
        Assert.isTrue(currency.getDescription().equals("Test description"));

    }

    @Test
    public void createCurrency() throws Exception {

        Currency currency = new Currency();
        currency.setCode("mavr");
        currency.setName("Mavrody");
        currency.setDescription("Mavrody's currency");

        Long id = auditableEntityService.persistAuditableEntity(currency);

        Assert.notNull(id);
        Assert.isTrue(id > 0);

    }

    @Test
    public void searchByWildcard() throws Exception {

        String searchString = "Num";

        List<TaxType> types = auditableEntityService.getAuditableEntitiesByNamePattern(searchString, TaxType.class);

        Assert.notNull(types);
        Assert.notEmpty(types);

        searchString = "cash";

        List<Tag> tags = auditableEntityService.getAuditableEntitiesByNamePattern(searchString, Tag.class);

        Assert.notNull(types);
        Assert.notEmpty(tags);

    }

}
