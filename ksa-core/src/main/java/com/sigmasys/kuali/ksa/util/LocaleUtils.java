package com.sigmasys.kuali.ksa.util;


import java.util.*;

/**
 * LocaleUtils contains default system wide locale-aware resources.
 *
 * @author Michael Ivanov
 */
public class LocaleUtils {

    private static final Map<String, String> params = new HashMap<String, String>();


    private LocaleUtils() {
    }

    public static void initLocalizedParameters(Map<String, String> params) {
        LocaleUtils.params.clear();
        LocaleUtils.params.putAll(params);
    }

    public static String getValue(String key) {
        return params.get(key);
    }

}
