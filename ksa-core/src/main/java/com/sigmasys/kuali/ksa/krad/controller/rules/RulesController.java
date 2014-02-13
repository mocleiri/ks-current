package com.sigmasys.kuali.ksa.krad.controller.rules;

import com.sigmasys.kuali.ksa.exception.InvalidRulesException;
import com.sigmasys.kuali.ksa.krad.form.rules.RulesForm;
import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.model.rule.RuleSet;
import com.sigmasys.kuali.ksa.model.rule.RuleType;
import com.sigmasys.kuali.ksa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * A controller for managing KSA business rules.
 *
 * @author Michael Ivanov
 */
@Controller
@RequestMapping(value = "/rulesView")
public class RulesController extends AbstractRuleController {

    private static final Log logger = LogFactory.getLog(RulesController.class);


    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected RulesForm createInitialForm(HttpServletRequest request) {
        RulesForm form = new RulesForm();
        initForm(form, request.getParameter("ruleSetName"), request.getParameter("ruleName"));
        return form;
    }

    private void initForm(RulesForm rulesForm, String ruleSetName, String ruleName) {

        rulesForm.setRuleName(StringUtils.isNotBlank(ruleName) ? ruleName : null);

        rulesForm.setRuleSetName(StringUtils.isNotBlank(ruleSetName) ? ruleSetName : null);

        List<String> ruleNames = StringUtils.isNotBlank(rulesForm.getRuleSetName()) ?
                brmPersistenceService.getRuleNames(rulesForm.getRuleSetName()) : brmPersistenceService.getRuleNames();

        rulesForm.initNameFinder(ruleNames != null ? ruleNames : Collections.<String>emptyList());

        List<RuleType> ruleTypes = brmPersistenceService.getRuleTypes();

        if (CollectionUtils.isNotEmpty(ruleTypes)) {
            List<String> ruleTypeNames = new ArrayList<String>(ruleTypes.size());
            for (RuleType ruleType : ruleTypes) {
                ruleTypeNames.add(ruleType.getName());
            }
            rulesForm.initRuleTypeFinder(ruleTypeNames);
        }
    }


    /**
     * @param form RuleForm instance
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") RulesForm form) {

        logger.debug("Page ID = " + form.getPageId());

        return getUIFModelAndView(form);
    }

    /**
     * @param form RuleForm instance
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=update")
    public ModelAndView update(@ModelAttribute("KualiForm") RulesForm form) {

        if (form.getRuleId() == null) {
            return handleError(form, "Rule ID is null");
        }

        if (StringUtils.isBlank(form.getRuleName())) {
            return handleError(form, "Rule name cannot be empty");
        }

        if (StringUtils.isBlank(form.getRuleLhs())) {
            return handleError(form, "Rule LHS cannot be empty");
        }

        if (StringUtils.isBlank(form.getRuleRhs())) {
            return handleError(form, "Rule RHS cannot be empty");
        }

        if (StringUtils.isBlank(form.getRuleType())) {
            return handleError(form, "Rule type is required");
        }

        try {

            Rule rule = new Rule();

            copyFormToRule(form, rule);

            brmPersistenceService.persistRule(rule);

            reloadRuleSets(form, false);

            setMessage(form, "Rule has been updated");

            logger.info("Updated Rule => \n" + rule);

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
    public ModelAndView select(@ModelAttribute("KualiForm") RulesForm form) {

        String ruleName = form.getRuleName();

        if (StringUtils.isBlank(ruleName)) {
            String errMsg = "Rule name must be specified";
            logger.error(errMsg);
            return handleError(form, errMsg);
        }

        Rule rule = brmPersistenceService.getRule(ruleName);

        if (rule == null) {
            String errMsg = "Rule specified by name '" + ruleName + "' does not exist";
            logger.error(errMsg);
            return handleError(form, errMsg);
        }

        copyRuleToForm(rule, form);

        logger.info("Selected Rule => \n" + rule);

        return getUIFModelAndView(form);
    }

    /**
     * @param form RulesForm
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=edit")
    public ModelAndView edit(@ModelAttribute("KualiForm") RulesForm form,
                             @RequestParam("ruleSetName") String ruleSetName,
                             @RequestParam("ruleName") String ruleName) {
        initForm(form, ruleSetName, ruleName);
        return select(form);
    }

    /**
     * @param form RulesForm
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=checkRuleNameExistence")
    public ModelAndView checkRuleNameExistence(@ModelAttribute("KualiForm") RulesForm form) {

        Rule rule = form.getNewRule();

        if (rule == null) {
            return handleError(form, "Rule cannot be null");
        }

        String ruleName = rule.getName();

        if (StringUtils.isBlank(ruleName)) {
            return handleError(form, "Rule name cannot be empty");
        } else if (ruleExists(ruleName)) {
            return handleError(form, "Rule name '" + ruleName + "' already exists");
        }

        setMessage(form, "Rule name '" + ruleName + "' is available");

        return getUIFModelAndView(form);
    }

    /**
     * @param form RuleForm instance
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=add")
    public ModelAndView add(@ModelAttribute("KualiForm") RulesForm form) {

        Rule rule = form.getNewRule();

        if (rule == null) {
            return handleError(form, "Rule cannot be null");
        }

        if (rule.getId() != null) {
            rule.setId(null);
        }

        String ruleName = rule.getName();

        if (StringUtils.isBlank(ruleName)) {

            return handleError(form, "Rule name cannot be empty");

        } else if (!ruleExists(ruleName)) {

            if (StringUtils.isBlank(rule.getLhs())) {
                return handleError(form, "Rule LHS cannot be empty");
            }

            if (StringUtils.isBlank(rule.getRhs())) {
                return handleError(form, "Rule RHS cannot be empty");
            }

            String ruleTypeName = form.getNewRuleType();

            if (StringUtils.isBlank(ruleTypeName)) {
                return handleError(form, "Rule type is required");
            }

            RuleType ruleType = brmPersistenceService.getRuleType(ruleTypeName);

            if (ruleType == null) {
                return handleError(form, "Rule type '" + ruleTypeName + "' does not exist");
            }

            rule.setType(ruleType);

            try {

                rule = BeanUtils.getDeepCopy(rule);

                String ruleSetName = form.getRuleSetName();

                if (StringUtils.isNotBlank(ruleSetName)) {
                    RuleSet ruleSet = brmPersistenceService.getRuleSet(ruleSetName);
                    if (ruleSet == null) {
                        return handleError(form, "Rule set '" + ruleSetName + "' does not exist");
                    }
                    brmPersistenceService.addRulesToRuleSet(ruleSet.getId(), rule);
                } else {
                    brmPersistenceService.persistRule(rule);
                }

                reloadRuleSets(form, true);

                setMessage(form, "A new Rule has been created");

                logger.info("Added Rule => \n" + rule);

            } catch (InvalidRulesException e) {
                logger.error(e.getMessage(), e);
                String errMsg = "There are problems compiling the rules. Please check the rule syntax and try again.";
                return handleError(form, errMsg);
            } catch (Exception e) {
                return handleError(form, e);
            }

        } else {
            return handleError(form, "Rule with name = '" + ruleName + "' already exists");
        }

        return getUIFModelAndView(form);
    }

    private void copyRuleToForm(Rule rule, RulesForm form) {
        form.setRuleId(rule.getId());
        form.setRuleName(rule.getName());
        form.setDescription(rule.getDescription());
        Integer priority = rule.getPriority();
        form.setRulePriority(priority != null ? priority.toString() : "0");
        form.setRuleLhs(rule.getLhs());
        form.setRuleRhs(rule.getRhs());
        form.setRuleType(rule.getType().getName());
    }

    private void copyFormToRule(RulesForm form, Rule rule) {
        rule.setId(form.getRuleId());
        rule.setName(form.getRuleName());
        rule.setDescription(form.getDescription());
        String priority = form.getRulePriority();
        rule.setPriority(StringUtils.isNotBlank(priority) ? Integer.parseInt(priority.trim()) : 0);
        rule.setLhs(form.getRuleLhs());
        rule.setRhs(form.getRuleRhs());
        RuleType ruleType = brmPersistenceService.getRuleType(form.getRuleType());
        if (ruleType == null) {
            String errMsg = "Rule Type with name '" + form.getRuleType() + "' does not exist";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
        rule.setType(ruleType);
    }

}
