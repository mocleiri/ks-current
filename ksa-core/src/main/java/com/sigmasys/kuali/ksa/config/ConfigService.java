package com.sigmasys.kuali.ksa.config;

import com.sigmasys.kuali.ksa.model.ConfigParameter;

import java.util.*;

/**
 * KSA Config Service
 *
 * @author Michael Ivanov
 *         Date: 3/22/12
 *         Time: 4:58 PM
 */
public interface ConfigService {

    /**
     * Returns the localized parameters for the given locale
     *
     * @param locale java.util.Locale instance
     * @return map of locale-aware parameters
     */
    Map<String, String> getLocalizedParameters(Locale locale);

    String getParameter(String name);

    Map<String, String> getParameterMap();

    Integer updateParameters(List<ConfigParameter> params);

    Integer deleteParameters(Set<String> paramNames);

    List<ConfigParameter> getParameters();

    List<ConfigParameter> getLockedParameters();

    List<ConfigParameter> getReadOnlyParameters();

    List<ConfigParameter> reloadParameters();

    void changeLoggingLevel(Map<String, String> loggers);

    Map<String, String> getLoggingLevel(String[] loggers);

}
