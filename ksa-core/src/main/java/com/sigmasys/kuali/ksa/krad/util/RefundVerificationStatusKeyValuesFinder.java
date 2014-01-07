package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.RefundStatus;
import com.sigmasys.kuali.ksa.model.RefundType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * A values finder for the Refund status adjustment dialog.
 * User: Sergey
 * Date: 10/31/13
 * Time: 11:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefundVerificationStatusKeyValuesFinder extends GenericKeyValuesFinder{


    @Override
    protected List<KeyValue> buildKeyValues() {
        List<KeyValue> result = new ArrayList<KeyValue>();

        result.add(new ConcreteKeyValue(RefundStatus.VERIFIED_CODE, "Verified"));
        result.add(new ConcreteKeyValue(RefundStatus.CANCELED_CODE, "Canceled"));

        return result;
    }}
