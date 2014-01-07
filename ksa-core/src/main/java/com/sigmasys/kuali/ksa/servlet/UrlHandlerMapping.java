package com.sigmasys.kuali.ksa.servlet;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Map;

/**
 * Lazy-init UrlHandlerMapping.
 *
 * @author Michael Ivanov
 */
@Service(DispatcherServlet.HANDLER_MAPPING_BEAN_NAME)
public class UrlHandlerMapping extends SimpleUrlHandlerMapping {


    private String transformUrl(String url) {
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        return url;
    }

    public boolean isRegistered(String url) {
        return getHandlerMap().containsKey(transformUrl(url));
    }


    @Override
    public void registerHandler(String url, Object object) {
        super.registerHandler(transformUrl(url), object);
    }

    @Override
    protected void registerHandlers(Map<String, Object> urlMap) {
        if (urlMap != null && !urlMap.isEmpty()) {
            super.registerHandlers(urlMap);
        }
    }
}
