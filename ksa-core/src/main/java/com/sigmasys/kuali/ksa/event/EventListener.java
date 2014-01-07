package com.sigmasys.kuali.ksa.event;


import java.util.EventObject;

/**
 * LoadConfigListener can be set by KSA services to do necessary actions
 * right after the configuration parameters have been loaded from the persistent store and initialized.
 *
 * @author Michael Ivanov
 */
public interface EventListener<T extends EventObject> extends java.util.EventListener {

    void handleEvent(T event);

}
