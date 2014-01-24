package org.kuali.student.core.organization.ui.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.student.core.organization.ui.form.model.ProposalInfoModel;
import org.kuali.student.r2.core.proposal.dto.ProposalInfo;

public class PersonAuthorizationForm extends UifFormBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6179171431175709924L;
	
	private ProposalInfo proposalInfo;
	private String submittedByName;	
	private String submittedForName;	
	private String submittedForId;	
	private String forOrganizationName;	
	private String forOrganizationId;	
	private String requestedSince;
	private String stateName;
	private String typeName;
	private String formattedDescription;
	private String formattedRationale;
		
	private List<ProposalInfoModel> proposals;
	
	private boolean clarificationRequired;
	private boolean renderAck;
	
	public ProposalInfo getProposalInfo() {
		return proposalInfo;
	}
	public void setProposalInfo(ProposalInfo proposalInfo) {
		this.proposalInfo = proposalInfo;
	}
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
	public String getSubmittedForId() {
		return submittedForId;
	}
	public void setSubmittedForId(String submittedForId) {
		this.submittedForId = submittedForId;
	}
	public String getForOrganizationName() {
		return forOrganizationName;
	}
	public void setForOrganizationName(String forOrganizationName) {
		this.forOrganizationName = forOrganizationName;
	}
	public String getForOrganizationId() {
		return forOrganizationId;
	}
	public void setForOrganizationId(String forOrganizationId) {
		this.forOrganizationId = forOrganizationId;
	}
	public String getRequestedSince() {
		return requestedSince;
	}
	public void setRequestedSince(String requestedSince) {
		this.requestedSince = requestedSince;
	}
	public List<ProposalInfoModel> getProposals() {
		return proposals;
	}
	public void setProposals(List<ProposalInfoModel> proposals) {
		this.proposals = proposals;
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
	public boolean getClarificationRequired() {
		return clarificationRequired;
	}
	public void setClarificationRequired(boolean clarificationRequired) {
		this.clarificationRequired = clarificationRequired;
	}
	public boolean getRenderAck() {
		return renderAck;
	}
	public void setRenderAck(boolean renderAck) {
		this.renderAck = renderAck;
	}	
	public String getFormattedDescription() {
		return formattedDescription;
	}
	public void setFormattedDescription(String formattedDescription) {
		this.formattedDescription = formattedDescription;
	}
	public String getFormattedRationale() {
		return formattedRationale;
	}
	public void setFormattedRationale(String formattedRationale) {
		this.formattedRationale = formattedRationale;
	}
	public String getEffectiveDateString(){
		SimpleDateFormat stringFormat = new SimpleDateFormat("mm/dd/yyyy");
		String strDate = new String();
		if(proposalInfo.getEffectiveDate() != null){
			strDate = stringFormat.format(proposalInfo.getEffectiveDate());
		}
		return strDate; 
	}
	
}