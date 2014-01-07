package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.DefermentForm;
import com.sigmasys.kuali.ksa.model.Account;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by: dmulderink on 10/6/12 at 2:29 PM
 */
@Controller
@RequestMapping(value = "/defermentView")
public class DefermentController extends GenericSearchController {

   private static final Log logger = LogFactory.getLog(DefermentController.class);
   
   /**
    * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
    */
   @Override
   protected DefermentForm createInitialForm(HttpServletRequest request) {
      DefermentForm form = new DefermentForm();
      String userId = request.getParameter("userId");

      if (userId != null) {

         Account account = accountService.getFullAccount(userId);

         if (account == null) {
            String errMsg = "Cannot find Account by ID = " + userId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
         }

         form.setAccount(account);
      } /*else {
           String errMsg = "'userId' request parameter cannot be null";
           logger.error(errMsg);
           throw new IllegalStateException(errMsg);
        }*/

      return form;
   }

   /**
    *
    * @param form
    * @return
    */
   @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
   public ModelAndView get(@ModelAttribute("KualiForm") DefermentForm form) {
      return getUIFModelAndView(form);
   }

}
