package com.sigmasys.kuali.ksa.event;

import com.sigmasys.kuali.ksa.service.security.AccessControlService;

import java.util.EventObject;

/**
 * LoadAccessControlEvent is used by LoadAccessControlListener to provide subscribers with the necessary event context.
 *
 * @author Michael Ivanov
 */
public class LoadAccessControlEvent extends EventObject {

    public LoadAccessControlEvent(AccessControlService acService) {
        super(acService);
    }

    @SuppressWarnings("unchecked")
    public AccessControlService getAccessControlService() {
        return (AccessControlService) getSource();
    }
}
