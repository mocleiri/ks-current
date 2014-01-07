package com.sigmasys.kuali.ksa.servlet;

import com.sigmasys.kuali.ksa.util.RequestUtils;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * RequestContextListener
 *
 * @author Michael Ivanov
 */
public class RequestContextListener implements ServletRequestListener {

    public void requestDestroyed(ServletRequestEvent event) {
        RequestUtils.setThreadRequest(null);
    }

    public void requestInitialized(ServletRequestEvent event) {
        RequestUtils.setThreadRequest((HttpServletRequest) event.getServletRequest());
    }
}
