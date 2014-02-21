package com.sigmasys.kuali.ksa.service.brm;

import com.sigmasys.kuali.ksa.model.Pair;
import com.sigmasys.kuali.ksa.model.SortOrder;
import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.model.rule.RuleSet;
import com.sigmasys.kuali.ksa.model.rule.RuleType;
import com.sigmasys.kuali.ksa.service.impl.GenericPersistenceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.*;

/**
 * BRM (Business Rules Management)  persistence service implementation.
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("unchecked")
@Service("brmPersistenceService")
@Transactional(readOnly = true)
public class BrmPersistenceServiceImpl extends GenericPersistenceService implements BrmPersistenceService {

    private static final Log logger = LogFactory.getLog(BrmPersistenceServiceImpl.class);

    /**
     * Persists Drools rule set specified by the given RuleSet instance
     *
     * @param ruleSet RuleSet instance
     * @return RuleSet ID
     */
    @Override
    @Transactional(readOnly = false)
    public Long persistRuleSet(RuleSet ruleSet) {
        return persistEntity(ruleSet);
    }

    /**
     * Persists a rule specified by the given Rule instance
     *
     * @param rule Rule instance
     * @return Rule ID
     */
    @Override
    @Transactional(readOnly = false)
    public Long persistRule(Rule rule) {
        return persistEntity(rule);
    }

    private RuleSet getRuleSet(String property, Object value) {
        Query query = em.createQuery("select rs from RuleSet rs " +
                " left outer join fetch rs.type t " +
                " left outer join fetch rs.rules r " +
                " where rs." + property + " = :value");
        query.setParameter("value", value);
        List<RuleSet> ruleSets = query.getResultList();
        return CollectionUtils.isNotEmpty(ruleSets) ? ruleSets.get(0) : null;
    }

    private Rule getRule(String property, Object value) {
        Query query = em.createQuery("select r from Rule r " +
                " left outer join fetch r.type t " +
                " where r." + property + " = :value");
        query.setParameter("value", value);
        List<Rule> rules = query.getResultList();
        return CollectionUtils.isNotEmpty(rules) ? rules.get(0) : null;
    }

    /**
     * Retrieves a rule set from the persistent store by ID
     *
     * @param id RuleSet identifier
     * @return RuleSet instance
     */
    @Override
    public RuleSet getRuleSet(Long id) {
        return getRuleSet("id", id);
    }

    /**
     * Retrieves a rule set from the persistent store by name
     *
     * @param name RuleSet name
     * @return RuleSet instance
     */
    @Override
    public RuleSet getRuleSet(String name) {
        return getRuleSet("name", name);
    }

    /**
     * Retrieves a rule from the persistent store by ID
     *
     * @param id Rule identifier
     * @return Rule instance
     */
    @Override
    public Rule getRule(Long id) {
        return getRule("id", id);
    }

    /**
     * Retrieves a rule from the persistent store by name
     *
     * @param name Rule name
     * @return Rule instance
     */
    @Override
    public Rule getRule(String name) {
        return getRule("name", name);
    }

    /**
     * Retrieves all existing rule sets from the persistent store.
     *
     * @return RuleSet instance
     */
    @Override
    public List<RuleSet> getRuleSets() {
        Query query = em.createQuery("select rs from RuleSet rs " +
                " left outer join fetch rs.type t " +
                " left outer join fetch rs.rules r " +
                " order by rs.name asc");
        return query.getResultList();
    }

    /**
     * Adds rules to a rule set, throws InvalidRulesException if any rule types don't match.
     *
     * @param ruleSetId Rule Set ID
     * @param rules     array of Rule instances
     * @return the modified rule set
     */
    @Override
    @Transactional(readOnly = false)
    public RuleSet addRulesToRuleSet(Long ruleSetId, Rule... rules) {

        RuleSet ruleSet = getRuleSet(ruleSetId);

        if (ruleSet == null) {
            String errMsg = "Rule Set with ID = " + ruleSetId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Long ruleTypeId = ruleSet.getType().getId();

        if (ruleSet.getRules() == null) {
            ruleSet.setRules(new HashSet<Rule>());
        }

        for (Rule rule : rules) {

            if (!ruleTypeId.equals(rule.getType().getId())) {
                String errMsg = "Rule Set's type '" + ruleSet.getType().getName() +
                        "' does not match Rule's type '" + rule.getType().getName() + "'";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            persistRule(rule);
            ruleSet.getRules().add(rule);
        }

        return ruleSet;
    }

    /**
     * Removes rules from a rule set
     *
     * @param ruleSetId Rule Set ID
     * @param ruleIds   array of Rule IDs to be removed
     * @return the modified rule set
     */
    @Override
    @Transactional(readOnly = false)
    public RuleSet deleteRulesFromRuleSet(Long ruleSetId, Long... ruleIds) {
        return deleteRulesFromRuleSet(ruleSetId, Arrays.asList(ruleIds));
    }

    /**
     * Removes rules from a rule set
     *
     * @param ruleSetId Rule Set ID
     * @param ruleIds   Collection of Rule IDs to be removed
     * @return the modified rule set
     */
    @Override
    @Transactional(readOnly = false)
    public RuleSet deleteRulesFromRuleSet(Long ruleSetId, Collection<Long> ruleIds) {

        RuleSet ruleSet = getRuleSet(ruleSetId);
        if (ruleSet == null) {
            String errMsg = "Rule Set with ID = " + ruleSetId + " does not exist";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Set<Rule> rules = ruleSet.getRules();

        if (rules != null) {

            Set<Rule> rulesToDelete = new HashSet<Rule>();

            for (Rule rule : rules) {
                if (ruleIds.contains(rule.getId())) {
                    rulesToDelete.add(rule);
                }
            }

            rules.removeAll(rulesToDelete);
            ruleSet.setRules(rules);

            persistRuleSet(ruleSet);
        }

        return ruleSet;
    }

    /**
     * Retrieves all existing rule names
     *
     * @return a list of rule names
     */
    @Override
    public List<String> getRuleNames() {
        Query query = em.createQuery("select r.name from Rule r order by r.name asc");
        return query.getResultList();
    }

    /**
     * Retrieves rule names for the specified rule set.
     *
     * @param ruleSetName Rule Set name
     * @return a list of rule names
     */
    @Override
    public List<String> getRuleNames(String ruleSetName) {

        RuleSet ruleSet = getRuleSet("name", ruleSetName);

        if (ruleSet == null) {
            String errMsg = "Cannot find Rule Set with name '" + ruleSetName + "'";
            logger.error(errMsg);
            throw new IllegalArgumentException(ruleSetName);
        }

        Set<Rule> rules = ruleSet.getRules();

        if (rules != null) {

            List<String> ruleNames = new ArrayList<String>(rules.size());

            for (Rule rule : rules) {
                ruleNames.add(rule.getName());
            }

            Collections.sort(ruleNames);

            return ruleNames;
        }

        return Collections.emptyList();
    }

    /**
     * Retrieves rule set names by names of rules contained in these rule sets.
     *
     * @param ruleNames Rule names
     * @return a list of rule set names
     */
    @Override
    public List<String> getRuleSetNamesByRuleNames(String... ruleNames) {
        Query query = em.createQuery("select distinct rs.name from RuleSet rs " +
                " left outer join rs.rules r " +
                " where r.name in (:ruleNames) " +
                " order by rs.name asc");
        query.setParameter("ruleNames", Arrays.asList(ruleNames));
        return query.getResultList();
    }

    /**
     * Retrieves all existing rule set names.
     *
     * @return a list of rule set names
     */
    @Override
    public List<String> getRuleSetNames() {
        Query query = em.createQuery("select rs.name from RuleSet rs order by rs.name asc");
        return query.getResultList();
    }

    /**
     * Retrieves all existing rule types.
     *
     * @return a list of rule types.
     */
    public List<RuleType> getRuleTypes() {
        return getEntities(RuleType.class, new Pair<String, SortOrder>("name", SortOrder.ASC));
    }

    /**
     * Retrieves a rule type from the persistent store by name
     *
     * @return a RuleType instance
     */
    public RuleType getRuleType(String ruleTypeName) {
        Query query = em.createQuery("select rt from RuleType rt where rt.name = :name");
        query.setParameter("name", ruleTypeName);
        List<RuleType> ruleTypes = query.getResultList();
        return CollectionUtils.isNotEmpty(ruleTypes) ? ruleTypes.get(0) : null;
    }


}
