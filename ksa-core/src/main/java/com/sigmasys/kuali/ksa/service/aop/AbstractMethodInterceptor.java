package com.sigmasys.kuali.ksa.service.aop;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;


/**
 * AbstractMethodInterceptor. A super-class class for all method interceptors used in the webapp.
 *
 * @author Michael Ivanov
 */
public class AbstractMethodInterceptor implements MethodInterceptor {

    private static final Log logger = LogFactory.getLog(AbstractMethodInterceptor.class);


    private Object targetObject;


    public AbstractMethodInterceptor(Object targetObject) {
        this.targetObject = targetObject;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (invocation.getThis() != null) {
            return invocation.proceed();
        } else if (targetObject != null) {
            try {
                return invocation.getMethod().invoke(targetObject, invocation.getArguments());
                // We have to propagate the original exception (which is wrapped by InvocationTargetException) if any occurs
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
        String errMsg = "Target object is required by the method interceptor";
        logger.error(errMsg);
        throw new IllegalStateException(errMsg);
    }
}
