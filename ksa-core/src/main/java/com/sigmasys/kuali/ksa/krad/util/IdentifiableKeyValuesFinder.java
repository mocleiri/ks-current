package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.Identifiable;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class IdentifiableKeyValuesFinder<T extends Identifiable> extends GenericKeyValuesFinder {

    private Class<T> type;

    public IdentifiableKeyValuesFinder(Class<T> type) {
        this(type, true);
    }

    public IdentifiableKeyValuesFinder(Class<T> type, boolean blankOption) {
        this.type = type;
        this.setBlankOption(blankOption);
    }

    @Override
    protected List<KeyValue> buildKeyValues() {

        // Get all entities of the generic type:
        T[] entities = type.getEnumConstants();

        List<KeyValue> result = new ArrayList<KeyValue>(entities.length);

        // Iterate through entities and add Key Value pairs:
        for (T entity : entities) {

            String key = entity.getId().toString();
            String value = entity.toString();

            ConcreteKeyValue keyValue = new ConcreteKeyValue(key, value);

            result.add(keyValue);
        }

        return result;
    }

}
