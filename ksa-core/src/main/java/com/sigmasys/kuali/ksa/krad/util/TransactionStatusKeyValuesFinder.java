package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.TransactionStatus;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * A KeyValues finder for the "Transaction Status" filter drop-down box.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 11/5/13
 * Time: 1:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionStatusKeyValuesFinder extends GenericKeyValuesFinder {

    @Override
    protected List<KeyValue> buildKeyValues() {
        // Add all RefundStatus elements with translations:
        List<KeyValue> result = new ArrayList<KeyValue>();

        result.add(new ConcreteKeyValue("", "Show All Status Types"));
        result.add(new ConcreteKeyValue(TransactionStatus.ACTIVE.getId(), TransactionStatus.ACTIVE.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.BOUNCING.getId(), TransactionStatus.BOUNCING.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.REFUNDING.getId(), TransactionStatus.REFUNDING.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.REFUND_REQUESTED.getId(), TransactionStatus.REFUND_REQUESTED.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.EXPIRED.getId(), TransactionStatus.EXPIRED.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.CANCELLING.getId(), TransactionStatus.CANCELLING.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.WRITING_OFF.getId(), TransactionStatus.WRITING_OFF.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.REVERSING.getId(), TransactionStatus.REVERSING.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.DISCOUNTING.getId(), TransactionStatus.DISCOUNTING.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.TRANSFERRING.getId(), TransactionStatus.TRANSFERRING.toString()));
        result.add(new ConcreteKeyValue(TransactionStatus.RECIPROCAL_OFFSET.getId(), TransactionStatus.RECIPROCAL_OFFSET.toString()));

        return result;
    }
}
