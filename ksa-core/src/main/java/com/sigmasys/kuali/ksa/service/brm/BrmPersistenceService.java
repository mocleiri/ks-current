package com.sigmasys.kuali.ksa.service.brm;

import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.model.rule.RuleSet;
import com.sigmasys.kuali.ksa.model.rule.RuleType;

import java.util.Collection;
import java.util.List;

/**
 * BRM (Business Rules Management) persistence service.
 *
 * @author Michael Ivanov
 */
public interface BrmPersistenceService {

    /**
     * Persists a rule set (knowledge base) specified by the given RuleSet instance
     *
     * @param ruleSet RuleSet instance
     * @return RuleSet ID
     */
    Long persistRuleSet(RuleSet ruleSet);

    /**
     * Persists a rule specified by the given Rule instance
     *
     * @param rule Rule instance
     * @return Rule ID
     */
    Long persistRule(Rule rule);

    /**
     * Retrieves a rule set (knowledge base) from the persistent store by ID
     *
     * @param id RuleSet ID
     * @return RuleSet instance
     */
    RuleSet getRuleSet(Long id);

    /**
     * Retrieves a rule set (knowledge base) from the persistent store by name
     *
     * @param name RuleSet name
     * @return RuleSet instance
     */
    RuleSet getRuleSet(String name);

    /**
     * Retrieves all existing rule sets (knowledge bases) from the persistent store.
     *
     * @return RuleSet instance
     */
    List<RuleSet> getRuleSets();

    /**
     * Retrieves a rule from the persistent store by ID
     *
     * @param id Rule identifier
     * @return Rule instance
     */
    Rule getRule(Long id);

    /**
     * Retrieves a rule from the persistent store by name
     *
     * @param name Rule name
     * @return Rule instance
     */
    Rule getRule(String name);

    /**
     * Adds rules to a rule set, throws IllegalsRulesException if any rule is invalid.
     *
     * @param ruleSetId Rule Set ID
     * @param rules     array of Rule instances
     * @return the modified rule set
     */
    RuleSet addRulesToRuleSet(Long ruleSetId, Rule... rules);

    /**
     * Removes rules from a rule set
     *
     * @param ruleSetId Rule Set ID
     * @param ruleIds   array of Rule IDs to be removed
     * @return the modified rule set
     */
    RuleSet deleteRulesFromRuleSet(Long ruleSetId, Long... ruleIds);

    /**
     * Removes rules from a rule set
     *
     * @param ruleSetId Rule Set ID
     * @param ruleIds   Collection of Rule IDs to be removed
     * @return the modified rule set
     */
    RuleSet deleteRulesFromRuleSet(Long ruleSetId, Collection<Long> ruleIds);

    /**
     * Retrieves all existing rule names
     *
     * @return a list of rule names
     */
    List<String> getRuleNames();

    /**
     * Retrieves rule names for the specified rule set.
     *
     * @param ruleSetName Rule Set name
     * @return a list of rule names
     */
    List<String> getRuleNames(String ruleSetName);

    /**
     * Retrieves all existing rule set names.
     *
     * @return a list of rule set names
     */
    List<String> getRuleSetNames();

    /**
     * Retrieves rule set names by names of rules contained in these rule sets.
     *
     * @param ruleNames Rule names
     * @return a list of rule set names
     */
    List<String> getRuleSetNamesByRuleNames(String... ruleNames);

    /**
     * Retrieves all existing rule types.
     *
     * @return a list of rule types.
     */
    List<RuleType> getRuleTypes();

    /**
     * Retrieves a rule type from the persistent store by name
     *
     * @return a RuleType instance
     */
    RuleType getRuleType(String ruleTypeName);

}
