package com.sigmasys.kuali.ksa.service.search;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * AbstractColumnMapper
 *
 * @author Michael Ivanov
 */

public abstract class AbstractColumnMapper {

    // There could be multiple threads trying to concurrently fill out mappings
    private static final Map<Class<?>, Map<String, String>> sortMappings =
            Collections.synchronizedMap(new HashMap<Class<?>, Map<String, String>>());

    private static final Map<Class<?>, Map<String, String>> translationMappings =
            Collections.synchronizedMap(new HashMap<Class<?>, Map<String, String>>());

    protected abstract Map<String, String> getFieldTranslationMapping();

    protected abstract Map<String, String> getFieldSortMapping();

    /**
     * Returns null if column is not mapped.
     */
    public String getQueryColumn(String attributeName) {
        Map<String, String> mapping = sortMappings.get(this.getClass());
        if (mapping == null) {
            mapping = getFieldSortMapping();
            sortMappings.put(this.getClass(), mapping);
        }
        return mapping != null ? mapping.get(attributeName) : null;
    }

    public String getFieldName(String mappedName) {
        Map<String, String> mapping = translationMappings.get(this.getClass());
        if (mapping == null) {
            mapping = getFieldTranslationMapping();
            translationMappings.put(this.getClass(), mapping);
        }
        if (mapping != null) {
            String val = mapping.get(mappedName);
            if (val != null) {
                return val;
            }
        }
        return mappedName;
    }
}