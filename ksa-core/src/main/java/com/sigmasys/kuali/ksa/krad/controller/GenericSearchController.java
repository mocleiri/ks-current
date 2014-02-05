package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.AbstractViewModel;
import com.sigmasys.kuali.ksa.model.SearchTypeValue;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.TransactionService;
import com.sigmasys.kuali.ksa.util.ErrorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


/**
 * GenericSearchController
 *
 * @author Michael Ivanov
 */
public abstract class GenericSearchController extends UifControllerBase {

    private static final String ACCOUNT_SEARCH_PAGE_ID = "AccountSearchPage";
    private static final String TRANSACTION_SEARCH_PAGE_ID = "TransactionSearchPage";

    protected Log logger = LogFactory.getLog(getClass());


    @Autowired
    protected AccountService accountService;

    @Autowired
    protected TransactionService transactionService;


    /**
     * This method can search for entities of different types specified by "searchType" and "searchValue" parameters
     *
     * @param form KSA form
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=searchByType")
    public ModelAndView searchByType(@ModelAttribute("KualiForm") AbstractViewModel form) {

        String searchType = form.getSearchType();
        if (searchType == null || searchType.isEmpty()) {
            throw new IllegalStateException("Search type is null");
        }

        String searchValue = form.getSearchValue();
        if (searchValue == null) {
            searchValue = "";
        }

        logger.debug("Search value = " + searchValue);

        SearchTypeValue searchTypeValue = SearchTypeValue.valueOf(searchType);
        switch (searchTypeValue) {
            case ACCOUNT:
                //form.setAccounts(accountService.findAccountsByNamePattern(searchValue));
                form.setAccounts(accountService.findAccountsByExpandedSearchPatterns(searchValue.split(" ")));
                form.setPageId(ACCOUNT_SEARCH_PAGE_ID);
                break;
            case TRANSACTION:
                form.setTransactions(transactionService.findTransactionsByStatementPattern(searchValue));
                form.setPageId(TRANSACTION_SEARCH_PAGE_ID);
                break;
            default:
                throw new IllegalStateException("Search type '" + searchTypeValue + "' is not supported");
        }

        return getUIFModelAndView(form);
    }

    protected ModelAndView handleError(AbstractViewModel viewModel, String errorMessage) {
        logger.error(errorMessage);
        GlobalVariables.getMessageMap().putError(viewModel.getViewId(), RiceKeyConstants.ERROR_CUSTOM, errorMessage);
        return getUIFModelAndView(viewModel);
    }

    protected ModelAndView handleError(AbstractViewModel viewModel, Throwable t) {
        return handleError(viewModel, ErrorUtils.getMessage(t));
    }

    protected void setMessage(AbstractViewModel viewModel, String message) {
        GlobalVariables.getMessageMap().putInfo(viewModel.getViewId(), RiceKeyConstants.ERROR_CUSTOM, message);
    }

}
