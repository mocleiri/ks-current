package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.RatesForm;
import com.sigmasys.kuali.ksa.model.fm.Rate;
import com.sigmasys.kuali.ksa.service.atp.AtpService;
import com.sigmasys.kuali.ksa.service.fm.RateService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if(!atpService.atpExists(atp)) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Academic Time Period '" + atp + "' not found");
            return getUIFModelAndView(form);
        }

        List<Rate> atpRates = rateService.getRatesByAtpId(atp);
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

        form.setRates(rateList);

        return getUIFModelAndView(form);
    }
}
