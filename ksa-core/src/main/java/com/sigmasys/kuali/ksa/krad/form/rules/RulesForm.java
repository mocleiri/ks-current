package com.sigmasys.kuali.ksa.krad.form.rules;


import com.sigmasys.kuali.ksa.model.rule.Rule;

/**
 * This form contains the rules related entities and properties.
 *
 * @author Michael Ivanov
 */
public class RulesForm extends AbstractRuleViewModel {

    private Long ruleId;
    private String ruleName;
    private String rulePriority;
    private String ruleLhs;
    private String ruleRhs;

    private Rule newRule;

    public RulesForm() {
        newRule = new Rule();
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRulePriority() {
        return rulePriority;
    }

    public void setRulePriority(String rulePriority) {
        this.rulePriority = rulePriority;
    }

    public String getRuleLhs() {
        return ruleLhs;
    }

    public void setRuleLhs(String ruleLhs) {
        this.ruleLhs = ruleLhs;
    }

    public String getRuleRhs() {
        return ruleRhs;
    }

    public void setRuleRhs(String ruleRhs) {
        this.ruleRhs = ruleRhs;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }


    public Rule getNewRule() {
        return newRule;
    }

    public void setNewRule(Rule newRule) {
        this.newRule = newRule;
    }

    public String getNewRuleType() {
        return newRuleType;
    }

    public void setNewRuleType(String newRuleType) {
        this.newRuleType = newRuleType;
    }
}
