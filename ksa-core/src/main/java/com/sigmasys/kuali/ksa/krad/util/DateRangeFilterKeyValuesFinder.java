package com.sigmasys.kuali.ksa.krad.util;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents values for the "Date range" combo-box on the Transaction filter section.
 *
 * User: Sergey
 * Date: 5/21/13
 * Time: 10:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateRangeFilterKeyValuesFinder extends GenericKeyValuesFinder {

    /**
     * Key for All dates.
     */
    public static final String ALL = "ALL";

    /**
     * Key for a specific date range.
     */
    public static final String RANGE = "RANGE";


    @Override
    protected List<KeyValue> buildKeyValues() {
        List<KeyValue> result = new ArrayList<KeyValue>();

        result.add(new ConcreteKeyValue(ALL, "Show All Transactions"));
        result.add(new ConcreteKeyValue(RANGE, "Custom Time Period"));

        return result;
    }
}
