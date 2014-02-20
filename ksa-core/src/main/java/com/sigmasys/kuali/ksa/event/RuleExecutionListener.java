package com.sigmasys.kuali.ksa.event;


/**
 * RuleExecutionListener can be set by KSA services to do necessary actions
 * right after the BRM rule has been executed.
 *
 * @author Michael Ivanov
 */
public abstract class RuleExecutionListener<T extends RuleExecutionEvent> implements EventListener<T> {

    public abstract void onLoad(T event);

    @Override
    public void handleEvent(T event) {
        onLoad(event);
    }
}
