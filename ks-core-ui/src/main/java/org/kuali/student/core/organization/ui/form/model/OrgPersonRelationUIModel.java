package org.kuali.student.core.organization.ui.form.model;

import org.kuali.student.r2.core.class1.state.dto.StateInfo;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.rice.kim.KSPerson;

public class OrgPersonRelationUIModel extends OrgPersonRelationInfo{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4161545659203095405L;
	
	private String personName;
	private OrgInfo orgInfo;
	private TypeInfo typeInfo;
	private StateInfo stateInfo;	
	
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public OrgInfo getOrgInfo() {
		return orgInfo;
	}
	public void setOrgInfo(OrgInfo orgInfo) {
		this.orgInfo = orgInfo;
	}
	public TypeInfo getTypeInfo() {
		return typeInfo;
	}
	public void setTypeInfo(TypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}
	public StateInfo getStateInfo() {
		return stateInfo;
	}
	public void setStateInfo(StateInfo stateInfo) {
		this.stateInfo = stateInfo;
	}	
	
}
