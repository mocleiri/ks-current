package com.sigmasys.kuali.ksa.krad.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sigmasys.kuali.ksa.krad.model.AccountSearchResultModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.sigmasys.kuali.ksa.krad.form.AdminForm;
import com.sigmasys.kuali.ksa.krad.model.AccountSearchInformationHolder;
import com.sigmasys.kuali.ksa.model.*;

/**
 * This class handles searches for a personal or organizational Accounts.
 *
 * @author Sergey
 */
@Controller
@RequestMapping(value = "/accountSearch")
public class AccountSearchController extends AccountManagementController {


    /**
     * Handles display of the Search Person Account page.
     *
     * @param form AdminForm
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=searchPersonAccount")
    public ModelAndView searchPersonAccount(@ModelAttribute("KualiForm") AdminForm form) {

        // Populate the form:
        populateForSearchPersonAccount(form);

        return getUIFModelAndView(form);
    }

    /**
     * Handles Person Account search and redirection to the search result page.
     *
     * @param form AdminForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=doSearchPersonAccount")
    public ModelAndView doSearchPersonAccount(@ModelAttribute("KualiForm") AdminForm form) {

        // TODO: Inspect the search parameters

        // TODO: Search for Accounts: Temporarily load all Accounts to have some data available for testing:
        List<Account> matchingAccounts = accountService.getFullAccounts();

        // TODO: Prepare the result:
        List<AccountSearchResultModel> accountSearchResults = prepareDisplayLines(matchingAccounts);

        form.setAccountSearchResults(accountSearchResults);

        // Navigate to the Search Result page:
        form.setPageId("SearchAccountResultsPage");

        return getUIFModelAndView(form);
    }


    /* *******************************************************************************************************************
      *
      * Helper methods.
      *
      * ******************************************************************************************************************/

    /*
      * Populates the specified form for a Search Person Account page.
      */
    private void populateForSearchPersonAccount(AdminForm form) {

        // Populate for New Person Account first:
        populateForNewPersonAccount(form);

        // Add additional Search specific info:
        AccountSearchInformationHolder accountSearchInfo = new AccountSearchInformationHolder();

        accountSearchInfo.setLastNameSubstringSearch(true);
        accountSearchInfo.setDobDateRange(new Pair<Date, Date>());
        accountSearchInfo.setLastUpdateDateRange(new Pair<Date, Date>());
        accountSearchInfo.setCreationDateRange(new Pair<Date, Date>());
        accountSearchInfo.setUserPreference(new UserPreference());
        accountSearchInfo.setSearchResultFields(new ArrayList<String>());
        form.setAccountSearchInfo(accountSearchInfo);
        form.getAccountInfo().setAccountType(null);
    }

    /**
     * Prepares a <code>List</code> of Account search result line items suitable for display.
     *
     * @param accounts Accounts - search results to be converted to display elements.
     * @return A <code>List</code> of display elements.
     */
    private List<AccountSearchResultModel> prepareDisplayLines(List<Account> accounts) {

        // Create the result collection:
        List<AccountSearchResultModel> lines = new ArrayList<AccountSearchResultModel>();

        // Add elements to the resultant collection:
        for (Account account : accounts) {

            // Create a new line item:
            AccountSearchResultModel line = new AccountSearchResultModel();
            AccountProtectedInfo accountProtectedInfo = accountService.getAccountProtectedInfo(account.getId());

            line.setAccount(account);
            line.setAccountProtectedInfo(accountProtectedInfo);
            line.setDateOfBirth(getAccountDateOfBirth(account));
            lines.add(line);
        }

        return lines;
    }
}
