package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.RefundForm;
import com.sigmasys.kuali.ksa.krad.util.AccountUtils;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.PaymentService;
import com.sigmasys.kuali.ksa.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * The Controller for the Account Refund view.
 * This controller alters the behavior of the Batch Refund view controller
 * by using an individual Account instead of a collection of Accounts.
 *
 * @author Sergey Godunov
 */
@Controller
@RequestMapping(value = "/accountRefund")
public class AccountRefundController extends BatchRefundController {

    /**
     * Generates an initial form object for the Account Refund page.
     *
     * @param request Request object.
     * @return The initial form.
     */
    @Override
    protected RefundForm createInitialForm(HttpServletRequest request) {

        // call the super-implementation:
        RefundForm form = super.createInitialForm(request);

        form.setBatch(false);

        // Initialize the Account:
        String userId = request.getParameter("userId");
        Account account = accountService.getFullAccount(userId);

        if (account != null) {
            form.setAccount(account);
            form.setAccounts(Arrays.asList(account));
        }

        // Set statistics:
        AccountUtils.populateTransactionHeading(form, userId);

        return form;
    }

}
