package com.sigmasys.kuali.ksa.servlet;

import com.sigmasys.kuali.ksa.util.ContextUtils;
import com.sigmasys.kuali.ksa.util.RequestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Must be used instead Spring version to initialize ContextUtils with web application context
 *
 * @author Michael Ivanov
 */
public class WebappContextLoaderListener extends ContextLoaderListener {

    private static final Log logger = LogFactory.getLog(WebappContextLoaderListener.class);

    /**
     * Override to find and store initialized web application context in global static ContextUtils
     */
    public void contextInitialized(ServletContextEvent event) {
        logger.info("Initializing WebApplicationContext...");
        super.contextInitialized(event);
        RequestUtils.setServletContext(event.getServletContext());
        ServletContext sc = event.getServletContext();
        logger.debug("Servlet context path = " + sc.getContextPath());
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        ContextUtils.initContext(applicationContext);
        logger.info("WebApplicationContext is initialized");
    }

}
