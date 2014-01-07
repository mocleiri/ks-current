package com.sigmasys.kuali.ksa.service.jaxws;

import com.sigmasys.kuali.ksa.annotation.Url;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.jaxws.JaxWsPortClientInterceptor;

import javax.jws.WebService;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.logging.Logger;

/**
 * A custom JaxWsPortClientInterceptor implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
public class JaxWsProxyFactoryBean extends JaxWsPortClientInterceptor implements FactoryBean<Object> {

    private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Object serviceProxy;
    private String baseServiceUrl;
    private String serviceUrl;


    @Override
    public void afterPropertiesSet() {

        super.afterPropertiesSet();

        try {

            // Retrieving service URL from @Url annotation
            Url urlAnnotation = getServiceInterface().getAnnotation(Url.class);
            if (urlAnnotation == null) {
                String errMsg = "@Url annotation must be set on the service interface " +
                        getServiceInterface().getName();
                logger.severe(errMsg);
                throw new IllegalStateException(errMsg);
            }

            serviceUrl = urlAnnotation.value();
            if (!baseServiceUrl.endsWith("/") && !serviceUrl.startsWith("/")) {
                baseServiceUrl += "/";
            }

            final String endpointAddress = baseServiceUrl + serviceUrl;
            setEndpointAddress(endpointAddress);
            setWsdlDocumentUrl(new URL(endpointAddress + "?wsdl"));

            // Retrieving service name, port name and namespace from @WebService annotation
            WebService webServiceAnnotation = getServiceInterface().getAnnotation(WebService.class);
            setServiceName(webServiceAnnotation.serviceName());
            setNamespaceUri(webServiceAnnotation.targetNamespace());
            setPortName(webServiceAnnotation.portName());

        } catch (Throwable t) {
            String msg = getClass().getSimpleName() + " initialization failed: " + t.getMessage();
            logger.severe(msg);
            throw new RuntimeException(msg, t);
        }

        // Build a proxy that also exposes the JAX-WS BindingProvider and GenericService interfaces.
        ProxyFactory pf = new ProxyFactory();
        pf.addInterface(getServiceInterface());
        pf.addInterface(BindingProvider.class);
        pf.addAdvice(this);

        serviceProxy = pf.getProxy(getBeanClassLoader());

    }

    /**
     * JAX WS session-aware client proxy factory method
     *
     * @return JAX WS proxy service
     */
    public Object getObject() {
        logger.info("Creating service proxy: interface = " + getServiceInterface() +
                ", baseServiceUrl = " + baseServiceUrl + ", serviceUrl = " + serviceUrl);
        return serviceProxy;
    }

    public Class<?> getObjectType() {
        return getServiceInterface();
    }

    public boolean isSingleton() {
        return true;
    }

    public String getBaseServiceUrl() {
        return baseServiceUrl;
    }

    public void setBaseServiceUrl(String baseServiceUrl) {
        this.baseServiceUrl = baseServiceUrl;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

}
