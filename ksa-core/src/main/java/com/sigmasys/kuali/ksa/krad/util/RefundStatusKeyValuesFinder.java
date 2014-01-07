package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.RefundStatus;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * All Key-Value pairs for the "Refund Status" combo-box.
 *
 * User: Sergey
 * Date: 5/18/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefundStatusKeyValuesFinder extends GenericKeyValuesFinder {

    @Override
    protected List<KeyValue> buildKeyValues() {
        // Add all RefundStatus elements with translations:
        List<KeyValue> result = new ArrayList<KeyValue>();

        result.add(new ConcreteKeyValue("", "Show All Status Types"));
        result.add(new ConcreteKeyValue(RefundStatus.UNVERIFIED_CODE, RefundStatus.UNVERIFIED.toString()));
        result.add(new ConcreteKeyValue(RefundStatus.VERIFIED_CODE, RefundStatus.VERIFIED.toString()));
        result.add(new ConcreteKeyValue(RefundStatus.ACTIVE_CODE, RefundStatus.ACTIVE.toString()));
        result.add(new ConcreteKeyValue(RefundStatus.CANCELED_CODE, RefundStatus.CANCELLED.toString()));
        result.add(new ConcreteKeyValue(RefundStatus.FAILED_CODE, RefundStatus.FAILED.toString()));

        return result;
    }

}
