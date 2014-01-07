package com.sigmasys.kuali.ksa.util;

import org.springframework.beans.factory.BeanFactory;

/**
 * ContextUtils
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("unchecked")
public class ContextUtils {

    private static BeanFactory applicationContext;

    private ContextUtils() {
    }

    private static void validateContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("Application context has not been initialized. " +
                    " Call initContext() prior to using other methods.");
        }
    }

    public static BeanFactory initContext(BeanFactory context) {
        if (context != null) {
            applicationContext = context;
            return applicationContext;
        }
        throw new IllegalArgumentException("Application context cannot be null");
    }

    public static BeanFactory getBeanFactory() {
        validateContext();
        return applicationContext;
    }

    public static <T> T getBean(String name) {
        validateContext();
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> beanInterface) {
        validateContext();
        return applicationContext.getBean(beanInterface);
    }

    public static <T> T getBean(String name, Class<T> beanInterface) {
        validateContext();
        return applicationContext.getBean(name, beanInterface);
    }
}
