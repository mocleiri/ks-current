package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.RateRolloverForm;
import com.sigmasys.kuali.ksa.service.fm.RateService;
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
        RateRolloverForm form = new RateRolloverForm();

        return form;
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=displayInitialPage")
    public ModelAndView displayInitialPage(@ModelAttribute("KualiForm") RateRolloverForm form) {

        return getUIFModelAndView(form);
    }
}
