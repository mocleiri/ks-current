package com.sigmasys.kuali.ksa.krad.util;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * String value finder.
 *
 * @author Michael Ivanov
 */
public class StringValueFinder extends KeyValuesBase {

    private List<KeyValue> keyValues = Collections.emptyList();

    public void initValues(List<String> values) {
        keyValues = new ArrayList<KeyValue>(values.size());
        for (String value : values) {
            keyValues.add(new ConcreteKeyValue(value, value));
        }
    }

    /**
     * This is an implementation of a key value finder, normally this would make a request to
     * a database to obtain the necessary values.
     *
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        return Collections.unmodifiableList(keyValues);
    }

}
