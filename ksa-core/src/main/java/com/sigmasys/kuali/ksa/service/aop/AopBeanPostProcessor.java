package com.sigmasys.kuali.ksa.service.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AopBeanPostProcessor
 *
 * @author Michael Ivanov
 */
@Service
public class AopBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AopProxy) {
            List<Advice> advices = ((AopProxy) bean).getAdvices(beanFactory);
            if (advices != null && !advices.isEmpty()) {
                Advised advised = (bean instanceof Advised) ? (Advised) bean : new ProxyFactory(bean);
                for (Advice advice : advices) {
                    advised.addAdvice(advice);
                }
                return (bean instanceof Advised) ? bean : ((ProxyFactory) advised).getProxy();
            }
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
