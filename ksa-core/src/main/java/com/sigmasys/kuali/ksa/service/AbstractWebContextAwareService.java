package com.sigmasys.kuali.ksa.service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 * Abstract JAX WS service class that provides access to the underlying web service context.
 * <p/>
 *
 * @author Michael Ivanov
 */
public abstract class AbstractWebContextAwareService {

    private WebServiceContext webServiceContext;

    @Resource
    protected void setWebServiceContext(WebServiceContext wsContext) {
        this.webServiceContext = wsContext;
    }

    protected HttpServletRequest getRequest() {
        MessageContext msgContext = webServiceContext.getMessageContext();
        return (HttpServletRequest) msgContext.get(MessageContext.SERVLET_REQUEST);
    }

    protected HttpServletResponse getResponse() {
        MessageContext msgContext = webServiceContext.getMessageContext();
        return (HttpServletResponse) msgContext.get(MessageContext.SERVLET_RESPONSE);
    }

    protected ServletContext getContext() {
        MessageContext msgContext = webServiceContext.getMessageContext();
        return (ServletContext) msgContext.get(MessageContext.SERVLET_CONTEXT);
    }

    protected HttpSession getSession() {
        HttpServletRequest request = getRequest();
        return (request != null) ? request.getSession(false) : null;
    }

}
