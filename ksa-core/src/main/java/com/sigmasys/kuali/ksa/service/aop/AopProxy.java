package com.sigmasys.kuali.ksa.service.aop;

import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.BeanFactory;

import java.util.List;

/**
 * Any bean implementing this interface will be wrapped by BeanPostProcessor with a Spring AOP proxy instance
 *
 * @author Michael Ivanov
 */
public interface AopProxy {

    List<Advice> getAdvices(BeanFactory beanFactory);

}
