package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.RateRolloverForm;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller to serve requests from the "Rate Rollover" page.
 * User: Sergey
 * Date: 12/4/13
 * Time: 10:54 PM
 */
@Controller
@RequestMapping(value = "/rateRolloverView")
public class RateRolloverController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(RateRolloverController.class);

    @Autowired
    private RateService rateService;


    /**
     * Creates the page's form.
     */
    @Override
    protected RateRolloverForm createInitialForm(HttpServletRequest request) {

        // Create the initial form object and set default values:
        RateRolloverForm form = new RateRolloverForm();

        form.setSelectedDateOption(RateRolloverForm.OPTION_NULL_DATES);

        return form;
    }

    /**
     * Displays the initial page.
     *
     * @param form  The form object.
     * @return Model and View.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=displayInitialPage")
    public ModelAndView displayInitialPage(@ModelAttribute("KualiForm") RateRolloverForm form) {

        // Update the selected Date option:
        updateSelectedDateOption(form);

        return getUIFModelAndView(form);
    }

    /**
     * Retrieves information about the "Rollover From" ATP.
     * Invoked when the "Get Information" button is pressed.
     *
     * @param form  The form object.
     * @return Model and View.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=getAtpFromInfo")
    public ModelAndView getAtpFromInfo(@ModelAttribute("KualiForm") RateRolloverForm form) {

        // Update the selected Date option:
        updateSelectedDateOption(form);

        return getUIFModelAndView(form);
    }

    /**
     * Performs the actual Rate rollover.
     * Invoked whe the "Rollover Rates" button is pressed.
     *
     * @param form The form object.
     * @return Model and View.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=rolloverRates")
    public ModelAndView rolloverRates(@ModelAttribute("KualiForm") RateRolloverForm form) {

        // Update the selected Date option:
        updateSelectedDateOption(form);

        // Reset the error message:
        form.setRolloverErrorMessage(null);

        return getUIFModelAndView(form);
    }

    /**
     * Properly sets the value of each of the Date options based on the selected value.
     *
     * @param form The form object.
     */
    private void updateSelectedDateOption(RateRolloverForm form) {

        // Inspect the current value of the "selectedDateOption" property:
        String selectedDateOption = form.getSelectedDateOption();

        if (StringUtils.isEmpty(selectedDateOption)
                || StringUtils.equals(selectedDateOption, RateRolloverForm.OPTION_NULL_DATES)) {

            // Reset the options and set the "Null Dates" option as selected:
            form.setSelectedDateOption(RateRolloverForm.OPTION_NULL_DATES);
            form.setNullDatesOption(RateRolloverForm.OPTION_RADIO_BUTTON_SELECTED);
            form.setAutoAdvancedDatesOption(null);
            form.setCustomDatesOption(null);

        } else if (StringUtils.equals(selectedDateOption, RateRolloverForm.OPTION_AUTO_ADVANCED_DATES)) {

            // Reset the options and set the "Auto Advanced Dates" option as selected:
            form.setSelectedDateOption(RateRolloverForm.OPTION_AUTO_ADVANCED_DATES);
            form.setAutoAdvancedDatesOption(RateRolloverForm.OPTION_RADIO_BUTTON_SELECTED);
            form.setNullDatesOption(null);
            form.setCustomDatesOption(null);

        } else { // "Custom Dates" option

            // Reset the options and set the "Custom Dates" option as selected:
            form.setSelectedDateOption(RateRolloverForm.OPTION_CUSTOM_DATES);
            form.setCustomDatesOption(RateRolloverForm.OPTION_RADIO_BUTTON_SELECTED);
            form.setNullDatesOption(null);
            form.setAutoAdvancedDatesOption(null);

        }
    }
}
