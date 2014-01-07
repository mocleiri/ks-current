package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * Date Type constants used by Rate.
 *
 * @author Michael Ivanov
 */
public enum TransactionDateType implements Identifiable {

    ALWAYS,
    UNTIL,
    AFTER;

    @Override
    public String getId() {
        return name();
    }

}
