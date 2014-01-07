package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.tp.ChargeDistributionPlan;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents values for the "Date range" combo-box on the Refund page.
 * User: Sergey
 * Date: 5/21/13
 * Time: 10:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DistributionPlanKeyValuesFinder extends GenericKeyValuesFinder {


    @Override
    protected List<KeyValue> buildKeyValues() {
        List<KeyValue> result = new ArrayList<KeyValue>();

        result.add(new ConcreteKeyValue(ChargeDistributionPlan.FULL_CODE, ChargeDistributionPlan.FULL.toString()));
        result.add(new ConcreteKeyValue(ChargeDistributionPlan.DIVIDED_CODE, ChargeDistributionPlan.DIVIDED.toString()));

        return result;
    }
}
