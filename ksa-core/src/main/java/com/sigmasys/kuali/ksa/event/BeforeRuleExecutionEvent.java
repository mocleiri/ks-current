package com.sigmasys.kuali.ksa.event;

import com.sigmasys.kuali.ksa.model.rule.Rule;


/**
 * BeforeRuleExecutionEvent is fired right before the rule execution.
 *
 * @author Michael Ivanov
 */
public class BeforeRuleExecutionEvent extends RuleExecutionEvent {

    public BeforeRuleExecutionEvent(Rule rule) {
        super(rule);
    }
}
