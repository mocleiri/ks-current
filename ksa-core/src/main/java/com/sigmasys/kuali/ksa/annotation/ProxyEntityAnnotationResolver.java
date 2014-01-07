package com.sigmasys.kuali.ksa.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * ProxyEntitiesAnnotationResolver.
 * Resolves @ProxyEntity annotations of the given object and method.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Component
@Qualifier(ProxyEntityAnnotationResolver.NAME)
public class ProxyEntityAnnotationResolver {

    private static final Log logger = LogFactory.getLog(ProxyEntityAnnotationResolver.class);

    public static final String NAME = "proxyEntitiesAnnotationResolver";

    public boolean resolve(Object object, Method method) {

        boolean proxyEntities = false;

        try {
            // If either class or method has annotation, use proxy
            ProxyEntity proxyAnnotation = method.getAnnotation(ProxyEntity.class);

            if (proxyAnnotation == null) {
                Method implMethod = object.getClass().getMethod(method.getName(), method.getParameterTypes());
                proxyAnnotation = implMethod.getAnnotation(ProxyEntity.class);
            }

            if (proxyAnnotation == null) {
                proxyAnnotation = object.getClass().getAnnotation(ProxyEntity.class);
            }

            if (proxyAnnotation != null) {
                proxyEntities = proxyAnnotation.value();
            }

        } catch (NoSuchMethodException e) {
            logger.error("Internal error, unable to find method on implementation class: " + method, e);
        }

        return proxyEntities;
    }

}