package com.sigmasys.kuali.ksa.event;

import com.sigmasys.kuali.ksa.model.rule.Rule;


/**
 * AfterRuleExecutionEvent is fired right after the rule execution.
 *
 * @author Michael Ivanov
 */
public class AfterRuleExecutionEvent extends RuleExecutionEvent {

    public AfterRuleExecutionEvent(Rule rule) {
        super(rule);
    }
}
