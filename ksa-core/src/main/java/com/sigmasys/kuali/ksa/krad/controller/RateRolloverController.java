package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.RateRolloverForm;
import com.sigmasys.kuali.ksa.model.fm.Rate;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.List;

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

    /**
     * The message displayed when the "Get Information" button is pressed.
     */
    private static final String ATP_ROLLOVER_FROM_INFO = "There are %d Rates in the selected academic time period.";

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

        // Reset the messages:
        resetDisplayableMessages(form);

        // Retrieve the number of Rates in the selected Academic Period to roll over From:
        int numRates = -2;

        try {

            // Calling this method may raise a Exception:
            numRates = getNumRatesInAcademicTimePeriodFrom(form);
        } catch (Exception e) {

            // Log the error:
            logger.error("There was an error retrieving Rates for the selected Academic Time Period.", e);
        }

        // Check if the result of the method was -2, which means an exception occured:
        if (numRates == -2) { // Exception getting Rates

            // Add an error message:
            form.setAtpRolloverFromError("There was an error retrieving Rates. See the log for details.");
        } else if (numRates == -1) { // No ATP selected

            // Add an appropriate error message:
            form.setAtpRolloverFromError("Please select a valid Academic Time Period.");
        } else if (numRates >= 0) { // A valid number of Rates found

            // Set the ATP Rollover From information message:
            form.setAtpRolloverFromInfo(String.format(ATP_ROLLOVER_FROM_INFO, numRates));
        }

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

        // Reset the messages:
        resetDisplayableMessages(form);

        // Update the selected Date option:
        updateSelectedDateOption(form);

        return getUIFModelAndView(form);
    }


    /*************************************************************************************
     *
     * Private helper methods.
     *
     *************************************************************************************/

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

    /**
     * Resets all displayable messages on the form. This is needed between form submissions.
     *
     * @param form The form object.
     */
    private void resetDisplayableMessages(RateRolloverForm form) {

        // Reset displayable messages:
        form.setAtpRolloverFromInfo(null);
        form.setRolloverErrorMessage(null);
        form.setAtpRolloverFromError(null);
    }

    /**
     * Returns the number of Rates in the currently selected Academic Time Period to roll over From.
     * Returns -1 if no From ATP has been selected.
     *
     * @param form  The form object.
     * @return      The number of Rates in the From ATP.
     */
    private int getNumRatesInAcademicTimePeriodFrom(RateRolloverForm form) {

        // Get the currently selected From ATP:
        String atpFrom = form.getAtpRolloverFrom();
        int numRates = -1;

        // Get a List of Rates by the currently selected ATP:
        if (StringUtils.isNotBlank(atpFrom)) {

            List<Rate> rates = rateService.getRatesByAtpId(atpFrom);

            numRates = CollectionUtils.isNotEmpty(rates) ? rates.size() : 0;
        }

        return numRates;
    }
}
