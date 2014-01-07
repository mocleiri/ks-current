package com.sigmasys.kuali.ksa.event;

import com.sigmasys.kuali.ksa.model.ConfigParameter;

import java.util.EventObject;
import java.util.List;

/**
 *
 * LoadConfigEvent is used by LoadConfigListener to provide subscribers with the necessary event context.
 *
 * @author Michael Ivanov
 */
public class LoadConfigEvent extends EventObject {

    public LoadConfigEvent(List<ConfigParameter> configParameters) {
        super(configParameters);
    }

    @SuppressWarnings("unchecked")
    public List<ConfigParameter> getConfigParameters() {
        return (List<ConfigParameter>) getSource();
    }
}
