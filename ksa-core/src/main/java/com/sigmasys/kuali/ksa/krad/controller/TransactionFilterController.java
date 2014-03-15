package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.TransactionFilterForm;
import com.sigmasys.kuali.ksa.krad.util.DateRangeFilterKeyValuesFinder;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.Tag;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * This class contains all common behavior of a Transaction filter.
 * This controller extends the downloading capabilities of the DownloadController.
 *
 * @author Sergey Godunov
 */
@Controller
public abstract class TransactionFilterController extends DownloadController {


    @Autowired
    private AuditableEntityService auditableEntityService;


    /**
     * Refreshes the underlying data model. This action happens after a filter
     * is changes and a refresh is required to apply the changed filter.
     *
     * @param model The form object.
     */
    protected abstract <T extends TransactionFilterForm> void refreshModel(T model);


    /**
     * Called by the framework to create the initial form object.
     *
     * @param request HTTP request.
     * @return The initial form object.
     */
    @Override
    protected TransactionFilterForm createInitialForm(HttpServletRequest request) {
        TransactionFilterForm form = new TransactionFilterForm();
        initializeForm(form);
        return form;
    }

    /**
     * Account filtering support callback.
     *
     * @param form The form object.
     * @return ModelAndView.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=filterAccounts")
    public ModelAndView filterAccounts(@ModelAttribute("KualiForm") TransactionFilterForm form) {

        String newFilterAccountId = form.getNewAccount();
        Account newFilterAccount = accountService.getFullAccount(newFilterAccountId);
        List<Account> filterAccounts = form.getAccounts();

        if (filterAccounts == null) {
            filterAccounts = new ArrayList<Account>();
            form.setAccounts(filterAccounts);
        }

        if ((newFilterAccount != null) && !filterAccounts.contains(newFilterAccount)) {
            filterAccounts.add(newFilterAccount);
        }

        // Refresh the model:
        refreshModel(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Remove" icon is clicked next to a filter Account.
     *
     * @param form      The form object.
     * @param accountId ID of the account to be removed from the filter.
     * @return ModelAndView
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeFilterAccount")
    public ModelAndView removeFilterAccount(@ModelAttribute("KualiForm") TransactionFilterForm form,
                                            @RequestParam("accountId") String accountId) throws Exception {

        // Find the filter Account with the specified ID and remove it from filtering:
        List<Account> filterAccounts = form.getAccounts();

        if (filterAccounts != null) {

            for (Account filterAccount : filterAccounts) {
                if (StringUtils.equals(filterAccount.getId(), accountId)) {
                    filterAccounts.remove(filterAccount);

                    break;
                }
            }
        }

        // Refresh the model:
        refreshModel(form);

        return getUIFModelAndView(form);
    }

    /**
     * Tag filtering callback.
     *
     * @param form The form object.
     * @return ModelAndView.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=filterTags")
    public ModelAndView filterTags(@ModelAttribute("KualiForm") TransactionFilterForm form) throws Exception {

        String newFilterTagCode = form.getNewTag();
        Tag newFilterTag = auditableEntityService.getAuditableEntity(newFilterTagCode, Tag.class);
        List<Tag> filterTags = form.getFilterTags();

        if (filterTags == null) {
            filterTags = new ArrayList<Tag>();
            form.setFilterTags(filterTags);
        }

        if (newFilterTag != null) {
            filterTags.add(newFilterTag);
        }

        // Refresh the model:
        refreshModel(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Remove" icon is clicked next to a filter Tag.
     *
     * @param form  The form object.
     * @param tagId ID of the account to be removed from the filter.
     * @return ModelAndView
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=removeFilterTag")
    public ModelAndView removeFilterTag(@ModelAttribute("KualiForm") TransactionFilterForm form,
                                        @RequestParam("tagId") Long tagId) throws Exception {

        // Find the filter Tag with the specified ID and remove it from filtering:
        List<Tag> filterTags = form.getFilterTags();

        if ((filterTags != null) && (tagId != null)) {

            for (Tag filterTag : filterTags) {
                if (filterTag.getId().compareTo(tagId) == 0) {
                    filterTags.remove(filterTag);

                    break;
                }
            }
        }

        // Refresh the model:
        refreshModel(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked by the view to refresh the model.
     *
     * @param form The form object.
     * @return ModelAndView
     * @throws Exception If an error occurred.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=refreshView")
    public ModelAndView refreshView(@ModelAttribute("KualiForm") TransactionFilterForm form) throws Exception {

        // Refresh the model:
        refreshModel(form);

        return getUIFModelAndView(form);
    }


    /**
     * Performs form initialization.
     *
     * @param form A form object.
     */
    protected void initializeForm(TransactionFilterForm form) {

        // Set the defaults:
        Calendar calendar = Calendar.getInstance();

        form.setDateRangeType(DateRangeFilterKeyValuesFinder.ALL);
        form.setFilterDateTo(calendar.getTime());
        calendar.add(Calendar.YEAR, -1);
        form.setFilterDateFrom(calendar.getTime());
        form.setAccounts(new ArrayList<Account>());
    }


    /**
     * Returns a List of filter Account IDs of the given form object.
     *
     * @param form The form object.
     * @return A List of filter Account IDs.
     */
    protected List<String> getFilterAccountIds(TransactionFilterForm form) {

        List<Account> filterAccounts = form.getAccounts();
        List<String> filterAccountIds = null;

        if ((filterAccounts != null) && !filterAccounts.isEmpty()) {

            filterAccountIds = new ArrayList<String>(filterAccounts.size());

            for (Account account : filterAccounts) {
                filterAccountIds.add(account.getId());
            }
        }

        return filterAccountIds;
    }
}
