package com.sigmasys.kuali.ksa.config;

import com.sigmasys.kuali.ksa.model.Constants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.ksb.messaging.config.KSBConfigurer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * KSB configurer extension
 *
 * @author Michael Ivanov
 */
public class KsbConfigurer extends KSBConfigurer implements BeanFactoryAware {

    private static final String MESSAGE_CLIENT_SPRING = "classpath:org/kuali/rice/ksb/config/KsbMessageClientSpringBeans.xml";
    private static final String OJB_MESSAGE_CLIENT_SPRING = "classpath:org/kuali/rice/ksb/config/KsbOjbMessageClientSpringBeans.xml";

    private boolean enableMessaging;

    private PlatformTransactionManager transactionManager;
    private DefaultTransactionDefinition transactionDefinition;


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        transactionManager = beanFactory.getBean("transactionManager", PlatformTransactionManager.class);
        transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionDefinition.setTimeout(300);
        transactionDefinition.setReadOnly(true);
    }

    @PostConstruct
    private void postConstruct() {
        enableMessaging = ConfigContext.getCurrentContextConfig().getBooleanProperty(Constants.RICE_MESSAGING_ENABLED, true);
    }

    @Override
    public List<String> getPrimarySpringFiles() {
        List<String> springFiles = super.getPrimarySpringFiles();
        if (!enableMessaging) {
            springFiles.remove(MESSAGE_CLIENT_SPRING);
            springFiles.remove(OJB_MESSAGE_CLIENT_SPRING);
        }
        return springFiles;
    }

    @Override
    protected void doAdditionalModuleStartLogic() {
        // KSA application does not need to use remote services during startup so do nothing
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent || applicationEvent instanceof ContextStoppedEvent) {
            LOG.info("Processing ApplicationEvent: " + applicationEvent.getClass().getName());
            TransactionStatus transaction = transactionManager.getTransaction(transactionDefinition);
            try {
                super.onApplicationEvent(applicationEvent);
                transactionManager.commit(transaction);
            } catch (Throwable t) {
                transactionManager.rollback(transaction);
                LOG.error(t.getMessage(), t);
                throw new RuntimeException(t.getMessage(), t);
            }
        }
    }

}
