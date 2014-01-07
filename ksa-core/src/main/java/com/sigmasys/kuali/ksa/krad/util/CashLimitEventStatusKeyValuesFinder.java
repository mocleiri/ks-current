package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.CashLimitEventStatus;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * A KeyValues finder for the "CashLimitEvent Status" filter drop-down box.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 11/10/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class CashLimitEventStatusKeyValuesFinder extends GenericKeyValuesFinder {

    @Override
    protected List<KeyValue> buildKeyValues() {
        // Add all RefundStatus elements with translations:
        List<KeyValue> result = new ArrayList<KeyValue>();

        result.add(new ConcreteKeyValue("", "Show All Status Types"));
        result.add(new ConcreteKeyValue(CashLimitEventStatus.COMPLETED.getId(), CashLimitEventStatus.COMPLETED.toString()));
        result.add(new ConcreteKeyValue(CashLimitEventStatus.IGNORED.getId(), CashLimitEventStatus.IGNORED.toString()));
        result.add(new ConcreteKeyValue(CashLimitEventStatus.QUEUED.getId(), CashLimitEventStatus.QUEUED.toString()));

        return result;
    }
}
