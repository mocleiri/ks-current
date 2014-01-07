package com.sigmasys.kuali.ksa.service.impl;


import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.LocalizedString;
import com.sigmasys.kuali.ksa.service.AbstractWebContextAwareService;
import com.sigmasys.kuali.ksa.service.LocalizationService;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.List;

/**
 * Localization WS implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("unchecked")
@Service
@WebService(serviceName = LocalizationService.SERVICE_NAME, portName = LocalizationService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
public class LocalizationWebService extends AbstractWebContextAwareService implements LocalizationService {


    private LocalizationService getLocalizationService() {
        return ContextUtils.getBean("localizationService", LocalizationService.class);
    }

    /**
     * Imports the locale-aware resources specified by java.io.Reader for the given import type.
     *
     * @param content    the content of the resources to be imported
     * @param importType Import type
     * @return a map  of localized strings for the target locale
     */
    @Override
    public List<LocalizedString> importResources(String content, ImportType importType) {
        return getLocalizationService().importResources(content, importType);
    }

    /**
     * Returns all the localized strings for the given locale
     *
     * @param locale the locale string, i.e. "en_US", "fr_FR"
     * @return a map of localized strings
     */
    @Override
    public List<LocalizedString> getLocalizedStrings(String locale) {
        return getLocalizationService().getLocalizedStrings(locale);
    }

}
