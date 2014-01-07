package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.SearchForm;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.service.InformationService;
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
 * Created by: dmulderink on 8/29/12 at 12:58 PM
 */
@Controller
@RequestMapping(value = "/searchView")
public class SearchController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(SearchController.class);

    @Autowired
    protected InformationService informationService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected SearchForm createInitialForm(HttpServletRequest request) {

        SearchForm form = new SearchForm();
        // page can be used for editing or viewing
        form.setOvDetailReadWriteState("true");
        String userId = request.getParameter("userId");

        if (userId != null) {

            Account account = accountService.getFullAccount(userId);

            if (account == null) {
                String errMsg = "Cannot find Account by ID = " + userId;
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            form.setAccount(account);
        }/* else {
          String errMsg = "'userId' request parameter cannot be null";
          logger.error(errMsg);
          throw new IllegalStateException(errMsg);
       }*/

        return form;
    }

    /**
     * @param form
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") SearchForm form, HttpServletRequest request) {

        // do get stuff...
        String pageId = request.getParameter("pageId");
        String userId = request.getParameter("userId");
        String transactionId = request.getParameter("id");


        return getUIFModelAndView(form);
    }

}
