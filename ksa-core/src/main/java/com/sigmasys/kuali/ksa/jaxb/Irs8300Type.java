package com.sigmasys.kuali.ksa.jaxb;

import java.util.ArrayList;
import java.util.List;

/**
 * IRS 8300 form types.
 *
 * @author Michael Ivanov
 */
public enum Irs8300Type {

    PERSONAL_PROPERTY,
    REAL_PROPERTY,
    PERSONAL_SERVICE,
    BUSINESS_SERVICE,
    INTANGIBLE_PROPERTY,
    DEBT_OBLIGATION,
    EXCHANGE_OF_CASH,
    ESCROW,
    BAIL;

    public static List<String> getNames() {
        Irs8300Type[] values = values();
        List<String> names = new ArrayList<String>(values.length);
        for (Irs8300Type value : values) {
            names.add(value.name());
        }
        return names;
    }

}
