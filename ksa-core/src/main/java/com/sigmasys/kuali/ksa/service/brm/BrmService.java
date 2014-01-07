package com.sigmasys.kuali.ksa.service.brm;

import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.model.rule.RuleSet;


/**
 * Business Rules Management (BRM) service.
 *
 * @author Michael Ivanov
 */
public interface BrmService {

    /**
     * Fires all rules from a Rule Set.
     *
     * @param ruleSetId  Rule Set ID
     * @param brmContext BRM context, van be any type
     * @return the modified BRM context
     */
    <T extends BrmContext> T fireRules(Long ruleSetId, T brmContext);

    /**
     * Fires all rules from a Rule Set.
     *
     * @param ruleSetName Rule Set name
     * @param brmContext  BRM context, can be any type
     * @return the modified BRM context
     */
    <T extends BrmContext> T fireRules(String ruleSetName, T brmContext);

    /**
     * Validates a given rule set and throws InvalidRulesException if there are any errors.
     *
     * @param ruleSet Rule Set
     */
    void validateRuleSet(RuleSet ruleSet);

    /**
     * Reloads all rule sets and other BRM resources associated with them.
     */
    void reloadRuleSets();

    /**
     * Reloads rule sets (knowledge base) specified by names
     *
     * @param ruleSetNames Rule Set names
     */
    void reloadRuleSets(String... ruleSetNames);

    /**
     * Attach new or persistent Rule entities to a rule set specified by ID.
     *
     * @param ruleSetName Rule Set name
     * @param rules       array of Rule instances
     * @return RuleSet    Updated RuleSet instance
     */
    RuleSet attachRulesToRuleSet(String ruleSetName, Rule... rules);

}
