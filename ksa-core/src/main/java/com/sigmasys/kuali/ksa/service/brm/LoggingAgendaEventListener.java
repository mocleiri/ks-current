package com.sigmasys.kuali.ksa.service.brm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.definition.rule.Rule;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.springframework.stereotype.Service;

/**
 * This class is used to log information about Drools rule execution.
 *
 * @author Michael Ivanov
 */
@Service("loggingAgendaEventListener")
public class LoggingAgendaEventListener extends DefaultAgendaEventListener {

    private static final Log logger = LogFactory.getLog(LoggingAgendaEventListener.class);

    @Override
    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        super.beforeActivationFired(event);
        Rule rule = event.getActivation().getRule();
        logger.info("The rule '" + rule.getName() + "' is being fired...");
    }

    @Override
    public void afterActivationFired(AfterActivationFiredEvent event) {
        Rule rule = event.getActivation().getRule();
        logger.info("The rule '" + rule.getName() + "' has been fired");
    }

}
