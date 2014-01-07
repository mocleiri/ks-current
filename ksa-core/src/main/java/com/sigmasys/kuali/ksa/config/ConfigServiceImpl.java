package com.sigmasys.kuali.ksa.config;

import com.sigmasys.kuali.ksa.event.EventMulticaster;
import com.sigmasys.kuali.ksa.event.LoadConfigEvent;
import com.sigmasys.kuali.ksa.model.ConfigParameter;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.LocalizedString;
import com.sigmasys.kuali.ksa.service.LocalizationService;
import com.sigmasys.kuali.ksa.util.LocaleUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * KSA Config Service implementation
 *
 * @author Michael Ivanov
 */
@Service("configService")
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ConfigServiceImpl implements ConfigService, InitializingBean {

    @Autowired
    @Qualifier("initialParameterConfigurer")
    private InitialParameterConfigurer parameterConfigurer;

    @Autowired
    @Qualifier("localizationService")
    private LocalizationService localizationService;


    @Override
    public void afterPropertiesSet() {
        initLocalizedParameters();
        // After loading all config parameters we have to fire LoadConfigEvent
        EventMulticaster.getInstance().fireEvent(new LoadConfigEvent(getParameters()));
    }

    private void initLocalizedParameters() {
        // Setting up locale if "locale" initial parameters exist
        String localeLang = getParameter(Constants.LOCALE_LANG);
        if (localeLang != null && !localeLang.trim().isEmpty()) {
            String localeCountry = getParameter(Constants.LOCALE_COUNTRY);
            Locale locale = (localeCountry != null && !localeCountry.trim().isEmpty()) ?
                    new Locale(localeLang, localeCountry) : new Locale(localeLang);
            Locale.setDefault(locale);
            // Loading locale aware resources
            LocaleUtils.initLocalizedParameters(getLocalizedParameters(locale));
        }
    }

    @Override
    public Map<String, String> getLocalizedParameters(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale cannot be null");
        }
        List<LocalizedString> localizedStrings =
                localizationService.getLocalizedStrings(locale.toString());
        if (localizedStrings != null) {
            Map<String, String> localizedParameters = new HashMap<String, String>(localizedStrings.size());
            for (LocalizedString string : localizedStrings) {
                localizedParameters.put(string.getId().getId(), string.getValue());
            }
            return localizedParameters;
        }
        return new HashMap<String, String>();
    }

    @Override
    public String getParameter(String name) {
        return getParameterMap().get(name);
    }

    @Override
    public Map<String, String> getParameterMap() {
        return parameterConfigurer.getParameterMap();
    }

    @Override
    @Transactional(readOnly = false)
    public Integer updateParameters(List<ConfigParameter> params) {
        return parameterConfigurer.updateInitialParameters(params);
    }

    @Override
    @Transactional(readOnly = false)
    public Integer deleteParameters(Set<String> paramNames) {
        return parameterConfigurer.deleteInitialParameters(paramNames);
    }

    @Override
    public List<ConfigParameter> getParameters() {
        return parameterConfigurer.getParameters();
    }

    @Override
    public List<ConfigParameter> getLockedParameters() {
        List<ConfigParameter> lockedParameters = new LinkedList<ConfigParameter>();
        for (ConfigParameter parameter : getParameters()) {
            if (parameter.isLocked()) {
                lockedParameters.add(parameter);
            }
        }
        return lockedParameters;
    }

    @Override
    public List<ConfigParameter> getReadOnlyParameters() {
        List<ConfigParameter> readOnlyParameters = new LinkedList<ConfigParameter>();
        for (ConfigParameter parameter : getParameters()) {
            if (parameter.isReadOnly()) {
                readOnlyParameters.add(parameter);
            }
        }
        return readOnlyParameters;
    }

    @Override
    public List<ConfigParameter> reloadParameters() {
        parameterConfigurer.loadDatabaseParameters();
        List<ConfigParameter> updatedParameters = getParameters();
        // After re-loading all config parameters we have to fire LoadConfigEvent
        EventMulticaster.getInstance().fireEvent(new LoadConfigEvent(updatedParameters));
        return updatedParameters;
    }

    private Logger getLogger(String loggerName) {
        Logger logger = ("ROOT".equals(loggerName)) ? LogManager.getRootLogger() : LogManager.getLogger(loggerName);
        if (logger != null) {
            return logger;
        }
        throw new RuntimeException("Can not find logger '" + loggerName + "'");
    }

    @Override
    public void changeLoggingLevel(Map<String, String> loggers) {
        for (Map.Entry<String, String> entry : loggers.entrySet()) {
            String loggerName = entry.getKey();
            String loggerLevel = entry.getValue();
            getLogger(loggerName).setLevel(Level.toLevel(loggerLevel));
        }
    }

    @Override
    public Map<String, String> getLoggingLevel(String[] loggers) {
        Map<String, String> loggerMap = new HashMap<String, String>(loggers.length);
        for (String loggerName : loggers) {
            Logger logger = getLogger(loggerName);
            loggerMap.put(loggerName, logger.getLevel() != null ? logger.getLevel().toString() : null);
        }
        return loggerMap;
    }

}
