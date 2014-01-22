package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.exception.GenericException;
import com.sigmasys.kuali.ksa.krad.form.RateCatalogForm;
import com.sigmasys.kuali.ksa.krad.model.RateCatalogModel;
import com.sigmasys.kuali.ksa.krad.util.AuditableEntityKeyValuesFinder;
import com.sigmasys.kuali.ksa.model.KeyPair;
import com.sigmasys.kuali.ksa.model.fm.RateCatalog;
import com.sigmasys.kuali.ksa.model.fm.RateType;
import com.sigmasys.kuali.ksa.service.PersistenceService;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Controller to server requests from the "Rate Catalog" page.
 * User: Sergey
 * Date: 12/4/13
 * Time: 10:49 PM
 */
@Controller
@RequestMapping(value = "/rateCatalogView")
public class RateCatalogController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(RateCatalogController.class);

    @Autowired
    private RateService rateService;

    @Autowired
    private PersistenceService persistenceService;


    /**
     * Creates the page's form.
     */
    @Override
    protected RateCatalogForm createInitialForm(HttpServletRequest request) {
        RateCatalogForm form = new RateCatalogForm();

        form.setRateTypeOptionsFinder(getRateTypeOptionsFinder());

        return form;
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=displayInitialPage")
    public ModelAndView displayInitialPage(@ModelAttribute("KualiForm") RateCatalogForm form) {

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") RateCatalogForm form) {
        String rateTypeString = form.getRateTypeId();
        Long rateTypeId;
        try {
            rateTypeId = Long.valueOf(rateTypeString);
        } catch(NumberFormatException e) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, rateTypeString + " is not a valid rate type");
            return getUIFModelAndView(form);
        }

        List<RateCatalog> rateCatalogList = rateService.getRateCatalogsByRateTypeId(rateTypeId);
        List<RateCatalogModel> models = new ArrayList<RateCatalogModel>();
        for(RateCatalog rateCatalog : rateCatalogList) {
            models.add(new RateCatalogModel(rateCatalog));
        }

        // Add a blank line at the top:
        models.add(0, new RateCatalogModel());

        form.setRateCatalogs(models);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=saveRateCatalog")
    public ModelAndView saveRateCatalog(@ModelAttribute("KualiForm") RateCatalogForm form,
                                        @RequestParam("rateCatalogId") Long rateCatalogId) {

        RateCatalog rateCatalog = null;
        List<KeyPair> keyPairs = null;

        for(RateCatalogModel rateCatalogModel : form.getRateCatalogs()) {
            Long rowId = rateCatalogModel.getRateCatalog().getId();
            if(rateCatalogId == null && rowId == null) {
                rateCatalog = rateCatalogModel.getRateCatalog();
                keyPairs = rateCatalogModel.getKeyPairs();
                break;
            } else if(rateCatalogId.equals(rowId)) {
                rateCatalog = rateCatalogModel.getRateCatalog();
                keyPairs = rateCatalogModel.getKeyPairs();
                break;
            }
        }

        if(rateCatalog == null) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Unable to find rate catalog for id: " + rateCatalogId);
            return getUIFModelAndView(form);
        }

        if(rateCatalog.getId() == null) {
            rateCatalog = rateService.createRateCatalog(rateCatalog.getCode(), rateCatalog.getRateType().getCode(), rateCatalog.getTransactionTypeId(), rateCatalog.getTransactionDateType(),
                    rateCatalog.getMinAmount(), rateCatalog.getMaxAmount(), rateCatalog.getMinLimitAmount(), rateCatalog.getMaxLimitAmount(), rateCatalog.getMinLimitUnits(),
                    rateCatalog.getMaxLimitUnits(), null, null, rateCatalog.isTransactionTypeFinal(), rateCatalog.isTransactionDateTypeFinal(), rateCatalog.isRecognitionDateDefinable(),
                    rateCatalog.isKeyPairFinal(), rateCatalog.isLimitAmount(), rateCatalog.isLimitAmount());
        } else {
            try{
                rateService.validateRateCatalog(rateCatalog);
                rateService.persistRateCatalog(rateCatalog);
            } catch(RuntimeException e) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
            }
        }

        // Save the name value pairs
        if(keyPairs != null) {
            for(KeyPair keyPair : keyPairs) {
                persistenceService.persistEntity(keyPair);
            }
            rateCatalog.setKeyPairs(new HashSet<KeyPair>(keyPairs));
            try{
                rateService.persistRateCatalog(rateCatalog);
            } catch(RuntimeException e) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
            }
        }
        return getUIFModelAndView(form);
    }

    public KeyValuesFinder getRateTypeOptionsFinder() {
        // Don't cache the values finder or else new entries will not show when added
        return new AuditableEntityKeyValuesFinder<RateType>(RateType.class, true);
    }

}
