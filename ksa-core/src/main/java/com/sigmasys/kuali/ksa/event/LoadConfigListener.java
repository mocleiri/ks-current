package com.sigmasys.kuali.ksa.event;



/**
 * LoadConfigListener can be set by KSA services to do necessary actions
 * right after the configuration parameters have been loaded from the persistent store and initialized.
 *
 * @author Michael Ivanov
 */
public abstract class LoadConfigListener implements EventListener<LoadConfigEvent> {

    public abstract void onLoad(LoadConfigEvent event);

    @Override
    public void handleEvent(LoadConfigEvent event) {
        onLoad(event);
    }
}
