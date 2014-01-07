package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.AlertForm;
import com.sigmasys.kuali.ksa.krad.util.AccountUtils;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.Alert;
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
import java.util.Date;

/**
 * Created by: dmulderink on 10/6/12 at 2:28 PM
 */
@Controller
@RequestMapping(value = "/alertView")
public class AlertController extends GenericSearchController {

   private static final Log logger = LogFactory.getLog(AlertController.class);

   @Autowired
   private InformationService informationService;
   
   /**
    * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
    */
   @Override
   protected AlertForm createInitialForm(HttpServletRequest request) {
      AlertForm form = new AlertForm();
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
    * @param request
    * @return
    */
   @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
   public ModelAndView get(@ModelAttribute("KualiForm") AlertForm form, HttpServletRequest request) {

      // do get stuff...

      String pageId = request.getParameter("pageId");
      // example user 1
      String userId = request.getParameter("userId");

      if (pageId != null && pageId.equals("AlertsPage")) {

         if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("'userId' request parameter must be specified");
         }

          AccountUtils.populateTransactionHeading(form, userId);

      } else if (pageId != null && pageId.equals("ViewAlertPage")) {
         if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("'userId' request parameter must be specified");
         }

         form.setInstructionalText("View an alert");

      } else if (pageId != null && pageId.equals("EditAlertPage")) {
         if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("'userId' request parameter must be specified");
         }

         form.setInstructionalText("Edit an alert");

      }

      return getUIFModelAndView(form);
   }

   /**
    *
    * @param form
    * @return
    */
   @RequestMapping(method= RequestMethod.POST, params="methodToCall=refresh")
   public ModelAndView refresh(@ModelAttribute("KualiForm") AlertForm form) {
      // do refresh stuff...
      return getUIFModelAndView(form);
   }

   /**
    * @param form
    * @return
    */
   @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addAlert")
   public ModelAndView insertAlert(@ModelAttribute("KualiForm") AlertForm form) {
      // do insert stuff...

      // TODO validate the field entries before inserting

      // the account should be satisfied by the link that got us into this page
      Account account = form.getAccount();

      Alert alert = form.getAlert();
      alert.setAccount(account);
      alert.setAccountId(account.getId());
      alert.setCreationDate(new Date());
      alert.setLastUpdate(new Date());
      //info.setCreatorId();
      //info.setCreatorId();
      informationService.persistInformation(alert);

      return getUIFModelAndView(form);
   }

   /**
    * @param form
    * @return
    */
   @RequestMapping(method = RequestMethod.POST, params = "methodToCall=updateAlert")
   public ModelAndView updateAlert(@ModelAttribute("KualiForm") AlertForm form) {
      // do refresh stuff...
      return getUIFModelAndView(form);
   }

}
