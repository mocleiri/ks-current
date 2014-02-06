package com.sigmasys.kuali.ksa.service.brm;

import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.util.BeanUtils;
import com.sigmasys.kuali.ksa.util.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.event.rule.ActivationEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to log information about Drools rule execution.
 *
 * @author Michael Ivanov
 */
@Service("loggingAgendaEventListener")
public class LoggingAgendaEventListener extends DefaultAgendaEventListener {

    private static final Log logger = LogFactory.getLog(LoggingAgendaEventListener.class);

    private final Map<String, Rule> rules = new HashMap<String, Rule>();

    @Autowired
    private BrmPersistenceService brmPersistenceService;


    private Rule getRule(ActivationEvent event) {
        String ruleName = event.getActivation().getRule().getName();
        Rule rule = rules.get(ruleName);
        if (rule == null) {
            rule = BeanUtils.getDeepCopy(brmPersistenceService.getRule(ruleName));
            rules.put(ruleName, rule);
        }
        return rule;
    }

    @Override
    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        super.beforeActivationFired(event);
        Rule rule = getRule(event);
        logger.info("The rule [" + rule.getName() + "][" + CommonUtils.nvl(rule.getDescription()) + "] is being fired...");
    }

    @Override
    public void afterActivationFired(AfterActivationFiredEvent event) {
        super.afterActivationFired(event);
        Rule rule = getRule(event);
        logger.info("The rule [" + rule.getName() + "][" + CommonUtils.nvl(rule.getDescription()) + "] has been fired");
    }

}
