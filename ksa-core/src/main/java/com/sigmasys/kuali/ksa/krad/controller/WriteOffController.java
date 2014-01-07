package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.WriteOffForm;
import com.sigmasys.kuali.ksa.model.Account;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by: dmulderink on 10/6/12 at 2:30 PM
 */
@Controller
@RequestMapping(value = "/writeOffView")
public class WriteOffController extends GenericSearchController {

   private static final Log logger = LogFactory.getLog(WriteOffController.class);
   
   /**
    * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
    */
   @Override
   protected WriteOffForm createInitialForm(HttpServletRequest request) {

      WriteOffForm form = new WriteOffForm();
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
   public ModelAndView get(@ModelAttribute("KualiForm") WriteOffForm form) {
      return getUIFModelAndView(form);
   }


}
