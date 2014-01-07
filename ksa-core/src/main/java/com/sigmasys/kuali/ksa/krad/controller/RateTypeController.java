package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.RateTypeForm;
import com.sigmasys.kuali.ksa.krad.model.RateTypeModel;
import com.sigmasys.kuali.ksa.model.fm.RateType;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import com.sigmasys.kuali.ksa.util.CommonUtils;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Controller to serve requests from the "Rate Types" page.
 *
 * @author Sergey Godunov
 */
@Controller
@RequestMapping(value = "/rateTypeView")
public class RateTypeController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(RateTypeController.class);


    @Autowired
    private RateService rateService;


    /**
     * Creates the page's form.
     */
    @Override
    protected RateTypeForm createInitialForm(HttpServletRequest request) {
        return new RateTypeForm();
    }

    /**
     * Invoked to display the initial page. Loads all RateType objects.
     *
     * @param form The form object.
     * @return Model And View.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=displayInitialPage")
    public ModelAndView displayInitialPage(@ModelAttribute("KualiForm") RateTypeForm form) {

        // Refresh the data model:
        refreshDataModel(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Delete" icon next to a Rate Type is pressed.
     *
     * @param form       The form object.
     * @param rateTypeId ID of a RateType to delete.
     * @return Model And View.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=deleteRateType")
    public ModelAndView deleteRateType(@ModelAttribute("KualiForm") RateTypeForm form,
                                       @RequestParam("rateTypeId") Long rateTypeId) {

        // Reset the error message:
        form.setErrorMessage(null);

        // Find the RateTypeModel with the given RateType ID and delete it:
        RateTypeModel rateTypeModel = findRateTypeModel(form, rateTypeId, true);

        if (rateTypeModel != null) {

            // Delete the RateType:
            try {
                rateService.deleteRateType(rateTypeModel.getId());
            } catch (Exception e) {
                // Set the error message:
                form.setErrorMessage(String.format("Cannot delete this Rate Type. %s", StringUtils.defaultString(e.getMessage())));
            }
        } else {
            // Set the error message:
            form.setErrorMessage("Cannot delete this Rate Type. Rate Type ID is not found.");
        }

        // Refresh the data model:
        refreshDataModel(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked to save or update the selected RateType when the "Save" icon is pressed.
     *
     * @param form       The form object.
     * @param rateTypeId ID of a Rate Type to update. If <code>null</code>, then it's a new RateType
     * @return Model and View.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=saveRateType")
    public ModelAndView saveRateType(@ModelAttribute("KualiForm") RateTypeForm form,
                                     @RequestParam("rateTypeId") Long rateTypeId) {

        // Reset the error message:
        form.setErrorMessage(null);

        // If the RateType ID is null, save a new RateType:
        if (rateTypeId == null) {

            // Find the RateTypeModel with a null RateType ID:
            RateTypeModel rateTypeModel = findRateTypeModel(form, null, false);

            if (rateTypeModel != null) {

                try {
                    // Create a new RateType:
                    RateType newRateType = rateService.createRateType(rateTypeModel.getCode(),
                            rateTypeModel.getName(), rateTypeModel.getDescription(), rateTypeModel.isGrouping(), rateTypeModel.getRateAmountTypeType());

                    // Set the new RateType object:
                    rateTypeModel.setRateType(newRateType);
                } catch (Exception e) {
                    // Set the error message:
                    form.setErrorMessage(String.format("Cannot create Rate Type. %s", StringUtils.defaultString(e.getMessage())));
                }
            }
        } else {

            // Find an existing RateType:
            RateTypeModel rateTypeModel = findRateTypeModel(form, rateTypeId, false);

            if (rateTypeModel != null) {

                // Update the editable fields:
                RateType rateType = rateTypeModel.getRateType();

                rateType.setName(rateTypeModel.getName());
                rateType.setDescription(rateTypeModel.getDescription());
                rateType.setGrouping(rateTypeModel.isGrouping());

                if (rateTypeModel.getRateAmountTypeType() != null) {
                    rateType.setRateAmountType(rateTypeModel.getRateAmountTypeType());
                }

                try {
                    // Persist the RateType object:
                    rateService.persistRateType(rateType);
                } catch (Exception e) {
                    // Set the error message:
                    form.setErrorMessage(String.format("Cannot save Rate Type. %s", StringUtils.defaultString(e.getMessage())));
                }
            } else {
                // Set an error message:
                form.setErrorMessage("Cannot save this Rate Type. Rate Type ID is not found.");
            }
        }

        // Refresh the data model:
        refreshDataModel(form);

        return getUIFModelAndView(form);
    }


    /*****************************************************************************
     *
     * Helper methods.
     *
     *****************************************************************************/

    /**
     * Refreshes the data model and sets on the form object.
     *
     * @param form The form object.
     */
    private void refreshDataModel(RateTypeForm form) {

        // Loads all RateTypes:
        List<RateType> allRateTypes = rateService.getAllRateTypes();

        // Created RateTypeModel objects:
        List<RateTypeModel> rateTypeModels = new ArrayList<RateTypeModel>();

        for (RateType rateType : allRateTypes) {

            // Create a new RateTypeModel and add to the list:
            rateTypeModels.add(new RateTypeModel(rateType));
        }

        // Add a blank line at the top:
        rateTypeModels.add(0, new RateTypeModel());

        // Set the list of RateTypeModel objects and set on the form object:
        form.setRateTypes(rateTypeModels);
    }

    /**
     * Finds a RateTypeModel with the given RateType ID in the form's list of RateTypeModels.
     *
     * @param form           Form object containing RateTypes.
     * @param rateTypeId     ID of a RateType to locate.
     * @param removeFromList Whether to remove the located RateTypeModel from the list.
     * @return The RateType found or <code>null</code> if none found.
     */
    private RateTypeModel findRateTypeModel(RateTypeForm form, Long rateTypeId, boolean removeFromList) {

        // Find the RateType with the given ID and delete it. Skip the rest:
        if (CollectionUtils.isNotEmpty(form.getRateTypes())) {

            for (Iterator<RateTypeModel> itRateTypeModels = form.getRateTypes().iterator(); itRateTypeModels.hasNext(); ) {

                // Get the next RateTypeModel
                RateTypeModel rateTypeModel = itRateTypeModels.next();

                // Compare the ID to find a match:
                if (CommonUtils.nullSafeCompare(rateTypeModel.getId(), rateTypeId) == 0) {

                    // Optionally, remove the RateTypeModel from the list:
                    if (removeFromList) {
                        itRateTypeModels.remove();
                    }

                    return rateTypeModel;
                }
            }
        }

        return null;
    }
}
