package edu.uw.kuali.student.web;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * This class is written as a work around to KULRICE-9221. It should be discarded after the issue is fixed and adopted.
 *
 * Provides a no op method invoker to be used in place of transaction interceptor
 *
 */
public class NoOpMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invocation.proceed();
    }

}
