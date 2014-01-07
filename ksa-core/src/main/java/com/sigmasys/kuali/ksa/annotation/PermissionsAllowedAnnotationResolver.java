package com.sigmasys.kuali.ksa.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * PermissionsAllowedAnnotationResolver.
 * Resolves @PermissionsAllowed annotations of the given object and method.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Component
@Qualifier(PermissionsAllowedAnnotationResolver.NAME)
public class PermissionsAllowedAnnotationResolver {

    private static final Log logger = LogFactory.getLog(PermissionsAllowedAnnotationResolver.class);

    public static final String NAME = "permissionsAllowedAnnotationResolver";

    public PermissionsAllowed resolve(Object object, Method method) {

        PermissionsAllowed permissionsAllowed = null;

        try {

            permissionsAllowed = AnnotationUtils.getAnnotation(PermissionsAllowed.class, method);

            if (permissionsAllowed == null) {
                Object target = object;
                while (target != null) {
                    Method implMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
                    permissionsAllowed = AnnotationUtils.getAnnotation(PermissionsAllowed.class, implMethod);
                    if (permissionsAllowed != null || !(target instanceof Advised)) {
                        break;
                    }
                    target = ((Advised) target).getTargetSource().getTarget();
                }
            }

            if (permissionsAllowed != null) {
                logger.debug("Resolved @PermissionsAllowed annotation on the method: " + method.getName());
            }

        } catch (Exception e) {
            logger.error("Unable to find @PermissionsAllowed annotation on the method: " + method.getName(), e);
        }

        return permissionsAllowed;
    }

}