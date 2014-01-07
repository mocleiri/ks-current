package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.SponsorForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by: dmulderink on 8/27/12 at 8:40 AM
 */
@Controller
@RequestMapping(value = "/sponsorView")
public class SponsorController extends GenericSearchController {

   /**
    * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
    */
   @Override
   protected SponsorForm createInitialForm(HttpServletRequest request) {
      return new SponsorForm();
   }

   /**
    *
    * @param form
    * @return
    */
   @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
   public ModelAndView get(@ModelAttribute("KualiForm") SponsorForm form) {
      return getUIFModelAndView(form);
   }
}
