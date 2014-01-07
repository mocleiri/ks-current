package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.RateCatalogForm;
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


    /**
     * Creates the page's form.
     */
    @Override
    protected RateCatalogForm createInitialForm(HttpServletRequest request) {
        RateCatalogForm form = new RateCatalogForm();

        return form;
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=displayInitialPage")
    public ModelAndView displayInitialPage(@ModelAttribute("KualiForm") RateCatalogForm form) {

        return getUIFModelAndView(form);
    }
}
