package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.model.rule.RuleSet;
import com.sigmasys.kuali.ksa.model.rule.RuleType;
import com.sigmasys.kuali.ksa.model.rule.RuleTypeEnum;
import com.sigmasys.kuali.ksa.service.brm.BrmContext;
import com.sigmasys.kuali.ksa.service.brm.BrmPersistenceService;
import com.sigmasys.kuali.ksa.util.CommonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@Transactional
public class BrmServiceTest extends AbstractServiceTest {

    @Autowired
    private AccountImportService accountImportService;

    @Autowired
    private BrmPersistenceService brmPersistenceService;

    @Autowired
    private AccountService accountService;


    @Before
    public void setUpWithinTransaction() {
        // set up test data within the transaction
        String userId = "admin";
        accountService.getOrCreateAccount(userId);
    }


    //@Test
    public void fireFeeAssessmentRules() throws Exception {

        // Importing accounts for Fee Management which is rule-based
        String content = CommonUtils.getResourceAsString("xmlImport/fee-management/student-profile-user1.xml");
        accountImportService.importStudentProfile(content);

        BrmContext brmContext = new BrmContext();
        brmContext.setAccount(accountService.getFullAccount("user1"));

        // TODO: implement assertions for the new FM

    }


    @Test
    public void addRules() throws Exception {

        RuleSet ruleSet = brmPersistenceService.getRuleSet("Fee Management");

        Assert.notNull(ruleSet);
        Assert.notEmpty(ruleSet.getRules());
        Assert.isTrue(1L == ruleSet.getId());

        int initialSize = ruleSet.getRules().size();

        List<Rule> rules = new LinkedList<Rule>();

        for (int i = 0; i < 20; i++) {
            Rule rule = new Rule();
            rule.setName("test rule " + i);
            rule.setType(ruleSet.getType());
            rule.setLhs("test lhs " + i);
            rule.setRhs("test rhs " + i);
            rules.add(rule);
        }

        ruleSet = brmPersistenceService.addRulesToRuleSet(1L, rules.toArray(new Rule[rules.size()]));

        Assert.notNull(ruleSet);
        Assert.notEmpty(ruleSet.getRules());
        Assert.isTrue(ruleSet.getRules().size() == (initialSize + 20));

    }

    @Test
    public void removeRules() throws Exception {

        RuleSet ruleSet = brmPersistenceService.getRuleSet("Fee Management");

        Assert.notNull(ruleSet);
        Assert.notEmpty(ruleSet.getRules());
        Assert.isTrue(1L == ruleSet.getId());

        Set<Rule> rules = ruleSet.getRules();
        List<Long> idsToRemove = new ArrayList<Long>(rules.size());
        for (Rule rule : rules) {
            Assert.notNull(rule.getId());
            idsToRemove.add(rule.getId());
        }

        ruleSet = brmPersistenceService.deleteRulesFromRuleSet(1L, idsToRemove.toArray(new Long[idsToRemove.size()]));

        Assert.notNull(ruleSet);
        Assert.isTrue(CollectionUtils.isEmpty(ruleSet.getRules()));

    }

    @Test
    public void persistRule() throws Exception {

        RuleSet ruleSet = brmPersistenceService.getRuleSet(1L);

        Assert.notNull(ruleSet);
        Assert.notEmpty(ruleSet.getRules());
        Assert.isTrue("Fee Management".equals(ruleSet.getName()));

        Rule rule = ruleSet.getRules().iterator().next();

        Assert.notNull(rule);

        rule.setName("Test name 1");
        rule.setPriority(9999);

        Long ruleId = brmPersistenceService.persistRule(rule);

        Assert.notNull(ruleId);
        Assert.isTrue(ruleId.equals(rule.getId()));

        rule = brmPersistenceService.getRule(ruleId);

        Assert.isTrue("Test name 1".equals(rule.getName()));
        Assert.isTrue(9999 == rule.getPriority());
    }


    @Test
    public void getRuleSetNamesByRuleNames() throws Exception {

        String[] ruleNames = {"Rule 1", "Rule 2", "Rule 3", "Payment Application Rule"};

        List<String> ruleSetNames = brmPersistenceService.getRuleSetNamesByRuleNames(ruleNames);

        Assert.notNull(ruleSetNames);
        Assert.notEmpty(ruleSetNames);
        Assert.isTrue(ruleSetNames.size() > 1);

        boolean hasPaymentApplication = false;
        for (String ruleSetName : ruleSetNames) {
            logger.debug("Rule set name = " + ruleSetName);
            Assert.notNull(ruleSetName);
            Assert.isTrue(ruleSetName.trim().length() > 0);
            if ("Payment Application".equals(ruleSetName)) {
                hasPaymentApplication = true;
            }
        }

        Assert.isTrue(hasPaymentApplication);
    }

    @Test
    public void getRuleTypes() throws Exception {

        List<RuleType> ruleTypes = brmPersistenceService.getRuleTypes();

        Assert.notNull(ruleTypes);
        Assert.notEmpty(ruleTypes);
        Assert.isTrue(ruleTypes.size() > 1);

        boolean hasDslr = false;
        for (RuleType ruleType : ruleTypes) {
            logger.debug("Rule type = " + ruleType);
            Assert.notNull(ruleType);
            Assert.notNull(ruleType.getId());
            Assert.notNull(ruleType.getName());
            if (RuleTypeEnum.DSLR.getId().equals(ruleType.getName())) {
                hasDslr = true;
            }
        }

        Assert.isTrue(hasDslr);
    }

    @Test
    public void getRuleNamesByRuleSetName() throws Exception {

        String ruleSetName = "Payment Application";

        List<String> ruleNames = brmPersistenceService.getRuleNames(ruleSetName);

        Assert.notNull(ruleNames);
        Assert.notEmpty(ruleNames);

        boolean hasPaymentApplicationRule = false;
        for (String ruleName : ruleNames) {
            logger.debug("Rule name = " + ruleName);
            Assert.notNull(ruleName);
            Assert.isTrue(ruleName.trim().length() > 0);
            if ("Payment Application Rule".equals(ruleName)) {
                hasPaymentApplicationRule = true;
            }
        }

        Assert.isTrue(hasPaymentApplicationRule);
    }

}
