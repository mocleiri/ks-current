package com.sigmasys.kuali.ksa.krad.controller.rules;

import com.sigmasys.kuali.ksa.exception.InvalidRulesException;
import com.sigmasys.kuali.ksa.krad.controller.GenericSearchController;
import com.sigmasys.kuali.ksa.krad.form.rules.RuleSetsForm;
import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.model.rule.RuleSet;
import com.sigmasys.kuali.ksa.model.rule.RuleType;
import com.sigmasys.kuali.ksa.service.brm.BrmPersistenceService;
import com.sigmasys.kuali.ksa.service.brm.BrmService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * A controller for managing KSA rule sets.
 *
 * @author Michael Ivanov
 */
@Controller
@RequestMapping(value = "/ruleSetsView")
public class RuleSetsController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(RuleSetsController.class);

    @Autowired
    private BrmService brmService;

    @Autowired
    private BrmPersistenceService brmPersistenceService;


    private boolean ruleSetExists(String ruleSetName) {
        return StringUtils.isNotBlank(ruleSetName) && brmPersistenceService.getRuleSet(ruleSetName) != null;
    }

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected RuleSetsForm createInitialForm(HttpServletRequest request) {
        RuleSetsForm ruleSetsForm = new RuleSetsForm();
        initForm(ruleSetsForm, request.getParameter("ruleSetName"));
        return ruleSetsForm;
    }

    protected void initForm(RuleSetsForm ruleSetsForm, String ruleSetName) {

        ruleSetsForm.initNameFinder(brmPersistenceService.getRuleSetNames());

        List<RuleType> ruleTypes = brmPersistenceService.getRuleTypes();

        if (CollectionUtils.isNotEmpty(ruleTypes)) {
            List<String> ruleTypeNames = new ArrayList<String>(ruleTypes.size());
            for (RuleType ruleType : ruleTypes) {
                ruleTypeNames.add(ruleType.getName());
            }
            ruleSetsForm.initRuleTypeFinder(ruleTypeNames);
        }

        ruleSetsForm.setRuleSetName(StringUtils.isNotBlank(ruleSetName) ? ruleSetName : null);

        ruleSetsForm.setRuleSetHeader("");

        ruleSetsForm.setRules(Collections.<Rule>emptyList());
    }


    /**
     * @param form RuleForm instance
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") RuleSetsForm form) {
        return getUIFModelAndView(form);
    }

    /**
     * @param form RuleForm instance
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=update")
    public ModelAndView update(@ModelAttribute("KualiForm") RuleSetsForm form) {

        if (form.getRuleSetId() == null) {
            return handleError(form, "Rule Set ID is null");
        }

        if (StringUtils.isBlank(form.getRuleSetName())) {
            return handleError(form, "Rule Set name cannot be empty");
        }

        if (StringUtils.isBlank(form.getRuleSetHeader())) {
            return handleError(form, "Rule Set header cannot be empty");
        }

        if (StringUtils.isBlank(form.getRuleType())) {
            return handleError(form, "Rule Set type is required");
        }

        try {

            RuleSet ruleSet = new RuleSet();

            copyFormToRuleSet(form, ruleSet);

            brmPersistenceService.persistRuleSet(ruleSet);

            brmService.reloadRuleSets(ruleSet.getName());

            setMessage(form, "Rule Set has been updated");
            logger.info("Updated Rule Set => \n" + ruleSet);

        } catch (InvalidRulesException e) {
            logger.error(e.getMessage(), e);
            String errMsg = "There are problems compiling the rules. Please check the rule syntax and try again.";
            return handleError(form, errMsg);
        } catch (Exception e) {
            return handleError(form, e);
        }

        return getUIFModelAndView(form);
    }

    /**
     * @param form RulesForm
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=select")
    public ModelAndView select(@ModelAttribute("KualiForm") RuleSetsForm form) {

        String ruleSetName = form.getRuleSetName();
        if (StringUtils.isBlank(ruleSetName)) {
            String errMsg = "Rule Set name must be specified";
            logger.error(errMsg);
            return handleError(form, errMsg);
        }

        RuleSet ruleSet = brmPersistenceService.getRuleSet(ruleSetName);
        if (ruleSet == null) {
            String errMsg = "Rule Set specified by name '" + ruleSetName + "' does not exist";
            logger.error(errMsg);
            return handleError(form, errMsg);
        }

        copyRuleSetToForm(ruleSet, form);

        logger.info("Selected Rule Set => \n" + ruleSet);

        return getUIFModelAndView(form);
    }

    /**
     * @param form RulesForm
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=edit")
    public ModelAndView edit(@ModelAttribute("KualiForm") RuleSetsForm form,
                             @RequestParam("ruleSetName") String ruleSetName) {
        initForm(form, ruleSetName);
        return select(form);
    }

    /**
     * @param form RulesForm
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=removeRule")
    public ModelAndView removeRule(@ModelAttribute("KualiForm") RuleSetsForm form,
                                   @RequestParam("ruleSetName") String ruleSetName,
                                   @RequestParam("ruleName") String ruleName) {

        if (StringUtils.isBlank(ruleSetName)) {
            String errMsg = "'ruleSetName' request parameter is required";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        if (StringUtils.isBlank(ruleName)) {
            String errMsg = "'ruleName' request parameter is required";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        RuleSet ruleSet = brmPersistenceService.getRuleSet(ruleSetName);

        if (ruleSet == null) {
            String errMsg = "Rule Set with name '" + ruleSetName + " does not exist";
            return handleError(form, errMsg);
        }

        try {

            Set<Rule> rules = ruleSet.getRules();

            if (CollectionUtils.isNotEmpty(rules)) {

                Rule ruleToRemove = null;

                for (Rule rule : rules) {
                    if (ruleName.equals(rule.getName())) {
                        ruleToRemove = rule;
                        break;
                    }
                }

                if (ruleToRemove != null) {

                    rules.remove(ruleToRemove);

                    brmPersistenceService.persistRuleSet(ruleSet);

                    brmService.reloadRuleSets(ruleSetName);

                    form.setRuleSetName(ruleSetName);

                    select(form);

                    setMessage(form, "Rule '" + ruleName + "' has been removed from Rule Set '" + ruleSetName + "'");

                } else {
                    return handleError(form, "Rule '" + ruleName + "' has not been found in Rule Set '" + ruleSetName + "'");
                }
            }

        } catch (InvalidRulesException e) {
            logger.error(e.getMessage(), e);
            String errMsg = "There are problems compiling the rules. Please check the rule syntax and try again.";
            return handleError(form, errMsg);
        } catch (Exception e) {
            return handleError(form, e);
        }

        return getUIFModelAndView(form);
    }

    /**
     * @param form RulesForm
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=checkRuleNameExistence")
    public ModelAndView checkRuleSetNameExistence(@ModelAttribute("KualiForm") RuleSetsForm form) {

        RuleSet ruleSet = form.getNewRuleSet();
        if (ruleSet == null) {
            return handleError(form, "Rule Set cannot be null");
        }

        String ruleSetName = ruleSet.getName();

        if (StringUtils.isBlank(ruleSetName)) {
            return handleError(form, "Rule Set name cannot be empty");
        } else if (ruleSetExists(ruleSetName)) {
            return handleError(form, "Rule Set name '" + ruleSetName + "' already exists");
        }

        setMessage(form, "Rule Set name '" + ruleSetName + "' is available");

        return getUIFModelAndView(form);
    }

    /**
     * @param form RuleForm instance
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=add")
    public ModelAndView add(@ModelAttribute("KualiForm") RuleSetsForm form) {

        // TODO:

        return getUIFModelAndView(form);
    }

    private void copyRuleSetToForm(RuleSet ruleSet, RuleSetsForm form) {
        form.setRuleSetId(ruleSet.getId());
        form.setRuleSetName(ruleSet.getName());
        form.setRuleType(ruleSet.getType().getName());
        form.setDescription(ruleSet.getDescription());
        form.setRuleSetHeader(ruleSet.getHeader());
        Set<Rule> setOfRules = ruleSet.getRules();
        if (CollectionUtils.isNotEmpty(setOfRules)) {
            List<Rule> rules = new ArrayList<Rule>(ruleSet.getRules());
            Collections.sort(rules, new Comparator<Rule>() {
                @Override
                public int compare(Rule rule1, Rule rule2) {
                    return rule1.getName().compareToIgnoreCase(rule2.getName());
                }
            });
            form.setRules(rules);
        }

    }

    private void copyFormToRuleSet(RuleSetsForm form, RuleSet ruleSet) {
        ruleSet.setId(form.getRuleSetId());
        ruleSet.setName(form.getRuleSetName());
        ruleSet.setDescription(form.getDescription());
        ruleSet.setHeader(form.getRuleSetHeader());
        RuleType ruleType = brmPersistenceService.getRuleType(form.getRuleType());
        if (ruleType == null) {
            String errMsg = "Rule Type with name '" + form.getRuleType() + "' does not exist";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
        ruleSet.setType(ruleType);
        List<Rule> listOfRules = form.getRules();
        if (CollectionUtils.isNotEmpty(listOfRules)) {
            Set<Rule> rules = new HashSet<Rule>(listOfRules);
            ruleSet.setRules(rules);
        }
    }

}
