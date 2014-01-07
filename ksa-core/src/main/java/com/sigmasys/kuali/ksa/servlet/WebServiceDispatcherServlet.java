package com.sigmasys.kuali.ksa.servlet;


import com.sigmasys.kuali.ksa.util.ContextUtils;
import com.sun.xml.ws.transport.http.servlet.WSServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;

import javax.servlet.Servlet;
import java.util.Iterator;

/**
 * WebServiceDispatcherServlet. It extends LazyInitDispatcherServlet.
 * JAX-WS handler mapping support is included.
 *
 * @author Michael Ivanov
 */
public class WebServiceDispatcherServlet extends LazyInitDispatcherServlet {

    private static final Log logger = LogFactory.getLog(WebServiceDispatcherServlet.class);


    @Override
    protected void initStrategies(ApplicationContext context) {
        super.initStrategies(context);
        registerJaxWsHandlers();
    }

    private void registerJaxWsHandlers() {
        try {
            Document document = new SAXReader().read(getServletContext().getResourceAsStream("/WEB-INF/sun-jaxws.xml"));
            // Iterating through child elements of root with element name "endpoint"
            for (Iterator i = document.getRootElement().elementIterator("endpoint"); i.hasNext(); ) {
                Element endpoint = (Element) i.next();
                // Retrieving the web service URL
                String endpointUrl = endpoint.attributeValue("url-pattern");
                if (!urlHandlerMapping.isRegistered(endpointUrl)) {
                    ServletWrappingController wrappingController =
                            ContextUtils.getBean(ServletWrappingController.BEAN_NAME);
                    // Wrapping and registering JAX WS Servlet
                    Servlet jaxWsServlet = new WSServlet();
                    wrappingController.setServletInstance(jaxWsServlet);
                    urlHandlerMapping.registerHandler(endpointUrl, wrappingController);
                    logger.debug("registerHandler: Registered JAX WS handler '" + wrappingController + "', " +
                            "jaxWsServlet = '" + jaxWsServlet +
                            "', URL = '" + endpointUrl + "'");
                }
            }
        } catch (Exception e) {
            logger.warn("Cannot load sun-jaxws.xml", e);
        }
    }

}
