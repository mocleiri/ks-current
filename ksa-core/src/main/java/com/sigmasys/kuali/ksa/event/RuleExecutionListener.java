package com.sigmasys.kuali.ksa.event;


/**
 * RuleExecutionListener can be set by KSA services to do necessary actions
 * right after the BRM rule has been executed.
 *
 * @author Michael Ivanov
 */
public abstract class RuleExecutionListener implements EventListener<RuleExecutionEvent> {

    public abstract void onLoad(RuleExecutionEvent event);

    @Override
    public void handleEvent(RuleExecutionEvent event) {
        onLoad(event);
    }
}
