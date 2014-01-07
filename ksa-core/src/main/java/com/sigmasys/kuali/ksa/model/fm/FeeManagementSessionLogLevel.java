package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * FeeManagementSessionLogLevel :)
 *
 * @author Michael Ivanov
 */
public enum FeeManagementSessionLogLevel implements Identifiable {

    INFO,
    WARN,
    ERROR;

    @Override
    public String getId() {
        return name();
    }

}
