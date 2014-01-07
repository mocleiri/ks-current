package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.ReportReconciliationForm;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.service.GeneralLedgerService;
import com.sigmasys.kuali.ksa.service.ReportService;
import com.sigmasys.kuali.ksa.service.TransactionExportService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by: dmulderink on 10/6/12 at 2:24 PM
 */
@Controller
@RequestMapping(value = "/reportReconciliationView")
public class ReportReconciliationController extends DownloadController {

    private static final Log logger = LogFactory.getLog(ReportReconciliationController.class);

    @Autowired
    protected GeneralLedgerService generalLedgerService;

    @Autowired
    protected TransactionExportService transactionExportService;

    @Autowired
    private ReportService reportService;


    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected ReportReconciliationForm createInitialForm(HttpServletRequest request) {
        ReportReconciliationForm form = new ReportReconciliationForm();
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
    public ModelAndView get(@ModelAttribute("KualiForm") ReportReconciliationForm form) {
        return getUIFModelAndView(form);
    }


    /**
     * Exports all Pending Transactions to the General ledger.
     *
     * @param form The form object.
     * @return The page's form.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=exportAllPendingTransactions")
    public ModelAndView exportAllPendingTransactions(@ModelAttribute("KualiForm") ReportReconciliationForm form) {

        // Call the service to export all pending transactions:
        transactionExportService.exportTransactions();

        return getUIFModelAndView(form);
    }

    /**
     * Makes all KSA Transactions effective.
     *
     * @param form The form object.
     * @return The page's form.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=makeTransactionsEffective")
    public ModelAndView makeTransactionsEffective(@ModelAttribute("KualiForm") ReportReconciliationForm form) {

        try {

            int size = transactionService.makeAllTransactionsEffective(false);

            String msg = (size > 0) ? size + " transactions have been made effective" : "No transactions have been made effective";

            GlobalVariables.getMessageMap().putInfo(getUIFModelAndView(form).getViewName(), RiceKeyConstants.ERROR_CUSTOM, msg);

        } catch (Exception e) {
            return handleError(form, e);
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=agedTransactions")
    public ModelAndView exportAgedBalanceReport(@ModelAttribute("KualiForm") ReportReconciliationForm form, HttpServletResponse response) throws IOException {

        List<Account> accounts = accountService.getAccountsByNamePattern("");

        List<String> userId = new ArrayList<String>(accounts.size());

        for (Account account : accounts) {
            userId.add(account.getId());
        }

        String report = reportService.generateAgedBalanceReport(userId, false, false);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Date today = Calendar.getInstance().getTime();

        String reportDate = df.format(today);

        String filename = reportDate + "_Aged_Balance_List.xml";

        // Set the response headers
        response.setContentType("application/xml");
        response.setContentLength(report.length());
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        // Copy the input stream to the response
        FileCopyUtils.copy(IOUtils.toInputStream(report), response.getOutputStream());


        return getUIFModelAndView(form);

    }


}
