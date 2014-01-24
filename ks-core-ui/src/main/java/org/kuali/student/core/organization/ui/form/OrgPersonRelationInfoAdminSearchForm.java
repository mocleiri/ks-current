package org.kuali.student.core.organization.ui.form;

import java.util.ArrayList;

import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.student.core.organization.ui.form.model.OrgPersonRelationUIModel;
import org.kuali.student.r2.core.organization.dto.OrgInfo;

public class OrgPersonRelationInfoAdminSearchForm extends UifFormBase{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4703388779356717776L;
	
	private String keywordSearch;
	private String personName;
	private String resultSize;
	private String selectedOrgPersonRelationId;
	private OrgPersonRelationUIModel orgPersonRelation;
	private ArrayList<OrgPersonRelationUIModel> orgPersonRelationUIModel;

	public ArrayList<OrgPersonRelationUIModel> getOrgPersonRelationUIModel() {
		return orgPersonRelationUIModel;
	}

	public void setOrgPersonRelationUIModel(
			ArrayList<OrgPersonRelationUIModel> orgPersonRelationUIModel) {
		this.orgPersonRelationUIModel = orgPersonRelationUIModel;
	}

	public String getKeywordSearch() {
		return keywordSearch;
	}

	public void setKeywordSearch(String keywordSearch) {
		this.keywordSearch = keywordSearch;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getSelectedOrgPersonRelationId() {
		return selectedOrgPersonRelationId;
	}

	public void setSelectedOrgPersonRelationId(String selectedOrgPersonRelationId) {
		this.selectedOrgPersonRelationId = selectedOrgPersonRelationId;
	}

	public OrgPersonRelationUIModel getOrgPersonRelation() {
		return orgPersonRelation;
	}

	public void setOrgPersonRelation(OrgPersonRelationUIModel orgPersonRelation) {
		this.orgPersonRelation = orgPersonRelation;
	}

	public String getResultSize() {
		return resultSize;
	}

	public void setResultSize(String resultSize) {
		this.resultSize = resultSize;
	}	
	
}