package com.sigmasys.kuali.ksa.krad.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sigmasys.kuali.ksa.util.CalendarUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This key values finder supplies value for the Report Years list
 * to generate 1098T report for.
 *
 * @author Sergey
 */
public class Generate1098TReportYearKeyValuesFinder extends KeyValuesBase {

    @Override
    public List<KeyValue> getKeyValues() {

        int currentYear = CalendarUtils.getYear(new Date());

        // Create a new list of report years:
        List<KeyValue> result = new ArrayList<KeyValue>(5);

        for (int i = 0; i < result.size(); i++) {
            String year = Integer.toString(currentYear - i);
            result.add(new ConcreteKeyValue(year, year));
        }

        return result;
    }

}
