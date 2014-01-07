package com.sigmasys.kuali.ksa.servlet;

import com.sigmasys.kuali.ksa.annotation.UrlMapping;
import com.sigmasys.kuali.ksa.util.CommonUtils;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * LazyInitDispatcherServlet.
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("unchecked")
public class LazyInitDispatcherServlet extends DispatcherServlet {

    private static final Log logger = LogFactory.getLog(LazyInitDispatcherServlet.class);

    // Map of <Service URL, Bean Name> for lazy instantiation
    private static final Map<String, String> urlHandlers = new Hashtable<String, String>();


    protected UrlHandlerMapping urlHandlerMapping;


    @Override
    protected void initStrategies(ApplicationContext context) {

        super.initStrategies(context);

        urlHandlerMapping = context.getBean(DispatcherServlet.HANDLER_MAPPING_BEAN_NAME, UrlHandlerMapping.class);

        // Registering bean names with their URLs retrieved from @UrlMapping within the given context
        for (String beanName : context.getBeanDefinitionNames()) {
            UrlMapping urlMapping = context.findAnnotationOnBean(beanName, UrlMapping.class);
            if (urlMapping != null) {
                String serviceUrl = urlMapping.value();
                urlHandlers.put(serviceUrl, beanName);
                logger.debug("Added new URL handler, bean name = '" + beanName + "', URL = '" + serviceUrl + "'");
            }
        }

        initHandlerAdapters();
    }

    private synchronized void registerHandler(HttpServletRequest request) {

        final String requestedUri = request.getRequestURI();
        logger.debug("registerHandler: requested URI = " + requestedUri);

        for (Map.Entry<String, String> entry : urlHandlers.entrySet()) {

            String url = entry.getKey();

            // If URL does not have "/" consider it a path relative to GWT module base URL
            if (!url.contains("/")) {
                url = CommonUtils.getModuleBaseUrl(request) + url;
            }

            if (requestedUri.contains(url) && !urlHandlerMapping.isRegistered(url)) {
                Object bean = ContextUtils.getBean(entry.getValue());
                // If the bean is a servlet - wrap it with ServletWrappingController
                if (bean instanceof Servlet) {
                    ServletWrappingController wrappingController =
                            ContextUtils.getBean(ServletWrappingController.BEAN_NAME);
                    wrappingController.setServletInstance((Servlet) bean);
                    urlHandlerMapping.registerHandler(url, wrappingController);
                    // If the bean is a Spring MVC controller just register it
                } else {
                    urlHandlerMapping.registerHandler(url, bean);
                }
                logger.debug("registerHandler: Registered URL handler '" + bean + "', bean name = '" + entry.getValue() +
                        "', URL = '" + url + "'");
            }
        }
    }

    private void initHandlerAdapters() {
        try {
            Field field = DispatcherServlet.class.getDeclaredField("handlerAdapters");
            field.setAccessible(true);
            List<HandlerAdapter> handlerAdapters = (List<HandlerAdapter>) field.get(this);
            if (handlerAdapters == null) {
                handlerAdapters = new LinkedList<HandlerAdapter>();
            }
            handlerAdapters.add(new SimpleServletHandlerAdapter());
            handlerAdapters.add(new SimpleControllerHandlerAdapter());
            field.set(this, handlerAdapters);
        } catch (Exception e) {
            logger.error(e);
        }
    }


    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        registerHandler(request);
        super.doDispatch(request, response);
    }

}

