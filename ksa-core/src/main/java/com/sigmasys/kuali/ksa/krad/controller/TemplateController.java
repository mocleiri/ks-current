package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.TemplateForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * A template that can be used as an example to create other controllers.
 *
 * @author Michael Ivanov
 */
@Controller
@RequestMapping(value = "/templateView")
public class TemplateController extends GenericSearchController {

    public static final String FIRST_PAGE_ID = "Page1";
    public static final String SECOND_PAGE_ID = "Page2";
    public static final String DEFAULT_PAGE_ID = FIRST_PAGE_ID;


    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected TemplateForm createInitialForm(HttpServletRequest request) {
        TemplateForm templateForm = new TemplateForm();
        templateForm.setPageId(DEFAULT_PAGE_ID);
        return templateForm;
    }

    /**
     * @param form
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") TemplateForm form, HttpServletRequest request) {

        String pageId = request.getParameter("pageId");
        if (pageId != null) {
            form.setPageId(pageId);
        }

        // TODO: provide business logic that may depend on 'pageId" request parameter

        return getUIFModelAndView(form);

    }


    /**
     * @param form
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=submit")
    public ModelAndView submit(@ModelAttribute("KualiForm") TemplateForm form) {

        // TODO: add logic to process the form parameters and perform submit

        form.setPageId(SECOND_PAGE_ID);

        return getUIFModelAndView(form);

    }
}
