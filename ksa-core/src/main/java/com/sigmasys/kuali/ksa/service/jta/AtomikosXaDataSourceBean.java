package com.sigmasys.kuali.ksa.service.jta;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.sigmasys.kuali.ksa.util.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;

import java.util.*;

/**
 * AtomikosXaDataSourceBean with default DB connection parameters.
 *
 * @author Michael Ivanov
 */
public class AtomikosXaDataSourceBean extends AtomikosDataSourceBean implements InitializingBean {

    private static final Log logger = LogFactory.getLog(AtomikosXaDataSourceBean.class);


    private Properties defaultXaProperties;

    public Properties getDefaultXaProperties() {
        return defaultXaProperties;
    }

    public void setDefaultXaProperties(Properties defaultXaProperties) {
        this.defaultXaProperties = defaultXaProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        logger.debug("Default XA properties: " + defaultXaProperties);

        Properties xaProperties = getXaProperties();

        if (defaultXaProperties != null && xaProperties != null && xaProperties.size() > 0) {

            for (Map.Entry<Object, Object> entry : xaProperties.entrySet()) {

                String name = (String) entry.getKey();
                String value = (String) entry.getValue();

                if (value != null) {

                    if (value.startsWith(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX) &&
                            value.endsWith(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX)) {

                        String defaultName = value.substring(
                                PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX.length(),
                                value.indexOf(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX));

                        String defaultValue = defaultXaProperties.getProperty(defaultName);

                        logger.debug("Default property name = " + defaultName + ", value = " + defaultValue);

                        if (defaultValue != null) {
                            xaProperties.setProperty(name, defaultValue);
                        }
                    }
                }
            }
        }

        logger.debug("Final XA properties: " + xaProperties);
    }

    @Override
    public void setUniqueResourceName(String resourceName) {
        super.setUniqueResourceName(CommonUtils.nvl(resourceName) + new Random(System.currentTimeMillis()).nextInt());
    }
}
