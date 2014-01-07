package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.AdminForm;
import com.sigmasys.kuali.ksa.model.Activity;
import com.sigmasys.kuali.ksa.model.GlTransaction;
import com.sigmasys.kuali.ksa.model.GlTransactionStatus;
import com.sigmasys.kuali.ksa.service.ActivityService;
import com.sigmasys.kuali.ksa.service.GeneralLedgerService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by: dmulderink on 10/5/12 at 6:55 PM
 */
@Controller
@RequestMapping(value = "/adminView")
public class AdminController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(AdminController.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private GeneralLedgerService glService;


    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected AdminForm createInitialForm(HttpServletRequest request) {
        return new AdminForm();
    }

    /**
     * @param form    AdminForm
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") AdminForm form, HttpServletRequest request) {

        String pageId = request.getParameter("pageId");
        logger.info("Admin Controller: Page '" + pageId + "'");

        if ("SystemActivity".equals(pageId)) {
            String limitString = form.getActivityLimit();
            int limit = -1;
            try {
                limit = Integer.parseInt(limitString);
            } catch(Throwable t) {
                // don't care what happens if it wasn't a valid int.  Treat it as unlimited.
            }

            List<Activity> activities = activityService.getActivities(limit);
            activities = this.filterActivities(form, activities);
            form.setActivities(activities);
            return getUIFModelAndView(form);
        } else if ("TransactionsExport".equals(pageId)) {
            List<GlTransaction> transactions = glService.getGlTransactionsByStatus(GlTransactionStatus.QUEUED);
            form.setGlTransactions(transactions);
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=filter")
    public ModelAndView filter(@ModelAttribute("KualiForm") AdminForm form, HttpServletRequest request) {

        String pageId = request.getParameter("pageId");
        logger.info("Admin Controller: Page '" + pageId + "'");

        if ("SystemActivity".equals(pageId)) {
            String limitString = form.getActivityLimit();
            int limit = -1;
            try {
                limit = Integer.parseInt(limitString);
            } catch(Throwable t) {
                // don't care what happens if it wasn't a valid int.  Treat it as unlimited.
            }

            List<Activity> activities = activityService.getActivities(limit);
            activities = this.filterActivities(form, activities);
            form.setActivities(activities);
        }

        return getUIFModelAndView(form);
    }

    private List<Activity> filterActivities(AdminForm form, List<Activity> activities) {

        List<Activity> filteredActivities = new ArrayList<Activity>();

        Date start = form.getStartingDate();
        Date end = form.getEndingDate();

        for (Activity a : activities) {

            a.setLogDetail(StringEscapeUtils.escapeHtml(a.getLogDetail()));
            //a.setLogDetail(a.getLogDetail().replaceAll("'", "&#39;"));

            Date timestamp = a.getTimestamp();

            if (start != null && (DateTimeComparator.getDateOnlyInstance().compare(start, timestamp) > 0)) {
                continue;
            }

            if (end != null && (DateTimeComparator.getDateOnlyInstance().compare(end, timestamp) < 0)) {
                continue;
            }

            filteredActivities.add(a);
        }

        return filteredActivities;
    }


}
