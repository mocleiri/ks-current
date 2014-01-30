package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.GenerateBillForm;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.Rollup;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.ReportService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by: dmulderink on 10/6/12 at 2:24 PM
 */
@Controller
@RequestMapping(value = "/generateBillView")
public class GenerateBillController extends DownloadController {

    private static final Log logger = LogFactory.getLog(GenerateBillController.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private AuditableEntityService auditableEntityService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected GenerateBillForm createInitialForm(HttpServletRequest request) {
        GenerateBillForm form = new GenerateBillForm();
        String userId = request.getParameter("userId");

        if (userId != null) {

            Account account = accountService.getFullAccount(userId);

            if (account == null) {
                String errMsg = "Cannot find Account by ID = " + userId;
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            form.setAccount(account);
        }

        return form;
    }

    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") GenerateBillForm form) {
        return getUIFModelAndView(form);
    }


    /**
     * Exports all Pending Transactions to the General ledger.
     *
     * @param form The form object.
     * @return The page's form.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=exportBill")
    public ModelAndView exportBill(@ModelAttribute("KualiForm") GenerateBillForm form, HttpServletResponse response) throws IOException {

        List<Rollup> sameDayRollups = form.getRollupsOnSameDate();
        Set<Long> sameDayRollupIds = new HashSet<Long>(sameDayRollups.size());
        for(Rollup rollup : sameDayRollups) {
            sameDayRollupIds.add(rollup.getId());
        }

        List<Rollup> sameStatementRollups = form.getRollupsOnSameStatement();
        Set<Long> sameStatementRollupsIds = new HashSet<Long>(sameStatementRollups.size());
        for(Rollup rollup : sameStatementRollups) {
            sameStatementRollupsIds.add(rollup.getId());
        }

        String xmlResponse = reportService.generateBill(form.getBillAccountId(), form.getBillMessage(),
                                                    form.getBillDate(), form.getStartDate(), form.getStartDate(),
                                                    sameDayRollupIds, sameStatementRollupsIds,
                                                    form.getShowOnlyUnbilledTransactions(),
                                                    form.getShowDeferments(), form.getShowDependents(),
                                                    form.getShowInternalTransactions(), form.getRunPaymentApplication());

        String filename = form.getBillAccountId() + "_Bill.xml";

        //            doDownload(xmlResponse, filename, "application/xml", response);
        // Set the response headers
        response.setContentType("application/xml");
        response.setContentLength(xmlResponse.length());
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        // Copy the input stream to the response
        FileCopyUtils.copy(IOUtils.toInputStream(xmlResponse), response.getOutputStream());


        //return getUIFModelAndView(form);
        return null;

    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addSameDayRollup")
    public ModelAndView addSameDayRollup(@ModelAttribute("KualiForm") GenerateBillForm form, HttpServletRequest request) {

        String name = form.getNewSameDayRollup();

        List<Rollup> rollups = auditableEntityService.getAuditableEntitiesByNamePattern(name, Rollup.class);

        if(rollups != null && rollups.size() > 0) {
            form.getRollupsOnSameDate().add(rollups.get(0));
            form.setNewSameDayRollup("");
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addSameStatementRollup")
    public ModelAndView addSameStatementRollup(@ModelAttribute("KualiForm") GenerateBillForm form, HttpServletRequest request) {

        String name = form.getNewSameStatementRollup();

        List<Rollup> rollups = auditableEntityService.getAuditableEntitiesByNamePattern(name, Rollup.class);

        if(rollups != null && rollups.size() > 0) {
            form.getRollupsOnSameStatement().add(rollups.get(0));
            form.setNewSameStatementRollup("");
        }

        return getUIFModelAndView(form);
    }

}
