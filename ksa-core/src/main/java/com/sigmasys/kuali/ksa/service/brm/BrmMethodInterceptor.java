package com.sigmasys.kuali.ksa.service.brm;

import com.sigmasys.kuali.ksa.service.aop.AbstractMethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * BRM method interceptor.
 *
 * @author Michael Ivanov
 */
public class BrmMethodInterceptor extends AbstractMethodInterceptor {

    private static final Log logger = LogFactory.getLog(BrmMethodInterceptor.class);

    public BrmMethodInterceptor(Object targetObject) {
        super(targetObject);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        int brmContextIndex = Arrays.asList(method.getParameterTypes()).indexOf(BrmContext.class);
        if (brmContextIndex >= 0) {
            List<Object> arguments = Arrays.asList(invocation.getArguments());
            if (brmContextIndex < arguments.size()) {
                Object contextObject = arguments.get(brmContextIndex);
                if (contextObject == null) {
                    String errMsg = "BRM context cannot be null";
                    logger.error(errMsg);
                    throw new IllegalArgumentException(errMsg);
                }
                BrmContext context = (BrmContext) contextObject;
                if (context.getAccount() == null) {
                    String errMsg = "Account is required in BRM context";
                    logger.error(errMsg);
                    throw new IllegalArgumentException(errMsg);
                }
                logger.debug("BrmContext: Account ID = " + context.getAccount().getId());
                if (context.getAttributes() == null) {
                    String errMsg = "BRM context attributes map cannot be null";
                    logger.error(errMsg);
                    throw new IllegalArgumentException(errMsg);
                }
                if (context.getGlobalVariables() == null) {
                    String errMsg = "BRM context global variables map cannot be null";
                    logger.error(errMsg);
                    throw new IllegalArgumentException(errMsg);
                }
            }
        }
        return super.invoke(invocation);
    }

}
