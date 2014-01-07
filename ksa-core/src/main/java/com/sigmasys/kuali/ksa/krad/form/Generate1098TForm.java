package com.sigmasys.kuali.ksa.krad.form;

import java.util.List;

import com.sigmasys.kuali.ksa.krad.model.Form1098TModel;
import com.sigmasys.kuali.ksa.model.*;
import org.apache.commons.lang.StringUtils;

/**
 * This class serves as a form behind the "Generate 1098T" screen. 
 * 
 * @author Sergey
 */
@SuppressWarnings("serial")
public class Generate1098TForm extends AbstractViewModel {

	/**
	 * Account associated with the 1098T View.
	 */
	private Account account;

	/**
	 * A List of reporting years. Multiple selection is allowed. 
	 */
	private List<String> reportYears;
	
	/**
	 * Generated Form 1098T collection lines for the search by account.
	 */
	private List<Form1098TModel> form1098TModels;

    /**
     * An error generating 1098T.
     */
    private String documentGenerationError;

    /**
     * An error viewing a generated 1098T form.
     */
    private String viewGeneratedFormError;


    /**
     * Answers whether to hide the "No saved documents" message.
     * That message must be shown only if the "Display Saved Document" button
     * has been pressed and none were found.
     *
     * @return boolean Whether to hide the "No saved documents" message.
     */
    public boolean getHideNoSavedDocumentsMessage() {
        return (form1098TModels == null)
                || ((form1098TModels != null) && !form1098TModels.isEmpty());
    }

	/**
	 * Returns the page header, which defaults to the Account holder's name.
	 * 
	 * @return Page header.
	 */
	public String getPageHeader() {
		return (account != null) && (account.getDefaultPersonName() != null) 
				? account.getDefaultPersonName().getDisplayValue() : "Generate 1098T";
	}
	
	/**
	 * Returns CSS display style of Address Line 2.
	 * 
	 * @return CSS display style of Address Line 2. 
	 */
	public String getAddressLine2DisplayStyle() {
		return (account != null) && (account.getDefaultPostalAddress() != null) && StringUtils.isNotBlank(account.getDefaultPostalAddress().getStreetAddress2()) ? "inline-block" : "none";
	}
	
	/**
	 * Returns CSS display style of Address Line 3.
	 * 
	 * @return CSS display style of Address Line 3. 
	 */
	public String getAddressLine3DisplayStyle() {
		return (account != null) && (account.getDefaultPostalAddress() != null) && StringUtils.isNotBlank(account.getDefaultPostalAddress().getStreetAddress3()) ? "inline-block" : "none";
	}

    /*
      * Determines the account type.
      */
    public String getAccountType() {
        return (account instanceof DirectChargeAccount)
                ? AccountTypeValue.DIRECT_CHARGE.toString() : AccountTypeValue.DELEGATE.toString();
    }

    public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<String> getReportYears() {
		return reportYears;
	}

	public void setReportYears(List<String> reportYears) {
		this.reportYears = reportYears;
	}

	public List<Form1098TModel> getForm1098TModels() {
		return form1098TModels;
	}

	public void setForm1098TModels(List<Form1098TModel> form1098TModels) {
		this.form1098TModels = form1098TModels;
	}

    public String getDocumentGenerationError() {
        return documentGenerationError;
    }

    public void setDocumentGenerationError(String documentGenerationError) {
        this.documentGenerationError = documentGenerationError;
    }

    public String getViewGeneratedFormError() {
        return viewGeneratedFormError;
    }

    public void setViewGeneratedFormError(String viewGeneratedFormError) {
        this.viewGeneratedFormError = viewGeneratedFormError;
    }
}
