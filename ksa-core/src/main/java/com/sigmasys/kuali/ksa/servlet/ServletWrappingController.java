package com.sigmasys.kuali.ksa.servlet;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Properties;


/**
 * Spring Controller implementation that mimics standard ServletWrappingController behaviour
 * (see its documentation), but with the important difference that it doesn't instantiate the
 * Servlet instance directly but delegate for this the BeanContext, so that we can also use IoC.
 *
 * @author Michael Ivanov
 */
@Service(ServletWrappingController.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ServletWrappingController extends AbstractController implements BeanNameAware, DisposableBean {

    public static final String BEAN_NAME = "wrappingController";

    private String servletName;
    private String beanName;
    private Servlet servletInstance;
    private String viewName;
    private Properties initParameters = new Properties();

    public ServletWrappingController() {
        super();
    }

    public ServletWrappingController(Servlet servletInstance) {
        this.servletInstance = servletInstance;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public void setInitParameters(Properties initParameters) {
        this.initParameters = initParameters;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void setServletInstance(Servlet servletInstance) {
        this.servletInstance = servletInstance;
        init();
    }

    public Servlet getServletInstance() {
        return servletInstance;
    }

    private void init() {
        if (getServletInstance() == null) {
            throw new IllegalArgumentException("servletInstance is required");
        }
        if (!Servlet.class.isAssignableFrom(getServletInstance().getClass())) {
            throw new IllegalArgumentException("servletInstance needs to implement interface [javax.servlet.Servlet]");
        }
        if (this.servletName == null) {
            this.servletName = this.beanName;
        }
        try {
            getServletInstance().init(new DelegatingServletConfig());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (METHOD_GET.equals(request.getMethod()) && viewName != null) {
            return new ModelAndView(viewName);
        } else {
            getServletInstance().service(request, response);
        }
        return null;
    }

    public void destroy() {
        getServletInstance().destroy();
    }

    class DelegatingServletConfig implements ServletConfig {

        public String getServletName() {
            return servletName;
        }

        public ServletContext getServletContext() {
            return getWebApplicationContext().getServletContext();
        }

        public String getInitParameter(String paramName) {
            return initParameters.getProperty(paramName);
        }

        public Enumeration<Object> getInitParameterNames() {
            return initParameters.keys();
        }
    }

}
