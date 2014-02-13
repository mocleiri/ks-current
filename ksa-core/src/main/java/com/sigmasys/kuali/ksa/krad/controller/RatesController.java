package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.RatesForm;
import com.sigmasys.kuali.ksa.krad.helper.RateRolloverHelper;
import com.sigmasys.kuali.ksa.krad.model.AtpModel;
import com.sigmasys.kuali.ksa.krad.model.RateModel;
import com.sigmasys.kuali.ksa.model.fm.Rate;
import com.sigmasys.kuali.ksa.model.fm.RateCatalog;
import com.sigmasys.kuali.ksa.model.fm.TransactionDateType;
import com.sigmasys.kuali.ksa.service.PersistenceService;
import com.sigmasys.kuali.ksa.service.atp.AtpService;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import com.sigmasys.kuali.ksa.util.BeanUtils;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
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
 * Controller to serve requests from the "Rates" page.
 * User: Sergey
 * Date: 12/4/13
 * Time: 10:53 PM
 */
@Controller
@RequestMapping(value = "/ratesView")
public class RatesController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(RatesController.class);

    @Autowired
    private RateService rateService;

    @Autowired
    private AtpService atpService;

    @Autowired
    private PersistenceService persistenceService;


    /**
     * Creates the page's form.
     */
    @Override
    protected RatesForm createInitialForm(HttpServletRequest request) {
        RatesForm form = new RatesForm();

        return form;
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=displayInitialPage")
    public ModelAndView displayInitialPage(@ModelAttribute("KualiForm") RatesForm form) {

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=getRates")
    public ModelAndView getRates(@ModelAttribute("KualiForm") RatesForm form) {

        Set<Rate> rates = new HashSet<Rate>();

        String atp = form.getAcademicTimePeriod();
        AtpModel atpModel = null;
        RateRolloverHelper helper = new RateRolloverHelper();

        try {
            List<AtpModel> atpModels = helper.searchForAtps(atp);
            if(atpModels == null || atpModels.size() == 0) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Academic Time Period '" + atp + "' not found");
                return getUIFModelAndView(form);
            } else if(atpModels.size() > 1) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Multiple Academic Time Periods match string '" + atp + "'");
                return getUIFModelAndView(form);
            } else {
                atpModel = atpModels.get(0);
            }
        } catch(Exception e) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Academic Time Period '" + atp + "' not found");
            return getUIFModelAndView(form);
        }
        List<Rate> atpRates = rateService.getRatesByAtpId(atpModel.getAtpId());
        rates.addAll(atpRates);

        String catalogCode = form.getRateCatalogCode();

        Long rateCatalogId;
        try {
            rateCatalogId = Long.valueOf(catalogCode);
        } catch(NumberFormatException e) {
            logger.error("Error converting '" + catalogCode + "' to a rate catalog id");
            rateCatalogId = null;
        }

        if(rateCatalogId != null) {
            List<Rate> catalogCodeRates = rateService.getRatesByCatalogId(rateCatalogId);
            rates.retainAll(catalogCodeRates);
        }

        String rateCode = form.getRateCode();
        String rateSubcode = form.getRateSubcode();

        if(rateCode != null && !"".equals(rateCode) && (rateSubcode == null || "".equals(rateSubcode))) {
            List<Rate> codeRates = rateService.getRatesByCode(rateCode);
            rates.retainAll(codeRates);
        } else if(rateCode != null && !"".equals(rateCode) && rateSubcode != null && !"".equals(rateSubcode)) {
            List<Rate> codeSubcodeRates = rateService.getRatesByCodeAndSubCode(rateCode, rateSubcode);
            rates.retainAll(codeSubcodeRates);
        }

        List<Rate> rateList = new ArrayList<Rate>();
        rateList.addAll(rates);
        rateList.add(0, new Rate());

        List<RateModel> rateModels = new ArrayList<RateModel>(rateList.size());
        for(Rate rate : rateList) {
            RateModel m = new RateModel(rate);

            RateCatalog rc = rateService.getRateCatalogByRateId(rate.getId());
            m.setRateCatalog(rc);

            // this should always be true but just in case, don't set the atp if it doesn't match.
            if(atpModel.getAtpId().equals(rate.getAtpId())) {
                m.setAtp(atpModel);
            }

            rateModels.add(m);
        }
        form.setRates(rateModels);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=saveRate")
    public ModelAndView saveRate(@ModelAttribute("KualiForm") RatesForm form, @RequestParam("rateId") Long rateId) {
        RateModel rateModel = findRate(form, rateId);

        if(rateModel == null) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Rate with id '" + rateId + "' not found");
            return getUIFModelAndView(form);
        }

        try{
            rateService.persistRate(rateModel.getRate());
            GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Rate saved");
        } catch(RuntimeException e) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=rolloverRate")
    public ModelAndView rolloverRate(@ModelAttribute("KualiForm") RatesForm form, @RequestParam("rateId") Long rateId) {
        RateModel rateModel = findRate(form, rateId);

        if(rateModel == null) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Rate with id '" + rateId + "' not found");
            return getUIFModelAndView(form);
        }

        Rate newRate = BeanUtils.getDeepCopy(rateModel.getRate());
        newRate.setId(null);
        newRate.getDefaultRateAmount().setId(null);


        String rolloverAcademicPeriod = rateModel.getRolloverAcademicPeriod();
        if(rolloverAcademicPeriod != null) {
            newRate.setAtpId(rolloverAcademicPeriod);
        }

        String rolloverTransactionDateType = rateModel.getRolloverTransactionDateType();
        if(rolloverTransactionDateType != null) {
            newRate.setTransactionDateType(EnumUtils.findById(TransactionDateType.class, rolloverTransactionDateType));
        }

        Date rolloverTransactionDate = rateModel.getRolloverTransactionDate();
        if(rolloverTransactionDate != null) {
            newRate.setTransactionDate(rolloverTransactionDate);
        }

        Date rolloverRecognitionDate = rateModel.getRolloverRecognitionDate();
        if(rolloverRecognitionDate != null) {
            newRate.setRecognitionDate(rolloverRecognitionDate);
        }

        try {
            persistenceService.persistEntity(newRate.getDefaultRateAmount());
            rateService.persistRate(newRate);
            GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Rate successfully rolled over");
        } catch(RuntimeException e) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
        }

        return getUIFModelAndView(form);
    }

    private RateModel findRate(RatesForm form, Long rateId) {
        for(RateModel model : form.getRates()) {
            if(rateId == null && model.getRate().getId() == null) {
                return model;
            }
            if(model.getRate().getId() != null && rateId.equals(model.getRate().getId())) {
                return model;
            }
        }
        return null;
    }


}
