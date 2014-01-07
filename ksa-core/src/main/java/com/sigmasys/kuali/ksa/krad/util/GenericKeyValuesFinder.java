package com.sigmasys.kuali.ksa.krad.util;

import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public abstract class GenericKeyValuesFinder extends KeyValuesBase {

    private boolean blankOption;
    private List<KeyValue> keyValues;


    @Override
    public synchronized List<KeyValue> getKeyValues() {
        if (keyValues == null) {
            keyValues = buildKeyValues();
            if (keyValues == null) {
                keyValues = new LinkedList<KeyValue>();
            }
            if (isBlankOption()) {
                keyValues.add(0, new ConcreteKeyValue("", ""));
            }
        }
        return keyValues;
    }

    /**
     * @return the blankOption
     */
    public boolean isBlankOption() {
        return this.blankOption;
    }

    /**
     * @param blankOption the blankOption to set
     */
    public void setBlankOption(boolean blankOption) {
        this.blankOption = blankOption;
    }

    /**
     * Builds KeyValues specific to the sub-class WITHOUT a blank option.
     *
     * @return A List of KeyValue objects.
     */
    protected abstract List<KeyValue> buildKeyValues();
}
