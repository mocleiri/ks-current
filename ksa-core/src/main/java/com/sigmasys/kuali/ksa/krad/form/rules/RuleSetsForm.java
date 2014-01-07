package com.sigmasys.kuali.ksa.krad.form.rules;


import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.model.rule.RuleSet;

import java.util.Collections;
import java.util.List;

/**
 * This form contains the rules related entities and properties.
 *
 * @author Michael Ivanov
 */
public class RuleSetsForm extends AbstractRuleViewModel {

    private Long ruleSetId;
    private String ruleSetHeader;

    private RuleSet newRuleSet;

    private List<Rule> rules;

    public RuleSetsForm() {
        newRuleSet = new RuleSet();
        rules = Collections.emptyList();
    }

    public Long getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(Long ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    public String getRuleSetHeader() {
        return ruleSetHeader;
    }

    public void setRuleSetHeader(String ruleSetHeader) {
        this.ruleSetHeader = ruleSetHeader;
    }

    public RuleSet getNewRuleSet() {
        return newRuleSet;
    }

    public void setNewRuleSet(RuleSet newRuleSet) {
        this.newRuleSet = newRuleSet;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

}
