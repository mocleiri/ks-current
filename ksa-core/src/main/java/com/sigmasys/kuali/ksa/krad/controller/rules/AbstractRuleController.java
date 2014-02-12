package com.sigmasys.kuali.ksa.krad.controller.rules;

import com.sigmasys.kuali.ksa.krad.controller.GenericSearchController;
import com.sigmasys.kuali.ksa.krad.form.rules.AbstractRuleViewModel;
import com.sigmasys.kuali.ksa.krad.form.rules.RulesForm;
import com.sigmasys.kuali.ksa.service.brm.BrmPersistenceService;
import com.sigmasys.kuali.ksa.service.brm.BrmService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * A generic rule controller for managing KSA business rules.
 *
 * @author Michael Ivanov
 */
public abstract class AbstractRuleController extends GenericSearchController {


    @Autowired
    protected BrmService brmService;

    @Autowired
    protected BrmPersistenceService brmPersistenceService;

    protected boolean ruleExists(String ruleName) {
        return StringUtils.isNotBlank(ruleName) && brmPersistenceService.getRule(ruleName) != null;
    }

    protected boolean ruleSetExists(String ruleSetName) {
        return StringUtils.isNotBlank(ruleSetName) && brmPersistenceService.getRuleSet(ruleSetName) != null;
    }

    protected void reloadRuleSets(AbstractRuleViewModel form, boolean isNewRule) {
        String ruleSetName = form.getRuleSetName();
        if (StringUtils.isNotBlank(ruleSetName)) {
            brmService.reloadRuleSets(ruleSetName);
        } else if (form instanceof RulesForm) {
            RulesForm rulesForm = (RulesForm) form;
            String ruleName = isNewRule ? rulesForm.getNewRule().getName() : rulesForm.getRuleName();
            List<String> ruleSetNames = brmPersistenceService.getRuleSetNamesByRuleNames(ruleName);
            if (CollectionUtils.isNotEmpty(ruleSetNames)) {
                brmService.reloadRuleSets(ruleSetNames.toArray(new String[ruleSetNames.size()]));
            }
        }
    }


}
