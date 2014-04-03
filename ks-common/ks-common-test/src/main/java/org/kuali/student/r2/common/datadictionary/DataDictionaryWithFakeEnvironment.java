/**
 * Copyright 2014 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package org.kuali.student.r2.common.datadictionary;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.service.impl.DataDictionaryServiceImpl;
import org.kuali.student.common.test.resourceloader.SimpleSpringResourceLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class allows us to parse data dictionary files along with creating a fake environment
 * for {@link GlobalResourceLoader}.
 *
 * @author Kuali Student Team
 */
public class DataDictionaryWithFakeEnvironment extends DataDictionary implements ApplicationContextAware {
    private static final String MOCK_APP_ID = "mock-app.id";
    private static final String APPLICATION_VERSION = "application.version";
    private static final String RICE_VERSION = "rice.version";
    private static final String VERSION_ONE = "1.0";
    // copied from org.kuali.rice.core.impl.datetime.DateTimeServiceImpl
    private static final String STRING_TO_DATE_FORMATS = "MM/dd/yyyy hh:mm a;MM/dd/yy;MM/dd/yyyy;MM-dd-yy;MM-dd-yyyy;MMddyy;MMMM dd;yyyy;MM/dd/yy HH:mm:ss;MM/dd/yyyy HH:mm:ss;MM-dd-yy HH:mm:ss;MMddyy HH:mm:ss;MMMM dd HH:mm:ss;yyyy HH:mm:ss";
    public static final String NAMESPACE_CODE = "http://student.kuali.org/wsdl/datadictionary";
    public static final String ALL_DICTIONARY_FILE_LOCATIONS = "allDictionaryFileLocations";

    private ApplicationContext applicationContext;

    public void init() throws IOException {
        Config config = new JAXBConfigImpl();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, MOCK_APP_ID);
        config.putProperty(APPLICATION_VERSION, VERSION_ONE);
        config.putProperty(RICE_VERSION, VERSION_ONE);
        config.putProperty(CoreConstants.STRING_TO_DATE_FORMATS, STRING_TO_DATE_FORMATS);
        ConfigContext.init(config);

        DataDictionaryServiceImpl dataDictionaryService = new DataDictionaryServiceImpl();
        DataDictionary embeddedDataDictionary = dataDictionaryService.getDataDictionary();

        // UIF files for adding to the embedded DD (behind the scenes)
        List<String> uifDictionaryLocations = Arrays.asList(
                "classpath:org/kuali/rice/krad/uif/UifControlDefinitions.xml",
                "classpath:org/kuali/rice/krad/uif/UifConfigurationDefinitions.xml",
                "classpath:org/kuali/rice/krad/uif/UifWidgetDefinitions.xml",
                "classpath:org/kuali/rice/krad/uif/UifFieldDefinitions.xml",
                "classpath:org/kuali/rice/krad/uif/UifElementDefinitions.xml",
                "classpath:org/kuali/rice/krad/uif/UifHeaderFooterDefinitions.xml",
                "classpath:org/kuali/rice/krad/datadictionary/DataDictionaryBaseTypes.xml",
                "classpath:org/kuali/rice/krad/uif/UifGroupDefinitions.xml",
                "classpath:org/kuali/rice/krad/uif/UifLayoutManagerDefinitions.xml",
                "classpath:org/kuali/rice/krad/uif/UifMenuDefinitions.xml",
                "classpath:org/kuali/rice/krad/uif/UifActionDefinitions.xml");

        for (String location : uifDictionaryLocations) {
            embeddedDataDictionary.addConfigFileLocation(NAMESPACE_CODE, location);
        }

        // all dictionary files for addition to the "real" DD
        @SuppressWarnings("unchecked")
        List<String> allDictionaryFileLocations = (List<String>) applicationContext.getBean(ALL_DICTIONARY_FILE_LOCATIONS);

        for (String location : allDictionaryFileLocations) {
            super.addConfigFileLocation(NAMESPACE_CODE, location);
        }

        ResourceLoader resourceLoader =
                new BaseResourceLoader(
                        new QName(MOCK_APP_ID, RiceConstants.DEFAULT_ROOT_RESOURCE_LOADER_NAME),
                        new SimpleSpringResourceLoader(applicationContext, dataDictionaryService)
                );

        try {
            GlobalResourceLoader.stop();
            GlobalResourceLoader.addResourceLoader(resourceLoader);
            GlobalResourceLoader.start();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing GRL", e);
        }

        embeddedDataDictionary.parseDataDictionaryConfigurationFiles(false);
        super.parseDataDictionaryConfigurationFiles(false);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}