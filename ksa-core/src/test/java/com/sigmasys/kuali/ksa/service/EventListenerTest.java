package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.event.*;
import com.sigmasys.kuali.ksa.model.ConfigParameter;
import com.sigmasys.kuali.ksa.model.Pair;
import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.model.rule.RuleSet;
import com.sigmasys.kuali.ksa.service.brm.BrmPersistenceService;
import com.sigmasys.kuali.ksa.service.security.AccessControlService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class EventListenerTest extends AbstractServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private BrmPersistenceService brmPersistenceService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AccessControlService acService;


    @Before
    public void setUpWithinTransaction() {
        // Set up test data within the transaction
        accountService.getOrCreateAccount(TEST_USER_ID);
    }

    @Test
    public void loadConfigEvent() {

        final List<ConfigParameter> configParameters = configService.getParameters();

        Assert.notNull(configParameters);
        Assert.notEmpty(configParameters);

        EventMulticaster.getInstance().addListener(new LoadConfigListener() {
            @Override
            public void onLoad(LoadConfigEvent event) {

                List<ConfigParameter> parameters = event.getConfigParameters();

                Assert.notNull(parameters);
                Assert.notEmpty(parameters);

                Assert.isTrue(CollectionUtils.isEqualCollection(parameters, configParameters));

                configParameters.removeAll(parameters);

                Assert.isTrue(configParameters.size() == 0);

                logger.info("LoadConfigEvent has been processed with the parameters: " + parameters);
            }
        });

        // Firing BeforeRuleExecutionEvent event
        EventMulticaster.getInstance().fireEvent(new LoadConfigEvent(configParameters));

        // Checking if the event has been processed
        Assert.isTrue(configParameters.size() == 0);
    }

    @Test
    public void loadAccessControlEvent() {

        final Set<String> userPermissions = acService.getUserPermissions(TEST_USER_ID);

        Assert.notNull(userPermissions);
        Assert.notEmpty(userPermissions);

        EventMulticaster.getInstance().addListener(new LoadAccessControlListener() {
            @Override
            public void onLoad(LoadAccessControlEvent event) {

                Assert.notNull(event.getSource());
                Assert.notNull(event.getAccessControlService());

                Set<String> permissions = event.getAccessControlService().getUserPermissions(TEST_USER_ID);

                Assert.notNull(permissions);
                Assert.notEmpty(permissions);

                Assert.isTrue(CollectionUtils.isEqualCollection(permissions, userPermissions));

                userPermissions.removeAll(permissions);

                Assert.isTrue(userPermissions.size() == 0);

                logger.info("LoadAccessControlEvent has been processed with the permissions: " + permissions);
            }
        });

        // Firing LoadAccessControlEvent event
        EventMulticaster.getInstance().fireEvent(new LoadAccessControlEvent(acService));

        // Checking if the event has been processed
        Assert.isTrue(userPermissions.size() == 0);
    }


    @Test
    public void beforeRuleExecutionEvent() {

        final Rule rule = createRule("rule12345", "rule12345 desc", "12345", "12345");

        Assert.notNull(rule);
        Assert.notNull(rule.getId());

        final Pair<Rule, Rule> rules = new Pair<Rule, Rule>(rule, null);

        EventMulticaster.getInstance().addListener(new RuleExecutionListener<BeforeRuleExecutionEvent>() {
            @Override
            public void onLoad(BeforeRuleExecutionEvent event) {

                Rule eventRule = event.getRule();

                Assert.notNull(eventRule);
                Assert.notNull(eventRule.getId());
                Assert.isTrue(eventRule.getId().equals(rule.getId()));

                rules.setB(rule);

                logger.info("BeforeRuleExecutionEvent has been processed with the rule: " + rule);
            }
        });

        // Firing BeforeRuleExecutionEvent event
        EventMulticaster.getInstance().fireEvent(new BeforeRuleExecutionEvent(rule));

        Assert.notNull(rules.getA());
        Assert.notNull(rules.getB());

        Assert.isTrue(rules.getA() == rules.getB());
    }

    @Test
    public void afterRuleExecutionEvent() {

        final Rule rule = createRule("_rule12345", "_rule12345 desc", "_12345", "_12345");

        Assert.notNull(rule);
        Assert.notNull(rule.getId());

        final Pair<Rule, Rule> rules = new Pair<Rule, Rule>(rule, null);

        EventMulticaster.getInstance().addListener(new RuleExecutionListener<AfterRuleExecutionEvent>() {
            @Override
            public void onLoad(AfterRuleExecutionEvent event) {

                Rule eventRule = event.getRule();

                Assert.notNull(eventRule);
                Assert.notNull(eventRule.getId());
                Assert.isTrue(eventRule.getId().equals(rule.getId()));

                rules.setB(rule);

                logger.info("AfterRuleExecutionEvent has been processed with the rule: " + rule);
            }
        });

        // Firing AfterRuleExecutionEvent event
        EventMulticaster.getInstance().fireEvent(new AfterRuleExecutionEvent(rule));

        Assert.notNull(rules.getA());
        Assert.notNull(rules.getB());

        Assert.isTrue(rules.getA() == rules.getB());
    }

    private Rule createRule(String name, String description, String lhs, String rhs) {

        RuleSet ruleSet = brmPersistenceService.getRuleSet("Payment Bouncing");

        Assert.notNull(ruleSet);

        Rule rule = new Rule();
        rule.setName(name);
        rule.setDescription(description);
        rule.setLhs(lhs);
        rule.setRhs(rhs);
        rule.setType(ruleSet.getType());

        brmPersistenceService.persistRule(rule);

        return rule;
    }

}