package com.sigmasys.kuali.ksa.config;

import com.sigmasys.kuali.ksa.model.Constants;
import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.framework.config.module.ModuleConfigurer;
import org.kuali.rice.core.framework.config.module.WebModuleConfiguration;
import org.kuali.rice.core.framework.persistence.ojb.RiceDataSourceConnectionFactory;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.service.impl.ViewServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;

import java.util.Arrays;
import java.util.List;

/**
 * KSA Module Configurer
 *
 * @author Michael Ivanov
 *         Date: 3/22/12
 *         Time: 12:17 AM
 */
public class KsaConfigurer extends ModuleConfigurer implements ApplicationContextAware, SmartApplicationListener {

    private static final String KSA_BEANS_PATH = "classpath:/META-INF/";
    private static final String KSA_BEANS_FILE = KSA_BEANS_PATH + "ksa-beans.xml";

    private List<String> dataDictionaryPackages;

    public KsaConfigurer() {
        super(Constants.MODULE_NAME);
        setValidRunModes(Arrays.asList(RunMode.LOCAL));
    }

    @Override
    protected String getDefaultConfigPackagePath() {
        return KSA_BEANS_PATH;
    }

    @Override
    protected String getDefaultSpringBeansPath(String configPackagePath) {
        return KSA_BEANS_FILE;
    }

    @Override
    public List<String> getPrimarySpringFiles() {
        return Arrays.asList(KSA_BEANS_FILE);
    }

    @Override
    public boolean hasWebInterface() {
        return false;
    }

    @Override
    protected WebModuleConfiguration loadWebModule() {
        WebModuleConfiguration configuration = super.loadWebModule();
        configuration.setWebSpringFiles(Arrays.asList(KSA_BEANS_FILE));
        return configuration;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // Required by Rice OJB
        RiceDataSourceConnectionFactory.addBeanFactory(applicationContext);
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
        return true;
    }

    @Override
    public boolean supportsSourceType(Class<?> aClass) {
        return true;
    }

    @Override
    public int getOrder() {
        // return a lower value which will give the data dictionary indexing higher precedence since DD indexing should
        // be started as soon as it can be
        return -500;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            List<String> locations = getDataDictionaryPackages();
            if (locations != null && !locations.isEmpty()) {
                DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
                try {
                    dataDictionaryService.addDataDictionaryLocations(Constants.MODULE_NAME, locations);
                    ((ViewServiceImpl) KRADServiceLocatorWeb.getViewService()).setDataDictionaryService(dataDictionaryService);
                    //dataDictionaryService.getDataDictionary().parseDataDictionaryConfigurationFiles(false);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot add KSA packages to data dictionary locations: " + e.getMessage(), e);
                }
            }
        }
    }

    public List<String> getDataDictionaryPackages() {
        return dataDictionaryPackages;
    }

    public void setDataDictionaryPackages(List<String> dataDictionaryPackages) {
        this.dataDictionaryPackages = dataDictionaryPackages;
    }
}
