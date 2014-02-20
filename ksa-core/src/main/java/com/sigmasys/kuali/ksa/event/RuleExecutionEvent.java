package com.sigmasys.kuali.ksa.event;

import com.sigmasys.kuali.ksa.model.rule.Rule;

import java.util.EventObject;

/**
 * RuleExecutionEvent is used by RuleExecutionListener to provide subscribers with the necessary event context.
 *
 * @author Michael Ivanov
 */
public abstract class RuleExecutionEvent extends EventObject {

    public RuleExecutionEvent(Rule rule) {
        super(rule);
    }

    @SuppressWarnings("unchecked")
    public Rule getRule() {
        return (Rule) getSource();
    }
}
