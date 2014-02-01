package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.AdminForm;
import com.sigmasys.kuali.ksa.krad.model.AccountInformationHolder;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.PersistenceService;
import com.sigmasys.kuali.ksa.service.UserPreferenceService;
import com.sigmasys.kuali.ksa.service.UserSessionManager;
import com.sigmasys.kuali.ksa.util.AccountUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;


// TODO - Implement a correct logic for new account creation

@Controller
@RequestMapping(value = "/accountManagement")
public class AccountManagementController extends GenericSearchController {

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private UserSessionManager userSessionManager;


    /**
     * Creates an initial form, which is the Admin page form.
     */
    @Override
    protected UifFormBase createInitialForm(HttpServletRequest request) {
        return new AdminForm();
    }

    /**
     * Handles creation of a new form for a new personal account.
     *
     * @param form Admin form.
     * @return The new form with a new personal account.
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=newPersonAccount")
    public ModelAndView newPersonAccount(@ModelAttribute("KualiForm") AdminForm form) {

        // Populate the form:
        populateForNewPersonAccount(form);

        return getUIFModelAndView(form);
    }

    /**
     * Cancels the current page and goes back to the landing page.
     *
     * @param form AdminForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=cancel")
    public ModelAndView returnToLandingPage(@ModelAttribute("KualiForm") AdminForm form) {

        // Nullify the Account and Account info:
        form.setAccount(null);
        form.setAccountInfo(null);
        form.setPageId("AccountManagement");
        form.setViewId("AdminView");

        return getUIFModelAndView(form);
    }

    /**
     * Handles saving of a new account.
     *
     * @param form AdminForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=saveNewPersonAccount")
    public ModelAndView saveNewPersonAccount(@ModelAttribute("KualiForm") AdminForm form) {

        // Get the objects to be persisted:
        Account account = setUpAccountFromForm(form);


        AccountProtectedInfo accountProtectedInfo = form.getAccountInfo().getAccountProtectedInfo();
        List<UserPreference> userPreferences = form.getAccountInfo().getUserPreferences();
        String accountId = account.getId();

        String statusTypeIdString = form.getAccountStatusTypeId();
        AccountStatusType statusType = null;
        if (statusTypeIdString != null && !"".equals(statusTypeIdString)) {
            try {
                Long statusTypeId = Long.valueOf(statusTypeIdString);
                statusType = auditableEntityService.getAuditableEntity(statusTypeId, AccountStatusType.class);
            } catch (Throwable e) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
            }
        }

        account.setStatusType(statusType);

        if (account.getOrgName() != null) {
            persistenceService.persistEntity(account.getOrgName());
        }


        // Persist the objects:
        persistenceService.persistEntity(account);

        String bankTypeIdString = form.getAccountBankTypeId();
        BankType bankType = null;
        if (bankTypeIdString != null && !"".equals(bankTypeIdString)) {
            try {
                Long bankTypeId = Long.valueOf(bankTypeIdString);
                bankType = auditableEntityService.getAuditableEntity(bankTypeId, BankType.class);
            } catch (Throwable e) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
            }
        }
        accountProtectedInfo.setBankType(bankType);

        String taxTypeIdString = form.getAccountTaxTypeId();
        TaxType taxType = null;
        if (taxTypeIdString != null && !"".equals(taxTypeIdString)) {
            try {
                Long taxTypeId = Long.valueOf(taxTypeIdString);
                taxType = auditableEntityService.getAuditableEntity(taxTypeId, TaxType.class);
            } catch (Throwable e) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
            }
        }
        accountProtectedInfo.setTaxType(taxType);

        String identityTypeString = form.getAccountIdentityTypeId();
        IdentityType identityType = null;
        if (identityTypeString != null && !"".equals(identityTypeString)) {
            try {
                Long identityTypeId = Long.valueOf(identityTypeString);
                identityType = auditableEntityService.getAuditableEntity(identityTypeId, IdentityType.class);
            } catch (Throwable e) {
                GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
            }
        }
        accountProtectedInfo.setIdentityType(identityType);

        accountProtectedInfo.setId(accountId);
        persistenceService.persistEntity(accountProtectedInfo);

        // Persist UserPreferences:
        for (UserPreference userPreference : userPreferences) {
            userPreference.setAccountId(accountId);
            userPreferenceService.persistUserPreference(userPreference);
        }

        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Account saved");

        // Return to the landing page:
        return returnToLandingPage(form);
    }

    /**
     * Handles edit of a person's account.
     *
     * @param form Admin form.
     * @return Page ModelAndView.
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=editPersonAccount")
    public ModelAndView editPersonAccount(@ModelAttribute("KualiForm") AdminForm form, @RequestParam("accountId") String accountId) {

        // Find the Account with the given ID:
        Account account = accountService.getFullAccount(accountId);

        // Populate the form with the Account details:
        populateForExistingAccount(form, account);

        // Set the navigation parameters:
        form.setViewId("AdminView");
        form.setPageId("NewPersonAccountPage");

        return getUIFModelAndView(form);
    }


    /* *******************************************************************************************************************
      *
      * Helper methods.
      *
      * ******************************************************************************************************************/

    /*
      * Populates the given form for a New Person Account page.
      */
    @SuppressWarnings("serial")
    protected void populateForNewPersonAccount(AdminForm form) {

        // Create a new AccountInformationHolder object:
        AccountInformationHolder accountInfo = new AccountInformationHolder();

        // The default account type is DirectChargeAccount
        ChargeableAccount account = new DirectChargeAccount();

        AccountProtectedInfo accountProtectedInfo = new AccountProtectedInfo();
        PersonName personName = new PersonName();
        PostalAddress postalAddress = new PostalAddress();
        ElectronicContact electronicContact = new ElectronicContact();

        // Set up the objects:
        accountProtectedInfo.setBankType(new BankType());
        accountProtectedInfo.setIdentityType(new IdentityType());
        accountProtectedInfo.setTaxType(new TaxType());
        personName.setDefault(true);
        postalAddress.setDefault(true);
        electronicContact.setDefault(true);
        account.setPersonNames(new HashSet<PersonName>(Arrays.asList(personName)));
        account.setPostalAddresses(new HashSet<PostalAddress>(Arrays.asList(postalAddress)));
        account.setElectronicContacts(new HashSet<ElectronicContact>(Arrays.asList(electronicContact)));
        account.setCreditLimit(new BigDecimal(0));
        account.setStatusType(new AccountStatusType());
        account.setLatePeriod(new LatePeriod());

        account.setAbleToAuthenticate(Boolean.TRUE);
        accountInfo.setAccountProtectedInfo(accountProtectedInfo);
        accountInfo.setDateOfBirth(new Date());
        accountInfo.setUserPreferences(new ArrayList<UserPreference>());
        accountInfo.setAccountType(AccountTypeValue.DIRECT_CHARGE_CODE);

        // Set the form's objects:
        form.setAccount(account);
        form.setAccountInfo(accountInfo);
    }

    /*
      * Populates the given form for an existing Account.
      */
    protected void populateForExistingAccount(AdminForm form, Account account) {

        // Set the Account attributes:
        String accountId = account.getId();
        AccountInformationHolder accountInfo = new AccountInformationHolder();

        AccountProtectedInfo accountProtectedInfo = accountService.getAccountProtectedInfo(accountId);
        List<UserPreference> userPreferences = userPreferenceService.getUserPreferences(accountId);

        accountInfo.setAccountProtectedInfo(accountProtectedInfo);
        accountInfo.setDateOfBirth(getAccountDateOfBirth(account));
        accountInfo.setUserPreferences(userPreferences);
        accountInfo.setAccountType(getAccountType(account).getId());

        AccountStatusType statusType = account.getStatusType();
        if (statusType != null && statusType.getId() != null) {
            form.setAccountStatusTypeId(statusType.getId().toString());
        }

        if (accountProtectedInfo != null) {
            BankType bankType = accountProtectedInfo.getBankType();
            if (bankType != null && bankType.getId() != null) {
                form.setAccountBankTypeId(bankType.getId().toString());
            }

            TaxType taxType = accountProtectedInfo.getTaxType();
            if (taxType != null && taxType.getId() != null) {
                form.setAccountTaxTypeId(taxType.getId().toString());
            }

            IdentityType identityType = accountProtectedInfo.getIdentityType();
            if (identityType != null && identityType.getId() != null) {
                form.setAccountIdentityTypeId(identityType.getId().toString());
            }

        }

        // Set the form's objects:
        form.setAccountInfo(accountInfo);
        form.setAccount(account);
    }

    /*
      * Determines if the given Account has a Date of Birth and returns it. Otherwise, returns null.
      */
    protected Date getAccountDateOfBirth(Account account) {
        return (account instanceof DirectChargeAccount) ? AccountUtils.cast(account, DirectChargeAccount.class).getDateOfBirth() : null;
    }

    /*
      * Determines the account type.
      */
    protected AccountTypeValue getAccountType(Account account) {
        return (account instanceof DirectChargeAccount) ? AccountTypeValue.DIRECT_CHARGE : AccountTypeValue.DELEGATE;
    }

    /*
      * Get the Account from the form object.
      * Creates a new Account object if the type selected on the form is different
      * from the one stored in the form object.
      */
    private Account setUpAccountFromForm(AdminForm form) {

        // Determine if we need to create a new account from the New Account form or can reuse the existing account:
        Account formAccount = form.getAccount();
        Account resultAccount = formAccount;

        // If there is no ID, then it's a new Account:
        if (formAccount != null && StringUtils.isBlank(formAccount.getId())) {

            String selectedAccountType = form.getAccountInfo().getAccountType();
            Account newAccount = null;

            if (StringUtils.equals(selectedAccountType, AccountTypeValue.DELEGATE_CODE)) {
                // Create a new DelegateAccount:
                newAccount = new DelegateAccount();
            } else if (StringUtils.equals(selectedAccountType, AccountTypeValue.DIRECT_CHARGE_CODE)) {
                // Create a new DirectChargeAccount:
                newAccount = new DirectChargeAccount();
                ((DirectChargeAccount) newAccount).setDateOfBirth(form.getAccountInfo().getDateOfBirth());
            }

            if (newAccount != null) {

                // Copy properties from the form stored Account to the new one:
                BeanUtils.copyProperties(formAccount, newAccount);

                if (formAccount instanceof ChargeableAccount && newAccount instanceof ChargeableAccount) {
                    LatePeriod existingLatePeriod =
                            auditableEntityService.getAuditableEntity(((ChargeableAccount) formAccount).getLatePeriod().getId(), LatePeriod.class);
                    ((ChargeableAccount) newAccount).setLatePeriod(existingLatePeriod);
                }

                AccountStatusType existingAccountStatusType = auditableEntityService.getAuditableEntity(form.getAccountStatusTypeId(), AccountStatusType.class);

                newAccount.setStatusType(existingAccountStatusType);

                resultAccount = newAccount;
            }

        } else if (resultAccount != null) {

            // For existing accounts just update the auditable information:
            resultAccount.setEditorId(userSessionManager.getUserId());
            resultAccount.setLastUpdate(new Date());
        }

        return resultAccount;
    }


}
