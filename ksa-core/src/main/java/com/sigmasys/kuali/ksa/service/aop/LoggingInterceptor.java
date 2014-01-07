package com.sigmasys.kuali.ksa.service.aop;

import com.sigmasys.kuali.ksa.service.UserSessionManager;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

/**
 * Logging interceptor. Works with Apache commons logger.
 *
 * @author Michael Ivanov
 */
public class LoggingInterceptor extends AbstractMethodInterceptor {

    private final Log logger = LogFactory.getLog(getClass());

    private static final String UNKNOWN_USER_ID = "Unknown";

    private UserSessionManager userSessionManager;


    public LoggingInterceptor(Object targetObject) {
        super(targetObject);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod();
        for (Method method : getTargetObject().getClass().getMethods()) {
            if (methodsMatch(targetMethod, method)) {
                log(invocation);
                break;
            }
        }
        return super.invoke(invocation);
    }

    private UserSessionManager getUserSessionManager() {
        if (userSessionManager == null) {
            userSessionManager = ContextUtils.getBean(UserSessionManager.class);
        }
        return userSessionManager;
    }

    private boolean methodsMatch(Method method1, Method method2) {
        if (!method1.getName().equals(method2.getName())) {
            return false;
        }
        Class<?>[] paramTypes1 = method1.getParameterTypes();
        Class<?>[] paramTypes2 = method2.getParameterTypes();
        if (paramTypes1.length != paramTypes2.length) {
            return false;
        }
        for (int i = 0; i < paramTypes1.length; i++) {
            if (!paramTypes1[i].getName().equals(paramTypes2[i].getName())) {
                return false;
            }
        }
        return true;
    }

    private void log(MethodInvocation invocation) {

        Method method = invocation.getMethod();
        String className = method.getDeclaringClass().getSimpleName();
        Object[] arguments = invocation.getArguments();
        Class<?>[] paramTypes = method.getParameterTypes();

        String userId = getUserSessionManager().getUserId();

        if (userId == null) {
            userId = UNKNOWN_USER_ID;
        }

        StringBuilder logBuffer = new StringBuilder("User '" + userId + "' ");

        logBuffer.append("performed the method call: ");
        logBuffer.append(className);
        logBuffer.append(" :: ");
        logBuffer.append(method.getReturnType().getSimpleName());
        logBuffer.append(" ");
        logBuffer.append(method.getName());
        logBuffer.append("(");

        for (int i = 0; i < arguments.length; i++) {

            if (i > 0) {
                logBuffer.append(", ");
            }

            logBuffer.append(paramTypes[i].getSimpleName());
            logBuffer.append(" '");
            logBuffer.append(arguments[i]);
            logBuffer.append("'");
        }

        logBuffer.append(")");

        logger.info(logBuffer.toString());
    }

}


