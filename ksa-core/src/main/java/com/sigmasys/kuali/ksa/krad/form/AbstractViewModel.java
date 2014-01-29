package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.krad.model.InformationModel;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingPlan;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyPlan;
import com.sigmasys.kuali.ksa.service.UserPreferenceService;
import com.sigmasys.kuali.ksa.service.UserSessionManager;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import com.sigmasys.kuali.ksa.util.LocaleUtils;
import com.sigmasys.kuali.ksa.util.RequestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.web.form.UifFormBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.util.*;

/**
 * The base abstract form. It is supposed to be extended by KSA subclasses.
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("unchecked")
public abstract class AbstractViewModel extends UifFormBase {

    protected final Log logger = LogFactory.getLog(getClass());

    private static final String USER_PREF_ATTR_NAME = "userPreferencesSessionAttr";
    private static final String LOCALIZED_PARAMS_ATTR_NAME = "localizedParamsSessionAttr";

    private String searchType;
    private String searchValue;

    // Collections for search results
    private List<Account> accounts;
    private List<Transaction> transactions;
    private List<Activity> activity;

    private String message;

    // Several screens use a unified header to show account info including alerts/flags/holds
    private Account account;
    private List<InformationModel> alerts;
    private List<InformationModel> flags;
    private List<InformationModel> holds;
    //private List<Memo> memos;
    private List<PaymentBillingPlan> paymentBillingPlans;
    private List<ThirdPartyPlan> thirdPartyPlans;


    private static final KeyValuesBase searchTypeValuesFinder = new KeyValuesBase() {
        /**
         * This is an implementation of a key value finder, normally this would make a request to
         * a database to obtain the necessary values.
         *
         * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
         */
        @Override
        public List<KeyValue> getKeyValues() {
            List<KeyValue> keyValues = new ArrayList<KeyValue>(3);
            keyValues.add(new ConcreteKeyValue(SearchTypeValue.ACCOUNT.name(), SearchTypeValue.ACCOUNT.toString()));
            keyValues.add(new ConcreteKeyValue(SearchTypeValue.TRANSACTION.name(), SearchTypeValue.TRANSACTION.toString()));
            keyValues.add(new ConcreteKeyValue(SearchTypeValue.ACTIVITY.name(), SearchTypeValue.ACTIVITY.toString()));
            return Collections.unmodifiableList(keyValues);
        }
    };

    /*
      This is a modification. The issue is to remove the drop-down searchType control in UifWidgetDefinitions
      Should we decide to reuse then this constructor and the setting of the searchType should be removed
     */
    public AbstractViewModel() {
        searchType = SearchTypeValue.ACCOUNT.name();
    }

    protected ConfigService getConfigService() {
        return ContextUtils.getBean(ConfigService.class);
    }

    protected UserPreferenceService getUserPreferenceService() {
        return ContextUtils.getBean(UserPreferenceService.class);
    }

    protected UserSessionManager getUserSessionManager() {
        return ContextUtils.getBean(UserSessionManager.class);
    }

    protected HttpSession getSession() {
        HttpServletRequest request = RequestUtils.getThreadRequest();
        if (request == null) {
            throw new IllegalStateException("HttpServletRequest is null");
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalStateException("HttpSession has not been initialized");
        }
        return session;
    }


    protected Map<String, String> getUserPreferences() {
        Map<String, String> preferencesMap = (Map<String, String>) getSession().getAttribute(USER_PREF_ATTR_NAME);
        if (preferencesMap == null) {
            String userId = getUserSessionManager().getUserId();
            if (userId == null) {
                throw new IllegalStateException("User ID is null");
            }
            preferencesMap = getUserPreferenceService().getUserPreferenceMap(userId);
            getSession().setAttribute(USER_PREF_ATTR_NAME, preferencesMap);
        }
        return preferencesMap;
    }

    /**
     * Returns the locale-aware string value using the locale from user preferences
     * or system wide LocaleUtils for the current default locale
     *
     * @param key parameter name
     * @return parameter value
     */
    public String getLValue(String key) {
        Map<String, String> params = (Map<String, String>) getSession().getAttribute(LOCALIZED_PARAMS_ATTR_NAME);
        if (params == null) {
            Map<String, String> userPreferences = getUserPreferences();
            if (userPreferences != null) {
                String localeLang = userPreferences.get(Constants.LOCALE_LANG);
                String localeCountry = userPreferences.get(Constants.LOCALE_COUNTRY);
                if (localeLang != null && localeCountry != null) {
                    Locale locale = new Locale(localeLang, localeCountry);
                    params = getConfigService().getLocalizedParameters(locale);
                    getSession().setAttribute(LOCALIZED_PARAMS_ATTR_NAME, params);
                }
            }
        }
        String value = (params != null) ? params.get(key) : null;
        if (value == null) {
            value = LocaleUtils.getValue(key);
        }
        logger.info("Localized string: key = " + key + ", value = " + value);
        return (value != null) ? value : "";
    }

    public KeyValuesBase getSearchTypeValuesFinder() {
        return searchTypeValuesFinder;
    }

    public String getContext() {
        HttpServletRequest request = RequestUtils.getThreadRequest();
        if (request == null) {
            throw new IllegalStateException("HttpServletRequest is null");
        }
        return request.getContextPath();
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Activity> getActivity() {
        return activity;
    }

    public void setActivity(List<Activity> activity) {
        this.activity = activity;
    }

    /**
     * Display a message to the user
     *
     * @deprecated Don't use this anymore.
     *             Instead use GlobalVariables.getMessageMap().putInfo();, GlobalVariables.getMessageMap().putWarning();, GlobalVariables.getMessageMap().putError();
     */
    @Deprecated
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFormattedDate(Date date) {
        return (date != null) ? DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(date) : "";
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<InformationModel> getAlerts() {
        if (alerts == null) {
            alerts = new ArrayList<InformationModel>();
        }
        return alerts;
    }

    public String getAlertTooltip() {
        return getInformationTooltip("Alerts", alerts);
    }

    public void setAlertObjects(List<Alert> alerts) {
        List<InformationModel> models = new ArrayList<InformationModel>(alerts.size());
        for (Alert alert : alerts) {
            models.add(new InformationModel(alert));
        }
        setAlerts(models);
    }

    public void setAlerts(List<InformationModel> alerts) {
        this.alerts = alerts;
    }

    public List<InformationModel> getFlags() {
        if (flags == null) {
            flags = new ArrayList<InformationModel>();
        }
        return flags;
    }

    public String getFlagTooltip() {
        return getInformationTooltip("Flags", flags);
    }

    public void setFlagObjects(List<Flag> flags) {
        List<InformationModel> models = new ArrayList<InformationModel>(flags.size());
        for (Flag flag : flags) {
            models.add(new InformationModel(flag));
        }
        setFlags(models);
    }

    public void setFlags(List<InformationModel> flags) {
        this.flags = flags;
    }

    /*
    public List<Memo> getMemos() {
        if (memos == null) {
            memos = new ArrayList<Memo>();
        }
        return memos;
    }

    public String getMemoTooltip() {
        return getInformationTooltip("Memos", alerts);
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
    }
    */
    public List<InformationModel> getHolds() {
        if (holds == null) {
            holds = new ArrayList<InformationModel>();
        }
        return holds;
    }

    public void setHolds(List<InformationModel> holds) {
        this.holds = holds;
    }

    public String getHoldTooltip() {
        return getInformationTooltip("Holds", holds);
    }

    private static int getItemsPerPage() {
        return Integer.valueOf(ContextUtils.getBean(ConfigService.class).getParameter(Constants.QUICKVIEW_INFORMATION_COUNT));
    }

    public static String getInformationTooltip(String name, List<InformationModel> items) {

        int itemsPerPage = getItemsPerPage();

        String html = "<b>" + name + " (";

        if (items == null || items.size() == 0) {
            html += "0/0)</b><br/><p>No " + name + "</p>";
            return html;
        }

        int size = items.size();

        if (size > itemsPerPage) {
            html += itemsPerPage + "/" + size + ")</b><br/>";
        } else {
            html += size + "/" + size + ")</b><br/>";
        }

        html += "<p>";
        for (int i = 0; i < items.size() && i < itemsPerPage; i++) {
            html += items.get(i).getDisplayValue() + "<br/>";
        }
        html += "</p>";

        return html;
    }


    public List<ThirdPartyPlan> getThirdPartyPlansForTooltip() {
        if(thirdPartyPlans == null) {
            thirdPartyPlans = new ArrayList<ThirdPartyPlan>();
        }
        return thirdPartyPlans;
    }

    public void setThirdPartyPlansForTooltip(List<ThirdPartyPlan> thirdPartyPlans) {
        this.thirdPartyPlans = thirdPartyPlans;
    }

    public boolean isAccountThirdParty() {
        return (account != null && account instanceof ThirdPartyAccount);
    }

    public String getThirdPartyTooltip() {
        StringBuilder html = new StringBuilder();

        html.append("<b>Current Active Third Party Payment Plans (");
        List<ThirdPartyPlan> plans = getThirdPartyPlansForTooltip();
        int size = plans.size();
        int itemsPerPage = getItemsPerPage();

        if(size == 0) {
            html.append("0/0)</b><br/><p>No third party plans</p>");
            return html.toString();
        }
        if(size > itemsPerPage) {
            html.append(itemsPerPage).append("/").append(size);
        } else {
            html.append(size).append("/").append(size);
        }
        html.append(")</b><br/><p>");

        for(int i = 0; i < size && i < itemsPerPage; i++) {
            html.append(plans.get(i).getName()).append("<br/>");
        }
        html.append("</p>");

        return html.toString();
    }

    public List<PaymentBillingPlan> getPaymentBillingPlansForTooltip() {
        if(paymentBillingPlans == null) {
            paymentBillingPlans = new ArrayList<PaymentBillingPlan>();
        }
        return paymentBillingPlans;
    }

    public void setPaymentBillingPlansForTooltip(List<PaymentBillingPlan> paymentBillingPlans) {
        this.paymentBillingPlans = paymentBillingPlans;
    }

    public String getPaymentBillingPlansTooltip() {
        StringBuilder html = new StringBuilder();

        html.append("<b>Current Active Payment Billing Plans (");
        List<PaymentBillingPlan> plans = getPaymentBillingPlansForTooltip();
        int size = plans.size();
        int itemsPerPage = getItemsPerPage();

        if(size == 0) {
            html.append("0/0)</b><br/><p>No payment billing plans</p>");
            return html.toString();
        }
        if(size > itemsPerPage) {
            html.append(itemsPerPage).append("/").append(size);
        } else {
            html.append(size).append("/").append(size);
        }
        html.append(")</b><br/><p>");

        for(int i = 0; i < size && i < itemsPerPage; i++) {
            html.append(plans.get(i).getName()).append("<br/>");
        }
        html.append("</p>");

        return html.toString();
    }


}


