package com.sigmasys.kuali.ksa.krad.form.rules;


import com.sigmasys.kuali.ksa.krad.form.AbstractViewModel;
import com.sigmasys.kuali.ksa.krad.util.StringValueFinder;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

import java.util.List;

/**
 * The generic class for rule/rule sets forms.
 *
 * @author Michael Ivanov
 */
public abstract class AbstractRuleViewModel extends AbstractViewModel {

    protected String ruleType;

    protected String editStatusMessage;
    protected String addStatusMessage;

    protected String newRuleType;

    protected String ruleSetName;

    protected String description;

    protected final StringValueFinder nameFinder = new StringValueFinder();
    protected final StringValueFinder ruleTypeFinder = new StringValueFinder();


    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public void initNameFinder(List<String> ruleNames) {
        nameFinder.initValues(ruleNames);
    }

    public void initRuleTypeFinder(List<String> ruleTypes) {
        ruleTypeFinder.initValues(ruleTypes);
    }

    public String getEditStatusMessage() {
        return editStatusMessage;
    }

    public void setEditStatusMessage(String editStatusMessage) {
        this.editStatusMessage = editStatusMessage;
    }

    public KeyValuesFinder getNameFinder() {
        return nameFinder;
    }

    public KeyValuesFinder getRuleTypeFinder() {
        return ruleTypeFinder;
    }

    public String getAddStatusMessage() {
        return addStatusMessage;
    }

    public void setAddStatusMessage(String addStatusMessage) {
        this.addStatusMessage = addStatusMessage;
    }

    public String getNewRuleType() {
        return newRuleType;
    }

    public void setNewRuleType(String newRuleType) {
        this.newRuleType = newRuleType;
    }


    public String getRuleSetName() {
        return ruleSetName;
    }

    public void setRuleSetName(String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
