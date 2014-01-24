package org.kuali.student.core.organization.ui.form.model;

import org.kuali.student.r2.core.proposal.dto.ProposalInfo;

public class ProposalInfoModel extends ProposalInfo {
	private String submittedByName;	
	private String submittedForName;
	private String organizationName;
	private String stateName;
	private String typeName;
	
	public String getSubmittedByName() {
		return submittedByName;
	}
	public void setSubmittedByName(String submittedByName) {
		this.submittedByName = submittedByName;
	}
	public String getSubmittedForName() {
		return submittedForName;
	}
	public void setSubmittedForName(String submittedForName) {
		this.submittedForName = submittedForName;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}	
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	
}
