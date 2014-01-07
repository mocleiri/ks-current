package com.sigmasys.kuali.ksa.service;


import com.sigmasys.kuali.ksa.config.KsaConfigurer;
import com.sigmasys.kuali.ksa.model.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.service.impl.ViewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {ServiceTestSuite.TEST_KSA_CONTEXT})
@SuppressWarnings("unchecked")
public class KradViewIntegrityTest extends AbstractServiceTest {

    public static final String SETTINGS_VIEW_NAME = "SettingsView.xml";

    @Autowired
    private KsaConfigurer ksaConfigurer;

    @Test
    public void parseViews() throws Exception {

        List<String> locations = ksaConfigurer.getDataDictionaryPackages();

        Assert.notNull(locations);
        Assert.notEmpty(locations);

        DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();

        Assert.notNull(dataDictionaryService);

        dataDictionaryService.addDataDictionaryLocations(Constants.MODULE_NAME, locations);

        ViewServiceImpl viewService = (ViewServiceImpl) KRADServiceLocatorWeb.getViewService();

        Assert.notNull(viewService);

        viewService.setDataDictionaryService(dataDictionaryService);

        DataDictionary dataDictionary = dataDictionaryService.getDataDictionary();

        Assert.notNull(dataDictionary);

        List<String> configFileLocations = dataDictionary.getModuleDictionaryFiles().get(Constants.MODULE_NAME);

        Assert.notNull(configFileLocations);
        Assert.notEmpty(configFileLocations);

        logger.info("Config File Locations: \n" + configFileLocations);

        boolean viewExists = false;
        for (String location : configFileLocations) {
            if (location.contains(SETTINGS_VIEW_NAME)) {
                viewExists = true;
                break;
            }
        }

        if (!viewExists) {
            throw new AssertionError("KSA KRAD views have not been loaded");
        }

        dataDictionary.parseDataDictionaryConfigurationFiles(false);
    }

}