package com.sigmasys.kuali.ksa.event;



/**
 * LoadAccessControlListener can be used by KSA services to handle LoadAccessControlEvent
 * right after the access control attributes have been loaded from the persistent store and initialized.
 *
 * @author Michael Ivanov
 */
public abstract class LoadAccessControlListener implements EventListener<LoadAccessControlEvent> {

    public abstract void onLoad(LoadAccessControlEvent event);

    @Override
    public void handleEvent(LoadAccessControlEvent event) {
        onLoad(event);
    }
}
